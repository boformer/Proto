package com.github.boformer.proto.event;

import org.spongepowered.api.Game;
import org.spongepowered.api.util.event.callback.CallbackList;

import com.github.boformer.proto.data.PlotID;

public class PlotAbandonEvent extends Event
{
	private final PlotID plotID;
	
	public PlotAbandonEvent(Game game, PlotID plotID)
	{
		super(game);
		this.plotID = plotID;
	}

	public PlotID getPlotID()
	{
		return plotID;
	}
	
	@Override
	public CallbackList getCallbacks() 
	{
		// TODO wait for sponge implementation to see how this works
		return null;
	}
}
