package com.github.boformer.proto.plotcheck;

import java.util.Random;

import org.spongepowered.api.Game;
import org.spongepowered.api.text.message.Message;
import org.spongepowered.api.text.message.Messages;
import org.spongepowered.api.world.World;

import com.github.boformer.proto.ProtoPlugin;
import com.github.boformer.proto.config.PluginConfig;
import com.github.boformer.proto.config.WorldConfig;
import com.github.boformer.proto.data.PlotID;

public class PlotRegenerationTask implements Runnable 
{
	private final ProtoPlugin plugin;
	private final Game game;
	private final PlotCheckManager plotCheckManager;
	
	private final Random random;
		
	public PlotRegenerationTask(ProtoPlugin plugin, Game game, PlotCheckManager plotCheckManager) 
	{
		this.plugin = plugin;
		this.game = game;
		this.plotCheckManager = plotCheckManager;
		
		random = new Random();
	}

	@Override
	public void run() 
	{
		if(plotCheckManager.getDeletionPlots().isEmpty()) return; //no plots marked for deletion
		
		
		PluginConfig config = plugin.getConfigManager().getPluginConfig();
		
		
		//check if server is idling
		double serverUsage = ((double) game.getOnlinePlayers().size()) / game.getMaxPlayers();
		
		if(serverUsage > config.regenMaxServerUsage) return;
		
		
		boolean warnPlayers = true;
		
		//delete a number of plots
		for(int i = 0; i < Math.min(config.regenPlotsPerTask, plotCheckManager.getDeletionPlots().size()); i++) 
		{
			int p = random.nextInt(plotCheckManager.getDeletionPlots().size());
			
			PlotID plotID = plotCheckManager.getDeletionPlots().get(p);
			
			//TODO load world if unloaded?
			World world = game.getWorld(plotID.getWorldName());
			
			if(world != null) continue; //world not loaded. skip
			
			WorldConfig worldConfig = plugin.getConfigManager().getWorldConfig(plotID.getWorldName());
			
			if(worldConfig == null || worldConfig.plotsEnabled == false || worldConfig.plotRegeneration == PlotRegeneration.NONE) 
			{
				//no plots or regeneration configured
				plugin.getDataManager().removePlotData(plotID);
				continue;
			}
			
			if(warnPlayers) 
			{
				//warn players
				game.broadcastMessage(Messages.of("Regenerating abandoned plots. Prepare for lag..."));
				
				warnPlayers = false; //only once...
			}
			
			if(worldConfig.plotRegeneration == PlotRegeneration.WORLD_GENERATOR) 
			{
				//TODO world generator
			}
			else if(worldConfig.plotRegeneration == PlotRegeneration.WORLDEDIT_SNAPSHOT)
			{
				if(plugin.getWorldEditConnector() == null) 
				{
					plugin.getLogger().warn("WorldEdit not installed! Can not restore landscape in world " + world.getName());
					continue;
				}
				
				//restore the plot. WorldEdit magic!
				plugin.getWorldEditConnector().restorePlotFromSnapshot(plotID);
			}
			else
			{
				plugin.getLogger().warn("Unknown plot regeneration action specified: " + worldConfig.plotRegeneration.name());
				continue;
			}
			
			//remove plot from database
			plotCheckManager.getDeletionPlots().remove(p);
		}
	}
}
