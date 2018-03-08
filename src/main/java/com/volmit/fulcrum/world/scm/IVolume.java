package com.volmit.fulcrum.world.scm;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.volmit.fulcrum.bukkit.Dimension;
import com.volmit.fulcrum.bukkit.Direction;
import com.volmit.fulcrum.lang.GList;
import com.volmit.fulcrum.lang.GMap;

public interface IVolume
{
	public Dimension getDimension();

	public Dimension getCenter();

	public void set(int x, int y, int z, IVariableBlockType type);

	public void set(Vector v, IVariableBlockType type);

	public void clear(IVariableBlockType type);

	public GMap<Vector, IVariableBlockType> vectorize();

	public Iterator<Vector> iterator();

	public IVolume copy();

	public MappedVolume rotate(Direction from, Direction to);

	public Vector getNormal(GList<Vector> vectorizedList);

	public void apply(GMap<Vector, IVariableBlockType> vectorizedMap) throws IncorrectVolumeException;

	public void apply(IVariableBlockType[][][] mapping) throws IncorrectVolumeException;

	public void replace(IVariableBlockType from, IVariableBlockType to);

	public IVariableBlockType get(int x, int y, int z);

	public IVariableBlockType get(Vector v);

	public void place(Location location, IVariableChooser chooser);
}
