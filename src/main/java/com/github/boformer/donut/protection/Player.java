package com.github.boformer.donut.protection;

import java.util.UUID;

import com.github.boformer.donut.protection.permission.PermissionSubject;

public class Player implements PermissionSubject
{
	private final String name;
	private final UUID uniqueID;

	public String getName() {
		return name;
	}

	public UUID getUniqueID() {
		return uniqueID;
	}

	public Player(String name, UUID uniqueID) {
		this.name = name;
		this.uniqueID = uniqueID;
	}





}
