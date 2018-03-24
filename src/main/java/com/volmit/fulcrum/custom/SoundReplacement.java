package com.volmit.fulcrum.custom;

public class SoundReplacement
{
	private String node;
	private String replacement;
	private CustomSound newSound;

	public SoundReplacement(String node, CustomSound newSound)
	{
		this.node = node;
		replacement = "m." + node;
		this.newSound = newSound;
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

	public CustomSound getNewSound()
	{
		return newSound;
	}

	public void setNewSound(CustomSound newSound)
	{
		this.newSound = newSound;
	}
}
