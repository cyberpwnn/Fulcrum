package com.volmit.fulcrum.registries;

import com.volmit.fulcrum.registry.FRegistered;
import com.volmit.fulcrum.util.OggSound;
import com.volmit.volume.lang.collections.GList;

public class FSound extends FRegistered
{
	private GList<OggSound> sounds;
	private String subtitle;
	private boolean stream;

	public FSound(String id)
	{
		super(id);
		sounds = new GList<OggSound>();
		stream = false;
	}

	public void add(OggSound sound)
	{
		sounds.add(sound);
	}

	public void add(OggSound sound, int f, int t)
	{
		for(int i = f; i < t + 1; i++)
		{
			add(new OggSound(sound.getId() + i));
		}
	}

	public String getSubtitle()
	{
		return subtitle;
	}

	public void setSubtitle(String subtitle)
	{
		this.subtitle = subtitle;
	}

	public boolean isStream()
	{
		return stream;
	}

	public void setStream(boolean stream)
	{
		this.stream = stream;
	}

	public GList<OggSound> getSounds()
	{
		return sounds;
	}
}
