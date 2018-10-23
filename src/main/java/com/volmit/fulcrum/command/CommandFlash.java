package com.volmit.fulcrum.command;

import com.volmit.fulcrum.Fulcrum;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;

public class CommandFlash extends PawnCommand
{
	public CommandFlash()
	{
		super("flash", "fl", "pull");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
	{
		if(!sender.isPlayer())
		{
			sender.sendMessage("FOR THE LAST TIME, YOU CANT USE RESOURCE PACKS BRO!");
			return true;
		}

		if(args.length == 1)
		{
			if(args[0].equalsIgnoreCase("origin"))
			{
				String rid = Fulcrum.contentRegistry.getRid();
				sender.sendMessage("Merging via " + rid);
				Fulcrum.adapter.sendResourcePackWeb(sender.player(), rid + ".zip");
			}

			else if(args[0].startsWith("v4:"))
			{
				String u = "http://192.168.1." + args[0].split("\\Q:\\E") + ":" + Fulcrum.server.getPort() + "/" + Fulcrum.contentRegistry.getRid() + ".zip";
				sender.sendMessage("Merging via " + u);
				Fulcrum.adapter.sendResourcePack(sender.player(), u);
			}

			else
			{
				sender.sendMessage("Sending pack " + args[0]);
				Fulcrum.adapter.sendResourcePack(sender.player(), args[1]);
			}
		}

		else
		{
			sender.sendMessage("/fu flash <url or origin>");
			sender.sendMessage("On Local Network:");
			sender.sendMessage("  flash v4:156 (meaning 192.168.1.156");
			sender.sendMessage("On Machine or WAN:");
			sender.sendMessage("  flash origin");
			sender.sendMessage("Some other pack: ");
			sender.sendMessage("  flash https://cdn.volmit.com/null.zip");
		}

		return true;
	}
}
