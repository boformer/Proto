package com.github.boformer.donut.protection.data;

import java.util.UUID;

public class PlotID
{
	private final int x, z;
	private final UUID worldID;


	public int getX()
	{
		return x;
	}

	public int getZ()
	{
		return z;
	}

	public UUID getWorldID()
	{
		return worldID;
	}

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