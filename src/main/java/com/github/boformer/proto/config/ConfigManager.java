package com.github.boformer.proto.config;

import java.util.List;
import java.util.UUID;

import com.github.boformer.proto.ProtoPlugin;
import com.github.boformer.proto.data.WorldData;

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
	
	/** 
	 * <i>Internal method: Create a new config manager.</i>
	 * 
	 * @param plugin The plugin
	 */
	public ConfigManager(ProtoPlugin plugin)
	{
		this.plugin = plugin;
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
		//TODO copy default plugin config
	}
	
	/**
	 * Gets the main plugin configuration.
	 * 
	 * @return The plugin configuration
	 */
	public PluginConfig getPluginConfig() 
	{
		//TODO
		return null;
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
		//TODO
		return null;
	}
	

	/**
	 * Gets the configuration for a world.
	 * 
	 * <p>Returns <code>null</code> if there is no configuration for a world.</p>
	 * 
	 * <p>The plugin ignores worlds without a configuration.</p>
	 * 
	 * @param worldID The world UUID
	 * @return The world configuration
	 */
	public WorldConfig getWorldConfig(UUID worldID) 
	{
		WorldData worldData;
		try
		{
			worldData = plugin.getDataManager().getWorldData(worldID);
		}
		catch (Exception e)
		{
			// TODO error message
			e.printStackTrace();
			
			return null; //TODO on exception return special safe world-cfg (block...)
		}
		
		if(worldData == null) return null;
		
		return getWorldConfig(worldData.getName());
	}
	
	/**
	 * Gets the list of all worlds with an existing configuration
	 * @return The list of world names
	 */
	public List<String> getWorldNames() 
	{
		//TODO
		return null;
	}
}
