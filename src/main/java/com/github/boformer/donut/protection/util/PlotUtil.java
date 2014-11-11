package com.github.boformer.donut.protection.util;

import org.spongepowered.api.math.Vector3d;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.github.boformer.donut.protection.config.WorldConfig;
import com.github.boformer.donut.protection.data.PlotID;

public class PlotUtil
{
	public static final PlotID calculatePlotID(Vector3d position, World world, WorldConfig worldConfig)
	{
		double oX = position.getX() - worldConfig.plotOriginX;
		double oZ = position.getZ() - worldConfig.plotOriginZ;
		
		int plotX = (int) Math.floor(oX / worldConfig.plotSizeX);
		int plotZ = (int) Math.floor(oZ / worldConfig.plotSizeZ);
		
		return new PlotID(plotX, plotZ, world.getUniqueID());
	}
}
