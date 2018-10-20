package com.volmit.fulcrum.registry;

import com.volmit.fulcrum.registrar.BlockRegistry;
import com.volmit.fulcrum.registrar.MaterialRegistry;
import com.volmit.fulcrum.registrar.ResourceRegistry;
import com.volmit.fulcrum.registrar.SoundRegistry;
import com.volmit.fulcrum.resourcepack.ResourcePack;

public class SuperConstructor
{
	private static SuperConstructor instance;
	private ResourcePack pack;
	private ResourceRegistry resourceRegistry;
	private SoundRegistry soundRegistry;
	private BlockRegistry blockRegistry;
	private MaterialRegistry materialRegistry;

	public SuperConstructor()
	{
		instance = this;
		pack = new ResourcePack();
		resourceRegistry = new ResourceRegistry();
		soundRegistry = new SoundRegistry();
		blockRegistry = new BlockRegistry();
		materialRegistry = new MaterialRegistry();
	}

	public void construct()
	{

	}

	public ResourcePack getPack()
	{
		return pack;
	}

	public ResourceRegistry getResourceRegistry()
	{
		return resourceRegistry;
	}

	public SoundRegistry getSoundRegistry()
	{
		return soundRegistry;
	}

	public BlockRegistry getBlockRegistry()
	{
		return blockRegistry;
	}

	public MaterialRegistry getMaterialRegistry()
	{
		return materialRegistry;
	}

	public static SuperConstructor getInstance()
	{
		return instance;
	}
}
