package com.volmit.fulcrum.resourcepack;

import com.volmit.volume.lang.json.JSONObject;

public class PackMeta
{
	private int format;
	private String description;

	public PackMeta(int format, String description)
	{
		this.format = format;
		this.description = description;
	}

	public PackMeta(String description)
	{
		this(4, description);
	}

	public PackMeta()
	{
		this("¯\\_(ツ)_/¯");
	}

	@Override
	public String toString()
	{
		return toString(4);
	}

	public String toString(int indentation)
	{
		JSONObject ja = new JSONObject();
		JSONObject pack = new JSONObject();
		pack.put("pack_format", getFormat());
		pack.put("description", getDescription());
		ja.put("pack", pack);
		return ja.toString(indentation);
	}

	public int getFormat()
	{
		return format;
	}

	public void setFormat(int format)
	{
		this.format = format;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
}
