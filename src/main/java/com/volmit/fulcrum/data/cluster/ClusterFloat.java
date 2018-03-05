package com.volmit.fulcrum.data.cluster;

public class ClusterFloat extends Cluster
{
	public ClusterFloat(Object object)
	{
		super(Float.class, "f", object);
	}

	@Override
	public String write()
	{
		return get().toString();
	}

	@Override
	public void read(String s)
	{
		set(Float.valueOf(s));
	}
}
