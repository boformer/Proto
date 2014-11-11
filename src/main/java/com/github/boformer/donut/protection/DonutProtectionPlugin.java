package com.github.boformer.donut.protection;

import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;

import com.github.boformer.donut.protection.config.ConfigManager;
import com.github.boformer.donut.protection.data.DataManager;
import com.github.boformer.donut.protection.event.BlockEventHandler;

public class DonutProtectionPlugin
{
	private ConfigManager configManager;
	private DataManager dataManager;


	@Subscribe
	public void onInit(PreInitializationEvent event)
	{
		// init ConfigManager
		configManager = new ConfigManager(this);
		configManager.initialize();

		// init DataManager
		dataManager = new DataManager(this);
		dataManager.initialize();

		// register events
		event.getGame().getEventManager().register(new BlockEventHandler(this));
	}

	@Subscribe
	public void onStop(ServerStoppingEvent event)
	{
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

}
