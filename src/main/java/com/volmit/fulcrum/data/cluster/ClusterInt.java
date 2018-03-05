package com.volmit.fulcrum.data.cluster;

public class ClusterInt extends Cluster
{
	public ClusterInt(Object object)
	{
		super(Integer.class, "i", object);
	}

	@Override
	public String write()
	{
		return get().toString();
	}

	@Override
	public void read(String s)
	{
		set(Integer.valueOf(s));
	}
}
