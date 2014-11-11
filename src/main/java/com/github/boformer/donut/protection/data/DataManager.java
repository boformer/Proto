package com.github.boformer.donut.protection.data;

import java.util.UUID;

import com.github.boformer.donut.protection.DonutProtectionPlugin;

public class DataManager
{
	private final DonutProtectionPlugin plugin;


	public DataManager(DonutProtectionPlugin plugin)
	{
		this.plugin = plugin;
	}

	public void initialize()
	{
		// TODO Auto-generated method stub
	}

	public WorldData getWorldData(UUID worldID)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public PlayerData getPlayerData(UUID playerID)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public PlotData getPlotData(PlotID plotID)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public GroupData getGroupData(int groupID)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean hasWorldPermission(UUID playerID, UUID worldID, String permissionString)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasPlotPermission(UUID playerID, PlotID plotID, String permissionString)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
