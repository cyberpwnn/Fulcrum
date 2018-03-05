package com.volmit.fulcrum.data.cluster;

public interface ICluster
{
	public Class<?> getType();

	public String getTypeKey();

	public Object get();

	public void set(Object o);

	public String write();

	public void read(String s);
}
