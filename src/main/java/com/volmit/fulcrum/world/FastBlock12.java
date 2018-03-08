package com.volmit.fulcrum.world;

import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.bukkit.BlockType;
import com.volmit.fulcrum.data.cluster.DataCluster;
import com.volmit.fulcrum.images.ImageBakery;

public class FastBlock12 implements FastBlock
{
	private Block b;

	public FastBlock12(Block b)
	{
		this.b = b;
	}

	@SuppressWarnings("deprecation")
	@Override
	public byte getData()
	{
		return b.getData();
	}

	@Override
	public Block getRelative(int modX, int modY, int modZ)
	{
		return b.getRelative(modX, modY, modZ);
	}

	@Override
	public Block getRelative(BlockFace face)
	{
		return b.getRelative(face);
	}

	@Override
	public Block getRelative(BlockFace face, int distance)
	{
		return b.getRelative(face, distance);
	}

	@Override
	public Material getType()
	{
		return b.getType();
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getTypeId()
	{
		return b.getTypeId();
	}

	@Override
	public byte getLightLevel()
	{
		return b.getLightLevel();
	}

	@Override
	public byte getLightFromSky()
	{
		return b.getLightFromSky();
	}

	@Override
	public byte getLightFromBlocks()
	{
		return b.getLightFromBlocks();
	}

	@Override
	public FastWorld getWorld()
	{
		return new FastWorld12(b.getWorld());
	}

	@Override
	public int getX()
	{
		return b.getX();
	}

	@Override
	public int getY()
	{
		return b.getY();
	}

	@Override
	public int getZ()
	{
		return b.getZ();
	}

	@Override
	public Location getLocation()
	{
		return b.getLocation();
	}

	@Override
	public Location getLocation(Location loc)
	{
		return b.getLocation(loc);
	}

	@Override
	public FastChunk getChunk()
	{
		return new FastChunk12(b.getChunk());
	}

	@Override
	public void setData(byte data)
	{
		setData(data, false);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setData(byte data, boolean applyPhysics)
	{
		if(applyPhysics)
		{
			b.setData(data, applyPhysics);
		}

		else
		{
			Fulcrum.adapter.setBlock(getLocation(), new BlockType(getType(), data));
		}
	}

	@Override
	public void setType(Material type)
	{
		setType(type, false);
	}

	@Override
	public void setType(Material type, boolean applyPhysics)
	{
		if(applyPhysics)
		{
			b.setType(type, applyPhysics);
		}

		else
		{
			Fulcrum.adapter.setBlock(getLocation(), new BlockType(type, getData()));
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean setTypeId(int type)
	{
		setType(Material.getMaterial(type));

		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean setTypeId(int type, boolean applyPhysics)
	{
		setType(Material.getMaterial(type), applyPhysics);

		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean setTypeIdAndData(int type, byte data, boolean applyPhysics)
	{
		if(applyPhysics)
		{
			return setTypeIdAndData(type, data, applyPhysics);
		}

		else
		{
			Fulcrum.adapter.setBlock(getLocation(), new BlockType(Material.getMaterial(type), data));
		}

		return true;
	}

	@Override
	public BlockFace getFace(Block block)
	{
		return b.getFace(block);
	}

	@Override
	public BlockState getState()
	{
		return b.getState();
	}

	@Override
	public Biome getBiome()
	{
		return b.getBiome();
	}

	@Override
	public void setBiome(Biome bio)
	{
		Fulcrum.adapter.setBiome(getWorld(), getX(), getZ(), bio);
	}

	@Override
	public boolean isBlockPowered()
	{
		return b.isBlockPowered();
	}

	@Override
	public boolean isBlockIndirectlyPowered()
	{
		return b.isBlockIndirectlyPowered();
	}

	@Override
	public boolean isBlockFacePowered(BlockFace face)
	{
		return b.isBlockFacePowered(face);
	}

	@Override
	public boolean isBlockFaceIndirectlyPowered(BlockFace face)
	{
		return b.isBlockFaceIndirectlyPowered(face);
	}

	@Override
	public int getBlockPower(BlockFace face)
	{
		return b.getBlockPower(face);
	}

	@Override
	public int getBlockPower()
	{
		return b.getBlockPower();
	}

	@Override
	public boolean isEmpty()
	{
		return b.isEmpty();
	}

	@Override
	public boolean isLiquid()
	{
		return b.isLiquid();
	}

	@Override
	public double getTemperature()
	{
		return b.getTemperature();
	}

	@Override
	public double getHumidity()
	{
		return b.getHumidity();
	}

	@Override
	public PistonMoveReaction getPistonMoveReaction()
	{
		return b.getPistonMoveReaction();
	}

	@Override
	public boolean breakNaturally()
	{
		return b.breakNaturally();
		// TODO faster
	}

	@Override
	public boolean breakNaturally(ItemStack tool)
	{
		return b.breakNaturally(tool);
		// TODO faster
	}

	@Override
	public Collection<ItemStack> getDrops()
	{
		return b.getDrops();
	}

	@Override
	public Collection<ItemStack> getDrops(ItemStack tool)
	{
		return b.getDrops(tool);
	}

	@Override
	public void setMetadata(String metadataKey, MetadataValue newMetadataValue)
	{
		b.setMetadata(metadataKey, newMetadataValue);
	}

	@Override
	public List<MetadataValue> getMetadata(String metadataKey)
	{
		return b.getMetadata(metadataKey);
	}

	@Override
	public boolean hasMetadata(String metadataKey)
	{
		return b.hasMetadata(metadataKey);
	}

	@Override
	public void removeMetadata(String metadataKey, Plugin owningPlugin)
	{
		b.removeMetadata(metadataKey, owningPlugin);
	}

	@Override
	public DataCluster pull(String node)
	{
		return getChunk().pull(node, this);
	}

	@Override
	public void push(String node, DataCluster cc)
	{
		getChunk().push(node, cc, this);
	}

	@Override
	public void setTexture(BlockFace face, String texture)
	{
		ImageBakery.setTexture(b, face, texture);
	}

	@Override
	public void removeTexture(BlockFace face)
	{
		ImageBakery.removeTexture(b, face);
	}

	@Override
	public void setTextures(String texture)
	{
		setTexture(BlockFace.NORTH, texture);
		setTexture(BlockFace.SOUTH, texture);
		setTexture(BlockFace.EAST, texture);
		setTexture(BlockFace.WEST, texture);
	}

	@Override
	public void removeTextures()
	{
		removeTexture(BlockFace.NORTH);
		removeTexture(BlockFace.SOUTH);
		removeTexture(BlockFace.EAST);
		removeTexture(BlockFace.WEST);
	}

	@Override
	public FastWorld getFastWorld()
	{
		return new FastWorld12(b.getWorld());
	}

	@Override
	public FastChunk getFastChunk()
	{
		return new FastChunk12(b.getChunk());
	}

	@Override
	public void lockBlockState(String node)
	{
		getFastWorld().lockState(node, this);
	}

	@Override
	public void drop(String node)
	{
		getFastWorld().drop(node, this);
	}
}
