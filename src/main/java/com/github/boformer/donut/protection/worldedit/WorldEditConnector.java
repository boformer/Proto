package com.github.boformer.donut.protection.worldedit;

import java.util.Set;

import org.spongepowered.api.entity.Player;
import org.spongepowered.api.world.World;

import com.github.boformer.donut.protection.DonutProtectionPlugin;
import com.github.boformer.donut.protection.config.WorldConfig;
import com.github.boformer.donut.protection.data.PlotID;
import com.github.boformer.donut.protection.data.WorldData;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.masks.Mask;
import com.sk89q.worldedit.masks.RegionMask;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

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
	
	//TODO add methods for snapshot restore
	//TODO subscribe to events
	
	private void createMask(Player player) 
	{
		World world = SpongeDummy.getPlayerWorld(player);
		
		WorldConfig worldConfig = plugin.getConfigManager().getWorldConfig(world);
		
		//TODO player bypassing?
		if(worldConfig == null)
		{
			//no configuration --> not our deal. remove mask
			clearMask(player);
		}
		
		MultiMask mask = new MultiMask();
		
		Set<PlotID> worldEditPlots = plugin.getDataManager().getPlotsByPermission(SpongeDummy.getPlayerUniqueId(player), world.getUniqueID(), "worldedit");
		
		for(PlotID plotID : worldEditPlots)  
		{
			Region region = getRegionForPlot(plotID, world, worldConfig);
			
			mask.add(new RegionMask(region));
		}
		
		LocalSession session = worldEdit.getSession(WorldEditDummy.getLocalPlayer(player));
		session.setMask(mask);
	}
	
	public void clearMask(Player player) 
	{
		LocalSession session = worldEdit.getSession(WorldEditDummy.getLocalPlayer(player));
		session.setMask(null);
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
