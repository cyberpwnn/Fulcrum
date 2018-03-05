package com.volmit.fulcrum.world;

import org.bukkit.block.Block;

import com.volmit.fulcrum.data.cluster.DataCluster;

public interface FastBlock extends Block
{
	public DataCluster readData(String node);

	public void writeData(String node, DataCluster cc);

	public boolean hasData(String node);
}
