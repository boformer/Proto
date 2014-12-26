package com.github.boformer.proto.config;

import com.typesafe.config.Config;

/**
 * Main plugin configuration.
 */
public class PluginConfig
{
	
	public final String databaseUrl;
	public final String databaseUser;
	public final String databasePassword;
	public final String databaseTablePrefix;
	
	public final boolean instantPlotRegen;
	public final double regenMaxServerUsage;
	public final int regenTaskInterval;
	public final int regenPlotsPerTask;
	
	public PluginConfig(Config config)
	{
		databaseUrl = config.getString("database.url");
		databaseUser = config.getString("database.user");
		databasePassword = config.getString("database.password");
		databaseTablePrefix = config.getString("database.table-prefix");
		
		instantPlotRegen = config.getBoolean("plot-regeneration.instant");
		regenMaxServerUsage = config.getDouble("plot-regeneration.max-server-usage");
		regenTaskInterval = config.getInt("plot-regeneration.task-interval");
		
		regenPlotsPerTask = config.getInt("plot-regeneration.plots-per-task");
		
	}
}
