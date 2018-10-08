package com.volmit.fulcrum.custom;

import com.volmit.volume.lang.collections.GMap;

public class MultiCustomSound implements ICustom
{
	private GMap<String, CustomSound> sounds;
	private String prefix;

	public MultiCustomSound(String prefix)
	{
		this.prefix = prefix;
		sounds = new GMap<String, CustomSound>();
	}

	public void expandToBlock(String node)
	{
		expandToBlock(node, node);
	}

	public void expandToBlock()
	{
		expandToBlock("h", "s");
	}

	public void expandToBlock(String hard, String soft)
	{
		CustomSound h = get(hard);
		CustomSound s = get(soft);
		register("step").getSoundPaths().putAll(s.getSoundPaths());
		register("break").getSoundPaths().putAll(h.getSoundPaths());
		register("place").getSoundPaths().putAll(h.getSoundPaths());
		register("dig").getSoundPaths().putAll(s.getSoundPaths());
		register("pickup").getSoundPaths().putAll(s.getSoundPaths());
		register("step").setSuggestedVolume(0.4f);
		register("break").setSuggestedVolume(1f);
		register("place").setSuggestedVolume(1f);
		register("dig").setSuggestedVolume(0.6f);
		register("pickup").setSuggestedVolume(0.6f);
		register("step").setSuggestedPitch(1.3f);
		register("break").setSuggestedPitch(1f);
		register("place").setSuggestedPitch(1f);
		register("dig").setSuggestedPitch(0.4f);
		register("pickup").setSuggestedPitch(1.5f);
	}

	public CustomSound getStep()
	{
		return get("step");
	}

	public CustomSound getBreak()
	{
		return get("break");
	}

	public CustomSound getPlace()
	{
		return get("place");
	}

	public CustomSound getPickup()
	{
		return get("pickup");
	}

	public CustomSound getDig()
	{
		return get("dig");
	}

	public void applyToBlock(CustomBlock block)
	{
		block.setStepSound(get("step"));
		block.setBreakSound(get("break"));
		block.setPlaceSound(get("place"));
		block.setDigSound(get("dig"));
		block.setPickupSound(get("pickup"));
	}

	public CustomSound get(String node)
	{
		return register(node);
	}

	public void registerV(String node, float volume)
	{
		register(node).setSuggestedVolume(volume);
	}

	public void registerP(String node, float pitch)
	{
		register(node).setSuggestedPitch(pitch);
	}

	public void registerCopy(String node, String of)
	{
		register(of).setSuggestedVolume(register(node).getSuggestedVolume());
		register(of).setSuggestedPitch(register(node).getSuggestedPitch());
		register(of).getSoundPaths().putAll(register(node).getSoundPaths().copy());
	}

	public void registerVP(String node, float volume, float pitch)
	{
		registerV(node, volume);
		registerP(node, pitch);
	}

	public void add(String node, String resource)
	{
		register(node).addSound(resource);
	}

	public void add(String node, String resource, int from, int to)
	{
		register(node).addSound(resource, from, to);
	}

	public void addHard(String resource)
	{
		add("h", resource);
	}

	public void addHard(String resource, int from, int to)
	{
		add("h", resource, from, to);
	}

	public void addSoft(String resource)
	{
		add("s", resource);
	}

	public void addSoft(String resource, int from, int to)
	{
		add("s", resource, from, to);
	}

	public CustomSound register(String node)
	{
		if(!sounds.containsKey(node))
		{
			sounds.put(node, new CustomSound(prefix + "." + node));
			sounds.get(node).setInstance(this);
		}

		return sounds.get(node);
	}

	public GMap<String, CustomSound> getSounds()
	{
		return sounds;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public void setSounds(GMap<String, CustomSound> sounds)
	{
		this.sounds = sounds;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}
}
