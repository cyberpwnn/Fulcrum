package com.volmit.fulcrum.data.cluster;

import java.util.List;

import com.volmit.fulcrum.lang.GList;

public class ClusterStringList extends Cluster
{
	public ClusterStringList(Object object)
	{
		super(List.class, "sl", object);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String write()
	{
		String k = "";

		for(String i : (List<String>) get())
		{
			k += ":/:" + i;
		}

		return k.substring(1);
	}

	@Override
	public void read(String s)
	{
		List<String> f = new GList<String>();

		for(String i : s.split(":/:"))
		{
			f.add(i);
		}
	}
}
