package com.github.boformer.proto.plotcheck;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.spongepowered.api.Game;
import org.spongepowered.api.world.World;

import com.github.boformer.proto.ProtoPlugin;
import com.github.boformer.proto.config.WorldConfig;
import com.github.boformer.proto.data.PlotID;
import com.github.boformer.proto.data.PlotState;

/**
 * Manages the expired and submitted plots.
 */
public class PlotCheckManager
{
	private final ProtoPlugin plugin;
	private final Game game;
	
	private List<PlotID> submittedPlots;
	private List<PlotID> expiredPlots;
	private List<PlotID> deletionPlots;
	
	private final Random random;
	
	//TODO call updatePlotLists every x hours and notify staff members
	//TODO or listen for plot state changes
	
	//TODO automatic deletion of expired plots: call automatic deletion method to delete plots when server is idling
	//TODO use plotstate "LOCKED_FOR_DELETION" while the plot is not regenerated
	
	
	/**
	 * <i>Internal constructor: Constructs a new PlotCheck manager.</i>
	 * 
	 * @param plugin
	 * @param game
	 */
	public PlotCheckManager(ProtoPlugin plugin, Game game)
	{
		this.plugin = plugin;
		this.game = game;
		
		random = new Random();
	}
	
	public void initialize() 
	{
		updatePlotLists();
	}
	
	public void updatePlotLists() 
	{
		Date currentDate = new Date();
		Calendar calendar = Calendar.getInstance();
		
		//find submitted plots in database
		try
		{
			submittedPlots = plugin.getDataManager().getPlotsByState(PlotState.SUBMITTED);
		}
		catch (Exception e)
		{
			plugin.getLogger().error("Error while updating list of submitted plots: " + e.getMessage());
			e.printStackTrace();
		}
		
		//find expired plots in database
		expiredPlots = new ArrayList<>();
		deletionPlots = new ArrayList<>();
		
		for(String worldName : plugin.getConfigManager().getWorldNames())
		{
			World world = game.getWorld(worldName);
			
			if(world == null) continue; //world not loaded
			
			WorldConfig worldConfig = plugin.getConfigManager().getWorldConfig(world.getName());
			
			if(worldConfig.plotExpirationDays < 0) continue; //plot expiration disabled
			
			//subtract configured number of days from current date
			calendar.setTime(currentDate);
			calendar.add(Calendar.DATE, - worldConfig.plotExpirationDays);
			
			//add plots to list
			try
			{
				List<PlotID> plotList;
				
				//TODO use enums?
				//when does a plot expire?
				if(worldConfig.plotExpirationStartTime == PlotExpirationStartTime.CREATION) 
				{
					plotList = plugin.getDataManager().getPlotsByLatestCreationDate(PlotState.CLAIMED, calendar.getTime(), world.getName());
				}
				else if(worldConfig.plotExpirationStartTime == PlotExpirationStartTime.LAST_MODIFICATION) 
				{
					plotList = plugin.getDataManager().getPlotsByLatestModificationDate(PlotState.CLAIMED, calendar.getTime(), world.getName());
				}
				else if(worldConfig.plotExpirationStartTime == PlotExpirationStartTime.LAST_OWNER_LOGIN) 
				{
					plotList = plugin.getDataManager().getPlotsByLatestPlayerLoginDate(PlotState.CLAIMED, calendar.getTime(), "owner", world.getName());
				}
				else if(worldConfig.plotExpirationStartTime == PlotExpirationStartTime.LAST_MANAGER_LOGIN) 
				{
					plotList = plugin.getDataManager().getPlotsByLatestPlayerLoginDate(PlotState.CLAIMED, calendar.getTime(), "manager", world.getName());
				}
				else
				{
					//not configured -> next world
					continue;
				}
			
				//TODO use enums?
				//what happens when a plot is expired?
				if(worldConfig.plotExpirationAction == PlotExpirationAction.STAFF_REVIEW) 
				{
					expiredPlots.addAll(plotList);
				}
				else if(worldConfig.plotExpirationAction == PlotExpirationAction.AUTO_DELETE) 
				{
					//TODO remove player access, change state to LOCKED_FOR_DELETION
					plugin.getDataManager().preparePlotsForDeletion(plotList);
					
					deletionPlots.addAll(plotList);
				}
				else 
				{
					//not configured -> next world
					continue;
				}
			}
			catch (Exception e)
			{
				plugin.getLogger().error("Error while updating list of expired plots for world  '" + world.getName() +  "': " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the list of submitted plots (state {@link PlotState#SUBMITTED}).
	 * 
	 * @return A list of plot IDs
	 */
	public List<PlotID> getSubmittedPlots()
	{
		return submittedPlots;
	}

	/**
	 * Gets the list of expired plots that are marked for deletion.
	 * 
	 * @return A list of plot IDs
	 */
	public List<PlotID> getDeletionPlots()
	{
		return deletionPlots;
	}
	
	/**
	 * Gets the list of expired plots that are marked for review.
	 * 
	 * @return A list of plot IDs
	 */
	public List<PlotID> getExpiredPlots()
	{
		return expiredPlots;
	}
	
	/**
	 * Gets a random plot that was submitted or a that is expired and should be reviewed by a staff member.
	 * 
	 * @return The plot ID or <code>null</code> if no plot found
	 */
	public PlotID getRandomReviewPlot() 
	{
		if(submittedPlots.size() > 0 && random.nextBoolean()) 
		{
			return submittedPlots.get(random.nextInt(submittedPlots.size()));
		}
		else if (expiredPlots.size() > 0)
		{
			return expiredPlots.get(random.nextInt(expiredPlots.size()));
		}
		else return null;
	}
}
