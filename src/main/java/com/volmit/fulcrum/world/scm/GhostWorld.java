package com.volmit.fulcrum.world.scm;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;

import com.volmit.fulcrum.bukkit.BlockType;
import com.volmit.volume.lang.collections.GMap;

public class GhostWorld
{
	private GMap<Chunk, ChunkSnapshot> snap;

	public GhostWorld()
	{
		snap = new GMap<Chunk, ChunkSnapshot>();
	}

	public void drop(Chunk c)
	{
		snap.remove(c);
	}

	@SuppressWarnings("deprecation")
	public BlockType get(Location l)
	{
		Chunk c = l.getChunk();

		if(!snap.containsKey(c))
		{
			snap.put(c, l.getChunk().getChunkSnapshot(false, false, false));
		}

		ChunkSnapshot s = snap.get(c);
		int cxb = l.getBlockX() - ((l.getBlockX() >> 4) << 4);
		int czb = l.getBlockZ() - ((l.getBlockZ() >> 4) << 4);
		return new BlockType(s.getBlockType(cxb, l.getBlockY(), czb), (byte) s.getBlockData(cxb, l.getBlockY(), czb));
	}

	public int size()
	{
		return snap.size();
	}
}
