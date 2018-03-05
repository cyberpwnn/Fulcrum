package com.volmit.fulcrum.data.cluster;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class YMLStorageMedium implements IStorageMethod<FileConfiguration>
{
	@Override
	public FileConfiguration save(DataCluster c)
	{
		FileConfiguration fc = new YamlConfiguration();

		for(String i : c.k())
		{
			fc.set(c.get(i).getTypeKey() + "@" + c, c.get(i).write());
		}

		return fc;
	}

	@Override
	public DataCluster load(FileConfiguration t)
	{
		DataCluster c = new DataCluster();

		for(String i : t.getKeys(true))
		{
			String ct = i.split("@")[0];
			String key = i.split("@")[1];
			ICluster cx = DataCluster.getClusterType(ct);
			cx.read(t.getString(i));
			c.put(key, cx);
		}

		return c;
	}
}
