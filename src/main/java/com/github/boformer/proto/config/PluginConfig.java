package com.github.boformer.proto.config;

import ninja.leaping.configurate.ConfigurationNode;

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
	
	public PluginConfig(ConfigurationNode config)
	{
		databaseUrl = config.getNode("database.url").getString();
		databaseUser = config.getNode("database.user").getString();
		databasePassword = config.getNode("database.password").getString();
		databaseTablePrefix = config.getNode("database.table-prefix").getString();
		
		instantPlotRegen = config.getNode("plot-regeneration.instant").getBoolean();
		regenMaxServerUsage = config.getNode("plot-regeneration.max-server-usage").getDouble();
		regenTaskInterval = config.getNode("plot-regeneration.task-interval").getInt();
		
		regenPlotsPerTask = config.getNode("plot-regeneration.plots-per-task").getInt();
		
	}
}
