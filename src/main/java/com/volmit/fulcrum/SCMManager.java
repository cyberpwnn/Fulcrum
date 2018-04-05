package com.volmit.fulcrum;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.volmit.dumpster.F;
import com.volmit.dumpster.FinalInteger;
import com.volmit.dumpster.GBiset;
import com.volmit.dumpster.GList;
import com.volmit.dumpster.GMap;
import com.volmit.fulcrum.bukkit.A;
import com.volmit.fulcrum.bukkit.BlockType;
import com.volmit.fulcrum.bukkit.Cuboid;
import com.volmit.fulcrum.bukkit.P;
import com.volmit.fulcrum.bukkit.ParticleEffect;
import com.volmit.fulcrum.bukkit.S;
import com.volmit.fulcrum.bukkit.TICK;
import com.volmit.fulcrum.bukkit.TaskLater;
import com.volmit.fulcrum.bukkit.W;
import com.volmit.fulcrum.custom.ContentManager;
import com.volmit.fulcrum.custom.CustomBlock;
import com.volmit.fulcrum.event.VolumeConstructEvent;
import com.volmit.fulcrum.lang.C;
import com.volmit.fulcrum.lang.MaterialBlock;
import com.volmit.fulcrum.sfx.Audio;
import com.volmit.fulcrum.vfx.particle.ParticleRedstone;
import com.volmit.fulcrum.world.scm.IMappedVolume;
import com.volmit.fulcrum.world.scm.IVolume;
import com.volmit.fulcrum.world.scm.PermutationType;
import com.volmit.fulcrum.world.scm.SCMVolume;

public class SCMManager implements Listener, CommandExecutor
{
	private GMap<String, IVolume> volumes;

