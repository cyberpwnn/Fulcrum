package com.volmit.fulcrum.event;

import com.volmit.dumpster.GList;
import com.volmit.fulcrum.custom.ICustomRecipe;

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
