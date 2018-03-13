package com.volmit.fulcrum.custom;

import java.net.URL;

import com.volmit.fulcrum.lang.GMap;
import com.volmit.fulcrum.lang.JSONArray;
import com.volmit.fulcrum.lang.JSONObject;

public class CustomSound
{
	private String node;
	private GMap<URL, String> soundPaths;
	private String subtitle;

	public CustomSound(String node)
	{
		this.node = node;
		this.soundPaths = new GMap<URL, String>();
		this.subtitle = null;
	}

	public String getNode()
	{
		return node;
	}

	public GMap<URL, String> getSoundPaths()
	{
		return soundPaths;
	}

	public String getSubtitle()
	{
		return subtitle;
	}

	public void setNode(String node)
	{
		this.node = node;
	}

	public void setSoundPaths(GMap<URL, String> soundPaths)
	{
		this.soundPaths = soundPaths;
	}

	public void setSubtitle(String subtitle)
	{
		this.subtitle = subtitle;
	}

	public void toJson(JSONObject sounds)
	{
		JSONObject o = new JSONObject();
		JSONArray arr = new JSONArray();

		for(String i : soundPaths.v())
		{
			arr.put(i);
		}

		o.put("sounds", arr);

		if(subtitle != null)
		{
			o.put("subtitle", subtitle);
		}
	}
}
