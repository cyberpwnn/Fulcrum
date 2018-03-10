package com.volmit.fulcrum.event;

import org.bukkit.event.Cancellable;

public class FulcrumCancellableEvent extends FulcrumEvent implements Cancellable
{
	private boolean cancelled = false;

	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled)
	{
		this.cancelled = cancelled;
	}
}
