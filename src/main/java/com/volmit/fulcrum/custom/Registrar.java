package com.volmit.fulcrum.custom;

import java.net.URL;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.volmit.dumpster.GList;
import com.volmit.fulcrum.bukkit.R;

public class Registrar
{
	private GList<Plugin> adv;
	private GList<ICustom> customs;

	public Registrar()
	{
		adv = new GList<Plugin>();
		customs = new GList<ICustom>();
	}

	public void register(ICustom custom)
	{
		customs.add(custom);
	}

	public void registerAdvancements(Plugin p)
	{
		adv.add(p);
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

		for(Plugin i : adv)
		{
			URL u = R.getURL(i.getClass(), "/assets/advancements.yml");

			if(u == null)
			{
				System.out.println("  Unable to find advancements: /assets/advancements.yml in " + i.getName());
				continue;
			}

			FileConfiguration fu = new YamlConfiguration();

			try
			{
				fu.loadFromString(ContentRegistry.read(u));
			}

			catch(Exception e)
			{
				System.out.println("  Unable to load advancements: /assets/advancements.yml in " + i.getName());
				e.printStackTrace();
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
