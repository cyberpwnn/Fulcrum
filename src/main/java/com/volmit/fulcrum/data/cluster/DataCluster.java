package com.volmit.fulcrum.data.cluster;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.volmit.dumpster.GList;
import com.volmit.dumpster.GMap;

public class DataCluster
{
	private static GList<ICluster> types = new GList<ICluster>();
	private GMap<String, ICluster> clusters;

	public DataCluster()
	{
		clusters = new GMap<String, ICluster>();
	}

	public void put(String key, ICluster c)
	{
		clusters.put(key, c);
	}

	public void put(String key, int c)
	{
		clusters.put(key, new ClusterInt(c));
	}

	public void put(String key, double c)
	{
		clusters.put(key, new ClusterDouble(c));
	}

	public void put(String key, float c)
	{
		clusters.put(key, new ClusterFloat(c));
	}

	public void put(String key, String c)
	{
		clusters.put(key, new ClusterString(c));
	}

	public void put(String key, boolean c)
	{
		clusters.put(key, new ClusterBoolean(c));
	}

	public void put(String key, long c)
	{
		clusters.put(key, new ClusterLong(c));
	}

	public void put(String key, List<String> c)
	{
		clusters.put(key, new ClusterStringList(new GList<String>(c)));
	}

	public String getString(String key)
	{
		return (String) get(key).get();
	}

	public Integer getInt(String key)
	{
		return (Integer) get(key).get();
	}

	public Float getFloat(String key)
	{
		return (Float) get(key).get();
	}

	public Double getDouble(String key)
	{
		return (Double) get(key).get();
	}

	public Long getLong(String key)
	{
		return (Long) get(key).get();
	}

	public Boolean getBoolean(String key)
	{
		return (Boolean) get(key).get();
	}

	@SuppressWarnings("unchecked")
	public GList<String> getStringList(String key)
	{
		return (GList<String>) get(key).get();
	}

	public boolean contains(String key)
	{
		return clusters.containsKey(key);
	}

	public GList<String> k()
	{
		return clusters.k();
	}

	public GList<String> tk()
	{
		GList<String> tk = new GList<String>();

		for(String i : k())
		{
			if(i.contains("."))
			{
				tk.add(i.split("\\.")[0]);
			}

			else
			{
				tk.add(i);
			}
		}

		tk.removeDuplicates();
		return tk;
	}

	public ICluster get(String key)
	{
		return clusters.get(key);
	}

	public <T> T save(IStorageMethod<T> storageMethod)
	{
		return storageMethod.save(this);
	}

	public <T> void load(IStorageMethod<T> storageMethod, T t)
	{
		put(storageMethod.load(t));
	}

	public void clear()
	{
		clusters.clear();
	}

	public void put(DataCluster cc)
	{
		clusters.putAll(cc.clusters);
	}

	public static ICluster getClusterType(String ctype)
	{
		for(ICluster i : types)
		{
			if(i.getTypeKey().equals(ctype))
			{
				try
				{
					return i.getClass().getConstructor(Object.class).newInstance((Object) null);
				}

				catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
				{
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	public static ICluster getClusterType(Class<?> ctype)
	{
		for(ICluster i : types)
		{
			if(i.getType().equals(ctype))
			{
				try
				{
					return i.getClass().getConstructor(Object.class).newInstance((Object) null);
				}

				catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
				{
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	public DataCluster crop(String out)
	{
		DataCluster cc = new DataCluster();

		for(String i : k())
		{
			if(i.startsWith(out + "."))
			{
				cc.put(i.replace(out + ".", ""), get(i));
			}
		}

		return cc;
	}

	public DataCluster copy()
	{
		DataCluster cc = new DataCluster();
		cc.put(this);
		return cc;
	}

	static
	{
		types.add(new ClusterBoolean(false));
		types.add(new ClusterString(""));
		types.add(new ClusterInt(0));
		types.add(new ClusterDouble(0.0));
		types.add(new ClusterFloat(0f));
		types.add(new ClusterLong(0l));
		types.add(new ClusterStringList(new GList<String>()));
	}

	public boolean isEmpty()
	{
		return clusters.isEmpty();
	}
}
