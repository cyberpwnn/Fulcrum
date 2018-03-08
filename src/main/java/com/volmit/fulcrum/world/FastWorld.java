package com.volmit.fulcrum.world;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.volmit.fulcrum.data.cluster.DataCluster;

public interface FastWorld extends World
{
	public DataCluster pull(String node, Block block);

	public DataCluster pull(String node, Chunk chunk);

	public DataCluster pull(String node);

	public void push(String node, DataCluster cc, Block block);

	public void push(String node, DataCluster cc, Chunk chunk);

	public void push(String node, DataCluster cc);

	public FastChunk getFastChunkAt(int x, int z);

	public FastChunk[] getLoadedFastChunks();

	public FastBlock getFastBlockAt(int x, int y, int z);

	public FastBlock getFastBlockAt(Location location);

	public void lockState(String node, Block block);

	public void lockState(String node, int x, int y, int z);

	public void lockState(String node, Location location);

	public void drop(String node);

	public void drop(String node, Block block);

	public void drop(String node, Chunk chunk);
}
