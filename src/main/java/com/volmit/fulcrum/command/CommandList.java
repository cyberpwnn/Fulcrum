package com.volmit.fulcrum.command;

import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.custom.ContentManager;
import com.volmit.fulcrum.custom.CustomAdvancement;
import com.volmit.fulcrum.custom.CustomBlock;
import com.volmit.fulcrum.custom.CustomInventory;
import com.volmit.fulcrum.custom.CustomItem;
import com.volmit.fulcrum.custom.CustomSound;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;
import com.volmit.volume.lang.collections.GList;

public class CommandList extends PawnCommand
{
	public CommandList()
	{
		super("list", "ls", "l");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
	{
		GList<String> ty = new GList<String>(args);

		if(ty.isEmpty())
		{
			ty.add("block");
			ty.add("item");
			ty.add("sound");
			ty.add("inventory");
			ty.add("advancement");
		}

		for(String v : ty)
		{
			if(v.toLowerCase().startsWith("block"))
			{
				for(CustomBlock i : ContentManager.getBlocks())
				{
					sender.sendMessage("BLOCK: " + i.getId());
				}
			}

			else if(v.toLowerCase().startsWith("item"))
			{
				for(CustomItem i : ContentManager.getItems())
				{
					sender.sendMessage("ITEM: " + i.getId());
				}
			}

			else if(v.toLowerCase().startsWith("adv"))
			{
				for(CustomAdvancement i : Fulcrum.contentRegistry.getAdvancements())
				{
					sender.sendMessage("ADV: " + i.getId());
				}
			}

			else if(v.toLowerCase().startsWith("inv"))
			{
				for(CustomInventory i : ContentManager.getInventories())
				{
					sender.sendMessage("INV: " + i.getId());
				}
			}

			else if(v.toLowerCase().startsWith("sound"))
			{
				for(CustomSound i : Fulcrum.contentRegistry.getSounds())
				{
					sender.sendMessage("SOUND: " + i.getNode());
				}
			}

			else
			{
				sender.sendMessage("Unreconized Category: " + v + " use /fu ty");
			}
		}

		return true;
	}
}
