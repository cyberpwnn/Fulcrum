package com.volmit.fulcrum.custom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.volmit.dumpster.JSONException;
import com.volmit.dumpster.JSONObject;
import com.volmit.fulcrum.bukkit.R;
import com.volmit.fulcrum.resourcepack.ResourcePack;

public class ModelSet
{
	private ModelType type;
	private String model;
	private URL defaultModel;
	private URL fulcrumModel;

	public ModelSet(ModelType type) throws IOException
	{
		this.type = type;
		String name = type.toString().toLowerCase();
		defaultModel = R.getURL("/assets/models/block/default_" + name + ".json");
		fulcrumModel = R.getURL("/assets/models/block/fulcrum_" + name + ".json");

		if(fulcrumModel == null)
		{
			throw new IOException("WARNING: MISSING FULCRUM MODEL FOR TYPE: " + type.name() + "\n Expected: " + "/assets/models/block/fulcrum_" + name + ".json");
		}

		if(defaultModel == null)
		{
			throw new IOException("WARNING: MISSING DEFAULT MODEL FOR TYPE: " + type.name() + "\n Expected: " + "/assets/models/block/default_" + name + ".json");
		}

		model = read(defaultModel);
	}

	private String read(URL url) throws IOException
	{
		String content = "";
		BufferedReader bu = new BufferedReader(new InputStreamReader(url.openStream()));
		String line = null;

		while((line = bu.readLine()) != null)
		{
			content += line;
		}

		bu.close();

		return content;
	}

	public ModelType getType()
	{
		return type;
	}

	public String getModel()
	{
		return model;
	}

	public URL getDefaultModel()
	{
		return defaultModel;
	}

	public URL getFulcrumModel()
	{
		return fulcrumModel;
	}

	public void export(ResourcePack pack) throws JSONException, IOException
	{
		pack.setResource("models/block/fulcrum_" + type.toString().toLowerCase() + ".json", new JSONObject(read(getFulcrumModel())).toString(0));
	}
}
