package com.volmit.fulcrum.registry;

import com.volmit.volume.lang.collections.GList;
import com.volmit.volume.lang.collections.GMap;

public abstract class Registrar<T extends Registered> implements Registry<T>
{
	private GMap<String, T> t;

	public Registrar()
	{
		t = new GMap<String, T>();
	}

	@Override
	public void register(T t) throws RegistryException
	{
		if(this.t.containsKey(t.getId()))
		{
			throw new RegistryException("Cannot register " + t.getId() + " as it is already registered");
		}

		this.t.put(t.getId(), t);
	}

	@Override
	public GList<String> getRegistries()
	{
		return t.k();
	}

	@Override
	public T getRegistered(String id)
	{
		return t.get(id);
	}

	@Override
	public void dump()
	{
		t.clear();
	}

	@Override
	public T getRegistered(Enum<?> id)
	{
		return getRegistered(id.name());
	}

	@Override
	public abstract void handle(T t);
}
