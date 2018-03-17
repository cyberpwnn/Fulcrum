package com.volmit.fulcrum.custom;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

import org.bukkit.Bukkit;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.bukkit.P;
import com.volmit.fulcrum.bukkit.R;
import com.volmit.fulcrum.bukkit.TaskLater;
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
	private URL inventoryTop;
	private URL inventoryBottom;
	private URL cubeAll;
	private URL cubeBottomTop;
	private URL cubeColumn;
	private URL cubeTop;
	private URL cubePedistal;
	private URL defaultInventoryTop;
	private URL defaultInventoryBottom;
	private URL defaultItem;
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
	private URL glint;
	private URL glintMeta;
	private String defaultInventoryContentTop;
	private String defaultInventoryContentBottom;
	private String defaultItemContent;
	private String defaultModelContent;
	private String defaultModelContentAll;
	private String defaultModelContentTop;
	private String defaultModelContentBottomTop;
	private String defaultModelContentColumn;
	private String defaultModelContentPedistal;
	private GList<ICustomBlock> blocks;
	private GList<CustomInventory> inventories;
	private GList<CustomSound> sounds;
	private GList<CustomItem> items;
	private GMap<String, ICustomBlock> idblocks;
	private GMap<Integer, ICustomBlock> superBlocks;
	private GMap<Short, String> blockid;
	private GMap<Short, String> itemid;
	private AllocationSpace ass;
	private GSet<Player> offGround;
	private GMap<Player, Integer> steps;
	private String rid;

	public ContentRegistry()
	{
		rid = "";
		inventories = new GList<CustomInventory>();
		superBlocks = new GMap<Integer, ICustomBlock>();
		steps = new GMap<Player, Integer>();
		sounds = new GList<CustomSound>();
		items = new GList<CustomItem>();
		blocks = new GList<ICustomBlock>();
		idblocks = new GMap<String, ICustomBlock>();
		blockid = new GMap<Short, String>();
		itemid = new GMap<Short, String>();
		Fulcrum.register(this);
		offGround = new GSet<Player>();
	}

	public void registerSound(CustomSound s)
	{
		sounds.add(s);
	}

	public void registerItem(CustomItem s)
	{
		items.add(s);
	}

	public void broke(Location l, String id)
	{
		ICustomBlock block = idblocks.get(id);

		if(block != null)
		{
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

	public ItemStack getBlock(String id)
	{
		ICustomBlock block = idblocks.get(id);

		if(block != null)
		{
			ItemStack is = block.getItem();
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
		GMap<String, CustomItem> items = new GMap<String, CustomItem>();
		GMap<String, CustomInventory> inventories = new GMap<String, CustomInventory>();
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

				if(fc.contains(key + ".shaded"))
				{
					try
					{
						b.setShaded(fc.getBoolean(key + ".shaded"));
					}

					catch(Exception e)
					{
						System.out.println("   Failed to parse shaded in " + key + ".shaded");
					}
				}

				if(fc.contains(key + ".stack-size"))
				{
					try
					{
						b.setStackSize(fc.getInt(key + ".stack-size"));
					}

					catch(Exception e)
					{
						System.out.println("   Failed to parse stack-size in " + key + ".stack-size");
					}
				}

				if(fc.contains(key + ".enchanted"))
				{
					try
					{
						b.setEnchanted(fc.getBoolean(key + ".enchanted"));
					}

					catch(Exception e)
					{
						System.out.println("   Failed to parse enchanted in " + key + ".enchanted");
					}
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

		if(fc.contains("content.inventories"))
		{
			GList<String> inventoryNodes = new GList<String>();

			for(String i : fc.getKeys(true))
			{
				if(i.startsWith("content.inventories."))
				{
					inventoryNodes.add(i.split("\\.")[2]);
				}
			}

			inventoryNodes.removeDuplicates();

			System.out.println(" Registering " + inventoryNodes.size() + " Inventories");

			for(String i : inventoryNodes)
			{
				String node = i.replaceAll("\\Q-\\E", "_");
				CustomInventory b = new CustomInventory(node);
				String key = "content.inventories." + i;
				boolean ee = false;

				if(fc.contains(key + ".enchanted.top"))
				{
					try
					{
						ee = true;
						b.setEnchantedTop(fc.getBoolean(key + ".enchanted.top"));
					}

					catch(Exception e)
					{
						System.out.println("   Failed to parse enchanted in " + key + ".enchanted.top");
					}
				}

				if(fc.contains(key + ".enchanted.bottom"))
				{
					try
					{
						ee = true;
						b.setEnchantedTop(fc.getBoolean(key + ".enchanted.bottom"));
					}

					catch(Exception e)
					{
						System.out.println("   Failed to parse enchanted in " + key + ".enchanted.bottom");
					}
				}

				if(fc.contains(key + ".enchanted") && !ee)
				{
					try
					{
						b.setEnchantedTop(fc.getBoolean(key + ".enchanted"));
					}

					catch(Exception e)
					{
						System.out.println("   Failed to parse enchanted in " + key + ".enchanted");
					}

					try
					{
						b.setEnchantedBottom(fc.getBoolean(key + ".enchanted"));
					}

					catch(Exception e)
					{
						System.out.println("   Failed to parse enchanted in " + key + ".enchanted");
					}
				}

				inventories.put(node, b);
			}
		}

		if(fc.contains("content.items"))
		{
			GList<String> itemNodes = new GList<String>();

			for(String i : fc.getKeys(true))
			{
				if(i.startsWith("content.items."))
				{
					itemNodes.add(i.split("\\.")[2]);
				}
			}

			itemNodes.removeDuplicates();

			System.out.println(" Registering " + itemNodes.size() + " Items");

			for(String i : itemNodes)
			{
				String node = i.replaceAll("\\Q-\\E", "_");
				CustomItem b = new CustomItem(node);
				String key = "content.items." + i;

				if(fc.contains(key + ".name"))
				{
					b.setName(fc.get(key + ".name").toString());
				}

				if(fc.contains(key + ".layers"))
				{
					try
					{
						b.setLayers(fc.getInt(key + ".layers"));
					}

					catch(Exception e)
					{
						System.out.println("   Failed to parse layers in " + key + ".layers");
					}
				}

				if(fc.contains(key + ".stack-size"))
				{
					try
					{
						b.setStackSize(fc.getInt(key + ".stack-size"));
					}

					catch(Exception e)
					{
						System.out.println("   Failed to parse stack-size in " + key + ".stack-size");
					}
				}

				if(fc.contains(key + ".enchanted"))
				{
					try
					{
						b.setEnchanted(fc.getBoolean(key + ".enchanted"));
					}

					catch(Exception e)
					{
						System.out.println("   Failed to parse enchanted in " + key + ".enchanted");
					}
				}

				items.put(node, b);
			}
		}

		for(CustomSound i : sounds.v())
		{
			registerSound(i);
		}

		for(CustomItem i : items.v())
		{
			registerItem(i);
		}

		for(CustomBlock i : blocks.v())
		{
			registerBlock(i);
		}

		for(CustomInventory i : inventories.v())
		{
			registerInventory(i);
		}
	}

	public void compileResources() throws IOException
	{
		ResourcePack pack = new ResourcePack();
		soundSilent = R.getURL("/assets/sounds/fulcrum/silent.ogg");
		glint = R.getURL("/assets/textures/misc/enchanted_item_glint.png");
		glintMeta = R.getURL("/assets/textures/misc/enchanted_item_glint.png.mcmeta");
		defaultSounds = R.getURL("/assets/sounds-default.json");
		inventoryTop = R.getURL("/assets/models/inventory/fulcrum_top.json");
		inventoryBottom = R.getURL("/assets/models/inventory/fulcrum_bottom.json");
		cube = R.getURL("/assets/models/block/fulcrum_cube.json");
		cubeAll = R.getURL("/assets/models/block/fulcrum_cube_all.json");
		cubeBottomTop = R.getURL("/assets/models/block/fulcrum_cube_bottom_top.json");
		cubeColumn = R.getURL("/assets/models/block/fulcrum_cube_column.json");
		cubeTop = R.getURL("/assets/models/block/fulcrum_cube_top.json");
		cubePedistal = R.getURL("/assets/models/block/fulcrum_pedestal.json");
		defaultInventoryTop = R.getURL("/assets/models/inventory/default_top.json");
		defaultInventoryBottom = R.getURL("/assets/models/inventory/default_bottom.json");
		defaultModel = R.getURL("/assets/models/block/default_cube.json");
		defaultItem = R.getURL("/assets/models/item/default_item.json");
		defaultModelAll = R.getURL("/assets/models/block/default_cube_all.json");
		defaultModelBottomTop = R.getURL("/assets/models/block/default_cube_bottom_top.json");
		defaultModelColumn = R.getURL("/assets/models/block/default_cube_column.json");
		defaultModelTop = R.getURL("/assets/models/block/default_cube_top.json");
		defaultModelPedistal = R.getURL("/assets/models/block/default_pedistal.json");
		newSpawner = R.getURL("/assets/textures/blocks/mob_spawner.png");
		missingTexture = R.getURL("/assets/textures/blocks/unknown.png");
		idblocks.clear();
		blockid.clear();
		defaultInventoryContentTop = read(defaultInventoryTop);
		defaultInventoryContentBottom = read(defaultInventoryBottom);
		defaultModelContent = read(defaultModel);
		defaultItemContent = read(defaultItem);
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
		ass = new AllocationSpace();
		ass.sacrificeNormal(Material.DIAMOND_HOE, "diamond_hoe", "diamond_hoe");
		ass.sacrificeShaded(Material.LEATHER_HELMET, "leather_helmet", "leather_helmet", "leather_helmet_overlay");

		for(CustomItem i : items)
		{
			JSONObject o = new JSONObject(defaultItemContent);
			JSONObject t = o.getJSONObject("textures");
			System.out.println("  Registering Item " + i.getId());

			for(int j = 0; j < i.getLayers(); j++)
			{
				URL url = i.getClass().getResource("/assets/textures/items/" + i.getId() + "_" + j + ".png");

				if(url == null && j == 0)
				{
					url = i.getClass().getResource("/assets/textures/items/" + i.getId() + ".png");
				}

				if(url == null)
				{
					System.out.println("   Unable to locate item texture " + i.getId() + "_" + j + ".png");
					url = missingTexture;
				}

				pack.setResource("textures/items/" + i.getId() + "_" + j + ".png", url);
				t.put("layer" + j, "items/" + i.getId() + "_" + j);
				o.put("textures", t);
			}

			pack.setResource("models/item/" + i.getId() + ".json", o.toString());
			AllocatedNode node = ass.allocateNormal("item/" + i.getId());
			i.setType(node.getMaterial());
			i.setDurability((short) node.getId());
			itemid.put(i.getDurability(), i.getId());
		}

		for(CustomInventory i : inventories)
		{
			System.out.println("  Registering Inventory " + i.getId());
			String ttop = "/assets/textures/inventories/" + i.getId() + "_top.png";
			String tbottom = "/assets/textures/inventories/" + i.getId() + "_bottom.png";

			URL ut = i.getClass().getResource(ttop);
			URL ub = i.getClass().getResource(tbottom);

			if(ut == null && ub == null)
			{
				System.out.println("   Unable to locate either inventory textures.");
				continue;
			}

			String modelTop = new JSONObject(defaultInventoryContentTop).toString(0).replace("$id", i.getId());
			String modelBottom = new JSONObject(defaultInventoryContentBottom).toString(0).replace("$id", i.getId());

			if(ut != null)
			{
				i.setTop(true);
				pack.setResource("models/inventory/" + i.getId() + "_top.json", modelTop);
				pack.setResource("textures/inventories/" + i.getId() + "_top.png", ut);
				AllocatedNode idx = ass.allocateNormal("inventory/" + i.getId() + "_top");
				i.setTypeTop(idx.getMaterial());
				i.setDurabilityTop((short) idx.getId());
				System.out.println("   Registered Inventory TOP " + i.getId() + " as " + idx.getMaterial().toString() + ":" + idx.getId());
			}

			if(ub != null)
			{
				i.setBottom(true);
				pack.setResource("models/inventory/" + i.getId() + "_bottom.json", modelBottom);
				pack.setResource("textures/inventories/" + i.getId() + "_bottom.png", ub);
				AllocatedNode idx = ass.allocateNormal("inventory/" + i.getId() + "_bottom");
				i.setTypeBottom(idx.getMaterial());
				i.setDurabilityBottom((short) idx.getId());
				System.out.println("   Registered Inventory BOTTOM " + i.getId() + " as " + idx.getMaterial().toString() + ":" + idx.getId());
			}
		}

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

			AllocatedNode idx = i.isShaded() ? ass.allocateShaded("block/" + i.getId()) : ass.allocateNormal("block/" + i.getId());
			i.setDurabilityLock((short) idx.getId());
			i.setType(idx.getMaterial());
			System.out.println("Registered BLOCK " + i.getId() + " to " + i.getType() + " @" + i.getDurabilityLock());
			idblocks.put(i.getId(), i);
			blockid.put(i.getDurabilityLock(), i.getId());
			superBlocks.put(idx.getSuperid(), i);
			i.setSuperID(idx.getSuperid());
			i.setMatt(ass.getNameForMaterial(i.getType()));
		}

		for(Material i : ass.getIorda())
		{
			JSONObject[] ox = ass.generateNormalModel(i);
			pack.setResource("models/" + ass.getNormalSuperModelName(i) + ".json", ox[1].toString());
			pack.setResource("models/" + ass.getNormalModelName(i) + ".json", ox[0].toString());
		}

		for(Material i : ass.getIordb())
		{
			JSONObject[] ox = ass.generateShadedModel(i);
			pack.setResource("models/" + ass.getShadedSuperModelName(i) + ".json", ox[1].toString());
			pack.setResource("models/" + ass.getShadedModelName(i) + ".json", ox[0].toString());
		}

		JSONObject desound = new JSONObject(read(defaultSounds));
		JSONObject soundx = new JSONObject();

		System.out.println("Reading " + F.f(desound.keySet().size()) + " default sound entries");

		for(String i : desound.keySet())
		{
			if(i.startsWith("block.metal.") && (i.endsWith(".step") || i.endsWith(".break") || i.endsWith(".fall") || i.endsWith("hit") || i.endsWith(".place")))
			{
				JSONObject mod = new JSONObject(desound.getJSONObject(i).toString());
				JSONObject old = new JSONObject(desound.getJSONObject(i).toString());
				mod.put("sounds", new JSONArray("[" + F.repeat("\"fulcrum/silent\",", 5000) + "]"));
				soundx.put(i.replace("block.", "material."), old);
				soundx.put(i, mod);
				System.out.println("Remapped Sound " + i.replace("block.", "material."));
			}

			if(i.equalsIgnoreCase("item.hoe.till"))
			{
				JSONObject mod = new JSONObject(desound.getJSONObject(i).toString());
				JSONObject old = new JSONObject(desound.getJSONObject(i).toString());
				mod.put("sounds", new JSONArray("[" + F.repeat("\"fulcrum/silent\",", 5000) + "]"));
				soundx.put("hoe.till", old);
				soundx.put(i, mod);
				System.out.println("Remapped Sound " + "hoe.till");
			}

			if(i.equalsIgnoreCase("item.armor.equip_leather"))
			{
				JSONObject mod = new JSONObject(desound.getJSONObject(i).toString());
				JSONObject old = new JSONObject(desound.getJSONObject(i).toString());
				mod.put("sounds", new JSONArray("[" + F.repeat("\"fulcrum/silent\",", 5000) + "]"));
				soundx.put("armor.equip_leather", old);
				soundx.put(i, mod);
				System.out.println("Remapped Sound " + "armor.equip_leather");
			}
		}

		System.out.println("Registering " + F.f(sounds.size()) + " custom sounds");

		for(CustomSound i : sounds)
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
		pack.setResource("models/inventory/fulcrum_top.json", new JSONObject(read(inventoryTop)).toString());
		pack.setResource("models/inventory/fulcrum_bottom.json", new JSONObject(read(inventoryBottom)).toString());
		pack.setResource("models/block/fulcrum_cube.json", new JSONObject(read(cube)).toString());
		pack.setResource("models/block/fulcrum_cube_all.json", new JSONObject(read(cubeAll)).toString());
		pack.setResource("models/block/fulcrum_cube_top.json", new JSONObject(read(cubeTop)).toString());
		pack.setResource("models/block/fulcrum_cube_bottom_top.json", new JSONObject(read(cubeBottomTop)).toString());
		pack.setResource("models/block/fulcrum_cube_column.json", new JSONObject(read(cubeColumn)).toString());
		pack.setResource("models/block/fulcrum_pedistal.json", new JSONObject(read(cubePedistal)).toString());
		pack.setResource("textures/misc/enchanted_item_glint.png", glint);
		pack.setResource("textures/misc/enchanted_item_glint.png.mcmeta", glintMeta);
		pack.setResource("textures/blocks/mob_spawner.png", newSpawner);
		pack.setResource("textures/blocks/unknown.png", missingTexture);
		pack.setResource("textures/items/unknown.png", missingTexture);
		rid = UUID.randomUUID().toString().replaceAll("-", "");
		File fpack = new File(Fulcrum.server.getRoot(), rid + ".zip");
		pack.writeToArchive(fpack);
		System.out.println("RESULTS:\n" + ass.toString());
		for(Player i : P.onlinePlayers())
		{
			Fulcrum.adapter.sendResourcePackWeb(i, rid + ".zip");
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

	public void registerInventory(CustomInventory i)
	{
		inventories.add(i);
	}

	@EventHandler
	public void on(PlayerJoinEvent e)
	{
		new TaskLater(5)
		{
			@Override
			public void run()
			{
				Fulcrum.adapter.sendResourcePackWeb(e.getPlayer(), rid + ".zip");
			}
		};
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
			broke(e.getBlock().getLocation(), blockid.get(d));
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
						stepped(e.getPlayer().getLocation(), blockid.get(d));
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
					stepped(e.getPlayer().getLocation(), blockid.get(d));
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
					stepped(e.getPlayer().getLocation(), blockid.get(d));
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

	@SuppressWarnings("deprecation")
	@EventHandler
	public void on(PlayerInteractEvent e)
	{
		if(e.getItem() == null)
		{
			return;
		}

		if(ass.getMattx().k().contains(e.getItem().getType()))
		{
			if(itemid.containsKey(e.getItem().getDurability()))
			{
				if(!e.getItem().getItemMeta().isUnbreakable())
				{
					return;
				}

				if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
				{
					e.setCancelled(true);
				}

				return;
			}

			if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			{
				if(blockid.containsKey(e.getItem().getDurability()))
				{
					if(!e.getItem().getItemMeta().isUnbreakable())
					{
						return;
					}

					e.setCancelled(true);

					if(e.getClickedBlock().getRelative(e.getBlockFace()).getLocation().clone().add(0.5, 0.5, 0.5).distance(e.getPlayer().getEyeLocation()) < 0.75)
					{
						return;
					}

					if(e.getClickedBlock().getRelative(e.getBlockFace()).getLocation().clone().add(0.5, 0.5, 0.5).distance(e.getPlayer().getLocation().clone().add(0, 0.5, 0)) < 0.75)
					{
						return;
					}

					if(e.getClickedBlock().getType().isSolid())
					{
						set(e.getClickedBlock().getRelative(e.getBlockFace()).getLocation(), blockid.get(e.getItem().getDurability()));
					}

					else
					{
						set(e.getClickedBlock().getLocation(), blockid.get(e.getItem().getDurability()));
					}

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

							return;
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

							return;
						}
					}
				}
			}

			if(!e.isCancelled() && ((e.getClickedBlock() == null || e.getClickedBlock().getType().equals(Material.AIR)) && e.getAction().equals(Action.LEFT_CLICK_BLOCK)))
			{
				if(e.getPlayer().getItemInHand().getType().toString().contains("LEGGINGS"))
				{
					e.setCancelled(true);
				}

				if(e.getPlayer().getItemInHand().getType().toString().contains("BOOTS"))
				{
					e.setCancelled(true);
				}

				if(e.getPlayer().getItemInHand().getType().toString().contains("CHESTPLATE"))
				{
					e.setCancelled(true);
				}

				if(e.getPlayer().getItemInHand().getType().toString().contains("HELMET"))
				{
					e.setCancelled(true);
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
		return blockid;
	}

	public String getDefaultModelContent()
	{
		return defaultModelContentAll;
	}

	public URL getCube()
	{
		return cube;
	}

	public void setCube(URL cube)
	{
		this.cube = cube;
	}

	public URL getCubeAll()
	{
		return cubeAll;
	}

	public void setCubeAll(URL cubeAll)
	{
		this.cubeAll = cubeAll;
	}

	public URL getCubeBottomTop()
	{
		return cubeBottomTop;
	}

	public void setCubeBottomTop(URL cubeBottomTop)
	{
		this.cubeBottomTop = cubeBottomTop;
	}

	public URL getCubeColumn()
	{
		return cubeColumn;
	}

	public void setCubeColumn(URL cubeColumn)
	{
		this.cubeColumn = cubeColumn;
	}

	public URL getCubeTop()
	{
		return cubeTop;
	}

	public void setCubeTop(URL cubeTop)
	{
		this.cubeTop = cubeTop;
	}

	public URL getCubePedistal()
	{
		return cubePedistal;
	}

	public void setCubePedistal(URL cubePedistal)
	{
		this.cubePedistal = cubePedistal;
	}

	public URL getDefaultModelAll()
	{
		return defaultModelAll;
	}

	public void setDefaultModelAll(URL defaultModelAll)
	{
		this.defaultModelAll = defaultModelAll;
	}

	public URL getDefaultModelBottomTop()
	{
		return defaultModelBottomTop;
	}

	public void setDefaultModelBottomTop(URL defaultModelBottomTop)
	{
		this.defaultModelBottomTop = defaultModelBottomTop;
	}

	public URL getDefaultModelColumn()
	{
		return defaultModelColumn;
	}

	public void setDefaultModelColumn(URL defaultModelColumn)
	{
		this.defaultModelColumn = defaultModelColumn;
	}

	public URL getDefaultModelTop()
	{
		return defaultModelTop;
	}

	public void setDefaultModelTop(URL defaultModelTop)
	{
		this.defaultModelTop = defaultModelTop;
	}

	public URL getDefaultModelPedistal()
	{
		return defaultModelPedistal;
	}

	public void setDefaultModelPedistal(URL defaultModelPedistal)
	{
		this.defaultModelPedistal = defaultModelPedistal;
	}

	public URL getDefaultSounds()
	{
		return defaultSounds;
	}

	public void setDefaultSounds(URL defaultSounds)
	{
		this.defaultSounds = defaultSounds;
	}

	public URL getSoundSilent()
	{
		return soundSilent;
	}

	public void setSoundSilent(URL soundSilent)
	{
		this.soundSilent = soundSilent;
	}

	public String getDefaultModelContentAll()
	{
		return defaultModelContentAll;
	}

	public void setDefaultModelContentAll(String defaultModelContentAll)
	{
		this.defaultModelContentAll = defaultModelContentAll;
	}

	public String getDefaultModelContentTop()
	{
		return defaultModelContentTop;
	}

	public void setDefaultModelContentTop(String defaultModelContentTop)
	{
		this.defaultModelContentTop = defaultModelContentTop;
	}

	public String getDefaultModelContentBottomTop()
	{
		return defaultModelContentBottomTop;
	}

	public void setDefaultModelContentBottomTop(String defaultModelContentBottomTop)
	{
		this.defaultModelContentBottomTop = defaultModelContentBottomTop;
	}

	public String getDefaultModelContentColumn()
	{
		return defaultModelContentColumn;
	}

	public void setDefaultModelContentColumn(String defaultModelContentColumn)
	{
		this.defaultModelContentColumn = defaultModelContentColumn;
	}

	public String getDefaultModelContentPedistal()
	{
		return defaultModelContentPedistal;
	}

	public void setDefaultModelContentPedistal(String defaultModelContentPedistal)
	{
		this.defaultModelContentPedistal = defaultModelContentPedistal;
	}

	public GList<CustomSound> getRegisterSounds()
	{
		return sounds;
	}

	public void setRegisterSounds(GList<CustomSound> registerSounds)
	{
		this.sounds = registerSounds;
	}

	public AllocationSpace getAss()
	{
		return ass;
	}

	public void setAss(AllocationSpace ass)
	{
		this.ass = ass;
	}

	public GSet<Player> getOffGround()
	{
		return offGround;
	}

	public void setOffGround(GSet<Player> offGround)
	{
		this.offGround = offGround;
	}

	public GMap<Player, Integer> getSteps()
	{
		return steps;
	}

	public void setSteps(GMap<Player, Integer> steps)
	{
		this.steps = steps;
	}

	public void setDefaultModel(URL defaultModel)
	{
		this.defaultModel = defaultModel;
	}

	public void setMissingTexture(URL missingTexture)
	{
		this.missingTexture = missingTexture;
	}

	public void setNewSpawner(URL newSpawner)
	{
		this.newSpawner = newSpawner;
	}

	public void setDefaultModelContent(String defaultModelContent)
	{
		this.defaultModelContent = defaultModelContent;
	}

	public void setBlocks(GList<ICustomBlock> blocks)
	{
		this.blocks = blocks;
	}

	public void setIdblocks(GMap<String, ICustomBlock> idblocks)
	{
		this.idblocks = idblocks;
	}

	public void setShortid(GMap<Short, String> shortid)
	{
		this.blockid = shortid;
	}

	public GMap<Integer, ICustomBlock> getSuperBlocks()
	{
		return superBlocks;
	}

	public void setSuperBlocks(GMap<Integer, ICustomBlock> superBlocks)
	{
		this.superBlocks = superBlocks;
	}

	public ICustomBlock getBlockFromSuper(int superId)
	{
		return superBlocks.get(superId);
	}

	public void showInventory(String string, Player p)
	{
		for(CustomInventory i : inventories)
		{
			if(i.getId().equalsIgnoreCase(string))
			{
				Inventory inv = Bukkit.createInventory(null, 3 * 9, "");
				inv.setItem(0, i.getTop());
				inv.setItem(18, i.getBottom());
				p.openInventory(inv);

				return;
			}
		}
	}

	public ItemStack getItem(String string)
	{
		for(CustomItem i : items)
		{
			if(i.getId().equalsIgnoreCase(string))
			{
				return i.getItem();
			}
		}

		return new ItemStack(Material.STONE);
	}

	public URL getInventoryTop()
	{
		return inventoryTop;
	}

	public void setInventoryTop(URL inventoryTop)
	{
		this.inventoryTop = inventoryTop;
	}

	public URL getInventoryBottom()
	{
		return inventoryBottom;
	}

	public void setInventoryBottom(URL inventoryBottom)
	{
		this.inventoryBottom = inventoryBottom;
	}

	public URL getDefaultInventoryTop()
	{
		return defaultInventoryTop;
	}

	public void setDefaultInventoryTop(URL defaultInventoryTop)
	{
		this.defaultInventoryTop = defaultInventoryTop;
	}

	public URL getDefaultInventoryBottom()
	{
		return defaultInventoryBottom;
	}

	public void setDefaultInventoryBottom(URL defaultInventoryBottom)
	{
		this.defaultInventoryBottom = defaultInventoryBottom;
	}

	public URL getDefaultItem()
	{
		return defaultItem;
	}

	public void setDefaultItem(URL defaultItem)
	{
		this.defaultItem = defaultItem;
	}

	public URL getGlint()
	{
		return glint;
	}

	public void setGlint(URL glint)
	{
		this.glint = glint;
	}

	public URL getGlintMeta()
	{
		return glintMeta;
	}

	public void setGlintMeta(URL glintMeta)
	{
		this.glintMeta = glintMeta;
	}

	public String getDefaultInventoryContentTop()
	{
		return defaultInventoryContentTop;
	}

	public void setDefaultInventoryContentTop(String defaultInventoryContentTop)
	{
		this.defaultInventoryContentTop = defaultInventoryContentTop;
	}

	public String getDefaultInventoryContentBottom()
	{
		return defaultInventoryContentBottom;
	}

	public void setDefaultInventoryContentBottom(String defaultInventoryContentBottom)
	{
		this.defaultInventoryContentBottom = defaultInventoryContentBottom;
	}

	public String getDefaultItemContent()
	{
		return defaultItemContent;
	}

	public void setDefaultItemContent(String defaultItemContent)
	{
		this.defaultItemContent = defaultItemContent;
	}

	public GList<CustomInventory> getInventories()
	{
		return inventories;
	}

	public void setInventories(GList<CustomInventory> inventories)
	{
		this.inventories = inventories;
	}

	public GList<CustomSound> getSounds()
	{
		return sounds;
	}

	public void setSounds(GList<CustomSound> sounds)
	{
		this.sounds = sounds;
	}

	public GList<CustomItem> getItems()
	{
		return items;
	}

	public void setItems(GList<CustomItem> items)
	{
		this.items = items;
	}
}
