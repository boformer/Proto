package com.github.boformer.proto.config;

import com.github.boformer.proto.plotcheck.PlotAbandonAction;
import com.github.boformer.proto.plotcheck.PlotExpirationAction;
import com.github.boformer.proto.plotcheck.PlotExpirationStartTime;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

/**
 * World configuration.
 */
public class WorldConfig
{
	public static final int DEFAULT_PLOT_SIZE = 32;
	public static final int DEFAULT_EXPIRATION_DAYS = 30;
	public static final PlotExpirationStartTime DEFAULT_EXPIRATION_START_TIME = PlotExpirationStartTime.LAST_MODIFICATION;
	public static final PlotExpirationAction DEFAULT_EXPIRATION_ACTION = PlotExpirationAction.STAFF_REVIEW;
	public static final PlotAbandonAction DEFAULT_PLOT_ABANDON_ACTION = PlotAbandonAction.NONE;
	
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
	
	public PlotAbandonAction plotAbandonAction;
	
	
	public WorldConfig(Config config)
	{

		if(config.hasPath("plot.enabled"))
		{
			plotsEnabled = config.getBoolean("plot.enabled");
		}
		else
		{
			plotsEnabled = false;
		}
		
		if(config.hasPath("plot.size-x") && config.hasPath("plot.size-z")) 
		{
			plotSizeX = config.getInt("plot.size-x");
			plotSizeZ = config.getInt("plot.size-z");
		}
		else
		{
			//TODO warning in console if plotsEnabled == true
			plotSizeX = DEFAULT_PLOT_SIZE;
			plotSizeZ = DEFAULT_PLOT_SIZE;
		}
		
		//TODO warning in console if plotsEnabled == true
		if(plotSizeX == 0 || plotSizeZ == 0) plotsEnabled = false; 
		
		if(config.hasPath("plot.origin-x"))
		{
			plotOriginX = config.getInt("plot.origin-x");
		}
		else
		{
			plotOriginX = 0;
		}
		
		if(config.hasPath("plot.origin-z"))
		{
			plotOriginZ = config.getInt("plot.origin-z");
		}
		else
		{
			plotOriginZ = 0;
		}
		
		//plotcheck
		if(config.hasPath("plot.plotcheck.expiration-days"))
		{
			plotExpirationDays = config.getInt("plot.plotcheck.expiration-days");
		}
		else
		{
			plotExpirationDays = DEFAULT_EXPIRATION_DAYS;
		}
		
		if(config.hasPath("plot.plotcheck.expiration-start-time"))
		{
			plotExpirationStartTime = PlotExpirationStartTime.valueOf(config.getString("plot.plotcheck.expiration-start-time"));
		}
		else
		{
			plotExpirationStartTime = DEFAULT_EXPIRATION_START_TIME;
		}
		
		if(config.hasPath("plot.plotcheck.expiration-action"))
		{
			plotExpirationAction = PlotExpirationAction.valueOf(config.getString("plot.plotcheck.expiration-action"));
		}
		else
		{
			plotExpirationAction = DEFAULT_EXPIRATION_ACTION;
		}
		
		//plot abandon
		if(config.hasPath("plot.abandon-action"))
		{
			plotAbandonAction = PlotAbandonAction.valueOf(config.getString("plot.abandon-action"));
			
			if(plotAbandonAction == null) plotAbandonAction = DEFAULT_PLOT_ABANDON_ACTION;
		}
		else
		{
			plotAbandonAction = DEFAULT_PLOT_ABANDON_ACTION;
		}
	}
	
}
