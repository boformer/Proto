package com.github.boformer.proto.event;

import java.util.Collections;
import java.util.Map;

import org.spongepowered.api.Game;
import org.spongepowered.api.util.event.callback.CallbackList;

import com.github.boformer.proto.access.PlayerPlotAccess;

//TODO javadoc

public class PlotAccessChangeEvent extends Event
{
	private final Map<PlayerPlotAccess, Boolean> accessChangeMap;


	public PlotAccessChangeEvent(Game game, Map<PlayerPlotAccess, Boolean> accessChangeMap)
	{
		super(game);
		this.accessChangeMap = Collections.unmodifiableMap(accessChangeMap);
	}

	public Map<PlayerPlotAccess, Boolean> getAccessChangeMap()
	{
		return accessChangeMap;
	}
	
	@Override
	public CallbackList getCallbacks() 
	{
		// TODO wait for sponge implementation to see how this works
		return null;
	}
}
