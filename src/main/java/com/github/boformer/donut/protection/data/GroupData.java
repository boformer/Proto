package com.github.boformer.donut.protection.data;

public class GroupData
{
	private final int id;
	private String name;


	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public GroupData(int id)
	{
		this.id = id;
	}
}
