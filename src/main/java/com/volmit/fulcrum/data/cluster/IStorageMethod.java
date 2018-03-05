package com.volmit.fulcrum.data.cluster;

public interface IStorageMethod<T>
{
	public T save(DataCluster c);

	public DataCluster load(T t);
}
