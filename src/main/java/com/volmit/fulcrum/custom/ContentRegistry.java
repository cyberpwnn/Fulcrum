package com.volmit.fulcrum.custom;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.bukkit.P;
import com.volmit.fulcrum.bukkit.R;
import com.volmit.fulcrum.lang.F;
import com.volmit.fulcrum.lang.GList;
import com.volmit.fulcrum.lang.GMap;
import com.volmit.fulcrum.lang.GSet;
import com.volmit.fulcrum.lang.JSONArray;
import com.volmit.fulcrum.lang.JSONObject;
import com.volmit.fulcrum.resourcepack.ResourcePack;
import com.volmit.fulcrum.sfx.Audio;

public class ContentRegistry implements Listener
{
	private URL cube;
	private URL cubeAll;
	private URL cubeBottomTop;
	private URL cubeColumn;
	private URL cubeTop;
	private URL cubePedistal;
	private URL defaultModel;
	private URL defaultModelAll;
	private URL defaultModelBottomTop;
	private URL defaultModelColumn;
	private URL defaultModelTop;
	private URL defaultModelPedistal;
	private URL missingTexture;
	private URL defaultSounds;
	private URL newSpawner;
	private URL soundSilent;
	private String defaultModelContent;
	private String defaultModelContentAll;
	private String defaultModelContentTop;
	private String defaultModelContentBottomTop;
	private String defaultModelContentColumn;
	private String defaultModelContentPedistal;
	private GList<ICustomBlock> blocks;
	private GMap<String, ICustomBlock> idblocks;
	private GMap<Short, String> shortid;
	private GList<CustomSound> registerSounds;
	private PredicateGenerator gen;
	private GSet<Player> offGround;
	private GMap<Player, Integer> steps;

	public ContentRegistry()
	{
		steps = new GMap<Player, Integer>();
		registerSounds = new GList<CustomSound>();
		blocks = new GList<ICustomBlock>();
		idblocks = new GMap<String, ICustomBlock>();
		shortid = new GMap<Short, String>();
		Fulcrum.register(this);
		offGround = new GSet<Player>();
	}

	public void registerSound(CustomSound s)
	{
		registerSounds.add(s);
	}

	public void broke(Location l, String id)
	{
		ICustomBlock block = idblocks.get(id);

		if(block != null)
		{
			block.breakParticles(l);

			if(block.getBreakSound() != null)
			{
				block.getBreakSound().play(l.clone().add(0.5, 0.5, 0.5));
			}
		}
	}

	public void stepped(Location l, String id)
	{
		ICustomBlock block = idblocks.get(id);

		if(block != null)
		{
			if(block.getStepSound() != null)
			{
				block.getStepSound().play(l);
			}
		}
	}

	public void set(Location l, String id)
	{
		ICustomBlock block = idblocks.get(id);

		if(block != null)
		{
			block.set(l);

			if(block.getPlaceSound() != null)
			{
				block.getPlaceSound().play(l.clone().add(0.5, 0.5, 0.5));
			}
		}
	}

	public ItemStack getItem(String id, int count)
	{
		ICustomBlock block = idblocks.get(id);

		if(block != null)
		{
			ItemStack is = block.getItem();
			is.setAmount(count);
			return is;
		}

		return new ItemStack(Material.STONE);
	}

