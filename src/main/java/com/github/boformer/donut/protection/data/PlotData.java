package com.github.boformer.donut.protection.data;

import java.util.UUID;

public class PlotData
{
	private final PlotID plotID;
	private String name;
	private int status;

	public int getStatus()
	{
		return status;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public PlotID getPlotID()
	{
		return plotID;
	}

	public PlotData(PlotID plotID)
	{
		this.plotID = plotID;
	}
}
