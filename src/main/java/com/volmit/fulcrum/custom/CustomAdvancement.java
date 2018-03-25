package com.volmit.fulcrum.custom;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.volmit.dumpster.JSONObject;
import com.volmit.fulcrum.Fulcrum;

import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.MinecraftKey;

public class CustomAdvancement implements ICustom
{
	private Advancement a;
	private FrameType frameType;
	private ItemStack icon;
	private String title;
	private String description;
	private String id;
	private String parent;
	private String background;
	private boolean announce;
	private boolean toast;
	private boolean hidden;

	public CustomAdvancement(String id)
	{
		this.id = id;
		frameType = FrameType.TASK;
		icon = new ItemStack(Material.PORK);
		title = "A Title";
		description = "A Description";
		parent = null;
		background = "minecraft:textures/gui/advancements/backgrounds/stone.png";
		announce = true;
		toast = true;
		hidden = false;
	}

	public void grant(Player p)
	{
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement grant " + p.getName() + " only fulcrum:" + getId());
	}

	public void revoke(Player p)
	{
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement revoke " + p.getName() + " only fulcrum:" + getId());
	}

	@SuppressWarnings("deprecation")
	public void load()
	{
		try
		{
			System.out.println("Loading Advancement " + getKey().toString());
			a = Bukkit.getUnsafe().loadAdvancement(getKey(), toJSON().toString(0));
		}

		catch(IllegalArgumentException e)
		{
			try
			{
				System.out.println(getKey().toString() + " already exists... Loading?");
				a = Bukkit.getAdvancement(getKey());
			}

			catch(Exception eee)
			{
				System.out.println("Well shit that diddnt fucking work. i give up.");
				eee.printStackTrace();
			}
		}
	}

	public NamespacedKey getKey()
	{
		return new NamespacedKey(Fulcrum.instance, id);
	}

	public JSONObject toJSON()
	{
		JSONObject o = new JSONObject();
		JSONObject display = new JSONObject();
		display.put("icon", getIconFor(icon));
		display.put("title", title);
		display.put("description", description);
		display.put("frame", frameType.toString());
		display.put("background", background);
		display.put("show_toast", toast);
		display.put("announce_to_chat", announce);
		display.put("hidden", hidden);
		o.put("display", display);

		JSONObject crit = new JSONObject();
		JSONObject imp = new JSONObject();
		imp.put("trigger", "minecraft:impossible");
		crit.put("imp", imp);
		o.put("criteria", crit);

		if(parent != null)
		{
			o.put("parent", parent);
		}

		return o;
	}

	public JSONObject getIconFor(ItemStack stack)
	{
		final int check = Item.getId(CraftItemStack.asNMSCopy(stack).getItem());
		final MinecraftKey matching = Item.REGISTRY.keySet().stream().filter(key -> Item.getId(Item.REGISTRY.get(key)) == check).findFirst().orElse(null);
		String k = Objects.toString(matching, null);
		@SuppressWarnings("deprecation")
		byte data = stack.getData().getData();
		JSONObject o = new JSONObject();
		o.put("item", k);
		o.put("data", (int) data);

		return o;
	}

	public Advancement getA()
	{
		return a;
	}

	public void setA(Advancement a)
	{
		this.a = a;
	}

	public FrameType getFrameType()
	{
		return frameType;
	}

	public void setFrameType(FrameType frameType)
	{
		this.frameType = frameType;
	}

	public ItemStack getIcon()
	{
		return icon;
	}

	public void setIcon(ItemStack icon)
	{
		this.icon = icon;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getParent()
	{
		return parent;
	}

	public void setParent(String parent)
	{
		this.parent = parent;
	}

	public void setParent(CustomAdvancement parent)
	{
		this.parent = parent.getId();
	}

	public String getBackground()
	{
		return background;
	}

	public void setBackground(String background)
	{
		this.background = background;
	}

	public boolean isAnnounce()
	{
		return announce;
	}

	public void setAnnounce(boolean announce)
	{
		this.announce = announce;
	}

	public boolean isToast()
	{
		return toast;
	}

	public void setToast(boolean toast)
	{
		this.toast = toast;
	}

	public boolean isHidden()
	{
		return hidden;
	}

	public void setHidden(boolean hidden)
	{
		this.hidden = hidden;
	}
}
