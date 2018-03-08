package com.volmit.fulcrum.world.scm;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.bukkit.BlockType;
import com.volmit.fulcrum.bukkit.Dimension;
import com.volmit.fulcrum.bukkit.Direction;
import com.volmit.fulcrum.lang.GList;
import com.volmit.fulcrum.lang.GMap;

public class MappedVolume implements IVolume
{
	private Dimension dim;
	private Dimension dimAr;
	private IVariableBlockType[][][] mapping;

	public MappedVolume(IVariableBlockType[][][] mapping)
	{
		this.mapping = mapping;
		this.dim = new Dimension(mapping.length, mapping[0].length, mapping[0][0].length);
		this.dimAr = new Dimension(dim.getWidth() - 1, dim.getHeight() - 1, dim.getDepth());
		clear(IVariableBlockType.anything());
	}

	public MappedVolume(Dimension dim)
	{
		this.dim = dim;
		this.dimAr = new Dimension(dim.getWidth() - 1, dim.getHeight() - 1, dim.getDepth());
		mapping = new IVariableBlockType[dim.getWidth()][dim.getHeight()][dim.getDepth()];
		clear(IVariableBlockType.anything());
	}

	@Override
	public Dimension getCenter()
	{
		return new Dimension(dim.getWidth() / 2, dim.getHeight() / 2, dim.getDepth() / 2);
	}

	@Override
	public Dimension getDimension()
	{
		return new Dimension(dim.getWidth(), dim.getHeight(), dim.getDepth());
	}

	@Override
	public void set(int x, int y, int z, IVariableBlockType type)
	{
		if(x > (dimAr.getWidth()) || y > (dimAr.getHeight()) || z > (dimAr.getDepth()) || x < 0 || y < 0 || z < 0)
		{
			return;
		}

		mapping[x][y][z] = type;
	}

	@Override
	public void clear(IVariableBlockType type)
	{
		for(int i = 0; i < getDimension().getWidth(); i++)
		{
			for(int j = 0; j < getDimension().getHeight(); j++)
			{
				for(int k = 0; k < getDimension().getDepth(); k++)
				{
					set(i, j, k, type);
				}
			}
		}
	}

	@Override
	public GMap<Vector, IVariableBlockType> vectorize()
	{
		GMap<Vector, IVariableBlockType> vx = new GMap<Vector, IVariableBlockType>();

		for(int i = 0; i < getDimension().getWidth(); i++)
		{
			for(int j = 0; j < getDimension().getHeight(); j++)
			{
				for(int k = 0; k < getDimension().getDepth(); k++)
				{
					vx.put(new Vector(i, j, k), mapping[i][j][k]);
				}
			}
		}

		return vx;
	}

	@Override
	public Iterator<Vector> iterator()
	{
		return vectorize().k().iterator();
	}

	@Override
	public IVolume copy()
	{
		IVariableBlockType[][][] mapping = new IVariableBlockType[dim.getWidth()][dim.getHeight()][dim.getDepth()];
		Iterator<Vector> it = iterator();

		while(it.hasNext())
		{
			Vector v = it.next();
			mapping[v.getBlockX()][v.getBlockY()][v.getBlockZ()] = this.mapping[v.getBlockX()][v.getBlockY()][v.getBlockZ()];
		}

		return new MappedVolume(mapping);
	}

	@Override
	public void apply(GMap<Vector, IVariableBlockType> vectorizedMap) throws IncorrectVolumeException
	{
		for(Vector i : vectorizedMap.k())
		{
			if(i.getBlockX() > dimAr.getWidth() || i.getBlockY() > dimAr.getHeight() || i.getBlockZ() > dimAr.getDepth())
			{
				throw new IncorrectVolumeException();
			}

			set(i, vectorizedMap.get(i));
		}
	}

	@Override
	public void apply(IVariableBlockType[][][] mapping) throws IncorrectVolumeException
	{
		if(mapping.length == dim.getWidth() && mapping[0].length == dim.getHeight() && mapping[0][0].length == dim.getDepth())
		{
			Iterator<Vector> it = iterator();

			while(it.hasNext())
			{
				Vector v = it.next();
				set(v, mapping[v.getBlockX()][v.getBlockY()][v.getBlockZ()]);
			}
		}
	}

	@Override
	public Vector getNormal(GList<Vector> vectorizedList)
	{
		int minx = Integer.MAX_VALUE;
		int miny = Integer.MAX_VALUE;
		int minz = Integer.MAX_VALUE;

		for(Vector i : vectorizedList)
		{
			if(i.getBlockX() < minx)
			{
				minx = i.getBlockX();
			}

			if(i.getBlockY() < miny)
			{
				miny = i.getBlockY();
			}

			if(i.getBlockZ() < minz)
			{
				minz = i.getBlockZ();
			}
		}

		return new Vector(minx < 0 ? -minx : 0, miny < 0 ? -miny : 0, minz < 0 ? -minz : 0);
	}

	@Override
	public MappedVolume rotate(Direction from, Direction to)
	{
		GMap<Vector, IVariableBlockType> a = vectorize();
		GMap<Vector, IVariableBlockType> b = new GMap<Vector, IVariableBlockType>();
		MappedVolume mv = new MappedVolume(dim);

		for(Vector i : a.k())
		{
			b.put(from.angle(i, to), a.get(i));
		}

		Vector c = getNormal(b.k());

		for(Vector i : b.k())
		{
			Vector d = i.clone().add(c);
			mv.set(d, b.get(i));
		}

		return mv;
	}

	@Override
	public void replace(IVariableBlockType from, IVariableBlockType to)
	{
		Iterator<Vector> it = iterator();

		while(it.hasNext())
		{
			Vector v = it.next();

			if(get(v).equals(from))
			{
				set(v, to);
			}
		}
	}

	@Override
	public void set(Vector v, IVariableBlockType type)
	{
		set(v.getBlockX(), v.getBlockY(), v.getBlockZ(), type);
	}

	@Override
	public IVariableBlockType get(int x, int y, int z)
	{
		return mapping[x][y][z];
	}

	@Override
	public IVariableBlockType get(Vector v)
	{
		return get(v.getBlockX(), v.getBlockY(), v.getBlockZ());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void place(Location location, IVariableChooser chooser)
	{
		for(Vector i : vectorize().k())
		{
			BlockType t = chooser.realize(get(i));
			Fulcrum.faster(location.clone().add(i).getBlock()).setTypeIdAndData(t.getMaterial().getId(), t.getData(), false);
		}
	}
}
