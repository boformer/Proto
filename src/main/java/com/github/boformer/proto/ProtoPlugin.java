package com.github.boformer.proto;

import org.apache.logging.log4j.Logger;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;

import com.github.boformer.proto.config.ConfigManager;
import com.github.boformer.proto.data.DataManager;
import com.github.boformer.proto.event.BlockEventHandler;

/**
 * The Plugin for Sponge. 
 * 
 * <p>This class listens for game state changes and provides access to data and config managers.</p>
 */
@Plugin(id = "Proto", name = "Proto", version = "1.0.0")
public class ProtoPlugin
{
	//TODO change some constructors to protected when possible, or use interfaces?
	//TODO call events
	//TODO create data for loaded worlds in config on startup
	//TODO worldgen plot regeneration
	
	private Logger logger;

	private ConfigManager configManager;
	private DataManager dataManager;


	/**
	 * Called when server starts up.
	 * 
	 * @param event The server initialization event
	 */
	@Subscribe
	public void onInit(PreInitializationEvent event)
	{
		logger = event.getPluginLog();

		// init ConfigManager
		configManager = new ConfigManager(this);
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
		event.getGame().getEventManager().register(new BlockEventHandler(this));
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

		// TODO
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
