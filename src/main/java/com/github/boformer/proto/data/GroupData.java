package com.github.boformer.proto.data;

/**
 * Data of a group.
 */
public class GroupData
{
	private int id;
	private String name;
	
	/**
	 * Gets the ID of a group. The ID is an integer and not the name to make group name changes possible.
	 * 
	 * @return The group ID
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * <i>Internal method: Sets the integer ID of the group. Should not be set from outside!</i>
	 * 
	 * @param id The new group ID
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * Gets the (display) name of the group.
	 * @return The group name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Renames the group.
	 * @param name The new group name
	 */
	public void setName(String name)
	{
		this.name = name;
	}
}
