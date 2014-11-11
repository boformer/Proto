package com.github.boformer.donut.protection.data;

import java.util.UUID;

public class WorldData
{
	private final String name;
	private final UUID uniqueID;


	public String getName()
	{
		return name;
	}

	public UUID getUniqueID()
	{
		return uniqueID;
	}

	public WorldData(String name, UUID uniqueID)
	{
		this.name = name;
		this.uniqueID = uniqueID;
	}
}
