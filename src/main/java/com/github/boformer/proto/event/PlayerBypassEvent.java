package com.github.boformer.proto.event;

import java.util.UUID;

import org.spongepowered.api.Game;
import org.spongepowered.api.util.event.callback.CallbackList;

//TODO javadoc
public class PlayerBypassEvent extends Event
{
	private final UUID playerID;
	private final boolean bypassing;


	public PlayerBypassEvent(Game game, UUID playerID, boolean bypassing)
	{
		super(game);
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
	public CallbackList getCallbacks() 
	{
		// TODO wait for sponge implementation to see how this works
		return null;
	}
}
