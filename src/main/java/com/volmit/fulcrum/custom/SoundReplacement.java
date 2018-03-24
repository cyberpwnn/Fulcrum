package com.volmit.fulcrum.custom;

import java.net.URL;

import org.bukkit.plugin.Plugin;

import com.volmit.fulcrum.bukkit.R;

public class SoundReplacement
{
	private String node;
	private String replacement;
	private String newSound;
	private URL ns;

	public SoundReplacement(Plugin plugin, String node, String newSound)
	{
		this.node = node;
		replacement = "m." + node;
		this.newSound = newSound;
		ns = R.getURL(plugin.getClass(), "/assets/sounds/" + newSound + ".ogg");
	}

	public String getNode()
	{
		return node;
	}

	public void setNode(String node)
	{
		this.node = node;
	}

	public String getReplacement()
	{
		return replacement;
	}

	public void setReplacement(String replacement)
	{
		this.replacement = replacement;
	}

	public String getNewSound()
	{
		return newSound;
	}

	public void setNewSound(String newSound)
	{
		this.newSound = newSound;
	}

	public URL getNs()
	{
		return ns;
	}

	public void setNs(URL ns)
	{
		this.ns = ns;
	}
}
