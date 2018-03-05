package com.volmit.fulcrum.data.cluster;

public class ClusterString extends Cluster
{
	public ClusterString(Object object)
	{
		super(String.class, "s", object);
	}

	@Override
	public String write()
	{
		return get().toString();
	}

	@Override
	public void read(String s)
	{
		set(s);
	}
}
