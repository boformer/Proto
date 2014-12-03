package com.github.boformer.proto.util;

import org.spongepowered.api.math.Vector3d;
import com.github.boformer.proto.config.ConfigManager;
import com.github.boformer.proto.config.WorldConfig;
import com.github.boformer.proto.data.PlotID;

/**
 * A static utility class to work with plots.
 */
public class PlotUtil
{
	/**
	 * Calculates the plot ID from a position in a world and a defined world config. 
	 * 
	 * <p>It considers the configured size and the origin of the plot grid.</p>
	 * 
	 * @param position The position (e.g. of a player) in a world
	 * @param world The world
	 * @param worldConfig The configuration that defines the plot grid. Can be obtained using {@link ConfigManager#getWorldConfig(String)}.
	 * @return The calculated plot ID
	 */
	public static final PlotID calculatePlotID(Vector3d position, String worldName, WorldConfig worldConfig)
	{
		double oX = position.getX() - worldConfig.plotOriginX;
		double oZ = position.getZ() - worldConfig.plotOriginZ;
		
		int plotX = (int) Math.floor(oX / worldConfig.plotSizeX);
		int plotZ = (int) Math.floor(oZ / worldConfig.plotSizeZ);
		
		return new PlotID(plotX, plotZ, worldName);
	}
}
