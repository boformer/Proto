package com.github.boformer.proto.worldedit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.message.CommandEvent;
import org.spongepowered.api.event.player.PlayerChangeWorldEvent;
import org.spongepowered.api.event.player.PlayerJoinEvent;
import org.spongepowered.api.util.event.Subscribe;
import org.spongepowered.api.world.World;

import com.github.boformer.proto.ProtoPlugin;
import com.github.boformer.proto.access.PlayerPlotAccess;
import com.github.boformer.proto.config.PlotAbandonAction;
import com.github.boformer.proto.config.WorldConfig;
import com.github.boformer.proto.data.PlotID;
import com.github.boformer.proto.event.PlotAbandonEvent;
import com.github.boformer.proto.event.PlotAccessChangeEvent;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.data.ChunkStore;
import com.sk89q.worldedit.data.MissingWorldException;
import com.sk89q.worldedit.masks.RegionMask;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.snapshots.Snapshot;
import com.sk89q.worldedit.snapshots.SnapshotRestore;

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
	
	//TODO subscribe to events
	//TODO update multimask instead of creating a new mask?
	//TODO listen to bypass event when implemented

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
		//register for events
		game.getEventManager().register(plugin, this);
		
		//create masks for all logged in players
		for(Player player : game.getOnlinePlayers()) 
		{
			updateMask(player);
		}
	}
	
	//TODO javadoc
	@Subscribe
	public void onPlotPermissionChange(PlotAccessChangeEvent event) 
	{
		List<Player> maskChangePlayers = new ArrayList<>();
		
		for(PlayerPlotAccess access : event.getAccessChangeMap().keySet()) 
		{
			if(!access.getPermission().equalsIgnoreCase("worldedit")) continue;
			
			Player player = game.getPlayer(access.getPlayerID()).orNull();
			
			//player not logged in
			if(player == null) continue;
			
			//Doesn't affect the player's current world
			if(!player.getWorld().getName().equals(access.getPlotID().getWorldName())) continue;
			
			maskChangePlayers.add(player);
		}
		
		for(Player player : maskChangePlayers) 
		{
			updateMask(player);
		}
	}
	
	//TODO javadoc
	@Subscribe
	public void onPlotAbandon(PlotAbandonEvent event) 
	{
		WorldConfig worldConfig = plugin.getConfigManager().getWorldConfig(event.getPlotID().getWorldName());
		
		if(worldConfig.plotAbandonAction == PlotAbandonAction.WORLDEDIT_SNAPSHOT_RESTORE) 
		{
			restorePlotFromSnapshot(event.getPlotID());
		}
	}

	
	//TODO javadoc
	@Subscribe
	public void onPlayerJoin(PlayerJoinEvent event) 
	{
		//method checks if world is plot world, just call it
		updateMask(event.getPlayer());
	}
	
	//TODO javadoc
	@Subscribe
	public void onCommand(CommandEvent event) 
	{
		//block the //gmask command, so players can't change the mask. Also the single-/ version ;)
		
		//ignore console commands
		if(!(event.getSource() instanceof Player)) return;
		
		Player player = (Player) event.getSource();
		
		if(event.getCommand().toLowerCase().startsWith("/gmask") || event.getCommand().toLowerCase().startsWith("//gmask")) 
		{
			World world = player.getWorld();
			WorldConfig worldConfig = plugin.getConfigManager().getWorldConfig(world.getName());
			
			//TODO config value for worldedit behaviour
			//TODO bypassing
			if(worldConfig != null && worldConfig.plotsEnabled)
			{
				event.setCancelled(true);
				
				player.sendMessage("You can't use this command in plot worlds.");
			}
		}
	}
	
	//TODO javadoc
	@Subscribe
	public void onPlayerChangeWorld(PlayerChangeWorldEvent event) 
	{
		//method checks if world is plot world, just call it
		updateMask(event.getPlayer(), event.getToWorld());
	}

	
	/**
	 * Replaces the global mask of a player with an updated version containing only the plots where the player has WorldEdit permission. Uses the world the player is in at the moment.
	 * @param player The player
	 */
	public void updateMask(Player player) 
	{
		World world = player.getWorld();
		
		updateMask(player, world);
	}
	
	/**
	 * Replaces the global mask of a player with an updated version containing only the plots where the player has WorldEdit permission.
	 * @param player The player
	 * @param world The world the player is in or enters
	 */
	public void updateMask(Player player, World world) 
	{
		if(SpongeDummy.hasWorldPermission(player, world, "proto.plot.worldedit.bypass")) 
		{
			//worldedit not restricted
			return;
		}
		
		WorldConfig worldConfig = plugin.getConfigManager().getWorldConfig(world.getName());
		
		if(worldConfig == null)
		{
			//no configuration --> not our deal. remove mask
			clearMask(player);
			return;
		}
		
		MultiMask mask = new MultiMask();
		LocalSession session = worldEdit.getSession(WorldEditDummy.getLocalPlayer(player));
		
		if(SpongeDummy.hasWorldPermission(player, world, "proto.plot.worldedit.plotsonly")) 
		{
			List<PlotID> worldEditPlots;
			try 
			{
				worldEditPlots = plugin.getDataManager().getPlotsByPermission(player.getUniqueId(), world.getName(), "worldedit");
			} 
			catch (Exception e) 
			{
				plugin.getLogger().error("[WorldEditConnector] Error while listing WorldEdit-enabled plots for player '" + player.getName() + "'. Using empty mask to prevent abuse.");
				plugin.getLogger().error("Details: " + e.getMessage());
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
		}
		else
		{
			//Permission: proto.plot.worldedit.disable
			//Just leave the mask empty
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
	
	/**
	 * Regenerates the land of an abandoned plot using the WorldEdit snapshot feature.
	 * 
	 * @param plotID The plot ID
	 */
	public void restorePlotFromSnapshot(PlotID plotID) 
	{
		World world = game.getWorld(plotID.getWorldName());
		
		//TODO load world?
		//TODO Important: put plot on a list of plots that have to be restored
		if(world == null) return;
		
		LocalWorld localWorld = WorldEditDummy.getLocalWorld(world); //TODO later change to 'SpongeUtil'?
		
		//TODO use worldData instead (for unloaded worlds)
		String worldName = world.getName();
		
		LocalConfiguration worldEditConfig = worldEdit.getConfiguration();
		EditSession session = new EditSession(localWorld, -1);
		
		if(worldEditConfig.snapshotRepo == null) 
		{
			plugin.getLogger().error("[WorldEditConnector] Can not regenerate abandoned plot: Snapshot/Backup restore is not configured in WorldEdit configuration.");
			return;
		}
		
		Snapshot snapshot = null;
		
		try
		{
			snapshot = worldEditConfig.snapshotRepo.getDefaultSnapshot(worldName);
		}
		catch (MissingWorldException e) 
		{}
		
		if(snapshot == null) 
		{
			//"No snapshot found. Can't restore plot!"
			plugin.getLogger().error("[WorldEditConnector] Can not regenerate abandoned plot: No snapshot found.");
			return;
		}
		
		ChunkStore chunkStore = null;
		
		//Load chunk store
		try
		{
			chunkStore = snapshot.getChunkStore();
		}
		catch (Exception e) 
		{
			plugin.getLogger().error("[WorldEditConnector] Failed to load snapshot: " + e.getMessage());
			e.printStackTrace();
			return;
		}
		
		WorldConfig worldConfig = plugin.getConfigManager().getWorldConfig(worldName);
		Region region = getRegionForPlot(plotID, world, worldConfig);

		try 
		{
			SnapshotRestore restore = new SnapshotRestore(chunkStore, session, region);
			
			try
			{
				restore.restore();
			}
			catch (MaxChangedBlocksException e) {}
            
            if (restore.hadTotalFailure()) 
            {
                String error = restore.getLastErrorMessage();
                if (error != null) 
                {
                	plugin.getLogger().error("[WorldEditConnector] Errors prevented any blocks from being restored.");
                	plugin.getLogger().error("Last error: " +  error);
                } 
                else 
                {
                	plugin.getLogger().error("[WorldEditConnector] No chunks could be loaded. (Bad archive?)");
                }
            } 
            else 
            {
            	plugin.getLogger().debug("[WorldEditConnector] Plot restored.");
            }
        } 
        finally 
        {
            try 
            {
                chunkStore.close();
            } 
            catch(IOException e) {}
        }
		
		//TODO unload world if loaded during process
	}	
	
	private static CuboidRegion getRegionForPlot(PlotID plotID, World world, WorldConfig worldConfig)
	{
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
