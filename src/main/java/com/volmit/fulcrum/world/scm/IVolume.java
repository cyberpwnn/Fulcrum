package com.volmit.fulcrum.world.scm;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;

import com.volmit.fulcrum.bukkit.Dimension;

public interface IVolume
{
	public PermutationType getPermutationType();

	public void setPermutationType(PermutationType type);

	public void save(File f) throws IOException;

	public void load(File f) throws IOException;

	public IMappedVolume match(Location location, SnappedWorld w);

	public void compile();

	public void place(Location origin);

	public VariableSchematic getVariableSchematic();

	public VectorSchematic getVectorSchematic();

	public Dimension getDimension();
}
