package com.github.boformer.proto.event;

import org.spongepowered.api.event.Result;

public abstract class Event implements org.spongepowered.api.event.Event
{
	private Result result = Result.DEFAULT;

	
	@Override
	public Result getResult()
	{
		return result;
	}

	@Override
	public void setResult(Result result)
	{
		this.result = result;
	}
}
