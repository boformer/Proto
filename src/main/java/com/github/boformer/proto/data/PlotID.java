package com.github.boformer.proto.data;

import java.util.UUID;

/**
 * A plot identifier. Includes the world and the position of the plot in the plot grid.
 */
public class PlotID
{
	private final int x, z;
	private final UUID worldID;


	/**
	 * Gets the x coordinate of the plot in the plot grid.
	 * 
	 * @return The x coordinate of the plot
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Gets the z coordinate of the plot in the plot grid.
	 * 
	 * @return The z coordinate of the plot
	 */
	public int getZ()
	{
		return z;
	}

	/**
	 * The unique identifier of the world where the plot is located.
	 * 
	 * @return The world UUID
	 */
	public UUID getWorldID()
	{
		return worldID;
	}

	/**
	 * Creates a new plot ID.
	 * 
	 * @param x The x coordinate of the plot
	 * @param z The z coordinate of the plot
	 * @param worldID The world UUID
	 */
	public PlotID(int x, int z, UUID worldID)
	{
		this.x = x;
		this.z = z;
		this.worldID = worldID;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((worldID == null) ? 0 : worldID.hashCode());
		result = prime * result + x;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		PlotID other = (PlotID) obj;
		if (worldID == null)
		{
			if (other.worldID != null) return false;
		}
		else if (!worldID.equals(other.worldID)) return false;
		if (x != other.x) return false;
		if (z != other.z) return false;
		return true;
	}
}