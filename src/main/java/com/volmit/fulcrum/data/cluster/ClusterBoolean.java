package com.volmit.fulcrum.data.cluster;

public class ClusterBoolean extends Cluster
{
	public ClusterBoolean(Object object)
	{
		super(Boolean.class, "b", object);
	}

	@Override
	public String write()
	{
		return get().toString();
	}

	@Override
	public void read(String s)
	{
		set(Boolean.valueOf(s));
	}
}
