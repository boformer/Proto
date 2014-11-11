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
}