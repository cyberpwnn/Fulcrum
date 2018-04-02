package com.volmit.fulcrum.custom;

import java.util.UUID;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.volmit.dumpster.GSet;
import com.volmit.fulcrum.Fulcrum;

public class ManagedInventory implements Listener
{
	private CustomInventory type;
	private Inventory inv;
	private String name;
	private GSet<Integer> slots;

	public ManagedInventory(CustomInventory type)
	{
		this.type = type;
		name = UUID.randomUUID().toString().replaceAll("-", "");
		inv = ContentManager.createInventory(type);
		slots = new GSet<Integer>();
		Fulcrum.register(this);
	}

	public void supportSlot(int slot)
	{
		if(ContentManager.isRestrictedSlot(slot))
		{
			return;
		}

		slots.add(slot);
	}

	private boolean is(Inventory v)
	{
		return v.getName().equals(name);
	}

	public void destroy()
	{
		Fulcrum.unregister(this);

		for(HumanEntity i : inv.getViewers())
		{
			i.closeInventory();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(InventoryClickEvent e)
	{
		if(e.getView().getTopInventory() != null && is(e.getView().getTopInventory()) && e.getClickedInventory().equals(e.getView().getTopInventory()))
		{
			int slot = e.getSlot();

			if(!slots.contains(slot))
			{
				e.setCancelled(true);
			}
		}
	}

	public CustomInventory getType()
	{
		return type;
	}

	public void setType(CustomInventory type)
	{
		this.type = type;
	}

	public Inventory getInv()
	{
		return inv;
	}

	public void setInv(Inventory inv)
	{
		this.inv = inv;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public GSet<Integer> getSlots()
	{
		return slots;
	}

	public void setSlots(GSet<Integer> slots)
	{
		this.slots = slots;
	}
}
