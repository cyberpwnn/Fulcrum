package com.volmit.fulcrum.world;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;

import com.volmit.fulcrum.data.cluster.DataCluster;

public class FastChunk12 implements FastChunk
{
	private Chunk c;

	public FastChunk12(Chunk c)
	{
		this.c = c;
	}

	@Override
	public int getX()
	{
		return c.getX();
	}

	@Override
	public int getZ()
	{
		return c.getZ();
	}

	@Override
	public FastWorld getWorld()
	{
		return new FastWorld12(c.getWorld());
	}

	@Override
	public FastBlock getBlock(int x, int y, int z)
	{
		return new FastBlock12(c.getBlock(x, y, z));
	}

	@Override
	public ChunkSnapshot getChunkSnapshot()
	{
		return c.getChunkSnapshot();
	}

	@Override
	public ChunkSnapshot getChunkSnapshot(boolean includeMaxblocky, boolean includeBiome, boolean includeBiomeTempRain)
	{
		return c.getChunkSnapshot(includeMaxblocky, includeBiome, includeBiomeTempRain);
	}

	@Override
	public Entity[] getEntities()
	{
		return c.getEntities();
	}

	@Override
	public BlockState[] getTileEntities()
	{
		return c.getTileEntities();
	}

	@Override
	public boolean isLoaded()
	{
		return c.isLoaded();
	}

	@Override
	public boolean load(boolean generate)
	{
		return c.load(generate);
	}

	@Override
	public boolean load()
	{
		return c.load();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean unload(boolean save, boolean safe)
	{
		return c.unload(save, safe);
	}

	@Override
	public boolean unload(boolean save)
	{
		return c.unload(save);
	}

	@Override
	public boolean unload()
	{
		return c.unload();
	}

	@Override
	public boolean isSlimeChunk()
	{
		return c.isSlimeChunk();
	}

	@Override
	public DataCluster pull(String node, Block block)
	{
		return getWorld().pull(node, block);
	}

	@Override
	public DataCluster pull(String node)
	{
		return getWorld().pull(node, this);
	}

	@Override
	public void push(String node, DataCluster cc, Block block)
	{
		getWorld().push(node, cc, block);
	}

	@Override
	public void push(String node, DataCluster cc)
	{
		getWorld().push(node, cc, this);
	}

	@Override
	public FastBlock getFastBlockAt(int x, int y, int z)
	{
		return new FastBlock12(c.getBlock(x, y, z));
	}

	@Override
	public FastWorld getFastWorld()
	{
		return new FastWorld12(c.getWorld());
	}

	@Override
	public void lockState(String node, int x, int y, int z)
	{
		lockState(node, c.getBlock(x, y, z));
	}

	@Override
	public void lockState(String node, Block block)
	{
		getFastWorld().lockState(node, block);
	}

	@Override
	public void drop(String node)
	{
		getFastWorld().drop(node, this);
	}
}
