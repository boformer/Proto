package com.github.boformer.proto.config;

import ninja.leaping.configurate.ConfigurationNode;

import com.github.boformer.proto.plotcheck.PlotRegeneration;
import com.github.boformer.proto.plotcheck.PlotExpirationAction;
import com.github.boformer.proto.plotcheck.PlotExpirationStartTime;

/**
 * World configuration.
 */
public class WorldConfig
{
	public static final int DEFAULT_PLOT_SIZE = 32;
	public static final int DEFAULT_EXPIRATION_DAYS = 30;
	public static final PlotExpirationStartTime DEFAULT_EXPIRATION_START_TIME = PlotExpirationStartTime.LAST_MODIFICATION;
	public static final PlotExpirationAction DEFAULT_EXPIRATION_ACTION = PlotExpirationAction.STAFF_REVIEW;
	public static final PlotRegeneration DEFAULT_PLOT_REGENERATION = PlotRegeneration.NONE;
	
	public boolean plotsEnabled;
	
	public int plotSizeX;
	public int plotSizeZ;
	
	public int plotOriginX;
	public int plotOriginZ;
	
	public int plotExpirationDays;
	
	//TODO replace with enums?
	//TODO verify these string values
	/*
	plugin.getLogger().error("Invalid plot configuration for world '" + worldName + "':");
	plugin.getLogger().error("The property plotcheck.expiration-action can only be STAFF_REVIEW or AUTO_DELETE ;)");
	 */
	public PlotExpirationStartTime plotExpirationStartTime;
	public PlotExpirationAction plotExpirationAction;
	
	public PlotRegeneration plotRegeneration;
	
	
	public WorldConfig(ConfigurationNode config)
	{

		if(!config.getNode("plot.enabled").isVirtual())
		{
			plotsEnabled = config.getNode("plot.enabled").getBoolean();
		}
		else
		{
			plotsEnabled = false;
		}
		
		if(!config.getNode("plot.size-x").isVirtual() && config.getNode("plot.size-z").isVirtual()) 
		{
			plotSizeX = config.getNode("plot.size-x").getInt();
			plotSizeZ = config.getNode("plot.size-z").getInt();
		}
		else
		{
			//TODO warning in console if plotsEnabled == true
			plotSizeX = DEFAULT_PLOT_SIZE;
			plotSizeZ = DEFAULT_PLOT_SIZE;
		}
		
		//TODO warning in console if plotsEnabled == true
		if(plotSizeX == 0 || plotSizeZ == 0) plotsEnabled = false; 
		
		if(!config.getNode("plot.origin-x").isVirtual())
		{
			plotOriginX = config.getNode("plot.origin-x").getInt();
		}
		else
		{
			plotOriginX = 0;
		}
		
		if(!config.getNode("plot.origin-z").isVirtual())
		{
			plotOriginZ = config.getNode("plot.origin-z").getInt();
		}
		else
		{
			plotOriginZ = 0;
		}
		
		//plotcheck
		if(!config.getNode("plot.plotcheck.expiration-days").isVirtual())
		{
			plotExpirationDays = config.getNode("plot.plotcheck.expiration-days").getInt();
		}
		else
		{
			plotExpirationDays = DEFAULT_EXPIRATION_DAYS;
		}
		
		if(!config.getNode("plot.plotcheck.expiration-start-time").isVirtual())
		{
			plotExpirationStartTime = PlotExpirationStartTime.valueOf(config.getString("plot.plotcheck.expiration-start-time"));
		}
		else
		{
			plotExpirationStartTime = DEFAULT_EXPIRATION_START_TIME;
		}
		
		if(!config.getNode("plot.plotcheck.expiration-action").isVirtual())
		{
			plotExpirationAction = PlotExpirationAction.valueOf(config.getString("plot.plotcheck.expiration-action"));
		}
		else
		{
			plotExpirationAction = DEFAULT_EXPIRATION_ACTION;
		}
		
		//plot abandon
		if(!config.getNode("plot.abandon-action").isVirtual())
		{
			plotRegeneration = PlotRegeneration.valueOf(config.getString("plot.regeneration"));
			
			if(plotRegeneration == null) plotRegeneration = DEFAULT_PLOT_REGENERATION;
		}
		else
		{
			plotRegeneration = DEFAULT_PLOT_REGENERATION;
		}
	}
	
}
