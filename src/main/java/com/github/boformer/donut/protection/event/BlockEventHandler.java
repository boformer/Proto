package com.github.boformer.donut.protection.event;

import org.spongepowered.api.event.SpongeEventHandler;

import com.github.boformer.donut.protection.DonutProtectionPlugin;
import com.github.boformer.donut.protection.Player;
import com.github.boformer.donut.protection.Plot;
import com.github.boformer.donut.protection.ProtectionType;
import com.github.boformer.donut.protection.World;
import com.github.boformer.donut.protection.permission.Permissions;

import dummy.sponge.BlockBreakEvent;

public class BlockEventHandler
{
	private final DonutProtectionPlugin plugin;

	public BlockEventHandler(DonutProtectionPlugin plugin) {
		this.plugin = plugin;
	}

	@SpongeEventHandler
	public void onBlockBreak(BlockBreakEvent event) //TODO replace dummy event when supported
	{
		World world = plugin.getDataStore().getWorld(event.getWorld());

		//freebuild world? --> our job ends here
		if(world.getProtectionType() == ProtectionType.FREEBUILD)
		{
			return;
		}


		Player player = plugin.getDataStore().getPlayer(event.getPlayer());

		if(world.getProtectionType() == ProtectionType.PLOTS)
		{
			Plot plot = plugin.getDataStore().getPlot(event.getLocation());

			//plot doesn't inherit permissions from world --> no other way to get permission
			//TODO

			if(!plot.isInheritWorldPermissions())
			{

				//TODO
				return;
			}
		}

		if(world.getProtectionType() == ProtectionType.WHITELIST)
		{
			//check player permission
			if(plugin.getDataStore().hasPermission(player, world, Permissions.BUILD))
			{
				return;
			}
			else
			{
				//--> player has no permission
				event.setCancelled(true);
				event.getPlayer().sendMessage("You do not have the permission to build in this world!"); //TODO messages
			}
		}
	}
}
