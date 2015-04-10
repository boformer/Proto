package com.github.boformer.proto;

import java.io.File;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.config.DefaultConfig;

import com.github.boformer.proto.config.ConfigManager;
import com.github.boformer.proto.data.DataManager;
import com.github.boformer.proto.event.PlayerEventHandler;
import com.github.boformer.proto.worldedit.WorldEditConnector;
import com.google.inject.Inject;
import com.sk89q.worldedit.WorldEdit;

/**
 * The Plugin for Sponge. 
 * 
 * <p>This class listens for game state changes and provides access to data and config managers.</p>
 */
@Plugin(id = "Proto", name = "Proto", version = "1.0.0-SNAPSHOT")
public class ProtoPlugin
{
	//TODO change some constructors to protected when possible, or use interfaces?
	//TODO call events
	//TODO create data for loaded worlds in config on startup
	//TODO worldgen plot regeneration
	
    @Inject
	private Game game;
	
	@Inject
	private Logger logger;
	
	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	
    @Inject
    @DefaultConfig(sharedRoot = false)
    private File configFile;

	private ConfigManager configManager;
	private DataManager dataManager;
	
	private WorldEditConnector worldEditConnector;
	
	/**
	 * Called when server starts up.
	 * 
	 * @param event The server initialization event
	 */
	@Subscribe
	public void onInit(PreInitializationEvent event)
	{
		// init ConfigManager
		configManager = new ConfigManager(this, configFile, configLoader);
		configManager.initialize();

		// init DataManager
		dataManager = new DataManager(this);
		
		try
		{
			dataManager.initialize();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}

		// register events
		game.getEventManager().register(this, new PlayerEventHandler(this));
		
		PluginContainer worldEditPluginContainer = game.getPluginManager().getPlugin("WorldEdit").orNull();
		
		// is WorldEdit installed?
		if(worldEditPluginContainer != null)
		{
			//TODO See how to get WorldEdit in Sponge...
			WorldEdit worldEdit = (/*Placeholder Cast!*/ WorldEdit) worldEditPluginContainer.getInstance();
			
			worldEditConnector = new WorldEditConnector(this, game, worldEdit);
			worldEditConnector.initialize();
		}
	}

	/**
	 * Called when server shuts down.
	 * 
	 * @param event The server stop event
	 */
	@Subscribe
	public void onStop(ServerStoppingEvent event)
	{
		try
		{
			dataManager.stop();
		}
		catch (Exception e)
		{
			logger.error("Error while shutting down data services. Some values may not have been saved. Details: " + e.getMessage());
			e.printStackTrace();
		}

		// TODO server stop
	}

	/**
	 * Gets the {@link ConfigManager}.
	 * 
	 * @return The config manager
	 */
	public ConfigManager getConfigManager()
	{
		return configManager;
	}
	
	/**
	 * Gets the {@link DataManager}.
	 * 
	 * @return The data manager
	 */
	public DataManager getDataManager()
	{
		return dataManager;
	}
	
	
	public WorldEditConnector getWorldEditConnector()
	{
		return worldEditConnector;
	}

	/**
	 * Gets the Logger for this plugin.
	 * 
	 * @return The plugin logger
	 */
	public Logger getLogger()
	{
		return logger;
	}

}
