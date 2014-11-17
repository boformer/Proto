package com.github.boformer.proto.event;

import java.util.Collections;
import java.util.Map;

import com.github.boformer.proto.access.PlayerPlotAccess;

//TODO javadoc

public class PlotAccessChangeEvent extends Event
{
	private final Map<PlayerPlotAccess, Boolean> accessChangeMap;


	public PlotAccessChangeEvent(Map<PlayerPlotAccess, Boolean> accessChangeMap)
	{
		this.accessChangeMap = Collections.unmodifiableMap(accessChangeMap);
	}

	public Map<PlayerPlotAccess, Boolean> getAccessChangeMap()
	{
		return accessChangeMap;
	}

	@Override
	public boolean isCancellable()
	{
		return false;
	}
}
