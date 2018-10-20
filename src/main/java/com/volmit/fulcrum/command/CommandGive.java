package com.volmit.fulcrum.command;

import com.volmit.fulcrum.custom.ContentManager;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;

public class CommandGive extends PawnCommand
{
	public CommandGive()
	{
		super("give", "g", "i");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
	{
		if(!sender.isPlayer())
		{
			sender.sendMessage("Not a player dude.");
			return true;
		}

		if(args.length >= 1 && args[0].contains(":"))
		{
			String type = args[0].split(":")[0].toLowerCase();
			String id = args[0].split(":")[1];

			if(type.startsWith("block"))
			{
				sender.player().getInventory().addItem(ContentManager.getAny(id).getItem());
			}

			else if(type.startsWith("item"))
			{
				sender.player().getInventory().addItem(ContentManager.getItem(id).getItem());
			}

			else if(type.startsWith("inv"))
			{
				sender.player().openInventory(ContentManager.createInventory(id));
			}

			else if(type.startsWith("adv"))
			{
				ContentManager.getAdvancement(id).grant(sender.player());
			}

			else if(type.startsWith("sound"))
			{
				sender.player().playSound(sender.player().getLocation(), id, 1f, 1f);
			}
		}

		else
		{
			sender.sendMessage("/fu i <type>:<id>");
		}

		return true;
	}
}
