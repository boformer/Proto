package com.github.boformer.donut.protection.data;

public class PlotData
{
	private final PlotID plotID;
	private String name;
	private int state;

	public int getState()
	{
		return state;
	}

	public void setState(int state)
	{
		this.state = state;
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
