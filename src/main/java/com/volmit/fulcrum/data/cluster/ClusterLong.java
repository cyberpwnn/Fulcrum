package com.volmit.fulcrum.data.cluster;

public class ClusterLong extends Cluster
{
	public ClusterLong(Object object)
	{
		super(Long.class, "l", object);
	}

	@Override
	public String write()
	{
		return get().toString();
	}

	@Override
	public void read(String s)
	{
		set(Long.valueOf(s));
	}
}
