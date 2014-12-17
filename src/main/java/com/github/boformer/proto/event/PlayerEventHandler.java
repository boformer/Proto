package com.github.boformer.proto.event;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.block.BlockLoc;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.EntityInteractionType;
import org.spongepowered.api.event.player.PlayerInteractEvent;
import org.spongepowered.api.util.event.Subscribe;
import org.spongepowered.api.world.World;

import com.github.boformer.proto.ProtoPlugin;
import com.github.boformer.proto.config.WorldConfig;
import com.github.boformer.proto.data.PlotData;
import com.github.boformer.proto.data.PlotID;
import com.github.boformer.proto.data.PlotState;
import com.github.boformer.proto.util.PlotUtil;

import dummy.sponge.SpongeDummy;

//TODO javadoc
public class PlayerEventHandler
{
	private final ProtoPlugin plugin;

	
	//TODO raw BlockChangeEvent, fluids, fire, pistons, flora, dispenser, item spawn (separate plugin?) etc.

	/**
	 * <i>Internal constructor: Constructs a new block event handler that can be registered in the server plugin manager.</i>
	 * 
	 * @param plugin The plugin
	 */
	public PlayerEventHandler(ProtoPlugin plugin)
	{
		this.plugin = plugin;
	}

	//TODO onblockplace
	
	/**
	 * Called when a player interacts with the world.
	 * 
	 * @param event The player interact event
	 */
	@Subscribe
	public void onPlayerInteract(PlayerInteractEvent event) // TODO replace dummy event when supported
	{
		//TODO other interaction? animals?
		
		
		
		
		/*
		 * What it does: 
		 * 1. no world config --> just ignore the event. return
		 * 2. plots enabled --> load plot data
		 *  	a) plot is claimed --> check server and plugin plot permission --> return
		 * 		b) plot is public --> continue with 3.
		 * 3. check server and plugin world permission --> return
		 */
		
		//always allow that (creative mode block select)
		if(event.getInteractionType() == EntityInteractionType.MIDDLE_CLICK) return;
		
		World world = (World) event.getPlayer().getWorld();
		
		WorldConfig worldConfig = plugin.getConfigManager().getWorldConfig(world.getName());

		//no config -> not our job
		if(worldConfig == null) return;
		
		try
		{
			if(worldConfig.plotsEnabled)
			{
				// plots enabled -> check plot permissions
				
				BlockLoc block = event.getBlock().orNull();
				
				// no block affected... air?
				if(block == null || block.getType() == BlockTypes.AIR) return;
				
				PlotID plotID = PlotUtil.calculatePlotID(block.getPosition(), world.getName(), worldConfig);
				PlotData plotData = plugin.getDataManager().getPlotData(plotID);
			
				if(plotData.getState() != PlotState.PUBLIC) 
				{
					//plot is not public -> now check permissions
					
					//TODO add proto.plot.build.*
					
					//1. server
					if(SpongeDummy.hasWorldPermission(event.getPlayer(), world, "proto.plot.build." + plotID.getX() + "." + plotID.getZ())) 
					{
						//player has permission in plot
						return;
					}
					
					//2. plugin
					else if(plugin.getDataManager().hasPlotAccess(event.getPlayer().getUniqueId(), plotID, "build")) 
					{
						//player has permission in plot
						return;
					}
					else
					{
						//TODO limit list items to config.maxListSize?
						List<String> managers = plugin.getDataManager().getPlayerNamesByPlotPermission(plotID, "manage");
						
						//player has no permission
						if(managers.size() == 0) 
						{
							event.getPlayer().sendMessage("You do not have the permission to build in this plot!");
						}
						else if(managers.size() == 1) 
						{
							event.getPlayer().sendMessage("You do not have the permission to build here!");
							event.getPlayer().sendMessage(String.format("Ask the plot's manager (%s) for build rights.", managers.get(0)));
						}
						else
						{
							event.getPlayer().sendMessage("You do not have the permission to build here!");							
							event.getPlayer().sendMessage("Ask one of the plot's managers for build rights:");
							event.getPlayer().sendMessage(StringUtils.join(managers, ", "));
						}
						
						event.setCancelled(true);
						return;
					}
				}
			
			}
		
			//plots not enabled or public plot -> check world permissions
			
			//permission sources: 1. server, 2. plugin 
			
			//1. server
			if(SpongeDummy.hasWorldPermission(event.getPlayer(), world, "proto.world.build")) 
			{
				//player has permission in world
				return;
			}
			
			//2. plugin
			else if(plugin.getDataManager().hasWorldAccess(event.getPlayer().getUniqueId(), world.getName(), "build")) 
			{
				//player has permission in world
				return;
			}
			
			else
			{
				//player has no permission
				
				if(worldConfig.plotsEnabled) 
				{
					if(SpongeDummy.hasWorldPermission(event.getPlayer(), world, "proto.plot.claim")) 
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
					//TODO limit list items to config.maxListSize?
					List<String> managers = plugin.getDataManager().getPlayerNamesByWorldPermission(world.getName(), "manage");
					
					if(managers.size() == 0) 
					{
						event.getPlayer().sendMessage("You do not have the permission to build in this world!");
					}
					else if(managers.size() == 1) 
					{
						event.getPlayer().sendMessage("You do not have the permission to build here!");
						event.getPlayer().sendMessage(String.format("Ask the world's manager (%s) for build rights.", managers.get(0)));
					}
					else
					{
						event.getPlayer().sendMessage("You do not have the permission to build here!");							
						event.getPlayer().sendMessage("Ask one of the world's managers for build rights:");
						event.getPlayer().sendMessage(StringUtils.join(managers, ", "));
					}
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
