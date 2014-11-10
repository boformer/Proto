package com.github.boformer.donut.protection;

import com.github.boformer.donut.protection.permission.PermissionObject;

public class Plot implements PermissionObject
{
	private final int x, z;
	private final World world;
	
	private String name;
	
	private boolean inheritWorldPermissions;

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}
	
	public World getWorld() {
		return world;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Plot(int x, int z, World world) {
		this.x = x;
		this.z = z;
		this.world = world;
	}

	public boolean isInheritWorldPermissions() {
		return inheritWorldPermissions;
	}

	public void setInheritWorldPermissions(boolean inheritWorldPermissions) 
	{
		this.inheritWorldPermissions = inheritWorldPermissions;
	}


}
