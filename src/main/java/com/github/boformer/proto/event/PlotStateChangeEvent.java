package com.github.boformer.proto.event;

import java.util.Collections;
import java.util.Map;

import com.github.boformer.proto.access.PlayerPlotAccess;
import com.github.boformer.proto.data.PlotID;
import com.github.boformer.proto.data.PlotState;

//TODO javadoc

public class PlotStateChangeEvent extends Event
{
	private final PlotID plotID;
	private final PlotState oldState;
	private final PlotState newState;


	public PlotStateChangeEvent(PlotID plotID, PlotState oldState, PlotState newState)
	{
		this.plotID = plotID;
		this.oldState = oldState;
		this.newState = newState;
	}

	public PlotID getPlotID()
	{
		return plotID;
	}

	public PlotState getOldState()
	{
		return oldState;
	}

	public PlotState getNewState()
	{
		return newState;
	}

	@Override
	public boolean isCancellable()
	{
		return false;
	}
}
