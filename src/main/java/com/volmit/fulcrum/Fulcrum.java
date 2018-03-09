package com.volmit.fulcrum;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.volmit.fulcrum.adapter.Adapter12;
import com.volmit.fulcrum.adapter.IAdapter;
import com.volmit.fulcrum.bukkit.Cuboid;
import com.volmit.fulcrum.bukkit.P;
import com.volmit.fulcrum.bukkit.ParticleEffect;
import com.volmit.fulcrum.bukkit.TICK;
import com.volmit.fulcrum.bukkit.Task;
import com.volmit.fulcrum.bukkit.TaskLater;
import com.volmit.fulcrum.images.ImageBakery;
import com.volmit.fulcrum.images.PluginUtil;
import com.volmit.fulcrum.lang.C;
import com.volmit.fulcrum.lang.GList;
import com.volmit.fulcrum.lang.GMap;
import com.volmit.fulcrum.lang.M;
import com.volmit.fulcrum.sfx.Audio;
import com.volmit.fulcrum.vfx.particle.ParticleEnchantmentTable;
import com.volmit.fulcrum.vfx.particle.ParticleExplosionLarge;
import com.volmit.fulcrum.vfx.particle.ParticleLava;
import com.volmit.fulcrum.vfx.particle.ParticlePortal;
import com.volmit.fulcrum.vfx.particle.ParticleRedstone;
import com.volmit.fulcrum.world.FastBlock;
import com.volmit.fulcrum.world.FastBlock12;
import com.volmit.fulcrum.world.FastChunk;
import com.volmit.fulcrum.world.FastChunk12;
import com.volmit.fulcrum.world.FastWorld;
import com.volmit.fulcrum.world.FastWorld12;
import com.volmit.fulcrum.world.MCACache;
import com.volmit.fulcrum.world.scm.PermutationType;
import com.volmit.fulcrum.world.scm.VariableSchematic;
import com.volmit.fulcrum.world.scm.VectorSchematic;

public class Fulcrum extends JavaPlugin implements CommandExecutor, Listener
{
	public static Fulcrum instance;
	public static IAdapter adapter;
	public static MCACache cache;
	public static long ms = M.ms();
	public static GMap<String, VectorSchematic> match;

