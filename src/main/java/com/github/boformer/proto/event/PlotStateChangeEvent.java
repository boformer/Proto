package com.github.boformer.proto.event;

import org.spongepowered.api.Game;
import org.spongepowered.api.util.event.callback.CallbackList;

import com.github.boformer.proto.data.PlotID;
import com.github.boformer.proto.data.PlotState;

//TODO javadoc

public class PlotStateChangeEvent extends Event
{
	private final PlotID plotID;
	private final PlotState oldState;
	private final PlotState newState;


	public PlotStateChangeEvent(Game game, PlotID plotID, PlotState oldState, PlotState newState)
	{
		super(game);
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
	public CallbackList getCallbacks() 
	{
		// TODO wait for sponge implementation to see how this works
		return null;
	}
}