	public void register(Plugin plugin) throws InvalidConfigurationException, IOException
	{
		URL yaml = R.getURL(plugin.getClass(), "/plugin.yml");
		FileConfiguration fc = new YamlConfiguration();
		String pluginKey = plugin.getName().toLowerCase().replaceAll(" ", "");
		GMap<String, CustomSound> sounds = new GMap<String, CustomSound>();
		GMap<String, String> srmap = new GMap<String, String>();
		GMap<String, CustomBlock> blocks = new GMap<String, CustomBlock>();
		fc.loadFromString(read(yaml));

		if(fc.contains("content.sounds"))
		{
			System.out.println("Registering " + plugin.getName());
			GList<String> soundNodes = new GList<String>();

			for(String i : fc.getKeys(true))
			{
				if(i.startsWith("content.sounds."))
				{
					soundNodes.add(i.split("\\.")[2]);
				}
			}

			soundNodes.removeDuplicates();
			System.out.println(" Registering " + soundNodes.size() + " Sounds");
			System.out.println(" Registering Sounds PASS 1");

			for(String i : soundNodes)
			{
				String node = pluginKey + "." + i.replaceAll("\\Q-\\E", ".");
				CustomSound sound = new CustomSound(node);
				String key = "content.sounds." + i;

				if(fc.contains(key + ".copy"))
				{
					continue;
				}

				if(fc.contains(key + ".volume"))
				{
					try
					{
						String s = fc.get(key + ".volume").toString().toLowerCase();
						s = s.endsWith("f") ? s.substring(0, s.length() - 1) : s;
						sound.setSuggestedVolume(Float.valueOf(s));
					}

					catch(Exception e)
					{
						System.out.println("   Failed to parse volume on sound " + key + ".volume");
					}
				}

				if(fc.contains(key + ".pitch"))
				{
					try
					{
						String s = fc.get(key + ".pitch").toString().toLowerCase();
						s = s.endsWith("f") ? s.substring(0, s.length() - 1) : s;
						sound.setSuggestedPitch(Float.valueOf(s));
					}

					catch(Exception e)
					{
						System.out.println("   Failed to parse pitch on sound " + key + ".pitch");
					}
				}

				if(fc.contains(key + ".subtitle"))
				{
					sound.setSubtitle(fc.get(key + ".subtitle").toString());
				}

				if(fc.contains(key + ".resources"))
				{
					try
					{
						for(String j : fc.getStringList(key + ".resources"))
						{
							GList<String> f = new GList<String>();

							if(j.contains("<"))
							{
								try
								{
									String h = j.split("<")[1].split(">")[0];
									String v = "<" + h + ">";
									int begin = Integer.valueOf(h.split("-")[0]);
									int end = Integer.valueOf(h.split("-")[1]);

									if(begin > end)
									{
										System.out.println("   Failed to parse resource in " + j + ". '" + v + "' is invalid <low-high>");
										continue;
									}

									for(int k = begin; k <= end; k++)
									{
										f.add(j.replace(v, k + ""));
									}
								}

								catch(Exception e)
								{
									System.out.println("   Failed to parse resource in " + j + " invalid <low-high>");
								}
							}

							else
							{
								f.add(j);
							}

							for(String k : f)
							{
								if(!R.exists(plugin.getClass(), "/assets/sounds/" + k + ".ogg"))
								{
									System.out.println("   Unable to locate jar resource: " + plugin.getName() + "/assets/sounds/" + k + ".ogg");
									continue;
								}

								URL url = R.getURL(plugin.getClass(), "/assets/sounds/" + k + ".ogg");
								sound.getSoundPaths().put(pluginKey + "/" + k + ".ogg", url);
							}
						}
					}

					catch(Exception e)
					{
						System.out.println("   Failed to parse resources on sound " + key + ".resources");
					}
				}

				System.out.println("  Registered Plugin Sound: " + node + " v:" + sound.getSuggestedVolume() + " p:" + sound.getSuggestedPitch() + " r:" + sound.getSoundPaths().size() + " s:" + sound.getSubtitle());
				sounds.put(node, sound);
				srmap.put(i, node);
			}

			System.out.println(" Registering Sounds PASS 2");

			for(String i : soundNodes)
			{
				String node = pluginKey + "." + i.replaceAll("\\Q-\\E", ".");
				CustomSound sound = new CustomSound(node);
				String key = "content.sounds." + i;

				if(!fc.contains(key + ".copy"))
				{
					continue;
				}

				String parentNode = pluginKey + "." + fc.get(key + ".copy").toString().replaceAll("\\Q-\\E", ".");
				CustomSound parent = sounds.get(parentNode);
				sound.setSubtitle(parent.getSubtitle());
				sound.setSuggestedPitch(parent.getSuggestedPitch());
				sound.setSuggestedVolume(parent.getSuggestedVolume());
				sound.setSoundPaths(parent.getSoundPaths().copy());

				if(fc.contains(key + ".volume"))
				{
					try
					{
						String s = fc.get(key + ".volume").toString().toLowerCase();
						s = s.endsWith("f") ? s.substring(0, s.length() - 1) : s;
						sound.setSuggestedVolume(Float.valueOf(s));
					}

					catch(Exception e)
					{
						System.out.println("   Failed to parse volume on sound " + key + ".volume");
					}
				}

				if(fc.contains(key + ".pitch"))
				{
					try
					{
						String s = fc.get(key + ".pitch").toString().toLowerCase();
						s = s.endsWith("f") ? s.substring(0, s.length() - 1) : s;
						sound.setSuggestedPitch(Float.valueOf(s));
					}

					catch(Exception e)
					{
						System.out.println("   Failed to parse pitch on sound " + key + ".pitch");
					}
				}

				if(fc.contains(key + ".subtitle"))
				{
					sound.setSubtitle(fc.get(key + ".subtitle").toString());
				}

				if(fc.contains(key + ".resources"))
				{
					try
					{
						for(String j : fc.getStringList(key + ".resources"))
						{
							GList<String> f = new GList<String>();

							if(j.contains("<"))
							{
								try
								{
									String h = j.split("<")[1].split(">")[0];
									String v = "<" + h + ">";
									int begin = Integer.valueOf(h.split("-")[0]);
									int end = Integer.valueOf(h.split("-")[1]);

									if(begin > end)
									{
										System.out.println("   Failed to parse resource in " + j + ". '" + v + "' is invalid <low-high>");
										continue;
									}

									for(int k = begin; k <= end; k++)
									{
										f.add(j.replace(v, k + ""));
									}
								}

								catch(Exception e)
								{
									System.out.println("   Failed to parse resource in " + j + " invalid <low-high>");
								}
							}

							else
							{
								f.add(j);
							}

							for(String k : f)
							{
								if(!R.exists(plugin.getClass(), "/assets/sounds/" + k + ".ogg"))
								{
									System.out.println("   Unable to locate jar resource: " + plugin.getName() + "/assets/sounds/" + k + ".ogg");
									continue;
								}

								URL url = R.getURL(plugin.getClass(), "/assets/sounds/" + k + ".ogg");
								sound.getSoundPaths().put(pluginKey + "/" + k + ".ogg", url);
							}
						}
					}

					catch(Exception e)
					{
						System.out.println("   Failed to parse resources on sound " + key + ".resources");
					}
				}

				System.out.println("  Registered Plugin Sound: " + node + " v:" + sound.getSuggestedVolume() + " p:" + sound.getSuggestedPitch() + " r:" + sound.getSoundPaths().size() + " s:" + sound.getSubtitle());
				sounds.put(node, sound);
				srmap.put(i, node);
			}
		}

		if(fc.contains("content.blocks"))
		{
			GList<String> blockNodes = new GList<String>();

			for(String i : fc.getKeys(true))
			{
				if(i.startsWith("content.blocks."))
				{
					blockNodes.add(i.split("\\.")[2]);
				}
			}

			blockNodes.removeDuplicates();

			System.out.println(" Registering " + blockNodes.size() + " Blocks");

			for(String i : blockNodes)
			{
				String node = i.replaceAll("\\Q-\\E", "_");
				CustomBlock b = new CustomBlock(node);
				String key = "content.blocks." + i;

				if(fc.contains(key + ".render-mode"))
				{
					BlockRenderType t = BlockRenderType.valueOf(fc.get(key + ".render-mode").toString());

					if(t != null)
					{
						b.setRenderType(t);
					}

					else
					{
						System.out.println("   Failed to parse render-mode in " + key + ".render-mode");
					}
				}

				if(fc.contains(key + ".name"))
				{
					b.setName(fc.get(key + ".name").toString());
				}

				if(fc.contains(key + ".sounds.break"))
				{
					try
					{
						CustomSound s = sounds.get(srmap.get(fc.get(key + ".sounds.break").toString()));
						b.setBreakSound(new Audio().s(s.getNode()).vp(s.getSuggestedVolume(), s.getSuggestedPitch()).c(SoundCategory.BLOCKS));
					}

					catch(Exception e)
					{
						System.out.println("   Failed to set break sound for " + i + " unidentified sound node");
					}
				}

				if(fc.contains(key + ".sounds.place"))
				{
					try
					{
						CustomSound s = sounds.get(srmap.get(fc.get(key + ".sounds.place").toString()));
						b.setPlaceSound(new Audio().s(s.getNode()).vp(s.getSuggestedVolume(), s.getSuggestedPitch()).c(SoundCategory.BLOCKS));
					}

					catch(Exception e)
					{
						System.out.println("   Failed to set place sound for " + i + " unidentified sound node");
					}
				}

				if(fc.contains(key + ".sounds.step"))
				{
					try
					{
						CustomSound s = sounds.get(srmap.get(fc.get(key + ".sounds.step").toString()));
						b.setStepSound(new Audio().s(s.getNode()).vp(s.getSuggestedVolume(), s.getSuggestedPitch()).c(SoundCategory.BLOCKS));
					}

					catch(Exception e)
					{
						System.out.println("   Failed to set step sound for " + i + " unidentified sound node");
					}
				}

				blocks.put(node, b);
			}
		}

		for(CustomSound i : sounds.v())
		{
			registerSound(i);
		}

		for(CustomBlock i : blocks.v())
		{
			registerBlock(i);
		}
	}

