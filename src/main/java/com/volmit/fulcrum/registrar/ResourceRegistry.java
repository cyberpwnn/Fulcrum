package com.volmit.fulcrum.registrar;

import com.volmit.fulcrum.registry.Registrar;
import com.volmit.fulcrum.registry.SuperConstructor;
import com.volmit.fulcrum.resourcepack.Resource;
import com.volmit.fulcrum.util.ResourceLocation;

public class ResourceRegistry extends Registrar<ResourceLocation>
{
	@Override
	public void handle(ResourceLocation t)
	{
		SuperConstructor.getInstance().getPack().addResource(new Resource(t.getAbsolute()));
	}
}