	@Override
	public void onEnable()
	{
		instance = this;
		cache = new MCACache();
		adapter = new Adapter12();
		match = new GMap<String, VectorSchematic>();

		File gf = new File(new File(getDataFolder(), "scm"), "volumes");

		if(gf.exists())
		{
			for(File i : gf.listFiles())
			{
				try
				{
					VariableSchematic vv = new VariableSchematic(i);
					VectorSchematic vvx = vv.toVectorSchematic();
					vvx.setPermutationType(PermutationType.ANY_AXIS);
					match.put(i.getName().replace(".scmv", ""), vvx);
				}

				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		try
		{
			ImageBakery.scan(new File(getDataFolder().getParentFile(), PluginUtil.getPluginFileName(getName())), "fulcrum");
			System.out.println("Loaded images");
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}

		getServer().getPluginManager().registerEvents(this, this);
		getCommand("fulcrum").setExecutor(this);
		getCommand("scm").setExecutor(this);

		new Task(0)
		{
			@Override
			public void run()
			{
				onTick();
			}
		};
	}

	@Override
	public void onDisable()
	{
		flushCache();
	}

	public static int getCacheSize()
	{
		return cache.size();
	}

	public static void flushCache()
	{
		try
		{
			cache.flush();
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public static FastBlock faster(Block b)
	{
		return new FastBlock12(b);
	}

	public static FastChunk faster(Chunk c)
	{
		return new FastChunk12(c);
	}

	public static FastWorld faster(String world)
	{
		return new FastWorld12(Bukkit.getWorld(world));
	}

	public static FastWorld faster(World world)
	{
		return new FastWorld12(world);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(command.getName().equalsIgnoreCase("scm"))
		{
			if(args.length == 0)
			{
				sender.sendMessage("/scm wand");
				sender.sendMessage("/scm save <id>");
				sender.sendMessage("/scm place <id>");
				sender.sendMessage("/scm delete <id>");
				sender.sendMessage("/scm list");
				return true;
			}

			else if(args[0].equalsIgnoreCase("wand"))
			{
				new Audio().s(Sound.BLOCK_END_PORTAL_FRAME_FILL).vp(1f, 0.5f).play((Player) sender);
				ItemStack is = new ItemStack(Material.GOLD_HOE);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(C.YELLOW + "SCM Wand");
				Location ll = ((Player) sender).getLocation();
				GList<String> s = new GList<String>();
				s.add(C.AQUA + "A: " + ll.getWorld().getName() + "@" + ll.getBlockX() + "." + ll.getBlockY() + "." + ll.getBlockZ());
				s.add(C.AQUA + "B: " + ll.getWorld().getName() + "@" + ll.getBlockX() + "." + ll.getBlockY() + "." + ll.getBlockZ());
				im.setLore(s);
				is.setItemMeta(im);
				((Player) sender).getInventory().addItem(is);
			}

			else if(args.length >= 1)
			{
				if(args[0].equalsIgnoreCase("list"))
				{
					File gf = new File(new File(getDataFolder(), "scm"), "volumes");

					if(gf.exists())
					{
						sender.sendMessage("Listing " + gf.listFiles().length + " SCM Volumes");

						for(File i : gf.listFiles())
						{
							sender.sendMessage(i.getName());
						}
					}

					else
					{
						sender.sendMessage("Listing " + gf.listFiles().length + " SCM Volumes");
					}
				}

				else if(args[0].equalsIgnoreCase("delete"))
				{
					if(args.length == 2)
					{
						File gf = new File(new File(new File(getDataFolder(), "scm"), "volumes"), args[1] + ".scmv");

						if(gf.exists())
						{
							gf.delete();
							match.remove(args[1]);
							new Audio().s(Sound.BLOCK_FIRE_EXTINGUISH).vp(1f, 1.5f).play((Player) sender);
							sender.sendMessage(args[1] + " Deleted.");
						}

						else
						{
							sender.sendMessage(args[1] + " does not exist.");
						}
					}

					else
					{
						sender.sendMessage("/scm delete <id>");
					}
				}

				else if(args[0].equalsIgnoreCase("save"))
				{
					if(args.length == 2)
					{
						Location[] f = getSelection(((Player) sender));

						if(f != null)
						{
							Cuboid c = new Cuboid(f[0], f[1]);

							if(c.volume() > 8192)
							{
								sender.sendMessage("SCM Volume too large.");
								return true;
							}

							try
							{
								VariableSchematic vv = new VariableSchematic(new Location(f[0].getWorld(), c.getLowerX(), c.getLowerY(), c.getLowerZ()), c);
								vv.save(new File(new File(new File(getDataFolder(), "scm"), "volumes"), args[1] + ".scmv"));
								new Audio().s(Sound.BLOCK_ENCHANTMENT_TABLE_USE).vp(1f, 1.5f).play((Player) sender);
								VectorSchematic vvx = vv.toVectorSchematic();
								vvx.setPermutationType(PermutationType.ANY_AXIS);
								match.put(args[1], vvx);
							}

							catch(IOException e)
							{
								e.printStackTrace();
							}

							sender.sendMessage("SCM Volume saved as " + args[1]);
						}

						else
						{
							sender.sendMessage("Hold a wand with a selection first (/scm wand)");
						}

					}

					else
					{
						sender.sendMessage("/scm save <id>");
					}
				}

				else if(args[0].equalsIgnoreCase("place"))
				{
					if(args.length == 2)
					{
						Location lx = P.targetBlock(((Player) sender), 12);
						new Audio().s(Sound.BLOCK_ENCHANTMENT_TABLE_USE).vp(1f, 1.5f).play((Player) sender);

						try
						{
							new VariableSchematic(new File(new File(new File(getDataFolder(), "scm"), "volumes"), args[1] + ".scmv")).place(lx);
						}

						catch(IOException e)
						{
							e.printStackTrace();
						}

						sender.sendMessage("SCM Volume " + args[1] + " placed at target.");
					}
				}

				else
				{
					sender.sendMessage("/scm place <id>");
				}
			}
		}

		if(command.getName().equalsIgnoreCase("fulcrum"))

		{
			Player p = (Player) sender;
			Location l = P.targetBlock(p, 100);

			return true;
		}

		return false;
	}

	public Location[] getSelection(Player p)
	{
		Location[] l = new Location[2];
		ItemStack is = p.getInventory().getItemInMainHand();
		if(is == null)
		{
			return null;
		}

		if(is.getType().equals(Material.GOLD_HOE))
		{
			ItemMeta im = is.getItemMeta();

			if(im.getDisplayName().equals(C.YELLOW + "SCM Wand"))
			{
				String a = im.getLore().get(0).split(":")[1].trim();
				String b = im.getLore().get(1).split(":")[1].trim();
				l[0] = new Location(Bukkit.getWorld(a.split("@")[0]), Integer.valueOf(a.split("@")[1].split("\\.")[0]), Integer.valueOf(a.split("@")[1].split("\\.")[1]), Integer.valueOf(a.split("@")[1].split("\\.")[2]));
				l[1] = new Location(Bukkit.getWorld(b.split("@")[0]), Integer.valueOf(b.split("@")[1].split("\\.")[0]), Integer.valueOf(b.split("@")[1].split("\\.")[1]), Integer.valueOf(b.split("@")[1].split("\\.")[2]));
				return l;
			}
		}

		return null;
	}

	@EventHandler
	public void on(BlockPlaceEvent e)
	{
		for(String i : match.k())
		{
			GMap<Vector, Location> ff = match.get(i).match(e.getBlock().getLocation());

			if(ff != null)
			{
				int k = 0;
				for(Vector j : ff.k())
				{
					k++;

					new TaskLater(k)
					{
						@Override
						public void run()
						{
							Location lx = ff.get(j);
							new Audio().s(Sound.BLOCK_FURNACE_FIRE_CRACKLE).vp(4f, (float) (Math.random() * 2)).play(lx);
							for(Player i : P.onlinePlayers())
							{
								i.sendBlockChange(lx, Material.AIR, (byte) 0);
							}

							for(int f = 0; f < 60; f++)
							{
								new ParticleEnchantmentTable().setDirection(Vector.getRandom().subtract(Vector.getRandom())).setSpread(7).play(lx.clone().add(0.5, 0.5, 0.5));
								new ParticlePortal().setDirection(Vector.getRandom().subtract(Vector.getRandom())).setSpread(7).play(lx.clone().add(0.5, 0.5, 0.5));
							}
						}
					};
				}

				new TaskLater(30 + k)
				{
					@Override
					public void run()
					{
						new Audio().s(Sound.BLOCK_END_PORTAL_SPAWN).vp(1f, 1.5f).play(e.getBlock().getLocation());
						new Audio().s(Sound.BLOCK_END_PORTAL_SPAWN).vp(1f, 0.4f).play(e.getBlock().getLocation());

						for(Vector j : ff.k())
						{
							Location lx = ff.get(j);

							adapter.makeSectionDirty(lx);

							new ParticleExplosionLarge().play(lx.clone().add(Vector.getRandom().subtract(Vector.getRandom())));

							for(int kk = 0; kk < 5; kk++)
							{
								new ParticleLava().play(lx.clone().add(Vector.getRandom().subtract(Vector.getRandom())));
							}
						}

						e.getPlayer().sendMessage("Constructed " + i);
					}
				};

				break;
			}
		}
	}

	@EventHandler
	public void on(PlayerInteractEvent e)
	{
		if(e.getItem() == null)
		{
			return;
		}

		if(e.getItem().getType().equals(Material.GOLD_HOE))
		{
			e.setCancelled(true);

			if(e.getAction().equals(Action.LEFT_CLICK_BLOCK))
			{
				new Audio().s(Sound.ENTITY_ENDEREYE_DEATH).vp(1f, 1.5f).play(e.getPlayer());
				ParticleEffect.ENCHANTMENT_TABLE.display(2.15f, 40, e.getClickedBlock().getLocation().clone().add(0.5, 1, 0.5), 32);
				ParticleEffect.SWEEP_ATTACK.display(2.15f, 1, P.getHand(e.getPlayer()), 32);

				ItemStack is = e.getItem().clone();
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(C.YELLOW + "SCM Wand");
				Location ll = e.getClickedBlock().getLocation();
				GList<String> lx = new GList<String>(im.getLore());
				lx.set(0, C.AQUA + "A: " + ll.getWorld().getName() + "@" + ll.getBlockX() + "." + ll.getBlockY() + "." + ll.getBlockZ());
				im.setLore(lx);
				is.setItemMeta(im);
				e.getPlayer().getInventory().setItemInMainHand(is);
			}

			else if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			{
				new Audio().s(Sound.ENTITY_ENDEREYE_DEATH).vp(1f, 1.2f).play(e.getPlayer());
				ParticleEffect.ENCHANTMENT_TABLE.display(2.15f, 40, e.getClickedBlock().getLocation().clone().add(0.5, 0.5, 0.5), 32);
				ParticleEffect.SWEEP_ATTACK.display(2.15f, 1, P.getHand(e.getPlayer()), 32);
				ItemStack is = e.getItem().clone();
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(C.YELLOW + "SCM Wand");
				Location ll = e.getClickedBlock().getLocation();
				GList<String> lx = new GList<String>(im.getLore());
				lx.set(1, C.AQUA + "B: " + ll.getWorld().getName() + "@" + ll.getBlockX() + "." + ll.getBlockY() + "." + ll.getBlockZ());
				im.setLore(lx);
				is.setItemMeta(im);
				e.getPlayer().getInventory().setItemInMainHand(is);
			}
		}
	}

	public static void register(Listener l)
	{
		Bukkit.getPluginManager().registerEvents(l, Fulcrum.instance);
	}

	public static void unregister(Listener l)
	{
		HandlerList.unregisterAll(l);
	}

	public void onTick()
	{
		TICK.tick++;
		cache.tick();

		for(Player i : P.onlinePlayers())
		{
			if(i.getInventory().getItemInMainHand().getType().equals(Material.GOLD_HOE))
			{
				Location[] d = getSelection(i);

				if(d != null)
				{
					double ds = d[0].distanceSquared(d[1]);

					if(TICK.tick % 3 == 0)
					{
						ParticleEffect.CRIT_MAGIC.display(0.1f, 1, d[0].clone().add(0.5, 0.5, 0.5).clone().add(Vector.getRandom().subtract(Vector.getRandom()).normalize().clone().multiply(0.65)), i);
						ParticleEffect.CRIT.display(0.1f, 1, d[1].clone().add(0.5, 0.5, 0.5).clone().add(Vector.getRandom().subtract(Vector.getRandom()).normalize().clone().multiply(0.65)), i);

						if(!d[0].getWorld().equals(d[1].getWorld()))
						{
							return;
						}

						if(d[0].distanceSquared(d[1]) > 64 * 64)
						{
							return;
						}

						int minx = Math.min(d[0].getBlockX(), d[1].getBlockX());
						int miny = Math.min(d[0].getBlockY(), d[1].getBlockY());
						int minz = Math.min(d[0].getBlockZ(), d[1].getBlockZ());
						int maxx = Math.max(d[0].getBlockX(), d[1].getBlockX());
						int maxy = Math.max(d[0].getBlockY(), d[1].getBlockY());
						int maxz = Math.max(d[0].getBlockZ(), d[1].getBlockZ());

						for(double j = minx - 1; j < maxx + 1; j += 0.25)
						{
							for(double k = miny - 1; k < maxy + 1; k += 0.25)
							{
								for(double l = minz - 1; l < maxz + 1; l += 0.25)
								{
									boolean jj = j == minx || j == maxx;
									boolean kk = k == miny || k == maxy;
									boolean ll = l == minz || l == maxz;
									double aa = j;
									double bb = k;
									double cc = l;

									if((jj && kk) || (jj && ll) || (ll && kk))
									{
										Vector push = new Vector(0, 0, 0);

										if(j == minx)
										{
											push.add(new Vector(-0.55, 0, 0));
										}

										if(k == miny)
										{
											push.add(new Vector(0, -0.55, 0));
										}

										if(l == minz)
										{
											push.add(new Vector(0, 0, -0.55));
										}

										if(j == maxx)
										{
											push.add(new Vector(0.55, 0, 0));
										}

										if(k == maxy)
										{
											push.add(new Vector(0, 0.55, 0));
										}

										if(l == maxz)
										{
											push.add(new Vector(0, 0, 0.55));
										}

										Location lv = new Location(d[0].getWorld(), aa, bb, cc).clone().add(0.5, 0.5, 0.5).clone().add(push);
										int color = Color.getHSBColor((float) (0.5f + (Math.sin((aa + bb + cc + (TICK.tick / 2)) / 20f) / 2)), 1, 1).getRGB();
										new ParticleRedstone().setColor(new Color(color)).play(lv, i);
									}
								}
							}
						}
					}
				}
			}
		}
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
