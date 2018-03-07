package com.volmit.fulcrum.world;

import org.bukkit.Chunk;
import org.bukkit.block.Block;

import com.volmit.fulcrum.data.cluster.DataCluster;
import com.volmit.fulcrum.lang.GList;

public interface FastChunk extends Chunk
{
	public DataCluster readData(String node, Block block);

	public DataCluster readData(String node);

	public void writeData(String node, DataCluster cc, Block block);

	public void writeData(String node, DataCluster cc);

	public boolean hasData(String node, Block block);

	public GList<FastBlock> getDataBlocks();

	public boolean hasData(String node);
}
