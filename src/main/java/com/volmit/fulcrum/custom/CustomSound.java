package com.volmit.fulcrum.custom;

import java.net.URL;

import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.bukkit.R;
import com.volmit.volume.lang.collections.GMap;
import com.volmit.volume.lang.json.JSONArray;
import com.volmit.volume.lang.json.JSONObject;

public class CustomSound implements ICustom
{
	private String node;
	private GMap<String, URL> soundPaths;
	private String subtitle;
	private float suggestedVolume;
	private float suggestedPitch;
	private Object inst;

	public CustomSound(String node)
	{
		this.node = node;
		this.soundPaths = new GMap<String, URL>();
		this.subtitle = "Did you hear that?";
		suggestedVolume = 1f;
		suggestedPitch = 1f;
		setInstance(this);
	}

	public CustomSound(String node, String bases, String urlx, Class<?> c, int max, String subtitle)
	{
		this(node);

		for(int i = 0; i < max; i++)
		{
			ContentRegistry cr = Fulcrum.contentRegistry;

			String v = urlx.replace("$", (i + 1) + "");

			if(v.startsWith("/"))
			{
				v = v.substring(1);
			}

			URL url = cr.access(v);

			if(url == null)
			{
				c.getResource("/" + v);
			}

			if(url == null)
			{
				cr.e("Unable to locate sound file " + v);
				continue;
			}

			soundPaths.put(bases.replace("$", (i + 1) + ""), url);
		}

		this.subtitle = subtitle;
	}

	public void setInstance(Object inst)
	{
		this.inst = inst;
	}

	public CustomSound addSound(String resource)
	{
		getSoundPaths().put(resource + ".ogg", R.getURL(inst.getClass(), "/assets/sounds/" + resource + ".ogg"));
		return this;
	}

	public void addSound(String packLocation, String resource)
	{
		getSoundPaths().put(packLocation + ".ogg", R.getURL(inst.getClass(), "/assets/sounds/" + resource + ".ogg"));
	}

	public void addSound(String resource, int from, int to)
	{
		for(int i = Math.min(from, to); i <= Math.max(from, to); i++)
		{
			URL l = R.getURL(inst.getClass(), ("/assets/sounds/" + resource + ".ogg").replace("$", i + ""));

			if(l == null)
			{
				System.out.println("Cant find " + ("/assets/sounds/" + resource + ".ogg").replace("$", i + ""));
				continue;
			}

			getSoundPaths().put((resource + ".ogg").replace("$", i + ""), l);
		}
	}

	public void addSound(String packLocation, String resource, int from, int to)
	{
		for(int i = Math.min(from, to); i <= Math.max(from, to); i++)
		{
			URL l = R.getURL(inst.getClass(), ("/assets/sounds/" + resource + ".ogg").replace("$", i + ""));

			if(l == null)
			{
				System.out.println("Cant find " + ("/assets/sounds/" + resource + ".ogg").replace("$", i + ""));
				continue;
			}

			getSoundPaths().put((packLocation + ".ogg").replace("$", i + ""), l);
		}
	}

	public String getNode()
	{
		return node;
	}

	public GMap<String, URL> getSoundPaths()
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

	public void setSoundPaths(GMap<String, URL> soundPaths)
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

		for(String i : soundPaths.k())
		{
			arr.put(i.replace(".ogg", ""));
		}

		o.put("sounds", arr);

		if(subtitle != null)
		{
			o.put("subtitle", subtitle);
		}

		sounds.put(node, o);
	}

	public float getSuggestedVolume()
	{
		return suggestedVolume;
	}

	public void setSuggestedVolume(float suggestedVolume)
	{
		this.suggestedVolume = suggestedVolume;
	}

	public float getSuggestedPitch()
	{
		return suggestedPitch;
	}

	public void setSuggestedPitch(float suggestedPitch)
	{
		this.suggestedPitch = suggestedPitch;
	}
}
