package com.github.boformer.donut.protection.worldedit;

import org.spongepowered.api.entity.Player;
import org.spongepowered.api.world.World;

import com.github.boformer.donut.protection.DonutProtectionPlugin;
import com.github.boformer.donut.protection.config.WorldConfig;
import com.github.boformer.donut.protection.data.PlotID;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.CuboidRegion;

import dummy.sponge.SpongeDummy;
import dummy.worldedit.WorldEditDummy;

public class WorldEditConnector
{
	private final DonutProtectionPlugin plugin;
	private final WorldEdit worldEdit;


	public WorldEditConnector(DonutProtectionPlugin plugin, WorldEdit worldEdit)
	{
		this.plugin = plugin;
		this.worldEdit = worldEdit;
	}
	
	public void initialize() 
	{
		//TODO listen for events
		//TODO create masks for all logged in players
	}
	
	private void createMask(Player player) 
	{
		World world = SpongeDummy.getPlayerWorld(player);
		
		//TODO
	}
	
	public void clearMask(Player player) 
	{
		//TODO
	}
	
	private static CuboidRegion getRegionForPlot(PlotID plotID, World world, WorldConfig worldConfig)
	{
		//TODO get LocalWorld from plotID.worldID
		LocalWorld localWorld = WorldEditDummy.getLocalWorld(world); // later change to 'SpongeUtil'?
		
		int lesserBoundaryX = plotID.getX() * worldConfig.plotSizeX + worldConfig.plotOriginX;
		int lesserBoundaryZ = plotID.getZ() * worldConfig.plotSizeZ + worldConfig.plotOriginZ;
		
		int greaterBoundaryX = (plotID.getX() + 1) * worldConfig.plotSizeX - 1 + worldConfig.plotOriginX;
		int greaterBoundaryZ = (plotID.getZ() + 1) * worldConfig.plotSizeZ - 1 + worldConfig.plotOriginZ;
		
		Vector pos1 = new Vector(lesserBoundaryX, 0, lesserBoundaryZ);
		Vector pos2 = new Vector(greaterBoundaryX, SpongeDummy.getWorldMaxHeight(world), greaterBoundaryZ);

		return new CuboidRegion(localWorld, pos1, pos2);
	}
}
