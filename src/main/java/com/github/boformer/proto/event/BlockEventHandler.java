package com.github.boformer.proto.event;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.event.Subscribe;

import com.github.boformer.proto.ProtoPlugin;
import com.github.boformer.proto.config.WorldConfig;
import com.github.boformer.proto.data.PlotData;
import com.github.boformer.proto.data.PlotID;
import com.github.boformer.proto.data.PlotState;
import com.github.boformer.proto.util.PlotUtil;

import dummy.sponge.BlockBreakEventDummy;
import dummy.sponge.SpongeDummy;

public class BlockEventHandler
{
	private final ProtoPlugin plugin;


	/**
	 * <i>Internal constructor: Constructs a new block event handler that can be registered in the server plugin manager.</i>
	 * 
	 * @param plugin The plugin
	 */
	public BlockEventHandler(ProtoPlugin plugin)
	{
		this.plugin = plugin;
	}

	//TODO onblockplace
	
	/**
	 * Called when a block is mined by a player.
	 * 
	 * @param event The block break event
	 */
	@Subscribe
	public void onBlockBreak(BlockBreakEventDummy event) // TODO replace dummy event when supported
	{
		/*
		 * What it does: 
		 * 1. no world config --> just ignore the event. return
		 * 2. plots enabled --> load plot data
		 *  	a) plot is claimed --> check server and plugin plot permission --> return
		 * 		b) plot is public --> continue with 3.
		 * 3. check server and plugin world permission --> return
		 */
		
		WorldConfig worldConfig = plugin.getConfigManager().getWorldConfig(event.getWorld().getName());

		//no config -> not our job
		if(worldConfig == null) return;
		
		try
		{
			if(worldConfig.plotsEnabled)
			{
				// plots enabled -> check plot permissions
				
				PlotID plotID = PlotUtil.calculatePlotID(event.getLocation().getPosition(), event.getWorld(), worldConfig);
				PlotData plotData = plugin.getDataManager().getPlotData(plotID);
			
				if(plotData.getState() != PlotState.PUBLIC) 
				{
					//plot is not public -> now check permissions
					
					//TODO add proto.plot.build.*
					
					//1. server
					if(SpongeDummy.hasWorldPermission(event.getPlayer(), event.getWorld(), "proto.plot.build." + plotID.getX() + "." + plotID.getZ())) 
					{
						//player has permission in plot
						return;
					}
					
					//2. plugin
					else if(plugin.getDataManager().hasPlotAccess(SpongeDummy.getPlayerUniqueId(event.getPlayer()), plotID, "build")) 
					{
						//player has permission in plot
						return;
					}
					else
					{
						List<String> managers = plugin.getDataManager().getPlayerNamesByPermission(plotID, "manage");
						
						//player has no permission
						event.getPlayer().sendMessage("You do not have the permission to build here! Ask one of the plot's managers:");
						//TODO singular: The plot manager
						//TODO apache commons lang
						event.getPlayer().sendMessage(StringUtils.join(managers, ", "));
						
						
						
						event.setCancelled(true);
						return;
						
						//TODO display plot owner info!
					}
				}
			
			}
		
			//plots not enabled or public plot -> check world permissions
			
			//permission sources: 1. server, 2. plugin 
			
			//1. server
			if(SpongeDummy.hasWorldPermission(event.getPlayer(), event.getWorld(), "proto.world.build")) 
			{
				//player has permission in world
				return;
			}
			
			//2. plugin
			else if(plugin.getDataManager().hasWorldAccess(SpongeDummy.getPlayerUniqueId(event.getPlayer()), event.getWorld().getUniqueID(), "build")) 
			{
				//player has permission in world
				return;
			}
			
			else
			{
				//player has no permission
				
				if(worldConfig.plotsEnabled) 
				{
					if(SpongeDummy.hasWorldPermission(event.getPlayer(), SpongeDummy.getPlayerWorld(event.getPlayer()), "proto.plot.claim")) 
					{
						event.getPlayer().sendMessage("In this world, you can not build in public plots!");
						event.getPlayer().sendMessage("Use /plot claim to claim this plot."); //TODO add price
					}
					else
					{
						event.getPlayer().sendMessage("You do not have the permission to build in this plot!");
					}
				}
				else
				{
					event.getPlayer().sendMessage("You do not have the permission to build in this world!");
					event.getPlayer().sendMessage("Ask a Staff member to get permission."); //TODO add list of managers/owners of this world
				}
				
				event.setCancelled(true);
				return;
			}
		}
		catch (Exception e)
		{
			event.getPlayer().sendMessage("Database Error: Can not check access permission! Please contact a Staff member.");
			event.setCancelled(true);
			return;
		}
	}
		
}
