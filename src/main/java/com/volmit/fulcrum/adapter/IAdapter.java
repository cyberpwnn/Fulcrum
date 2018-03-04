package com.volmit.fulcrum.adapter;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import com.volmit.fulcrum.bukkit.BlockType;
import com.volmit.fulcrum.lang.GList;

public interface IAdapter
{
	public int getBiomeId(Biome biome);

	public Biome getBiome(int id);

	public BlockType getBlock(Location location);

	public void setBlock(Location l, BlockType m);

	public void makeDirty(Chunk c, int section);

	public void makeDirty(Chunk c);

	public int getBitmask(Chunk c);

	public boolean[] getValidSections(Chunk c);

	public void makeDirty(Location l);

	public void sendBlockChange(Location l, BlockType t, Player player);

	public void sendMultiBlockChange(Chunk c, GList<Location> points) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException;

	public void sendChunkSection(Chunk c, int section);
}
