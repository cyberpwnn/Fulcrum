package com.volmit.fulcrum.world.generator;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.google.common.collect.Sets;

import net.minecraft.server.v1_12_R1.BiomeBase.BiomeMeta;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_12_R1.ChunkGenerator;
import net.minecraft.server.v1_12_R1.ChunkRegionLoader;
import net.minecraft.server.v1_12_R1.CrashReport;
import net.minecraft.server.v1_12_R1.CrashReportSystemDetails;
import net.minecraft.server.v1_12_R1.EnumCreatureType;
import net.minecraft.server.v1_12_R1.ExceptionWorldConflict;
import net.minecraft.server.v1_12_R1.IChunkLoader;
import net.minecraft.server.v1_12_R1.IChunkProvider;
import net.minecraft.server.v1_12_R1.ReportedException;
import net.minecraft.server.v1_12_R1.World;
import net.minecraft.server.v1_12_R1.WorldServer;

public class FulcrumChunkProvider implements IChunkProvider
{
	public final Set<Long> unloadQueue = Sets.newHashSet();
	public final ChunkGenerator chunkGenerator;
	private final IChunkLoader chunkLoader;
	public final Long2ObjectMap<Chunk> chunks = new Long2ObjectOpenHashMap<Chunk>(8192);
	public final WorldServer world;

	public FulcrumChunkProvider(WorldServer worldserver, IChunkLoader ichunkloader, ChunkGenerator chunkgenerator)
	{
		this.world = worldserver;
		this.chunkLoader = ichunkloader;
		this.chunkGenerator = chunkgenerator;
	}

	public Collection<Chunk> getChunks()
	{
		return this.chunks.values();
	}

	public void unload(Chunk chunk)
	{
		if(this.world.worldProvider.c(chunk.locX, chunk.locZ))
		{
			this.unloadQueue.add(Long.valueOf(ChunkCoordIntPair.a(chunk.locX, chunk.locZ)));
			chunk.d = true;
		}
	}

	public void unloadAll()
	{
		ObjectIterator<Chunk> objectiterator = this.chunks.values().iterator();

		while(objectiterator.hasNext())
		{
			Chunk chunk = (Chunk) objectiterator.next();
			this.unload(chunk);
		}
	}

	@Override
	@Nullable
	public Chunk getLoadedChunkAt(int i, int j)
	{
		long k = ChunkCoordIntPair.a(i, j);
		Chunk chunk = (Chunk) this.chunks.get(k);

		if(chunk != null)
		{
			chunk.d = false;
		}

		return chunk;
	}

	@Nullable
	public Chunk getOrLoadChunkAt(int i, int j)
	{
		Chunk chunk = this.getLoadedChunkAt(i, j);
		if(chunk == null)
		{
			ChunkRegionLoader loader = null;
			if(this.chunkLoader instanceof ChunkRegionLoader)
			{
				loader = (ChunkRegionLoader) this.chunkLoader;
			}

			if(loader != null && loader.chunkExists(i, j))
			{
				chunk = FulcrumChunkIOExecutor.syncChunkLoad(this.world, loader, this, i, j);
			}
		}

		return chunk;
	}

	@Nullable
	public Chunk originalGetOrLoadChunkAt(int i, int j)
	{
		Chunk chunk = this.getLoadedChunkAt(i, j);
		if(chunk == null)
		{
			chunk = this.loadChunk(i, j);
			if(chunk != null)
			{
				this.chunks.put(ChunkCoordIntPair.a(i, j), chunk);
				chunk.addEntities();
				chunk.loadNearby(this, this.chunkGenerator, false);
			}
		}

		return chunk;
	}

	public Chunk getChunkIfLoaded(int x, int z)
	{
		return (Chunk) this.chunks.get(ChunkCoordIntPair.a(x, z));
	}

	@Override
	public Chunk getChunkAt(int i, int j)
	{
		return this.getChunkAt(i, j, (Runnable) null);
	}

	public Chunk getChunkAt(int i, int j, Runnable runnable)
	{
		return this.getChunkAt(i, j, runnable, true);
	}

	public Chunk getChunkAt(int i, int j, Runnable runnable, boolean generate)
	{
		Chunk chunk = this.getChunkIfLoaded(i, j);
		ChunkRegionLoader loader = null;
		if(this.chunkLoader instanceof ChunkRegionLoader)
		{
			loader = (ChunkRegionLoader) this.chunkLoader;
		}

		if(chunk == null && loader != null && loader.chunkExists(i, j))
		{
			if(runnable != null)
			{
				FulcrumChunkIOExecutor.queueChunkLoad(this.world, loader, this, i, j, runnable);
				return null;
			}

			chunk = FulcrumChunkIOExecutor.syncChunkLoad(this.world, loader, this, i, j);
		}
		else if(chunk == null && generate)
		{
			chunk = this.originalGetChunkAt(i, j);
		}

		if(runnable != null)
		{
			runnable.run();
		}

		return chunk;
	}

