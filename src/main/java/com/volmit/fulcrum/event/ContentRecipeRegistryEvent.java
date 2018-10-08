package com.volmit.fulcrum.event;

import com.volmit.fulcrum.custom.ICustomRecipe;
import com.volmit.volume.lang.collections.GList;

public class ContentRecipeRegistryEvent extends FulcrumEvent
{
	private GList<ICustomRecipe> r;

	public ContentRecipeRegistryEvent(GList<ICustomRecipe> r)
	{
		this.r = r;
	}

	public void register(ICustomRecipe c)
	{
		r.add(c);
	}
}
