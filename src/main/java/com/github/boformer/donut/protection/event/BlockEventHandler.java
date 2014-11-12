package com.github.boformer.donut.protection.event;

import org.spongepowered.api.event.Subscribe;

import com.github.boformer.donut.protection.DonutProtectionPlugin;
import com.github.boformer.donut.protection.config.WorldConfig;
import com.github.boformer.donut.protection.data.PlotData;
import com.github.boformer.donut.protection.data.PlotID;
import com.github.boformer.donut.protection.data.PlotStatus;
import com.github.boformer.donut.protection.util.PlotUtil;

import dummy.sponge.BlockBreakEventDummy;
import dummy.sponge.Dummy;

public class BlockEventHandler
{
	private final DonutProtectionPlugin plugin;


	public BlockEventHandler(DonutProtectionPlugin plugin)
	{
		this.plugin = plugin;
	}

	//TODO onblockplace
	
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
		
		WorldConfig worldConfig = plugin.getConfigManager().getWorldConfig(event.getWorld());

		//no config -> not our job
		if(worldConfig == null) return;
		
		try
		{
			if(worldConfig.plotsEnabled)
			{
				// plots enabled -> check plot permissions
				
				PlotID plotID = PlotUtil.calculatePlotID(event.getLocation().getPosition(), event.getWorld(), worldConfig);
				PlotData plotData = plugin.getDataManager().getPlotData(plotID);
			
				if(plotData.getStatus() != PlotStatus.PUBLIC) 
				{
					//plot is not public -> now check permissions
					
					//1. server
					if(Dummy.hasWorldPermission(event.getPlayer(), event.getWorld(), "donut.protection.build.plot." + plotID.getX() + "." + plotID.getZ())) 
					{
						//player has permission in plot
						return;
					}
					
					//2. plugin
					else if(plugin.getDataManager().hasPlotAccess(Dummy.getPlayerUniqueId(event.getPlayer()), plotID, "build")) 
					{
						//player has permission in plot
						return;
					}
					else
					{
						//player has no permission
						event.getPlayer().sendMessage("You do not have the permission to build in this plot!");
						event.setCancelled(true);
						return;
						
						//TODO display plot owner info!
					}
				}
			
			}
		
			//plots not enabled or public plot -> check world permissions
			
			//permission sources: 1. server, 2. plugin 
			
			//1. server
			if(Dummy.hasWorldPermission(event.getPlayer(), event.getWorld(), "donut.protection.build")) 
			{
				//player has permission in world
				return;
			}
			
			//2. plugin
			else if(plugin.getDataManager().hasWorldAccess(Dummy.getPlayerUniqueId(event.getPlayer()), event.getWorld().getUniqueID(), "build")) 
			{
				//player has permission in world
				return;
			}
			
			else
			{
				//player has no permission
				
				if(worldConfig.plotsEnabled) 
				{
					event.getPlayer().sendMessage("You do not have the permission to build in this plot!");
					//TODO display info how to claim if player has 'claim' permission
				}
				else
				{
					event.getPlayer().sendMessage("You do not have the permission to build in this world!");
					//TODO display info how to get permission (list managers/owners of the world)
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
