package com.github.boformer.donut.protection.config;

import com.github.boformer.donut.protection.DonutProtectionPlugin;

public class ConfigManager
{
	private final DonutProtectionPlugin plugin;
	
	public ConfigManager(DonutProtectionPlugin plugin)
	{
		this.plugin = plugin;
	}

	public void initialize()
	{
		//TODO copy default plugin config
	}
	
	public PluginConfig getPluginConfig() 
	{
		//TODO
		return null;
	}
	
	public WorldConfig getWorldConfig(org.spongepowered.api.world.World world) 
	{
		//TODO
		return null;
	}


}
