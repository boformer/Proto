package com.github.boformer.donut.protection.permission;

public class Permission
{
	private final PermissionSubject subject;
	private final PermissionObject object;
	
	private String permissionString;

	public PermissionSubject getSubject() {
		return subject;
	}

	public PermissionObject getObject() {
		return object;
	}
	
	public String getPermissionString() {
		return permissionString;
	}

	public void setPermissionString(String permissionString) {
		this.permissionString = permissionString;
	}

	public Permission(PermissionSubject subject, PermissionObject object) {
		this.subject = subject;
		this.object = object;
	}
}
