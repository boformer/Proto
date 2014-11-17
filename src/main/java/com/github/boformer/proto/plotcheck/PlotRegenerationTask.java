package com.github.boformer.proto.plotcheck;

public class PlotRegenerationTask implements Runnable 
{
	private final PlotCheckManager plotCheckManager;
	
	
	public PlotRegenerationTask(PlotCheckManager plotCheckManager) 
	{
		this.plotCheckManager = plotCheckManager;
	}

	@Override
	public void run() 
	{
		if(plotCheckManager.getDeletionPlots().isEmpty()) return; //no plots marked for deletion
		
		//TODO check if server is idling
		
		//TODO delete a number of plots, maybe warn players with countdown (lag...)
	}

}
