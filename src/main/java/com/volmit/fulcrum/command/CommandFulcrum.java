package com.volmit.fulcrum.command;

import com.volmit.volume.bukkit.command.Command;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;
import com.volmit.volume.bukkit.util.text.C;

public class CommandFulcrum extends PawnCommand
{
	@Command
	public CommandFlash flash;

	@Command
	public CommandList list;

	@Command
	public CommandTypes types;

	@Command
	public CommandGive give;

	public CommandFulcrum()
	{
		super("fulcrum", "ful", "fu");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
	{
		sender.sendMessage(C.RED + "/fu " + C.WHITE + C.BOLD + "flash " + C.RESET + C.WHITE + " <url>|master" + C.GRAY + " Flash a pack to player.");
		sender.sendMessage(C.RED + "/fu " + C.WHITE + C.BOLD + "types " + C.RESET + C.GRAY + " List all registered types.");
		sender.sendMessage(C.RED + "/fu " + C.WHITE + C.BOLD + "list " + C.RESET + C.WHITE + "[types...]" + C.GRAY + " List all nodes");
		sender.sendMessage(C.RED + "/fu " + C.WHITE + C.BOLD + "give " + C.RESET + C.WHITE + "<type>:<id>" + C.GRAY + " Give node.");

		return true;
	}
}
