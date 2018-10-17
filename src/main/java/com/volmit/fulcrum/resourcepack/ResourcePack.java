package com.volmit.fulcrum.resourcepack;

import com.volmit.volume.lang.collections.GList;

public class ResourcePack
{
	private final ResourcePackMeta packMeta;
	private final GList<Resource> resources;

	public ResourcePack()
	{
		packMeta = new ResourcePackMeta();
		resources = new GList<Resource>();
		addResource("pack.png");
	}

	public ResourcePackMeta getPackMeta()
	{
		return packMeta;
	}

	public GList<Resource> getResources()
	{
		return resources;
	}

	public void addResource(Resource r)
	{
		getResources().add(r);
	}

	public void addResource(String path)
	{
		addResource(new Resource(path));
	}
}
