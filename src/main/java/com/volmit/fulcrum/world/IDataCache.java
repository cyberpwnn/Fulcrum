package com.volmit.fulcrum.world;

import java.io.File;
import java.io.IOException;

import com.volmit.fulcrum.data.cluster.DataCluster;

public interface IDataCache<T>
{
	public void flush() throws IOException;

	public int size();

	public DataCluster read(T t, File f) throws IOException;

	public void write(T t, DataCluster c, File f);

	public boolean has(T t);
}
