package com.github.boformer.donut.protection;

import java.util.UUID;

import com.github.boformer.donut.protection.permission.PermissionObject;

public class World implements PermissionObject
{
	private final String name;
	private final UUID uniqueID;

	ProtectionType protectionType;

	public String getName() {
		return name;
	}

	public UUID getUniqueID() {
		return uniqueID;
	}

	public ProtectionType getProtectionType() {
		return protectionType;
	}

	public void setProtectionType(ProtectionType protectionType) {
		this.protectionType = protectionType;
	}

	public World(String name, UUID uniqueID) {
		this.name = name;
		this.uniqueID = uniqueID;

		this.protectionType = ProtectionType.FREEBUILD;
	}
}
