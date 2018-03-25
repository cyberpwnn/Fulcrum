package com.volmit.fulcrum.custom;

public enum FrameType
{
	GOAL,
	CHALLENGE,
	TASK;

	@Override
	public String toString()
	{
		return name().toLowerCase();
	}
}
