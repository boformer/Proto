package com.github.boformer.proto.data;

import java.util.UUID;

/**
 * Data of a world.
 */
public class WorldData
{
	private final String name;
	private final UUID uniqueID;

	/**
	 * Gets the last known world name. This name might change. To identify a world, use {@link #getUniqueID()}.
	 * 
	 * @return The world name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the world unique identifier (UUID). This ID is used to identify the player and never changes.
	 * 
	 * @return The world UUID
	 */
	public UUID getUniqueID()
	{
		return uniqueID;
	}

	/**
	 * <i>Internal Constructor: Creates a new world data instance.</i>
	 * 
	 * @param name The world name
	 * @param uniqueID The world UUID
	 */
	public WorldData(String name, UUID uniqueID)
	{
		this.name = name;
		this.uniqueID = uniqueID;
	}
}
