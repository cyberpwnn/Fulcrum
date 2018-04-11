package com.volmit.fulcrum.world.generator;

import net.minecraft.server.v1_12_R1.ChunkRegionLoader;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.World;

class FulcrumQueuedChunk
{
	final int x;
	final int z;
	final ChunkRegionLoader loader;
	final World world;
	final FulcrumChunkProvider provider;
	NBTTagCompound compound;

	public FulcrumQueuedChunk(int x, int z, ChunkRegionLoader loader, World world, FulcrumChunkProvider provider)
	{
		this.x = x;
		this.z = z;
		this.loader = loader;
		this.world = world;
		this.provider = provider;
	}

	@Override
	public int hashCode()
	{
		return this.x * 31 + this.z * 29 ^ this.world.hashCode();
	}

	@Override
	public boolean equals(Object object)
	{
		if(object instanceof FulcrumQueuedChunk)
		{
			FulcrumQueuedChunk other = (FulcrumQueuedChunk) object;
			return this.x == other.x && this.z == other.z && this.world == other.world;
		}
		else
		{
			return false;
		}
	}
}