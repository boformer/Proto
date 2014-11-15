package com.github.boformer.proto.data;

import java.util.Date;

/**
 * Data of a plot.
 */
public class PlotData
{
	private final PlotID plotID;

	private String name;
	private int state;

	private Date creationDate;
	private Date lastModificationDate;


	/**
	 * Gets the {@link PlotState} of the plot.
	 * 
	 * @return The plot state
	 */
	public int getState()
	{
		return state;
	}

	/**
	 * Sets the plot state.
	 * 
	 * @param state The plot state
	 */
	public void setState(int state)
	{
		this.state = state;
	}

	/**
	 * Gets the name of the plot. Players can name their plot for faster teleportation.
	 * 
	 * @return The plot name or <code>null</code> if the plot is not named.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Renames the plot.
	 * 
	 * @param name The new plot name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the date when the plot was created/claimed.
	 * 
	 * @return The plot creation date
	 */
	public Date getCreationDate()
	{
		return creationDate;
	}

	/**
	 * <i>Internal method: Sets the plot creation date.</i>
	 * 
	 * @param creationDate The plot creation date
	 */
	public void setCreationDate(Date creationDate)
	{
		this.creationDate = creationDate;
	}

	/**
	 * Gets the date when the plot was last modified (e.g. block changes)
	 * 
	 * @return The plot last modification date
	 */
	public Date getLastModificationDate()
	{
		return lastModificationDate;
	}

	/**
	 * Sets the date when the plot was last modified.
	 * 
	 * @param lastModificationDate The plot last modification date
	 */
	public void setLastModificationDate(Date lastModificationDate)
	{
		this.lastModificationDate = lastModificationDate;
	}

	/**
	 * Gets the ID of this plot. The ID contains the world UUID, and the x and z grid coordinate.
	 * 
	 * @return The plot ID
	 */
	public PlotID getPlotID()
	{
		return plotID;
	}

	/**
	 * <i>Internal Constructor: Creates a new plot data instance.</i>
	 * 
	 * @param plotID The plot ID
	 */
	public PlotData(PlotID plotID)
	{
		this.plotID = plotID;
	}
}
