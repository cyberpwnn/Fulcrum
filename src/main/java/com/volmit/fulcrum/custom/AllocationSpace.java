package com.volmit.fulcrum.custom;

import org.bukkit.Material;

import com.volmit.dumpster.F;
import com.volmit.dumpster.GList;
import com.volmit.dumpster.GMap;
import com.volmit.dumpster.JSONObject;

public class AllocationSpace
{
	private GMap<Material, PredicateGenerator> normalAllocations;
	private GMap<Material, PredicateGenerator> shadedAllocations;
	private GMap<String, String> normalModelTextures;
	private GMap<String, String[]> shadedModelTextures;
	private GMap<String, Integer> superIds;
	private GMap<Material, String> mattx;
	private GList<Material> iorda;
	private GList<Material> iordb;
	private boolean minify;
	private boolean cyclic;
	private GList<Material> iva;
	private GList<Material> ivb;

	public AllocationSpace(boolean minify, boolean cyclic)
	{
		mattx = new GMap<Material, String>();
		normalModelTextures = new GMap<String, String>();
		shadedModelTextures = new GMap<String, String[]>();
		normalAllocations = new GMap<Material, PredicateGenerator>();
		shadedAllocations = new GMap<Material, PredicateGenerator>();
		superIds = new GMap<String, Integer>();
		iorda = new GList<Material>();
		iordb = new GList<Material>();
		iva = new GList<Material>();
		ivb = new GList<Material>();
		this.minify = minify;
		this.cyclic = cyclic;
	}

	public String getNormalSuperModelName(Material m)
	{
		return normalAllocations.get(m).modelSuperName();
	}

	public String getShadedSuperModelName(Material m)
	{
		return shadedAllocations.get(m).modelSuperName();
	}

	public String getNormalModelName(Material m)
	{
		return normalAllocations.get(m).getModel();
	}

	public String getShadedModelName(Material m)
	{
		return shadedAllocations.get(m).getModel();
	}

	public JSONObject[] generateNormalModel(Material mat)
	{
		normalAllocations.get(mat).generate();
		return new JSONObject[] {normalAllocations.get(mat).generateModel(normalModelTextures.get(normalAllocations.get(mat).getModel())), normalAllocations.get(mat).getParenter()};
	}

	public JSONObject[] generateShadedModel(Material mat)
	{
		shadedAllocations.get(mat).generate();
		return new JSONObject[] {shadedAllocations.get(mat).generateModel(shadedModelTextures.get(shadedAllocations.get(mat).getModel())[0], shadedModelTextures.get(shadedAllocations.get(mat).getModel())[1]), shadedAllocations.get(mat).getParenter()};
	}

	private GList<Material> usableNormals()
	{
		GList<Material> u = new GList<Material>(iorda.copy());

		for(Material i : iorda)
		{
			if(normalAllocations.get(i).isFull())
			{
				u.remove(i);
			}
		}

		return u;
	}

	private GList<Material> usableShaded()
	{
		GList<Material> u = new GList<Material>(iordb.copy());

		for(Material i : iordb)
		{
			if(shadedAllocations.get(i).isFull())
			{
				u.remove(i);
			}
		}

		return u;
	}

	private Material nextNormal()
	{
		if(iva.isEmpty())
		{
			iva = new GList<Material>(usableNormals().copy());
		}

		return iva.pop();
	}

	private Material nextShaded()
	{
		if(ivb.isEmpty())
		{
			ivb = new GList<Material>(usableShaded().copy());
		}

		return ivb.pop();
	}

	public AllocatedNode allocateNormal(String model)
	{
		if(usableNormals().isEmpty())
		{
			return null;
		}

		Material i = cyclic ? nextNormal() : usableNormals().get(0);
		int id = normalAllocations.get(i).register(model);
		superIds.put(model, superIds.size() + 100);
		return new AllocatedNode(i, id, superIds.get(model));
	}

	public AllocatedNode allocateShaded(String model)
	{
		if(usableShaded().isEmpty())
		{
			return null;
		}

		Material i = cyclic ? nextShaded() : usableShaded().get(0);
		int id = shadedAllocations.get(i).register(model);
		superIds.put(model, superIds.size() + 100);
		return new AllocatedNode(i, id, superIds.get(model));
	}

	public int getNormalCapacity()
	{
		int f = 0;

		for(Material i : normalAllocations.k())
		{
			f += normalAllocations.get(i).getMax();
		}

		return f;
	}

	public int getShadedCapacity()
	{
		int f = 0;

		for(Material i : shadedAllocations.k())
		{
			f += shadedAllocations.get(i).getMax();
		}

		return f;
	}

	public int getNormalUse()
	{
		int f = 0;

		for(Material i : normalAllocations.k())
		{
			f += normalAllocations.get(i).getUse();
		}

		return f;
	}

	public int getShadedUse()
	{
		int f = 0;

		for(Material i : shadedAllocations.k())
		{
			f += shadedAllocations.get(i).getUse();
		}

		return f;
	}

	public void sacrificeNormal(Material type, String model, String texture) throws UnsupportedOperationException
	{
		normalAllocations.put(type, new PredicateGenerator(type, "item/" + model, minify));
		normalModelTextures.put("item/" + model, "items/" + texture);
		mattx.put(type, model);
		iorda.add(type);
	}

	public void sacrificeShaded(Material type, String model, String texture, String texture2) throws UnsupportedOperationException
	{
		shadedAllocations.put(type, new PredicateGenerator(type, "item/" + model, minify));
		shadedModelTextures.put("item/" + model, new String[] {"items/" + texture, "items/" + texture2});
		mattx.put(type, model);
		iordb.add(type);
	}

	public String getNameForMaterial(Material m)
	{
		return mattx.get(m);
	}

	public GMap<Material, PredicateGenerator> getNormalAllocations()
	{
		return normalAllocations;
	}

	public GMap<Material, PredicateGenerator> getShadedAllocations()
	{
		return shadedAllocations;
	}

	public GMap<String, String> getNormalModelTextures()
	{
		return normalModelTextures;
	}

	public GMap<String, String[]> getShadedModelTextures()
	{
		return shadedModelTextures;
	}

	public GMap<String, Integer> getSuperIds()
	{
		return superIds;
	}

	public GMap<Material, String> getMattx()
	{
		return mattx;
	}

	public GList<Material> getIorda()
	{
		return iorda;
	}

	public GList<Material> getIordb()
	{
		return iordb;
	}

	@Override
	public String toString()
	{
		String f = "";

		f += "Normal: " + F.f(getNormalUse()) + " of " + F.f(getNormalCapacity()) + " across " + getNormalAllocations().k().size() + " items (" + F.pc((double) getNormalUse() / (double) getNormalCapacity(), 1) + " use)" + "\n";
		f += "Shaded: " + F.f(getShadedUse()) + " of " + F.f(getShadedCapacity()) + " across " + getShadedAllocations().k().size() + " items (" + F.pc((double) getShadedUse() / (double) getShadedCapacity(), 1) + " use)" + "";

		return f;
	}
}
