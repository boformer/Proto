package com.github.boformer.proto.config;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.spongepowered.api.util.config.ConfigFile;

import com.github.boformer.proto.ProtoPlugin;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

/**
 * Provides and manages the plugin and world configuration.
 * 
 * <p>The configuration is stored in the suggested configuration file provided by the server.</p>
 * 
 * <p>The configuration should only be edited by the server admin (e.g. plot size), 
 * while the data managed by the {@link com.github.boformer.proto.data.DataManager} is modified by the plugin at runtime (e.g. table of plots).</p>
 */
public class ConfigManager
{
	private final ProtoPlugin plugin;
	
	private final File pluginConfigFile;
	
	private PluginConfig pluginConfig;
	private HashMap<String, WorldConfig> worldConfigs;
	
	/** 
	 * <i>Internal method: Create a new config manager.</i>
	 * 
	 * @param plugin The plugin
	 * @param pluginConfigFile The recommended config file
	 */
	public ConfigManager(ProtoPlugin plugin, File pluginConfigFile) 
	{
		this.plugin = plugin;
		this.pluginConfigFile = pluginConfigFile;
	}
	
	/** 
	 * <i>Internal method: Initializes the config manager when the server starts up.</i>
	 * 
	 * <p>Copies the default config values into the suggested configuration file on first startup.</p>
	 * 
	 * <p>Also loads the plugin and world configurations into memory for fast access.</p>
	 */
	public void initialize()
	{
		//copy default plugin config
		File fallbackPluginConfigFile = new File(getClass().getClassLoader().getResource("Proto.conf").getFile());
		ConfigFile fallbackPluginConfig = ConfigFile.parseFile(fallbackPluginConfigFile);
		
		ConfigFile config = ConfigFile.parseFile(pluginConfigFile).withFallback(fallbackPluginConfig);
		config.save(true);
		
		pluginConfig = new PluginConfig(config);
		
		for(Map.Entry<String, ConfigValue> entry : config.getConfig("worlds").entrySet()) 
		{
			Config section = config.getConfig("worlds").getConfig(entry.getKey());
			
			WorldConfig worldConfig = new WorldConfig(section);
			
			worldConfigs.put(entry.getKey(), worldConfig);
		}
	}
	


	/**
	 * Gets the main plugin configuration.
	 * 
	 * @return The plugin configuration
	 */
	public PluginConfig getPluginConfig() 
	{
		return pluginConfig;
	}
	
	//TODO use world UUID instead? problem when world changes name?
	/**
	 * Gets the configuration for a world.
	 * 
	 * <p>Returns <code>null</code> if there is no configuration for a world.</p>
	 * 
	 * <p>The plugin ignores worlds without a configuration.</p>
	 * 
	 * @param name The name of the world
	 * @return The world configuration
	 */
	public WorldConfig getWorldConfig(String name) 
	{
		return worldConfigs.get(name);
	}
	
	/**
	 * Gets the list of all worlds with an existing configuration
	 * @return The list of world names
	 */
	public Set<String> getWorldNames() 
	{
		return Collections.unmodifiableSet(worldConfigs.keySet());
	}
}
