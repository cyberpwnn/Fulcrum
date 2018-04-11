package com.volmit.fulcrum.world.generator;

import org.bukkit.craftbukkit.v1_12_R1.util.AsynchronousExecutor;

import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.ChunkRegionLoader;
import net.minecraft.server.v1_12_R1.World;

public class FulcrumChunkIOExecutor
{
	static final int BASE_THREADS = 1;
	static final int PLAYERS_PER_THREAD = 50;
	private static final AsynchronousExecutor<FulcrumQueuedChunk, Chunk, Runnable, RuntimeException> instance = new AsynchronousExecutor<FulcrumQueuedChunk, Chunk, Runnable, RuntimeException>(new FulcrumChunkIOProvider(), 1);

	public static Chunk syncChunkLoad(World world, ChunkRegionLoader loader, FulcrumChunkProvider provider, int x, int z)
	{
		return (Chunk) instance.getSkipQueue(new FulcrumQueuedChunk(x, z, loader, world, provider));
	}

	public static void queueChunkLoad(World world, ChunkRegionLoader loader, FulcrumChunkProvider provider, int x, int z, Runnable runnable)
	{
		instance.add(new FulcrumQueuedChunk(x, z, loader, world, provider), runnable);
	}

	public static void dropQueuedChunkLoad(World world, int x, int z, Runnable runnable)
	{
		instance.drop(new FulcrumQueuedChunk(x, z, (ChunkRegionLoader) null, world, (FulcrumChunkProvider) null), runnable);
	}

	public static void adjustPoolSize(int players)
	{
		int size = Math.max(1, (int) Math.ceil((double) (players / 50)));
		instance.setActiveThreads(size);
	}

	public static void tick()
	{
		instance.finishActive();
	}
}