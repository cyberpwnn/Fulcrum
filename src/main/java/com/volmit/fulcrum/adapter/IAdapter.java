package com.volmit.fulcrum.adapter;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.volmit.fulcrum.bukkit.BlockType;
import com.volmit.fulcrum.lang.GList;

public interface IAdapter
{
	public void queueUpdate(Block b);

	public void pushPhysics();

	public boolean isPushingPhysics();

	public void popPhysics();

	public ItemStack getSkull(String uri) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException;

	public void applyPhysics(Block b);

	public void sendReload(Chunk c);

	public void sendReload(Chunk c, Player p);

	public void notifyEntity(Entity e);

	public void notifyEntity(Entity e, Player p);

	public void sendChunkSection(Chunk c, int bitmask, Player p);

	public void sendUnload(Chunk c);

	public void sendUnload(Chunk c, Player p);

	public int getBiomeId(Biome biome);

	public Biome getBiome(int id);

	public void setBiome(World world, int x, int z, Biome b);

	public BlockType getBlock(Location location);

	public void setBlock(Location l, BlockType m);

	public void makeDirty(Chunk c, int section);

	public void makeDirty(Chunk c);

	public void makeFullyDirty(Chunk c);

	public int getBitmask(Chunk c);

	public boolean[] getValidSections(Chunk c);

	public void makeDirty(Location l);

	public void sendBlockChange(Location l, BlockType t, Player player);

	public void sendMultiBlockChange(Chunk c, GList<Location> points) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException;

	public void sendChunkSection(Chunk c, int section);
}
