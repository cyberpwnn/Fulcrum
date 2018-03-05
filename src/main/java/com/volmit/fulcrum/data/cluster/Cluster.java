package com.volmit.fulcrum.data.cluster;

public abstract class Cluster implements ICluster
{
	private Class<?> type;
	private String typeKey;
	private Object object;

	public Cluster(Class<?> type, String typeKey, Object object)
	{
		this.type = type;
		this.typeKey = typeKey;
		this.object = object;
	}

	@Override
	public Class<?> getType()
	{
		return type;
	}

	@Override
	public String getTypeKey()
	{
		return typeKey;
	}

	@Override
	public Object get()
	{
		return object;
	}

	@Override
	public void set(Object o)
	{
		object = o;
	}

	@Override
	public abstract String write();

	@Override
	public abstract void read(String s);
}
