package com.volmit.fulcrum.event;

import com.volmit.fulcrum.custom.ICustom;
import com.volmit.fulcrum.custom.Registrar;

public class ContentRegistryEvent extends FulcrumEvent
{
	private Registrar r;

	public ContentRegistryEvent(Registrar r)
	{
		this.r = r;
	}

	public void register(ICustom c)
	{
		r.register(c);
	}
}
