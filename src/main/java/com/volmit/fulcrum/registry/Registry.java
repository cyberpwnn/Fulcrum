package com.volmit.fulcrum.registry;

import com.volmit.volume.lang.collections.GList;

public interface Registry<T extends Registered>
{
	public void register(T t) throws RegistryException;

	public GList<String> getRegistries();

	public void dump();

	public T getRegistered(String id);

	public T getRegistered(Enum<?> id);

	public void handle(T t);
}
