package com.github.boformer.donut.protection.data;

import com.github.boformer.donut.protection.plotcheck.PlotCheckManager;

/**
 * Contains integers representing the state of a plot.
 */
public class PlotState
{
	//TODO change to enum?
	
	/**
	 * The default state of a plot. Plots with this state can be claimed. The plugin ignores any permissions defined for plots with this state.
	 */
	public final static int PUBLIC = 0;
	
	/**
	 * The state of a plot when a player claimed it. Plots with this state will be listed in {@link PlotCheckManager#getExpiredPlots()} if they are older than a configured amount of days.
	 */
	public final static int CLAIMED = 1;
	
	/**
	 * The state of a plot when a player submitted it. Plots with this state will be listed in {@link PlotCheckManager#getSubmittedPlots()}.
	 */
	public final static int SUBMITTED = 2;
	
	/**
	 * The state of a staff-reviewed plot. A plot that is marked as finished will never expire.
	 */
	public final static int FINISHED = 3;
}
