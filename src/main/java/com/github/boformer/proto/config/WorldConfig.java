package com.github.boformer.proto.config;

/**
 * World configuration.
 */
public class WorldConfig
{
	public boolean plotsEnabled;
	
	public int plotSizeX;
	public int plotSizeZ;
	
	public int plotOriginX;
	public int plotOriginZ;
	
	public int plotExpirationDays;
	
	//TODO replace with enums?
	//TODO verify these string values
	/*
	plugin.getLogger().error("Invalid plot configuration for world '" + worldName + "':");
	plugin.getLogger().error("The property plotcheck.expiration-action can only be STAFF_REVIEW or AUTO_DELETE ;)");
	 */
	public String plotExpirationStartTime;
	public String plotExpirationAction;
	
	
}
