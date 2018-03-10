package com.volmit.fulcrum.world.scm;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;

import com.volmit.fulcrum.bukkit.BlockType;
import com.volmit.fulcrum.lang.GMap;

public class SnappedWorld
{
	private GMap<Chunk, ChunkSnapshot> snap;

	public SnappedWorld(World world)
	{
		snap = new GMap<Chunk, ChunkSnapshot>();
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
}