	public Chunk originalGetChunkAt(int i, int j)
	{
		Chunk chunk = this.originalGetOrLoadChunkAt(i, j);
		if(chunk == null)
		{
			long k = ChunkCoordIntPair.a(i, j);

			try
			{
				chunk = this.chunkGenerator.getOrCreateChunk(i, j);
			}
			catch(Throwable arg8)
			{
				CrashReport crashreport = CrashReport.a(arg8, "Exception generating new chunk");
				CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Chunk to be generated");
				crashreportsystemdetails.a("Location", String.format("%d,%d", new Object[] {Integer.valueOf(i), Integer.valueOf(j)}));
				crashreportsystemdetails.a("Position hash", Long.valueOf(k));
				crashreportsystemdetails.a("Generator", this.chunkGenerator);
				throw new ReportedException(crashreport);
			}

			this.chunks.put(k, chunk);
			chunk.addEntities();
			chunk.loadNearby(this, this.chunkGenerator, true);
		}

		return chunk;
	}

	@Nullable
	public Chunk loadChunk(int i, int j)
	{
		try
		{
			Chunk exception = this.chunkLoader.a(this.world, i, j);
			if(exception != null)
			{
				exception.setLastSaved(this.world.getTime());
				this.chunkGenerator.recreateStructures(exception, i, j);
			}

			return exception;
		}
		catch(Exception arg3)
		{
			System.out.println("Couldn\'t load chunk");
			arg3.printStackTrace();
			return null;
		}
	}

	public void saveChunkNOP(Chunk chunk)
	{
		try
		{
			this.chunkLoader.b(this.world, chunk);
		}

		catch(Exception arg2)
		{
			System.out.println("Couldn\'t save entities");
			arg2.printStackTrace();
		}
	}

	public void saveChunk(Chunk chunk)
	{
		try
		{
			chunk.setLastSaved(this.world.getTime());
			this.chunkLoader.saveChunk(this.world, chunk);
		}
		catch(IOException arg2)
		{
			System.out.println("Couldn\'t save chunk");
			arg2.printStackTrace();
		}
		catch(ExceptionWorldConflict arg3)
		{
			System.out.println("Couldn\'t save chunk; already in use by another instance of Minecraft?");
			arg3.printStackTrace();
		}

	}

	public boolean a(boolean flag)
	{
		int i = 0;
		ObjectIterator<Chunk> iterator = this.chunks.values().iterator();

		while(iterator.hasNext())
		{
			Chunk chunk = (Chunk) iterator.next();
			if(flag)
			{
				this.saveChunkNOP(chunk);
			}

			if(chunk.a(flag))
			{
				this.saveChunk(chunk);
				chunk.f(false);
				++i;
				if(i == 24 && !flag)
				{
					return false;
				}
			}
		}

		return true;
	}

	public void c()
	{
		this.chunkLoader.c();
	}

	@Override
	public boolean unloadChunks()
	{
		if(!this.world.savingDisabled)
		{
			if(!this.unloadQueue.isEmpty())
			{
				Iterator<Long> iterator = this.unloadQueue.iterator();

				for(int i = 0; i < 100 && iterator.hasNext(); iterator.remove())
				{
					Long olong = (Long) iterator.next();
					Chunk chunk = (Chunk) this.chunks.get(olong);
					if(chunk != null && chunk.d && this.unloadChunk(chunk, true))
					{
						++i;
					}
				}
			}

			this.chunkLoader.b();
		}

		return false;
	}

	public boolean unloadChunk(Chunk chunk, boolean save)
	{
		ChunkUnloadEvent event = new ChunkUnloadEvent(chunk.bukkitChunk, save);
		this.world.getServer().getPluginManager().callEvent(event);
		if(event.isCancelled())
		{
			return false;
		}
		else
		{
			save = event.isSaveChunk();

			for(int x = -2; x < 3; ++x)
			{
				for(int z = -2; z < 3; ++z)
				{
					if(x != 0 || z != 0)
					{
						Chunk neighbor = this.getChunkIfLoaded(chunk.locX + x, chunk.locZ + z);
						if(neighbor != null)
						{
							neighbor.setNeighborUnloaded(-x, -z);
							chunk.setNeighborUnloaded(x, z);
						}
					}
				}
			}

			chunk.removeEntities();
			if(save)
			{
				this.saveChunk(chunk);
				this.saveChunkNOP(chunk);
			}

			this.chunks.remove(chunk.chunkKey);
			return true;
		}
	}

	public boolean canSave()
	{
		return !this.world.savingDisabled;
	}

	@Override
	public String getName()
	{
		return "ServerChunkCache: " + this.chunks.size() + " Drop: " + this.unloadQueue.size();
	}

	public List<BiomeMeta> a(EnumCreatureType enumcreaturetype, BlockPosition blockposition)
	{
		return this.chunkGenerator.getMobsFor(enumcreaturetype, blockposition);
	}

	@Nullable
	public BlockPosition a(World world, String s, BlockPosition blockposition, boolean flag)
	{
		return this.chunkGenerator.findNearestMapFeature(world, s, blockposition, flag);
	}

	public boolean a(World world, String s, BlockPosition blockposition)
	{
		return this.chunkGenerator.a(world, s, blockposition);
	}

	public int chunkCount()
	{
		return this.chunks.size();
	}

	public boolean isLoaded(int i, int j)
	{
		return this.chunks.containsKey(ChunkCoordIntPair.a(i, j));
	}

	@Override
	public boolean e(int i, int j)
	{
		return this.chunks.containsKey(ChunkCoordIntPair.a(i, j)) || this.chunkLoader.chunkExists(i, j);
	}
}