	public SCMManager()
	{
		Fulcrum.register(this);
		Fulcrum.instance.getCommand("scm").setExecutor(this);
		volumes = new GMap<String, IVolume>();

		File gf = getSCMFolder();

		new TaskLater(5)
		{
			@Override
			public void run()
			{
				if(gf.exists())
				{
					for(File i : gf.listFiles())
					{
						try
						{
							IVolume v = new SCMVolume(i);
							volumes.put(i.getName().replace(".scmv", ""), v);
						}

						catch(IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		};
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(command.getName().equalsIgnoreCase("scm"))
		{
			if(args.length == 0)
			{
				sender.sendMessage("/scm wand");
				sender.sendMessage("/scm update");
				sender.sendMessage("/scm list");
				sender.sendMessage("/scm status");
				sender.sendMessage("/scm save <id>");
				sender.sendMessage("/scm set <custom block>");
				sender.sendMessage("/scm place <id>");
				sender.sendMessage("/scm delete <id>");
				return true;
			}

			else if(args[0].equalsIgnoreCase("wand"))
			{
				new Audio().s(Sound.BLOCK_END_PORTAL_FRAME_FILL).vp(1f, 0.5f).play((Player) sender);
				ItemStack is = new ItemStack(Material.IRON_AXE);
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

			else if(args[0].equalsIgnoreCase("list"))
			{
				sender.sendMessage("Listing " + volumes.size() + " SCM Volumes");

				for(String i : volumes.k())
				{
					sender.sendMessage(i);
				}
			}

			else if(args[0].equalsIgnoreCase("status"))
			{
				int s = 0;

				for(String i : volumes.k())
				{
					s += volumes.get(i).getVectorSchematic().getTypes().size();
				}

				sender.sendMessage("There are " + volumes.size() + " SCM Volumes");
				sender.sendMessage("Cached " + F.f(s) + " SCM Vector Maps");
			}

			else if(args[0].equalsIgnoreCase("update"))
			{
				volumes.clear();
				File gf = getSCMFolder();

				if(gf.exists())
				{
					for(File i : gf.listFiles())
					{
						sender.sendMessage("Loading " + i.getName());

						try
						{
							IVolume v = new SCMVolume(i);
							volumes.put(i.getName().replace(".scmv", ""), v);
						}

						catch(IOException e)
						{
							e.printStackTrace();
						}
					}
				}

				sender.sendMessage("Updated " + volumes.size() + " SCM Volumes");
			}

			else if(args.length >= 1)
			{
				if(args[0].equalsIgnoreCase("set"))
				{
					if(args.length == 2)
					{
						CustomBlock cb = ContentManager.getBlock(args[1]);

						if(cb != null)
						{
							Cuboid c = new Cuboid(getSelection((Player) sender)[0], getSelection((Player) sender)[1]);
							Iterator<Block> it = c.iterator();
							FinalInteger d = new FinalInteger(0);
							while(it.hasNext())
							{
								d.add(1);
								Block b = it.next();
								new TaskLater(d.get() / 100)
								{
									@Override
									public void run()
									{
										ContentManager.setBlock(b, cb);
									}
								};
							}

							new TaskLater((int) (c.volume() / 100.0))
							{
								@Override
								public void run()
								{
									sender.sendMessage("Done");
								}
							};
						}

						else
						{
							MaterialBlock mb = W.getMaterialBlock(args[1]);

							if(mb != null)
							{
								Cuboid c = new Cuboid(getSelection((Player) sender)[0], getSelection((Player) sender)[1]);
								Iterator<Block> it = c.iterator();
								Fulcrum.adapter.pushPhysics();

								while(it.hasNext())
								{
									Block b = it.next();
									Fulcrum.adapter.setBlock(b.getLocation(), new BlockType(mb.getMaterial(), mb.getData()));
								}

								Fulcrum.adapter.popPhysics();

								sender.sendMessage("Done");
							}
						}
					}

					else
					{
						sender.sendMessage("/scm set <BLOCK>");
					}
				}

				if(args[0].equalsIgnoreCase("delete"))
				{
					if(args.length == 2)
					{
						File gf = getSCMFile(args[1]);

						if(gf.exists())
						{
							gf.delete();
							volumes.remove(args[1]);
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
								IVolume vv = new SCMVolume(c, PermutationType.ANY_AXIS);
								vv.save(getSCMFile(args[1]));
								new Audio().s(Sound.BLOCK_ENCHANTMENT_TABLE_USE).vp(1f, 1.5f).play((Player) sender);
								volumes.put(args[1], vv);
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
						if(volumes.containsKey(args[1]))
						{
							Location lx = P.targetBlock(((Player) sender), 12);
							new Audio().s(Sound.BLOCK_ENCHANTMENT_TABLE_USE).vp(1f, 1.5f).play((Player) sender);
							volumes.get(args[1]).place(lx);
							sender.sendMessage("SCM Volume " + args[1] + " placed at target.");
						}

						else
						{
							sender.sendMessage("SCM Volume " + args[1] + " not found. (try /scm update");
						}
					}

					else
					{
						sender.sendMessage("/scm place <id>");
					}
				}
			}

			return true;
		}

		return false;
	}

	public File getSCMFolder()
	{
		return new File(new File(Fulcrum.instance.getDataFolder(), "scm"), "volumes");
	}

	public File getSCMFile(String name)
	{
		return new File(getSCMFolder(), name + ".scmv");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(BlockPlaceEvent e)
	{
		new A()
		{
			@Override
			public void run()
			{
				GBiset<String, IMappedVolume> s = doMatch(e.getBlock().getLocation());

				if(s != null)
				{
					new S()
					{
						@Override
						public void run()
						{
							Fulcrum.callEvent(new VolumeConstructEvent(e, volumes.get(s.getA()), s.getB(), s.getA()));
						}
					};
				}
			}
		};
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(BlockBreakEvent e)
	{
		new A()
		{
			@Override
			public void run()
			{
				for(Block i : W.blockFaces(e.getBlock()))
				{
					GBiset<String, IMappedVolume> s = doMatch(i.getLocation());

					if(s != null)
					{
						new S()
						{
							@Override
							public void run()
							{
								Fulcrum.callEvent(new VolumeConstructEvent(e, volumes.get(s.getA()), s.getB(), s.getA()));
							}
						};

						return;
					}
				}

				GBiset<String, IMappedVolume> s = doMatch(e.getBlock().getLocation());

				if(s != null)
				{
					new S()
					{
						@Override
						public void run()
						{
							Fulcrum.callEvent(new VolumeConstructEvent(e, volumes.get(s.getA()), s.getB(), s.getA()));
						}
					};
				}
			}
		};
	}

	public GBiset<String, IMappedVolume> doMatch(Location at)
	{
		GBiset<String, IMappedVolume> s = null;

		for(String i : volumes.k())
		{
			IVolume v = volumes.get(i);
			IMappedVolume m = v.match(at);

			if(m != null)
			{
				s = new GBiset<String, IMappedVolume>(i, m);
				break;
			}
		}

		return s;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void on(PlayerInteractEvent e)
	{
		if(e.getItem() == null)
		{
			return;
		}

		if(e.getItem().getType().equals(Material.IRON_AXE))
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

	public Location[] getSelection(Player p)
	{
		Location[] l = new Location[2];
		ItemStack is = p.getInventory().getItemInMainHand();
		if(is == null)
		{
			return null;
		}

		if(is.getType().equals(Material.IRON_AXE))
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

	public void onTick()
	{
		for(Player i : P.onlinePlayers())
		{
			if(i.getInventory().getItemInMainHand().getType().equals(Material.IRON_AXE))
			{
				Location[] d = getSelection(i);

				if(d != null)
				{
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

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(VolumeConstructEvent e)
	{
		if(e.getCause() instanceof BlockPlaceEvent)
		{
			((BlockPlaceEvent) e.getCause()).getPlayer().sendMessage("Constructed " + e.getVolumeName());
		}

		if(e.getCause() instanceof BlockBreakEvent)
		{
			((BlockBreakEvent) e.getCause()).getPlayer().sendMessage("Constructed " + e.getVolumeName());
		}

		new Audio().s(Sound.BLOCK_ENCHANTMENT_TABLE_USE).vp(5f, 1.5f).play(e.getMappedVolume().getReverseRealizedMapping().k().pickRandom());
	}
}
