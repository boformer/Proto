package com.github.boformer.donut.protection.data;

import java.util.Date;

public class PlotData
{
	private final PlotID plotID;

	private String name;
	private int state;

	private Date creationDate;
	private Date lastModificationDate;


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

	public Date getCreationDate()
	{
		return creationDate;
	}

	public void setCreationDate(Date creationDate)
	{
		this.creationDate = creationDate;
	}

	public Date getLastModificationDate()
	{
		return lastModificationDate;
	}

	public void setLastModificationDate(Date lastModificationDate)
	{
		this.lastModificationDate = lastModificationDate;
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
