package com.volmit.fulcrum.custom;

import com.volmit.dumpster.GList;

public class Registrar
{
	private GList<ICustom> customs;

	public Registrar()
	{
		customs = new GList<ICustom>();
	}

	public void register(ICustom custom)
	{
		customs.add(custom);
	}

	public boolean connect(ContentRegistry r)
	{
		boolean m = false;

		for(ICustom i : customs.copy())
		{
			if(i instanceof MultiCustomSound)
			{
				for(CustomSound j : ((MultiCustomSound) i).getSounds().v())
				{
					register(j);
				}
			}
		}

		for(ICustom i : customs)
		{
			if(i instanceof CustomSound)
			{
				m = true;
				r.registerSound((CustomSound) i);
			}

			if(i instanceof CustomAdvancement)
			{
				r.registerAdvancement((CustomAdvancement) i);
			}

			if(i instanceof CustomBlock)
			{
				m = true;
				r.registerBlock((CustomBlock) i);
			}

			if(i instanceof CustomItem)
			{
				m = true;
				r.registerItem((CustomItem) i);
			}

			if(i instanceof CustomInventory)
			{
				m = true;
				r.registerInventory((CustomInventory) i);
			}
		}

		return m;
	}
}
