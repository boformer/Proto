package com.github.boformer.donut.protection.data;

import java.util.UUID;

/**
 * Data of a player.
 */
public class PlayerData
{
	private String name;
	private final UUID uniqueID;


	/**
	 * Gets the last known user name of the player (when the player last logged in). A player might be able to change this name, but not the unique ID (UUID).
	 * 
	 * @return The player name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * <i>Internal Method: Changes the last known name of the player (on name change).</i>
	 * 
	 * @param name The new player name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the player unique identifier (UUID). This ID is used to identify the player and never changes.
	 * 
	 * @return The player UUID
	 */
	public UUID getUniqueID()
	{
		return uniqueID;
	}

	/**
	 * <i>Internal Constructor: Creates a new player data instance.</i>
	 * 
	 * @param name The player name
	 * @param uniqueID The player UUID
	 */
	public PlayerData(String name, UUID uniqueID)
	{
		this.name = name;
		this.uniqueID = uniqueID;
	}
}