	public void compileResources() throws IOException
	{
		ResourcePack pack = new ResourcePack();
		soundSilent = R.getURL("/assets/sounds/fulcrum/silent.ogg");
		defaultSounds = R.getURL("/assets/sounds-default.json");
		cube = R.getURL("/assets/models/block/fulcrum_cube.json");
		cubeAll = R.getURL("/assets/models/block/fulcrum_cube_all.json");
		cubeBottomTop = R.getURL("/assets/models/block/fulcrum_cube_bottom_top.json");
		cubeColumn = R.getURL("/assets/models/block/fulcrum_cube_column.json");
		cubeTop = R.getURL("/assets/models/block/fulcrum_cube_top.json");
		cubePedistal = R.getURL("/assets/models/block/fulcrum_pedestal.json");
		defaultModel = R.getURL("/assets/models/block/default_cube.json");
		defaultModelAll = R.getURL("/assets/models/block/default_cube_all.json");
		defaultModelBottomTop = R.getURL("/assets/models/block/default_cube_bottom_top.json");
		defaultModelColumn = R.getURL("/assets/models/block/default_cube_column.json");
		defaultModelTop = R.getURL("/assets/models/block/default_cube_top.json");
		defaultModelPedistal = R.getURL("/assets/models/block/default_pedistal.json");
		newSpawner = R.getURL("/assets/textures/blocks/mob_spawner.png");
		missingTexture = R.getURL("/assets/textures/blocks/unknown.png");
		gen = new PredicateGenerator(Material.DIAMOND_HOE, "item/diamond_hoe");
		idblocks.clear();
		shortid.clear();
		defaultModelContent = read(defaultModel);
		defaultModelContentAll = read(defaultModelAll);
		defaultModelContentTop = read(defaultModelTop);
		defaultModelContentBottomTop = read(defaultModelBottomTop);
		defaultModelContentColumn = read(defaultModelColumn);
		defaultModelContentPedistal = read(defaultModelPedistal);
		BlockRenderType.ALL.setMc(defaultModelContentAll);
		BlockRenderType.MANUAL.setMc(defaultModelContent);
		BlockRenderType.TOP.setMc(defaultModelContentTop);
		BlockRenderType.TOP_BOTTOM.setMc(defaultModelContentBottomTop);
		BlockRenderType.COLUMN.setMc(defaultModelContentColumn);
		BlockRenderType.PEDISTAL.setMc(defaultModelContentPedistal);

		int max = 0;
		for(ICustomBlock i : blocks)
		{
			BlockRenderType rt = i.getRenderType();
			String m = "/assets/models/block/" + i.getId() + ".json";
			URL model = i.getClass().getResource(m);

			for(String j : rt.getRequiredTextures())
			{
				String a = j.isEmpty() ? "" : ("_" + j);
				String t = "/assets/textures/blocks/" + i.getId() + a + ".png";
				URL texture = i.getClass().getResource(t);

				if(texture != null)
				{
					pack.setResource("textures/blocks/" + i.getId() + a + ".png", texture);
				}

				else
				{
					pack.setResource("textures/blocks/" + i.getId() + a + ".png", missingTexture);
					System.out.println("WARNING: " + i.getId() + " MISSING TEXTURE: " + t);
				}
			}

			if(model != null)
			{
				pack.setResource("models/block/" + i.getId() + ".json", model);
			}

			else
			{
				String newModel = rt.getModelContent().replaceAll("\\Q$id\\E", i.getId());
				pack.setResource("models/block/" + i.getId() + ".json", new JSONObject(newModel).toString());
			}

			i.setDurabilityLock((short) gen.getModels().size());
			System.out.println("Registered BLOCK " + i.getId() + " to " + gen.getModels().size());
			gen.getModels().add("block/" + i.getId());
			idblocks.put(i.getId(), i);
			shortid.put(i.getDurabilityLock(), i.getId());
			max++;
		}

		gen.generate();
		pack.setResource("models/" + gen.modelSuperName() + ".json", gen.getParenter().toString());
		pack.setResource("models/" + gen.getModel() + ".json", gen.generateModel("items/diamond_hoe").toString());
		System.out.println(max + " of " + gen.getMax() + " total predicates generated (" + F.pc((double) max / (double) gen.getMax()) + " Utilization)");

		JSONObject desound = new JSONObject(read(defaultSounds));
		JSONObject soundx = new JSONObject();

		System.out.println("Reading " + F.f(desound.keySet().size()) + " default sound entries");

		for(String i : desound.keySet())
		{
			if(i.startsWith("block.metal.") && (i.endsWith(".step") || i.endsWith(".break") || i.endsWith(".fall") || i.endsWith("hit") || i.endsWith(".place")))
			{
				JSONObject mod = new JSONObject(desound.getJSONObject(i).toString());
				JSONObject old = new JSONObject(desound.getJSONObject(i).toString());
				mod.put("sounds", new JSONArray("[" + F.repeat("\"fulcrum/silent\",", 2000) + "]"));
				soundx.put(i.replace("block.", "material."), old);
				soundx.put(i, mod);
				System.out.println("Remapped Sound " + i.replace("block.", "material."));
			}

			if(i.equalsIgnoreCase("item.hoe.till"))
			{
				JSONObject mod = new JSONObject(desound.getJSONObject(i).toString());
				JSONObject old = new JSONObject(desound.getJSONObject(i).toString());
				mod.put("sounds", new JSONArray("[" + F.repeat("\"fulcrum/silent\",", 2000) + "]"));
				soundx.put("material.hoe.till", old);
				soundx.put(i, mod);
				System.out.println("Remapped Sound " + "material.hoe.till");
			}
		}

		System.out.println("Registering " + F.f(registerSounds.size()) + " custom sounds");

		for(CustomSound i : registerSounds)
		{
			System.out.println("Registering Sound: " + i.getNode());
			for(String j : i.getSoundPaths().k())
			{
				pack.setResource("sounds/" + j, i.getSoundPaths().get(j));
			}

			i.toJson(soundx);
		}

		pack.setResource("sounds.json", soundx.toString());
		pack.setResource("sounds/fulcrum/silent.ogg", soundSilent);
		pack.setResource("models/block/fulcrum_cube.json", new JSONObject(read(cube)).toString());
		pack.setResource("models/block/fulcrum_cube.json", new JSONObject(read(cube)).toString());
		pack.setResource("models/block/fulcrum_cube_all.json", new JSONObject(read(cubeAll)).toString());
		pack.setResource("models/block/fulcrum_cube_top.json", new JSONObject(read(cubeTop)).toString());
		pack.setResource("models/block/fulcrum_cube_bottom_top.json", new JSONObject(read(cubeBottomTop)).toString());
		pack.setResource("models/block/fulcrum_cube_column.json", new JSONObject(read(cubeColumn)).toString());
		pack.setResource("models/block/fulcrum_pedistal.json", new JSONObject(read(cubePedistal)).toString());
		pack.setResource("textures/blocks/mob_spawner.png", newSpawner);
		pack.setResource("textures/blocks/unknown.png", missingTexture);
		pack.setResource("textures/items/unknown.png", missingTexture);
		File fpack = new File(Fulcrum.server.getRoot(), "pack.zip");
		pack.writeToArchive(fpack);

		for(Player i : P.onlinePlayers())
		{
			Fulcrum.adapter.sendResourcePackWeb(i, "pack.zip");
		}
	}

