package com.volmit.fulcrum.world.generator;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.craftbukkit.v1_12_R1.util.AsynchronousExecutor.CallBackProvider;

import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_12_R1.ChunkRegionLoader;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

class FulcrumChunkIOProvider implements CallBackProvider<FulcrumQueuedChunk, Chunk, Runnable, RuntimeException>
{
	private final AtomicInteger threadNumber = new AtomicInteger(1);

	@Override
	public Chunk callStage1(FulcrumQueuedChunk queuedChunk) throws RuntimeException
	{
		try
		{
			ChunkRegionLoader ex = queuedChunk.loader;
			Object[] data = ex.loadChunk(queuedChunk.world, queuedChunk.x, queuedChunk.z);
			if(data != null)
			{
				queuedChunk.compound = (NBTTagCompound) data[1];
				return (Chunk) data[0];
			}
			else
			{
				return null;
			}
		}
		catch(IOException arg3)
		{
			throw new RuntimeException(arg3);
		}
	}

	@Override
	public void callStage2(FulcrumQueuedChunk queuedChunk, Chunk chunk) throws RuntimeException
	{
		if(chunk == null)
		{
			queuedChunk.provider.originalGetChunkAt(queuedChunk.x, queuedChunk.z);
		}
		else
		{
			queuedChunk.loader.loadEntities(chunk, queuedChunk.compound.getCompound("Level"), queuedChunk.world);
			chunk.setLastSaved(queuedChunk.provider.world.getTime());
			queuedChunk.provider.chunks.put(ChunkCoordIntPair.a(queuedChunk.x, queuedChunk.z), chunk);
			chunk.addEntities();
			if(queuedChunk.provider.chunkGenerator != null)
			{
				queuedChunk.provider.chunkGenerator.recreateStructures(chunk, queuedChunk.x, queuedChunk.z);
			}

			chunk.loadNearby(queuedChunk.provider, queuedChunk.provider.chunkGenerator, false);
		}
	}

	@Override
	public void callStage3(FulcrumQueuedChunk queuedChunk, Chunk chunk, Runnable runnable) throws RuntimeException
	{
		runnable.run();
	}

	@Override
	public Thread newThread(Runnable runnable)
	{
		Thread thread = new Thread(runnable, "Chunk I/O Executor Thread-" + this.threadNumber.getAndIncrement());
		thread.setDaemon(true);
		return thread;
	}
}