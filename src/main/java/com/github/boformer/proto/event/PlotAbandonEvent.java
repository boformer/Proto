package com.github.boformer.proto.event;

import com.github.boformer.proto.data.PlotID;

public class PlotAbandonEvent extends Event
{
	private final PlotID plotID;
	
	public PlotAbandonEvent(PlotID plotID)
	{
		this.plotID = plotID;
	}

	public PlotID getPlotID()
	{
		return plotID;
	}

	@Override
	public boolean isCancellable()
	{
		return false;
	}
}
