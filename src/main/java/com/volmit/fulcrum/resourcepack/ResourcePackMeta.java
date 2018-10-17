package com.volmit.fulcrum.resourcepack;

import com.volmit.fulcrum.util.Environment;
import com.volmit.volume.lang.json.JSONObject;

public class ResourcePackMeta
{
	private String description;
	private int version;

	public ResourcePackMeta(String description, int version)
	{
		this.description = description;
		this.version = version;
	}

	public ResourcePackMeta(String description)
	{
		this(description, -1);
	}

	public ResourcePackMeta()
	{
		this("¯\\_(ツ)_/¯");
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public int getVersion()
	{
		return version;
	}

	public void setVersion(int version)
	{
		this.version = version;
	}

	public JSONObject realize()
	{
		JSONObject j = new JSONObject();
		JSONObject pack = new JSONObject();
		pack.put("description", getDescription());
		pack.put("pack_format", getVersion() == -1 ? Environment.getOptimalPackVersion() : getVersion());
		j.put("pack", pack);

		return j;
	}
}
