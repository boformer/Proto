package com.github.boformer.donut.protection.data;

import java.util.UUID;

public class PlayerData
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

	public PlayerData(String name, UUID uniqueID)
	{
		this.name = name;
		this.uniqueID = uniqueID;
	}
}
