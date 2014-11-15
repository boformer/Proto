package com.github.boformer.proto.worldedit;

import java.util.List;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.Player;
import org.spongepowered.api.world.World;

import com.github.boformer.proto.ProtoPlugin;
import com.github.boformer.proto.config.WorldConfig;
import com.github.boformer.proto.data.PlotID;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.masks.RegionMask;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.snapshots.Snapshot;

import dummy.sponge.SpongeDummy;
import dummy.worldedit.WorldEditDummy;

/**
 * Handles all features that require the WorldEdit plugin:
 * 
 * <ul>
 * <li>Restrict WorldEdit features to the plot of a player using the WorldEdit global mask</li>
 * <li>Reset the area of a deleted plot to a snapshot of the world</li>
 * </ul>
 */
public class WorldEditConnector
{
	private final ProtoPlugin plugin;
	private final Game game;
	private final WorldEdit worldEdit;


	/**
	 * <i>Internal constructor: Create a new WorldEdit connector.</i>
	 * 
	 * @param plugin The plugin
	 * @param worldEdit The WorldEdit instance
	 */
	public WorldEditConnector(ProtoPlugin plugin, Game game, WorldEdit worldEdit)
	{
		this.plugin = plugin;
		this.game = game;
		this.worldEdit = worldEdit;
	}
	
	/** 
	 * <i>Internal method: Initializes the WorldEdit connector when the server starts up.</i>
	 */
	public void initialize() 
	{
		//TODO listen for events
		//TODO create masks for all logged in players
	}
	
	//TODO add methods for snapshot restore
	//TODO subscribe to events
	//TODO update multimask instead of creating a new mask?
	
	/**
	 * Replaces the global mask of a player with an updated version containing only the plots where the player has WorldEdit permission.
	 * @param player The player
	 */
	public void createMask(Player player) 
	{
		World world = SpongeDummy.getPlayerWorld(player);
		
		WorldConfig worldConfig = plugin.getConfigManager().getWorldConfig(world.getName());
		
		//TODO player bypassing?
		
		//TODO add a permission protection.useworldeditonlyinplots and protection.dontuseworldeditatall and check that first
		
		if(worldConfig == null)
		{
			//no configuration --> not our deal. remove mask
			clearMask(player);
		}
		
		MultiMask mask = new MultiMask();
		LocalSession session = worldEdit.getSession(WorldEditDummy.getLocalPlayer(player));
		
		List<PlotID> worldEditPlots;
		try 
		{
			worldEditPlots = plugin.getDataManager().getPlotsByPermission(SpongeDummy.getPlayerUniqueId(player), world.getUniqueID(), "worldedit");
		} 
		catch (Exception e) 
		{
			// TODO add error message
			e.printStackTrace();
			
			//set empty mask to prevent abuse
			session.setMask(mask);
			return;
		}
		
		for(PlotID plotID : worldEditPlots)  
		{
			Region region = getRegionForPlot(plotID, world, worldConfig);
			
			mask.add(new RegionMask(region));
		}
		
		session.setMask(mask);
	}
	
	/**
	 * Clears the global mask of a player (e.g. when he leaves the plot world)
	 * @param player The player
	 */
	public void clearMask(Player player) 
	{
		LocalSession session = worldEdit.getSession(WorldEditDummy.getLocalPlayer(player));
		session.setMask(null);
	}
	
	
	public void restorePlot(PlotID plotID) 
	{
		World world = game.getWorld(plotID.getWorldID());
		
		//TODO load world?
		//TODO Important: put plot on a list of plots that have to be restored
		if(world == null) return;
		
		//TODO get LocalWorld from plotID.worldID
		LocalWorld localWorld = WorldEditDummy.getLocalWorld(world); // later change to 'SpongeUtil'?
		
		//TODO use worldData instead (for unloaded worlds)
		String worldName = world.getName();
		
		LocalConfiguration worldEditConfig = worldEdit.getConfiguration();
		EditSession session = new EditSession(localWorld, -1);
		
		Snapshot snapshot = null;
		
		if(worldEditConfig.snapshotRepo == null) 
		{
			plugin.getLogger().error("[WorldEditConnector] Snapshot/backup restore is not configured.");
		}
		
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
