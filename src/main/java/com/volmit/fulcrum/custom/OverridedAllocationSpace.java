package com.volmit.fulcrum.custom;

import org.bukkit.DyeColor;
import org.bukkit.Material;

import com.volmit.dumpster.F;
import com.volmit.dumpster.GList;
import com.volmit.dumpster.GMap;
import com.volmit.fulcrum.bukkit.BlockType;

public class OverridedAllocationSpace
{
	private GMap<BlockType, CustomBlock> allocated;
	private GList<BlockType> unallocatedNormals;
	private GList<BlockType> unallocatedAlphas;
	private GList<BlockType> pn;
	private GList<BlockType> pa;

	public OverridedAllocationSpace()
	{
		allocated = new GMap<BlockType, CustomBlock>();
		unallocatedNormals = new GList<BlockType>();
		unallocatedAlphas = new GList<BlockType>();
		pn = new GList<BlockType>();
		pa = new GList<BlockType>();
	}

	public BlockType allocate(CustomBlock block)
	{
		BlockType b = null;

		if(block.getBlockRenderType().equals(BlockRenderType.ALPHA) && !pa.isEmpty())
		{
			b = pa.pop();
		}

		else if(block.getBlockRenderType().equals(BlockRenderType.NORMAL) && !pn.isEmpty())
		{
			b = pn.pop();
		}

		if(b == null)
		{
			System.out.println("  WARNING: NO SUITABLE BLOCKS TO OVERRIDE FOR " + block.getBlockRenderType() + " BY " + block.getId());
		}

		else
		{
			allocated.put(b, block);
		}

		return b;
	}

	public void sacrificeNormal(BlockType type)
	{
		unallocatedNormals.add(type);
		pn.add(type);
	}

	public void sacrificeAlpha(BlockType type)
	{
		unallocatedAlphas.add(type);
		pa.add(type);
	}

	public void sacrificeNormal(Material mat, int from, int to)
	{
		for(int i = from; i <= to; i++)
		{
			sacrificeNormal(new BlockType(mat, (byte) i));
		}
	}

	public void sacrificeAlpha(Material mat, int from, int to)
	{
		for(int i = from; i <= to; i++)
		{
			sacrificeAlpha(new BlockType(mat, (byte) i));
		}
	}

	public void sacrificeNormal(Material mat, int to)
	{
		sacrificeNormal(mat, 0, to);
	}

	public void sacrificeAlpha(Material mat, int to)
	{
		sacrificeAlpha(mat, 0, to);
	}

	public GMap<BlockType, CustomBlock> getAllocated()
	{
		return allocated;
	}

	public GList<BlockType> getUnallocatedNormals()
	{
		return unallocatedNormals;
	}

	public GList<BlockType> getUnallocatedAlphas()
	{
		return unallocatedAlphas;
	}

	public int getNormalUse()
	{
		return unallocatedNormals.size() - pn.size();
	}

	public int getAlphaUse()
	{
		return unallocatedAlphas.size() - pa.size();
	}

	@Override
	public String toString()
	{
		String f = "";

		f += "Block Normal: " + F.f(getNormalUse()) + " of " + F.f(getUnallocatedNormals().size()) + " (" + F.pc((double) getNormalUse() / (double) getUnallocatedNormals().size(), 1) + " use)" + "\n";
		f += "Block Alpha: " + F.f(getAlphaUse()) + " of " + F.f(getUnallocatedAlphas().size()) + " (" + F.pc((double) getAlphaUse() / (double) getUnallocatedAlphas().size(), 1) + " use)" + "";

		return f;
	}

	@SuppressWarnings("deprecation")
	public String adapt(BlockType b)
	{
		if(b.getMaterial().equals(Material.WOOL) || b.getMaterial().equals(Material.STAINED_CLAY) || b.getMaterial().equals(Material.CONCRETE) || b.getMaterial().equals(Material.CONCRETE_POWDER))
		{
			for(DyeColor i : DyeColor.values())
			{
				if(i.getWoolData() == b.getData())
				{
					if(b.getMaterial().equals(Material.STAINED_CLAY))
					{
						return "hardened_clay_stained_" + i.name().toLowerCase();
					}

					if(b.getMaterial().equals(Material.STAINED_GLASS))
					{
						return "glass_" + i.name().toLowerCase();
					}

					if(b.getMaterial().equals(Material.WOOL))
					{
						return "wool_colored_" + i.name().toLowerCase();
					}

					return b.getMaterial().name().toLowerCase() + "_" + i.name().toLowerCase();
				}
			}
		}

		return b.getMaterial().name().toLowerCase();
	}
}
