package com.github.boformer.donut.protection;

import org.spongepowered.api.event.SpongeEventHandler;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;

import com.github.boformer.donut.protection.data.DataStore;

public class DonutProtectionPlugin 
{
	private DataStore dataStore;
	
	@SpongeEventHandler
	public void onInit(PreInitializationEvent event) 
	{
		//TODO init Configuration
		
		//init DataStore
		dataStore = new DataStore();
	}
	
	@SpongeEventHandler
	public void onStop(ServerStoppingEvent event) 
	{
		
	}

	public DataStore getDataStore() {
		return dataStore;
	}
}
