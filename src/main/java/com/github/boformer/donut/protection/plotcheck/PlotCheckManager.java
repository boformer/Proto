package com.github.boformer.donut.protection.plotcheck;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.spongepowered.api.Game;
import org.spongepowered.api.world.World;

import com.github.boformer.donut.protection.DonutProtectionPlugin;
import com.github.boformer.donut.protection.config.WorldConfig;
import com.github.boformer.donut.protection.data.PlotID;
import com.github.boformer.donut.protection.data.PlotState;

public class PlotCheckManager
{
	private final DonutProtectionPlugin plugin;
	private final Game game;
	
	private List<PlotID> submittedPlots;
	private List<PlotID> expiredPlots;
	
	Random random;
	
	//TODO call updatePlotLists every x hours and notify staff members
	//TODO Listen for plot state changes
	
	public PlotCheckManager(DonutProtectionPlugin plugin, Game game)
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
				expiredPlots.addAll(plugin.getDataManager().getPlotsByPermission(PlotState.CLAIMED, calendar.getTime(), world.getUniqueID()));
			}
			catch (Exception e)
			{
				plugin.getLogger().error("Error while updating list of expired plots for world  '" + world.getName() +  "': " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public List<PlotID> getSubmittedPlots()
	{
		return submittedPlots;
	}

	public List<PlotID> getExpiredPlots()
	{
		return expiredPlots;
	}
	
	public PlotID getRandomPlot() 
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
