package com.volmit.fulcrum.data.cluster;

public class ClusterDouble extends Cluster
{
	public ClusterDouble(Object object)
	{
		super(Double.class, "d", object);
	}

	@Override
	public String write()
	{
		return get().toString();
	}

	@Override
	public void read(String s)
	{
		set(Double.valueOf(s));
	}
}
