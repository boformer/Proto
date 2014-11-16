package com.github.boformer.proto.event;

import java.util.UUID;

//TODO javadoc
public class PlayerBypassEvent extends Event
{
	private final UUID playerID;
	private final boolean bypassing;


	public PlayerBypassEvent(UUID playerID, boolean bypassing)
	{
		this.playerID = playerID;
		this.bypassing = bypassing;
	}

	public UUID getPlayerID()
	{
		return playerID;
	}

	public boolean isBypassing()
	{
		return bypassing;
	}

	@Override
	public boolean isCancellable()
	{
		return false;
	}

}
