package com.volmit.fulcrum.world.scm;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.volmit.volume.lang.collections.GMap;

public interface IMappedVolume
{
	public VariableBlock getType(Vector v);

	public VariableBlock getType(Location l);

	public GMap<Vector, VariableBlock> getMapping();

	public GMap<Vector, Location> getRealizedMapping();

	public GMap<Location, Vector> getReverseRealizedMapping();
}
