package com.github.boformer.proto.config;

import com.typesafe.config.Config;

/**
 * Main plugin configuration.
 */
public class PluginConfig
{
	public PluginConfig(Config config)
	{
		// TODO Auto-generated constructor stub
	}
	public String databaseUrl;
	public String databaseUser;
	public String databasePassword;
	public String databaseTablePrefix;
}
