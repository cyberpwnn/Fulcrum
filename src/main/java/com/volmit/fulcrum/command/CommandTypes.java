package com.volmit.fulcrum.command;

import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;

public class CommandTypes extends PawnCommand
{
	public CommandTypes()
	{
		super("types", "t", "ty");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
	{
		sender.sendMessage("block:");
		sender.sendMessage("item:");
		sender.sendMessage("sound:");
		sender.sendMessage("inventory:");
		sender.sendMessage("advancement:");

		return true;
	}
}
