package com.github.boformer.donut.protection.data;

import java.util.UUID;

import com.github.boformer.donut.protection.Player;
import com.github.boformer.donut.protection.Plot;
import com.github.boformer.donut.protection.World;
import com.github.boformer.donut.protection.permission.PermissionObject;
import com.github.boformer.donut.protection.permission.PermissionSubject;

public class DataStore
{

	public World getWorld(UUID uniqueID) {
		// TODO Auto-generated method stub
		return null;
	}





	public boolean hasPermission(PermissionSubject subject, PermissionObject object, String permissionString) {
		// TODO Auto-generated method stub
		return false;
	}


	//Sponge --> DonutProtection

	public World getWorld(org.spongepowered.api.world.World world) {
		// TODO Auto-generated method stub
		return null;
	}

	public Player getPlayer(org.spongepowered.api.entity.Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	public Plot getPlot(org.spongepowered.api.world.Location location) {
		// TODO Auto-generated method stub
		return null;
	}
}
