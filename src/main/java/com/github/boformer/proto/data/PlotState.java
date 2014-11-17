package com.github.boformer.proto.data;

import java.util.HashMap;
import com.github.boformer.proto.plotcheck.PlotCheckManager;

/**
 * Contains integers representing the state of a plot.
 */
public enum PlotState
{
	/**
	 * The default state of a plot. Plots with this state can be claimed. The plugin ignores any permissions defined for plots with this state.
	 */
	PUBLIC(0), 
	
	/**
	 * The state of a plot when a player claimed it. Plots with this state will be listed in {@link PlotCheckManager#getExpiredPlots()} or {@link PlotCheckManager#getDeletionPlots()} if they are older than a configured amount of days.
	 */
	CLAIMED(1), 
	
	/**
	 * The state of a plot when a player submitted it. Plots with this state will be listed in {@link PlotCheckManager#getSubmittedPlots()}.
	 */
	SUBMITTED(2), 
	
	/**
	 * The state of a staff-reviewed plot. A plot that is marked as finished will never expire.
	 */
	FINISHED(3), 
	
	/**
	 * The state of a deleted plot that has to be regenerated/restored first before it is released to the public. All building actions blocked.
	 */
	LOCKED_FOR_DELETION(4);
	
	private static final HashMap<Integer, PlotState> BY_ID = new HashMap<>();
	
	static {
		for (PlotState state : values()) {
			BY_ID.put(state.id, state);
		}
	}
	
	public static PlotState byId(int id) 
	{
		return BY_ID.get(id);
	}
	
	private final int id;

	
	private PlotState(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}
	
	
}
