package com.volmit.fulcrum.util;

import com.volmit.fulcrum.registry.Registered;

public interface ResourceLocation extends Registered
{
	public String getAbsolute();

	public String getProject();

	@Override
	public String getId();
}
