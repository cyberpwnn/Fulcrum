package com.volmit.fulcrum.world;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.volmit.fulcrum.data.cluster.DataCluster;

public interface FastBlock extends Block
{
	public DataCluster readData(String node);

	public void writeData(String node, DataCluster cc);

	public boolean hasData(String node);

	public void setTexture(BlockFace face, String texture);

	public void removeTexture(BlockFace face);

	public void setTextures(String texture);

	public void removeTextures();
}
