package com.github.boformer.proto.data;

import java.util.UUID;

/**
 * Data of a world.
 */
public class WorldData
{
	//TODO drop this class or extend it?
	
	private final String name;

	/**
	 * Gets the world name.
	 * 
	 * @return The world name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * <i>Internal Constructor: Creates a new world data instance.</i>
	 * 
	 * @param name The world name
	 */
	public WorldData(String name)
	{
		this.name = name;
	}
}
