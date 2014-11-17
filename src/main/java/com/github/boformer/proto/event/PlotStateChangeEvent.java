package com.github.boformer.proto.event;

import java.util.Collections;
import java.util.Map;

import com.github.boformer.proto.access.PlayerPlotAccess;
import com.github.boformer.proto.data.PlotID;

//TODO javadoc

public class PlotStateChangeEvent extends Event
{
	private final PlotID plotID;
	private final int oldState;
	private final int newState;


	public PlotStateChangeEvent(PlotID plotID, int oldState, int newState)
	{
		this.plotID = plotID;
		this.oldState = oldState;
		this.newState = newState;
	}

	public PlotID getPlotID()
	{
		return plotID;
	}

	public int getOldState()
	{
		return oldState;
	}

	public int getNewState()
	{
		return newState;
	}

	@Override
	public boolean isCancellable()
	{
		return false;
	}
}
