package com.volmit.fulcrum.world;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.google.common.io.Files;
import com.volmit.fulcrum.data.cluster.DataCluster;
import com.volmit.fulcrum.data.cluster.RAWStorageMedium;
import com.volmit.fulcrum.lang.GMap;

public class WorldCache implements IDataCache<WorldKey>
{
	private GMap<WorldKey, DataCluster> cache;
	private GMap<WorldKey, File> files;

	public WorldCache()
	{
		cache = new GMap<WorldKey, DataCluster>();
		files = new GMap<WorldKey, File>();
	}

	@Override
	public void flush() throws IOException
	{
		for(WorldKey i : cache.k())
		{
			if(files.containsKey(i))
			{
				write(files.get(i), cache.get(i));
			}
		}

		cache.clear();
	}

	@Override
	public int size()
	{
		return cache.size();
	}

	@Override
	public DataCluster read(WorldKey t, File f) throws IOException
	{
		if(!cache.containsKey(t))
		{
			cache.put(t, read(f));
			files.put(t, f);
		}

		return cache.get(t);
	}

	@Override
	public void write(WorldKey t, DataCluster c, File f)
	{
		cache.put(t, c);
		files.put(t, f);
	}

	public static DataCluster read(File f) throws IOException
	{
		if(!f.exists())
		{
			return new DataCluster();
		}

		return new RAWStorageMedium().load(ByteBuffer.wrap(Files.toByteArray(f)));
	}

	public static void write(File f, DataCluster cc) throws IOException
	{
		if(!f.exists())
		{
			f.getParentFile().mkdirs();
			f.createNewFile();
		}

		Files.write(new RAWStorageMedium().save(cc).array(), f);
	}

	@Override
	public boolean has(WorldKey t)
	{
		return cache.containsKey(t);
	}
}
