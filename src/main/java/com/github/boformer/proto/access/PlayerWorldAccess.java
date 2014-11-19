package com.github.boformer.proto.access;

import java.util.UUID;

/**
 * Represents a single permission of a player in a world.
 */
public class PlayerWorldAccess
{
	private final UUID playerID;
	private final String worldName;
	private final String permission;


	/**
	 * Gets the unique identifier of the player.
	 * 
	 * @return The player UUID
	 */
	public UUID getPlayerID()
	{
		return playerID;
	}

	/**
	 * Gets the name of the world.
	 * 
	 * @return The world name
	 */
	public String getWorldName() 
	{
		return worldName;
	}

	/**
	 * Gets the permission string.
	 * 
	 * @return The permission
	 */
	public String getPermission()
	{
		return permission;
	}

	/**
	 * Creates a new permission node.
	 * 
	 * @param playerID The player UUID
	 * @param worldName The world name
	 * @param permission The permission
	 */
	public PlayerWorldAccess(UUID playerID, String worldName, String permission)
	{
		this.playerID = playerID;
		this.worldName = worldName;
		this.permission = permission;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((permission == null) ? 0 : permission.hashCode());
		result = prime * result + ((playerID == null) ? 0 : playerID.hashCode());
		result = prime * result + ((worldName == null) ? 0 : worldName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		PlayerWorldAccess other = (PlayerWorldAccess) obj;
		if (permission == null)
		{
			if (other.permission != null) return false;
		}
		else if (!permission.equals(other.permission)) return false;
		if (playerID == null)
		{
			if (other.playerID != null) return false;
		}
		else if (!playerID.equals(other.playerID)) return false;
		if (worldName == null)
		{
			if (other.worldName != null) return false;
		}
		else if (!worldName.equals(other.worldName)) return false;
		return true;
	}

}
