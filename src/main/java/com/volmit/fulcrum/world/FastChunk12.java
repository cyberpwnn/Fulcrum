package com.volmit.fulcrum.world;

import java.io.File;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;

import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.data.cluster.DataCluster;
import com.volmit.fulcrum.lang.GList;

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
	public DataCluster readData(String node, Block block)
	{
		return getWorld().readData(node, block);
	}

	@Override
	public DataCluster readData(String node)
	{
		return getWorld().readData(node, this);
	}

	@Override
	public void writeData(String node, DataCluster cc, Block block)
	{
		getWorld().writeData(node, cc, block);
	}

	@Override
	public void writeData(String node, DataCluster cc)
	{
		getWorld().writeData(node, cc, this);
	}

	@Override
	public boolean hasData(String node, Block block)
	{
		return getWorld().hasData(node, block);
	}

	@Override
	public boolean hasData(String node)
	{
		return getWorld().hasData(node, this);
	}

	private File getFileData()
	{
		return new File(getWorld().getWorldFolder(), "fulcrum");
	}

	private File getFileData(Chunk c)
	{
		return new File(new File(getFileData(), toMCATag(c)), c.getX() + "." + c.getZ());
	}

	private String toMCATag(Chunk c)
	{
		return (c.getX() >> 5) + "." + (c.getZ() >> 5);
	}

	@Override
	public GList<FastBlock> getDataBlocks()
	{
		GList<FastBlock> fb = new GList<FastBlock>();

		if(!getFileData(this).exists() && Fulcrum.blockCache.size() < 1)
		{
			return fb;
		}

		for(File i : Fulcrum.blockCache.files.v())
		{
			String[] f = i.getParentFile().getName().split("\\.");
			fb.add((FastBlock) getWorld().getBlockAt(Integer.valueOf(f[0]), Integer.valueOf(f[1]), Integer.valueOf(f[2])));
		}

		if(getFileData(this).exists())
		{
			for(File i : getFileData(this).listFiles())
			{
				if(i.isDirectory())
				{
					String[] f = i.getName().split("\\.");
					fb.add((FastBlock) getWorld().getBlockAt(Integer.valueOf(f[0]), Integer.valueOf(f[1]), Integer.valueOf(f[2])));
				}
			}
		}

		return fb;
	}
}
