package com.volmit.fulcrum.custom;

import org.bukkit.Material;

public class AllocatedNode
{
	private Material mat;
	private int id;
	private int superid;

	public AllocatedNode(Material mat, int id, int superid)
	{
		this.mat = mat;
		this.id = id;
		this.superid = superid;
	}

	public Material getMaterial()
	{
		return mat;
	}

	public int getId()
	{
		return id;
	}

	public Material getMat()
	{
		return mat;
	}

	public void setMat(Material mat)
	{
		this.mat = mat;
	}

	public int getSuperid()
	{
		return superid;
	}

	public void setSuperid(int superid)
	{
		this.superid = superid;
	}

	public void setId(int id)
	{
		this.id = id;
	}
}
