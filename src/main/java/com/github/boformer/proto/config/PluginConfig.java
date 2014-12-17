package com.github.boformer.proto.config;

import com.typesafe.config.Config;

/**
 * Main plugin configuration.
 */
public class PluginConfig
{
	public PluginConfig(Config config)
	{
		databaseUrl = config.getString("database.url");
		databaseUser = config.getString("database.user");
		databasePassword = config.getString("database.password");
		databaseTablePrefix = config.getString("database.table-prefix");
	}
	public final String databaseUrl;
	public final String databaseUser;
	public final String databasePassword;
	public final String databaseTablePrefix;
}