	public String read(URL url) throws IOException
	{
		String content = "";
		BufferedReader bu = new BufferedReader(new InputStreamReader(url.openStream()));
		String line = null;

		while((line = bu.readLine()) != null)
		{
			content += "\n" + line;
		}

		bu.close();

		return content;
	}

	public void registerBlock(ICustomBlock block)
	{
		blocks.add(block);
	}

	@EventHandler
	public void on(PlayerJoinEvent e)
	{
		Fulcrum.adapter.sendResourcePackWeb(e.getPlayer(), "pack.zip");
	}

	@EventHandler
	public void on(PlayerQuitEvent e)
	{
		steps.remove(e.getPlayer());
		offGround.add(e.getPlayer());
	}

	@EventHandler
	public void on(BlockPlaceEvent e)
	{
		if(Fulcrum.adapter.isMetal(e.getBlock().getType()))
		{
			new Audio().s("material.metal.place").vp(1f, 1.34f).setCategory(SoundCategory.BLOCKS).play(e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5));
		}
	}

	@EventHandler
	public void on(BlockBreakEvent e)
	{
		if(e.getBlock().getType().equals(Material.MOB_SPAWNER))
		{
			short d = Fulcrum.adapter.getSpawnerType(e.getBlock().getLocation());
			broke(e.getBlock().getLocation(), shortid.get(d));
		}

		else if(Fulcrum.adapter.isMetal(e.getBlock().getType()))
		{
			new Audio().s("material.metal.break").vp(1f, 1.34f).setCategory(SoundCategory.BLOCKS).play(e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5));
		}
	}

	@EventHandler
	public void on(PlayerMoveEvent e)
	{
		if(!steps.containsKey(e.getPlayer()))
		{
			steps.put(e.getPlayer(), 0);
		}

		if((e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) && e.getPlayer().isOnGround())
		{
			steps.put(e.getPlayer(), steps.get(e.getPlayer()) + 1);

			if(e.getPlayer().isSprinting())
			{
				if(steps.get(e.getPlayer()) % 4 == 0)
				{
					if(e.getPlayer().getLocation().clone().add(0, -0.2, 0).getBlock().getType().equals(Material.MOB_SPAWNER))
					{
						short d = Fulcrum.adapter.getSpawnerType(e.getPlayer().getLocation().clone().add(0, -0.2, 0).getBlock().getLocation());
						stepped(e.getPlayer().getLocation(), shortid.get(d));
					}

					else if(Fulcrum.adapter.isMetal(e.getPlayer().getLocation().clone().add(0, -0.2, 0).getBlock().getType()))
					{
						new Audio().s("material.metal.step").vp(0.3f, 1.34f).setCategory(SoundCategory.BLOCKS).play(e.getPlayer().getLocation());
					}
				}
			}

			else if(steps.get(e.getPlayer()) % 6 == 0)
			{
				if(e.getPlayer().getLocation().clone().add(0, -0.2, 0).getBlock().getType().equals(Material.MOB_SPAWNER))
				{
					short d = Fulcrum.adapter.getSpawnerType(e.getPlayer().getLocation().clone().add(0, -0.2, 0).getBlock().getLocation());
					stepped(e.getPlayer().getLocation(), shortid.get(d));
				}

				else if(Fulcrum.adapter.isMetal(e.getPlayer().getLocation().clone().add(0, -0.2, 0).getBlock().getType()))
				{
					new Audio().s("material.metal.step").vp(0.3f, 1.34f).setCategory(SoundCategory.BLOCKS).play(e.getPlayer().getLocation());
				}
			}
		}

		if(e.getPlayer().isOnGround())
		{
			if(offGround.contains(e.getPlayer()))
			{
				offGround.remove(e.getPlayer());

				if(e.getPlayer().getLocation().clone().add(0, -0.2, 0).getBlock().getType().equals(Material.MOB_SPAWNER))
				{
					short d = Fulcrum.adapter.getSpawnerType(e.getPlayer().getLocation().clone().add(0, -0.2, 0).getBlock().getLocation());
					stepped(e.getPlayer().getLocation(), shortid.get(d));
				}

				else if(Fulcrum.adapter.isMetal(e.getPlayer().getLocation().clone().add(0, -0.2, 0).getBlock().getType()))
				{
					new Audio().s("material.metal.fall").vp(0.3f, 1.34f).setCategory(SoundCategory.BLOCKS).play(e.getPlayer().getLocation());
				}
			}
		}

		else
		{
			offGround.add(e.getPlayer());
		}
	}

	@EventHandler
	public void on(PlayerInteractEvent e)
	{
		if(e.getItem() == null)
		{
			return;
		}

		if(e.getItem().getType().equals(Material.DIAMOND_HOE))
		{
			if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			{
				if(shortid.containsKey(e.getItem().getDurability()))
				{
					e.setCancelled(true);
					set(e.getClickedBlock().getRelative(e.getBlockFace()).getLocation(), shortid.get(e.getItem().getDurability()));

					if(!e.getPlayer().getGameMode().equals(GameMode.CREATIVE))
					{
						if(e.getItem().equals(e.getPlayer().getInventory().getItemInMainHand()))
						{
							ItemStack is = e.getPlayer().getInventory().getItemInMainHand().clone();

							if(is.getAmount() > 1)
							{
								is.setAmount(is.getAmount() - 1);
								e.getPlayer().getInventory().setItemInMainHand(is);
							}

							else
							{
								e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
							}
						}

						else if(e.getItem().equals(e.getPlayer().getInventory().getItemInOffHand()))
						{
							ItemStack is = e.getPlayer().getInventory().getItemInOffHand().clone();

							if(is.getAmount() > 1)
							{
								is.setAmount(is.getAmount() - 1);
								e.getPlayer().getInventory().setItemInOffHand(is);
							}

							else
							{
								e.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
							}
						}

					}
				}
			}
		}
	}

	public URL getCubeHead()
	{
		return cubeAll;
	}

	public URL getDefaultModel()
	{
		return defaultModelAll;
	}

	public URL getMissingTexture()
	{
		return missingTexture;
	}

	public URL getNewSpawner()
	{
		return newSpawner;
	}

	public GList<ICustomBlock> getBlocks()
	{
		return blocks;
	}

	public GMap<String, ICustomBlock> getIdblocks()
	{
		return idblocks;
	}

	public GMap<Short, String> getShortid()
	{
		return shortid;
	}

	public PredicateGenerator getGen()
	{
		return gen;
	}

	public String getDefaultModelContent()
	{
		return defaultModelContentAll;
	}
}
