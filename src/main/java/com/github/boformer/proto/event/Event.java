package com.github.boformer.proto.event;

import org.spongepowered.api.Game;

public abstract class Event implements org.spongepowered.api.event.GameEvent
{
	private final Game game;
	
	public Event(Game game) 
	{
		this.game = game;
	}

	@Override
	public Game getGame() 
	{
		return game;
	}
}
