package com.github.boformer.proto.data;

/**
 * A plot identifier. Includes the world and the position of the plot in the plot grid.
 */
public class PlotID
{
	private final int x, z;
	private final String worldName;


	/**
	 * Gets the x coordinate of the plot in the plot grid.
	 * 
	 * @return The x coordinate of the plot
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Gets the z coordinate of the plot in the plot grid.
	 * 
	 * @return The z coordinate of the plot
	 */
	public int getZ()
	{
		return z;
	}

	/**
	 * The name of the world where the plot is located.
	 * 
	 * @return The world name
	 */
	public String getWorldName()
	{
		return worldName;
	}

	/**
	 * Creates a new plot ID.
	 * 
	 * @param x The x coordinate of the plot
	 * @param z The z coordinate of the plot
	 * @param worldName The world name
	 */
	public PlotID(int x, int z,String worldName)
	{
		this.x = x;
		this.z = z;
		this.worldName = worldName;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((worldName == null) ? 0 : worldName.hashCode());
		result = prime * result + x;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		PlotID other = (PlotID) obj;
		if (worldName == null)
		{
			if (other.worldName != null) return false;
		}
		else if (!worldName.equals(other.worldName)) return false;
		if (x != other.x) return false;
		if (z != other.z) return false;
		return true;
	}
}