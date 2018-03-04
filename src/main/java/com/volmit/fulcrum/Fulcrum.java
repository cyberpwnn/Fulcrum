package com.volmit.fulcrum;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.volmit.fulcrum.adapter.Adapter12;
import com.volmit.fulcrum.adapter.IAdapter;
import com.volmit.fulcrum.bukkit.BlockType;
import com.volmit.fulcrum.bukkit.P;
import com.volmit.fulcrum.bukkit.TICK;
import com.volmit.fulcrum.bukkit.Task;

public class Fulcrum extends JavaPlugin implements CommandExecutor
{
	public static Fulcrum instance;
	private IAdapter adapter;

	@Override
	public void onLoad()
	{
		instance = this;
	}

	@Override
	public void onEnable()
	{
		getCommand("fulcrum").setExecutor(this);

		new Task(0)
		{
			@Override
			public void run()
			{
				onTick();
			}
		};

		adapter = new Adapter12();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(command.getName().equalsIgnoreCase("fulcrum"))
		{
			Player p = P.getAnyPlayer();
			Location ll = P.targetBlock(p, 332);

			if(args[0].equalsIgnoreCase("sing"))
			{
				adapter.setBlock(ll, new BlockType(Material.SPONGE));
				return true;
			}

			if(args[0].equalsIgnoreCase("mul"))
			{
				for(int i = 0; i < 4; i++)
				{
					for(int j = 0; j < 4; j++)
					{
						adapter.setBlock(ll.getChunk().getBlock(i, ll.getBlockY(), j).getLocation(), new BlockType(Material.SPONGE));
					}
				}

				return true;
			}

			if(args[0].equalsIgnoreCase("map"))
			{
				for(int i = 0; i < 16; i++)
				{
					for(int j = 0; j < 16; j++)
					{
						for(int k = 0; k < 256; k++)
						{
							if(ll.getChunk().getBlock(i, k, j).getType().isSolid())
							{
								adapter.setBlock(ll.getChunk().getBlock(i, k, j).getLocation(), new BlockType(Material.SPONGE));
							}
						}
					}
				}

				return true;
			}

			return true;
		}

		return false;
	}

	@Override
	public void onDisable()
	{

	}

	public void onTick()
	{
		TICK.tick++;
	}

	public int startTask(int delay, Runnable r)
	{
		return Bukkit.getScheduler().scheduleSyncDelayedTask(instance, r, delay);
	}

	public int startRepeatingTask(int delay, int interval, Runnable r)
	{
		return Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, r, delay, interval);
	}

	public void stopTask(int id)
	{
		Bukkit.getScheduler().cancelTask(id);
	}
}
