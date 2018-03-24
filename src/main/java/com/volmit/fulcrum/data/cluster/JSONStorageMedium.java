package com.volmit.fulcrum.data.cluster;

import com.volmit.dumpster.GList;
import com.volmit.dumpster.JSONObject;

public class JSONStorageMedium implements IStorageMethod<JSONObject>
{
	@Override
	public JSONObject save(DataCluster c)
	{
		JSONObject fc = new JSONObject();

		for(String i : c.k())
		{
			fc.put(c.get(i).getTypeKey() + "@" + c, c.get(i).write());
		}

		return fc;
	}

	@Override
	public DataCluster load(JSONObject t)
	{
		DataCluster c = new DataCluster();

		for(String i : new GList<String>(t.keys()))
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
