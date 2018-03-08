package com.volmit.fulcrum.world;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.volmit.fulcrum.data.cluster.DataCluster;

public interface FastBlock extends Block
{
	public DataCluster pull(String node);

	public void push(String node, DataCluster cc);

	public void setTexture(BlockFace face, String texture);

	public void removeTexture(BlockFace face);

	public void setTextures(String texture);

	public void removeTextures();

	public FastWorld getFastWorld();

	public FastChunk getFastChunk();

	public void lockBlockState(String node);

	public void drop(String node);
}
