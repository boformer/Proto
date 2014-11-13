package com.github.boformer.donut.protection;

import org.apache.logging.log4j.Logger;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;

import com.github.boformer.donut.protection.config.ConfigManager;
import com.github.boformer.donut.protection.data.DataManager;
import com.github.boformer.donut.protection.event.BlockEventHandler;

@Plugin(id = "DonutProtection", name = "DonutProtection", version = "1.0.0")
public class DonutProtectionPlugin
{
	private Logger logger;

	private ConfigManager configManager;
	private DataManager dataManager;


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

	public ConfigManager getConfigManager()
	{
		return configManager;
	}

	public DataManager getDataManager()
	{
		return dataManager;
	}

	public Logger getLogger()
	{
		return logger;
	}

}
