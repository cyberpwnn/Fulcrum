package com.volmit.fulcrum.resourcepack;

import com.volmit.volume.lang.collections.GList;
import com.volmit.volume.lang.collections.GMap;

public abstract class Package
{
	private final PackMeta meta;
	private final GMap<String, PackResource> resources;

	public Package()
	{
		meta = new PackMeta();
		resources = new GMap<String, PackResource>();
	}

	public PackResource getResource(String path)
	{
		if(!path.startsWith("/"))
		{
			path = "/" + path;
		}

		return getResources().get(path);
	}

	public GList<String> getResourcePaths()
	{
		return getResources().k();
	}

	public void setResouce(String path, PackResource r)
	{
		if(!path.startsWith("/"))
		{
			path = "/" + path;
		}

		getResources().put(path, r);
	}

	public PackResource getPackIcon()
	{
		return getResource("pack.png");
	}

	public void setPackIcon(PackResource packIcon)
	{
		setResouce("pack.png", packIcon);
	}

	public PackMeta getMeta()
	{
		return meta;
	}

	public GMap<String, PackResource> getResources()
	{
		return resources;
	}
}
