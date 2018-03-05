package com.volmit.fulcrum.world;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.volmit.fulcrum.data.cluster.DataCluster;

public interface FastWorld extends World
{
	public DataCluster readData(String node, Block block);

	public DataCluster readData(String node, Chunk chunk);

	public DataCluster readData(String node);

	public void writeData(String node, DataCluster cc, Block block);

	public void writeData(String node, DataCluster cc, Chunk chunk);

	public void writeData(String node, DataCluster cc);

	public boolean hasData(String node, Block block);

	public boolean hasData(String node, Chunk chunk);

	public boolean hasData(String node);
}
