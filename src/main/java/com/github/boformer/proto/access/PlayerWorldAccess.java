package com.github.boformer.proto.access;

import java.util.UUID;

/**
 * Represents a single permission of a player in a world.
 * 
 * <p><i>Usually this class is only used internally.</i></p>
 */
public class PlayerWorldAccess
{
	private final UUID playerID;
	private final UUID worldID;
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
	 * Gets the unique identifier of the world.
	 * 
	 * @return The world UUID
	 */
	public UUID getWorldID()
	{
		return worldID;
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
	 * @param worldID The world UUID
	 * @param permission The permission
	 */
	public PlayerWorldAccess(UUID playerID, UUID worldID, String permission)
	{
		this.playerID = playerID;
		this.worldID = worldID;
		this.permission = permission;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((permission == null) ? 0 : permission.hashCode());
		result = prime * result + ((playerID == null) ? 0 : playerID.hashCode());
		result = prime * result + ((worldID == null) ? 0 : worldID.hashCode());
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
		if (worldID == null)
		{
			if (other.worldID != null) return false;
		}
		else if (!worldID.equals(other.worldID)) return false;
		return true;
	}

}
