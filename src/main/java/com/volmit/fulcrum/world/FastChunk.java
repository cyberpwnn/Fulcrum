package com.volmit.fulcrum.world;

import org.bukkit.Chunk;
import org.bukkit.block.Block;

import com.volmit.fulcrum.data.cluster.DataCluster;

public interface FastChunk extends Chunk
{
	public DataCluster pull(String node, Block block);

	public DataCluster pull(String node);

	public void push(String node, DataCluster cc, Block block);

	public void push(String node, DataCluster cc);

	public void lockState(String node, int x, int y, int z);

	public void lockState(String node, Block block);

	public void drop(String node);

	public FastBlock getFastBlockAt(int x, int y, int z);

	public FastWorld getFastWorld();
}
