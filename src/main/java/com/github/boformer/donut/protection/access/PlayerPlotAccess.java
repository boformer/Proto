package com.github.boformer.donut.protection.access;

import java.util.UUID;

import com.github.boformer.donut.protection.data.PlotID;

public class PlayerPlotAccess
{
	private final UUID playerID;
	private final PlotID plotID;
	private final String permission;


	public UUID getPlayerID()
	{
		return playerID;
	}

	public PlotID getPlotID()
	{
		return plotID;
	}

	public String getPermission()
	{
		return permission;
	}

	public PlayerPlotAccess(UUID playerID, PlotID plotID, String permission)
	{
		this.playerID = playerID;
		this.plotID = plotID;
		this.permission = permission;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((permission == null) ? 0 : permission.hashCode());
		result = prime * result + ((playerID == null) ? 0 : playerID.hashCode());
		result = prime * result + ((plotID == null) ? 0 : plotID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		PlayerPlotAccess other = (PlayerPlotAccess) obj;
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
		if (plotID == null)
		{
			if (other.plotID != null) return false;
		}
		else if (!plotID.equals(other.plotID)) return false;
		return true;
	}
}
