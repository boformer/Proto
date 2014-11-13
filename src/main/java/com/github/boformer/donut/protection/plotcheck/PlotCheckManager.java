package com.github.boformer.donut.protection.plotcheck;

import java.util.List;

import com.github.boformer.donut.protection.DonutProtectionPlugin;
import com.github.boformer.donut.protection.data.PlotID;

public class PlotCheckManager
{
	private final DonutProtectionPlugin plugin;
	
	private List<PlotID> submittedPlots;
	private List<PlotID> expiredPlots;
	
	//TODO call updatePlotLists every x hours and notify staff members
	//TODO
	
	public PlotCheckManager(DonutProtectionPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	public void initialize() 
	{
		updatePlotLists();
	}
	
	public void updatePlotLists() 
	{
		//TODO
	}
	
	
}
