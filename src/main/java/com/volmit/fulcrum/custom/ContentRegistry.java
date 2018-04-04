package com.volmit.fulcrum.custom;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.google.common.io.Files;
import com.volmit.dumpster.CNum;
import com.volmit.dumpster.F;
import com.volmit.dumpster.GList;
import com.volmit.dumpster.GMap;
import com.volmit.dumpster.GSet;
import com.volmit.dumpster.JSONArray;
import com.volmit.dumpster.JSONException;
import com.volmit.dumpster.JSONObject;
import com.volmit.dumpster.Profiler;
import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.bukkit.BlockType;
import com.volmit.fulcrum.bukkit.P;
import com.volmit.fulcrum.bukkit.R;
import com.volmit.fulcrum.bukkit.TaskLater;
import com.volmit.fulcrum.bukkit.Worlds;
import com.volmit.fulcrum.event.ContentRecipeRegistryEvent;
import com.volmit.fulcrum.event.ContentRegistryEvent;
import com.volmit.fulcrum.resourcepack.ResourcePack;

public class ContentRegistry implements Listener
{
	private URL inventoryTop;
	private URL inventoryBottom;
	private URL defaultInventoryTop;
	private URL defaultInventoryBottom;
	private URL defaultItem;
	private URL missingTexture;
	private URL defaultSounds;
	private URL newSpawner;
	private URL soundHide;
	private URL soundBell;
	private URL soundWoosh;
	private URL soundSilent;
	private String defaultInventoryContentTop;
	private String defaultInventoryContentBottom;
	private String defaultItemContent;
	private GList<CustomBlock> blocks;
	private GList<CustomInventory> inventories;
	private GList<CustomSound> sounds;
	private GList<CustomAdvancement> advancements;
	private GList<CustomItem> items;
	private GList<SoundReplacement> soundReplacements;
	private GList<ICustomRecipe> recipes;
	private GList<CompilerFlag> flags;
	private GMap<Integer, CustomBlock> superBlocks;
	private GMap<Integer, CustomItem> superItems;
	private GMap<Integer, CustomInventory> superInventories;
	private GMap<ModelType, ModelSet> blockModels;
	private AllocationSpace ass;
	private OverridedAllocationSpace oass;
	private String rid;
	private ResourcePack pack;
	private int msid;

	public ContentRegistry()
	{
		msid = -10000;
		superBlocks = new GMap<Integer, CustomBlock>();
		superItems = new GMap<Integer, CustomItem>();
		superInventories = new GMap<Integer, CustomInventory>();
		inventories = new GList<CustomInventory>();
		sounds = new GList<CustomSound>();
		items = new GList<CustomItem>();
		blocks = new GList<CustomBlock>();
		advancements = new GList<CustomAdvancement>();
		recipes = new GList<ICustomRecipe>();
		soundReplacements = new GList<SoundReplacement>();
		blockModels = new GMap<ModelType, ModelSet>();
		flags = new GList<CompilerFlag>();
		Fulcrum.register(this);
	}

	public boolean hasFlag(CompilerFlag flag)
	{
		return flags.contains(flag);
	}

	public void clean()
	{
		System.out.println("Cleaning " + Worlds.getWorlds().size() + " Worlds");

		for(World i : Worlds.getWorlds())
		{
			clean(i);
		}
	}

	public void clean(World w)
	{
		System.out.println(" Cleaning " + w.getName());
		File f = new File(w.getWorldFolder(), "data" + File.separator + "advancements" + File.separator + "fulcrum");
		delete(f);
	}

	private void delete(File f)
	{
		if(!f.exists())
		{
			return;
		}

		if(f.isDirectory())
		{
			for(File i : f.listFiles())
			{
				delete(i);
			}
		}

		f.delete();
	}

	public void registerModelType(ModelSet set)
	{
		blockModels.put(set.getType(), set);
	}

	public void registerRecipe(ICustomRecipe r)
	{
		recipes.add(r);
	}

	public void registerSoundReplacement(SoundReplacement s)
	{
		soundReplacements.add(s);
	}

	public void registerSound(CustomSound s)
	{
		sounds.add(s);
	}

	public void registerItem(CustomItem s)
	{
		items.add(s);
	}

	public void registerBlock(CustomBlock block)
	{
		blocks.add(block);
	}

	public void registerAdvancement(CustomAdvancement adv)
	{
		advancements.add(adv);
	}

	public void registerInventory(CustomInventory i)
	{
		inventories.add(i);
	}

	public void compileResources(CompilerFlag... flagSet) throws IOException, NoSuchAlgorithmException, InterruptedException
	{
		flags = new GList<CompilerFlag>(flagSet);
		Profiler pr = new Profiler();
		pr.begin();
		Registrar rr = new Registrar();
		ContentRegistryEvent e = new ContentRegistryEvent(rr);
		Fulcrum.callEvent(e);

		if(!rr.connect(this))
		{
			return;
		}

		registerDebug();

		if(hasFlag(CompilerFlag.CONCURRENT_REGISTRY))
		{
			System.out.println("Warning: Using Concurrent Registry");
			loadResources();

			Thread tAdvancements = new Thread("Fulcrum Registry - Advancements")
			{
				@Override
				public void run()
				{
					processAdvancements();
				}
			};

			Thread tBaseContent = new Thread("Fulcrum Registry - Content")
			{
				@Override
				public void run()
				{
					processItems();
					processBlocks();
					processInventories();
				}
			};

			Thread tSounds = new Thread("Fulcrum Registry - Sounds")
			{
				@Override
				public void run()
				{
					try
					{
						processSounds();
					}

					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			};

			Thread tRecipes = new Thread("Fulcrum Registry - Recipes")
			{
				@Override
				public void run()
				{
					processRecipes();
				}
			};

			Thread tPredicates = new Thread("Fulcrum Registry - Predicates")
			{
				@Override
				public void run()
				{
					buildPredicates();
				}
			};

			tBaseContent.start();
			tSounds.start();
			tAdvancements.start();
			tBaseContent.join();
			tRecipes.start();
			tPredicates.start();
			tAdvancements.join();
			tSounds.join();
			tRecipes.join();
			tPredicates.join();
			mergeResources();
		}

		else
		{
			loadResources();
			processAdvancements();
			processItems();
			processBlocks();
			processInventories();
			processSounds();
			buildPredicates();
			mergeResources();
			processRecipes();
		}

		pr.end();
		System.out.println("Items: " + F.f(items.size()));
		System.out.println("Blocks: " + F.f(blocks.size()));
		System.out.println("Sounds: " + F.f(sounds.size()));
		System.out.println("Advancements: " + F.f(advancements.size()));
		System.out.println("Inventories: " + F.f(inventories.size()));
		System.out.println(F.f(pack.size()) + " Resources compiled in " + F.time(pr.getMilliseconds(), 2));
	}

	private void registerDebug()
	{
		registerInventory(new CustomInventory("debug_slots"));
	}

	private void processAdvancements()
	{
		System.out.println("Registering " + advancements.size() + " Advancements");

		for(CustomAdvancement i : advancements)
		{
			i.load();
			System.out.println("  Registered Advancement " + i.getKey().toString());
		}
	}

	private void mergeResources() throws IOException, NoSuchAlgorithmException
	{
		rid = UUID.randomUUID().toString().replaceAll("-", "");
		File fpack = new File(Fulcrum.server.getRoot(), rid + ".zip");
		byte[] hash = pack.writeToArchive(fpack);
		System.out.println("RESULTS:\n" + ass.toString() + "\n" + oass.toString());
		File hasf = new File(Fulcrum.server.getRoot(), "latest-hash.md5");
		boolean needsToUpdate = true;

		if(hasf.exists())
		{
			byte[] oldHash = Files.toByteArray(hasf);
			System.out.println("New Hash: " + Hex.encodeHexString(hash));
			System.out.println("Old Hash: " + Hex.encodeHexString(oldHash));

			if(Arrays.equals(hash, oldHash))
			{
				System.out.println("Last hash is identical to the current hash. Not sending to players.");
				needsToUpdate = false;
			}
		}

		System.out.println("Writing latest hash");
		Files.write(hash, hasf);

		for(Player i : P.onlinePlayers())
		{
			if(needsToUpdate)
			{
				Fulcrum.adapter.sendResourcePackWeb(i, rid + ".zip");
			}

			else
			{
				i.sendMessage("Merged with New Hash: " + Hex.encodeHexString(hash));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void processRecipes()
	{
		ContentRecipeRegistryEvent er = new ContentRecipeRegistryEvent(recipes);
		Fulcrum.callEvent(er);
		int v = 0;

		Bukkit.resetRecipes();

		for(ICustomRecipe i : recipes)
		{
			v++;
			NamespacedKey n = new NamespacedKey(Fulcrum.instance, "recipe-" + v);

			if(i instanceof CustomShapelessRecipe)
			{
				CustomShapelessRecipe s = (CustomShapelessRecipe) i;
				ShapelessRecipe r = new ShapelessRecipe(n, i.getResult());
				List<ItemStack> isx = s.getIngredients().copy();

				try
				{
					Field f = r.getClass().getDeclaredField("ingredients");
					f.setAccessible(true);
					((List<ItemStack>) f.get(r)).addAll(isx);
					Bukkit.getServer().addRecipe(r);
				}

				catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}

			if(i instanceof CustomShapedRecipe)
			{
				CustomShapedRecipe s = (CustomShapedRecipe) i;
				ShapedRecipe r = new ShapedRecipe(n, s.getResult());
				r.shape(s.getPattern());
				GMap<Character, ItemStack> isx = s.getIngredients().copy();

				try
				{
					Field f = r.getClass().getDeclaredField("ingredients");
					f.setAccessible(true);
					((Map<Character, ItemStack>) f.get(r)).putAll(isx);
					Bukkit.getServer().addRecipe(r);
				}

				catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}
		}

		System.out.println("Registered " + getRecipes().size() + " recipes");
	}

	public String getRid()
	{
		return rid;
	}

	private void buildPredicates()
	{
		for(Material i : ass.getIorda())
		{
			JSONObject[] ox = ass.generateNormalModel(i);
			pack.setResource("models/" + ass.getNormalSuperModelName(i) + ".json", ox[1].toString(idf()));
			pack.setResource("models/" + ass.getNormalModelName(i) + ".json", ox[0].toString(idf()));
		}

		for(Material i : ass.getIordb())
		{
			JSONObject[] ox = ass.generateShadedModel(i);
			pack.setResource("models/" + ass.getShadedSuperModelName(i) + ".json", ox[1].toString(idf()));
			pack.setResource("models/" + ass.getShadedModelName(i) + ".json", ox[0].toString(idf()));
		}
	}

	private int idf()
	{
		return hasFlag(CompilerFlag.JSON_MINIFICATION) ? 0 : 4;
	}

	private void loadResources() throws IOException
	{
		pack = new ResourcePack();
		pack.setOptimizePngs(hasFlag(CompilerFlag.PNG_COMPRESSION));

		for(ModelType i : ModelType.values())
		{
			registerModelType(new ModelSet(i));
		}

		soundHide = R.getURL("/assets/sounds/fulcrum/hide.ogg");
		soundSilent = R.getURL("/assets/sounds/fulcrum/silent.ogg");
		soundWoosh = R.getURL("/assets/sounds/fulcrum/woosh.ogg");
		soundBell = R.getURL("/assets/sounds/fulcrum/bell.ogg");
		defaultSounds = R.getURL("/assets/sounds-default.json");
		inventoryTop = R.getURL("/assets/models/inventory/fulcrum_top.json");
		inventoryBottom = R.getURL("/assets/models/inventory/fulcrum_bottom.json");
		defaultItem = R.getURL("/assets/models/item/default_item.json");
		defaultInventoryTop = R.getURL("/assets/models/inventory/default_top.json");
		defaultInventoryBottom = R.getURL("/assets/models/inventory/default_bottom.json");
		newSpawner = R.getURL("/assets/textures/blocks/mob_spawner.png");
		missingTexture = R.getURL("/assets/textures/blocks/unknown.png");
		defaultInventoryContentTop = read(defaultInventoryTop);
		defaultInventoryContentBottom = read(defaultInventoryBottom);
		defaultItemContent = read(defaultItem);

		System.out.println("Compiling " + blockModels.size() + " Model Types");

		for(ModelType i : blockModels.k())
		{
			i.setMc(blockModels.get(i).getModel());
			blockModels.get(i).export(pack, idf());
			System.out.println("  Compiled Model Type " + i.name() + " as " + blockModels.get(i).getFulcrumModel().toString());
		}

		ass = new AllocationSpace(hasFlag(CompilerFlag.PREDICATE_MINIFICATION), hasFlag(CompilerFlag.PREDICATE_CYCLING));
		ass.sacrificeNormal(Material.DIAMOND_HOE, "diamond_hoe", "diamond_hoe");
		ass.sacrificeNormal(Material.IRON_HOE, "iron_hoe", "iron_hoe");
		ass.sacrificeNormal(Material.STONE_HOE, "stone_hoe", "stone_hoe");
		ass.sacrificeNormal(Material.WOOD_HOE, "wooden_hoe", "wood_hoe");
		ass.sacrificeNormal(Material.GOLD_HOE, "golden_hoe", "gold_hoe");
		ass.sacrificeShaded(Material.LEATHER_HELMET, "leather_helmet", "leather_helmet", "leather_helmet_overlay");
		oass = new OverridedAllocationSpace();
		oass.sacrificeNormal(Material.WOOL, 15);
		oass.sacrificeNormal(Material.CONCRETE, 15);
		oass.sacrificeNormal(Material.CONCRETE_POWDER, 15);
		oass.sacrificeNormal(Material.STAINED_CLAY, 15);
		oass.sacrificeNormal(new BlockType(Material.BLACK_GLAZED_TERRACOTTA));
		oass.sacrificeNormal(new BlockType(Material.BLUE_GLAZED_TERRACOTTA));
		oass.sacrificeNormal(new BlockType(Material.BROWN_GLAZED_TERRACOTTA));
		oass.sacrificeNormal(new BlockType(Material.CYAN_GLAZED_TERRACOTTA));
		oass.sacrificeNormal(new BlockType(Material.GRAY_GLAZED_TERRACOTTA));
		oass.sacrificeNormal(new BlockType(Material.GREEN_GLAZED_TERRACOTTA));
		oass.sacrificeNormal(new BlockType(Material.LIGHT_BLUE_GLAZED_TERRACOTTA));
		oass.sacrificeNormal(new BlockType(Material.LIME_GLAZED_TERRACOTTA));
		oass.sacrificeNormal(new BlockType(Material.MAGENTA_GLAZED_TERRACOTTA));
		oass.sacrificeNormal(new BlockType(Material.ORANGE_GLAZED_TERRACOTTA));
		oass.sacrificeNormal(new BlockType(Material.PINK_GLAZED_TERRACOTTA));
		oass.sacrificeNormal(new BlockType(Material.PURPLE_GLAZED_TERRACOTTA));
		oass.sacrificeNormal(new BlockType(Material.RED_GLAZED_TERRACOTTA));
		oass.sacrificeNormal(new BlockType(Material.SILVER_GLAZED_TERRACOTTA));
		oass.sacrificeNormal(new BlockType(Material.WHITE_GLAZED_TERRACOTTA));
		oass.sacrificeAlpha(Material.STAINED_GLASS, 15);
		sounds.removeDuplicates();
		blocks.removeDuplicates();
		inventories.removeDuplicates();
		items.removeDuplicates();
		pack.setResource("sounds/fulcrum/woosh.ogg", soundWoosh);
		pack.setResource("sounds/fulcrum/hide.ogg", soundHide);
		pack.setResource("sounds/fulcrum/silent.ogg", soundSilent);
		pack.setResource("sounds/fulcrum/bell.ogg", soundBell);
		pack.setResource("models/inventory/fulcrum_top.json", new JSONObject(read(inventoryTop)).toString(idf()));
		pack.setResource("models/inventory/fulcrum_bottom.json", new JSONObject(read(inventoryBottom)).toString(idf()));
		pack.setResource("textures/blocks/mob_spawner.png", newSpawner);
		pack.setResource("textures/blocks/unknown.png", missingTexture);
		pack.setResource("textures/items/unknown.png", missingTexture);
	}

	private void processSounds() throws JSONException, IOException
	{
		JSONObject desound = new JSONObject(read(defaultSounds));
		JSONObject soundx = new JSONObject();
		CustomSound soundSilent = new CustomSound("f.s").addSound("fulcrum/silent");
		CustomSound soundBell = new CustomSound("f.b").addSound("fulcrum/bell");
		CustomSound soundIn = new CustomSound("f.i").addSound("fulcrum/woosh");
		CustomSound soundOut = new CustomSound("f.o").addSound("fulcrum/hide");
		registerSoundReplacement(new SoundReplacement("block.metal.step", soundSilent));
		registerSoundReplacement(new SoundReplacement("block.metal.break", soundSilent));
		registerSoundReplacement(new SoundReplacement("block.metal.fall", soundSilent));
		registerSoundReplacement(new SoundReplacement("block.metal.hit", soundSilent));
		registerSoundReplacement(new SoundReplacement("block.metal.place", soundSilent));
		registerSoundReplacement(new SoundReplacement("block.cloth.step", soundSilent));
		registerSoundReplacement(new SoundReplacement("block.cloth.break", soundSilent));
		registerSoundReplacement(new SoundReplacement("block.cloth.fall", soundSilent));
		registerSoundReplacement(new SoundReplacement("block.cloth.hit", soundSilent));
		registerSoundReplacement(new SoundReplacement("block.cloth.place", soundSilent));
		registerSoundReplacement(new SoundReplacement("block.stone.step", soundSilent));
		registerSoundReplacement(new SoundReplacement("block.stone.break", soundSilent));
		registerSoundReplacement(new SoundReplacement("block.stone.fall", soundSilent));
		registerSoundReplacement(new SoundReplacement("block.stone.hit", soundSilent));
		registerSoundReplacement(new SoundReplacement("block.stone.place", soundSilent));
		registerSoundReplacement(new SoundReplacement("block.glass.step", soundSilent));
		registerSoundReplacement(new SoundReplacement("block.glass.break", soundSilent));
		registerSoundReplacement(new SoundReplacement("block.glass.fall", soundSilent));
		registerSoundReplacement(new SoundReplacement("block.glass.hit", soundSilent));
		registerSoundReplacement(new SoundReplacement("block.glass.place", soundSilent));
		registerSoundReplacement(new SoundReplacement("item.hoe.till", soundSilent));
		registerSoundReplacement(new SoundReplacement("entity.item.pickup", soundSilent));
		registerSoundReplacement(new SoundReplacement("item.hoe.till", soundSilent));
		registerSoundReplacement(new SoundReplacement("ui.toast.challenge_complete", soundBell));
		registerSoundReplacement(new SoundReplacement("ui.toast.in", soundIn));
		registerSoundReplacement(new SoundReplacement("ui.toast.out", soundOut));
		System.out.println("Reading " + F.f(desound.keySet().size()) + " default sound entries");
		System.out.println("Processing " + F.f(soundReplacements.size()) + " sound replacements");
		GSet<String> ffv = new GSet<String>(desound.keySet());
		GList<Thread> tvm = new GList<Thread>();
		for(String i : ffv)
		{
			for(SoundReplacement j : soundReplacements)
			{
				if(j.getNode().equals(i))
				{
					if(!hasFlag(CompilerFlag.CONCURRENT_REGISTRY))
					{
						remap(desound, i, soundx, j);
					}

					else
					{
						if(j.getNewSound().getSoundPaths().k().size() == 1)
						{
							remap(desound, i, soundx, j);
						}

						else
						{
							tvm.add(new Thread("Fulcrum Registry - Sound Remap " + (tvm.size() + 1))
							{
								@Override
								public void run()
								{
									remap(desound, i, soundx, j);
								}
							});
						}
					}
				}
			}
		}

		for(Thread i : tvm)
		{
			i.start();
		}

		for(Thread i : tvm)
		{
			try
			{
				i.join();
			}

			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		System.out.println("Registering " + F.f(sounds.size()) + " custom sounds");

		for(CustomSound i : sounds)
		{
			System.out.println("  Registering Sound: " + i.getNode());
			for(String j : i.getSoundPaths().k())
			{
				pack.setResource("sounds/" + j, i.getSoundPaths().get(j));
			}

			i.toJson(soundx);
		}

		pack.setResource("sounds.json", soundx.toString(idf()));
	}

	private void remap(JSONObject desound, String i, JSONObject soundx, SoundReplacement j)
	{
		JSONObject mod = new JSONObject(desound.getJSONObject(i).toString(idf()));
		JSONObject old = new JSONObject(desound.getJSONObject(i).toString(idf()));
		GList<String> keys = j.getNewSound().getSoundPaths().k();
		String d = "[";
		CNum c = new CNum(keys.size() - 1);
		int vv = 5000;

		if(c.getMax() == 0)
		{
			d += F.repeat("\"" + keys.get(0).replace(".ogg", "") + "\",", vv);
		}

		else
		{
			for(int k = 0; k < vv; k++)
			{
				c.add(1);
				d += "\"" + keys.get(c.get()).replace(".ogg", "") + "\",";
			}
		}

		d = d.substring(0, d.length() - 1) + "]";
		mod.put("sounds", new JSONArray(d));
		soundx.put(j.getReplacement(), old);
		soundx.put(i, mod);
		System.out.println("  Remapped Sound " + j.getNode() + " -> " + j.getReplacement());
	}

	private void processInventories()
	{
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

			String modelTop = new JSONObject(defaultInventoryContentTop).toString(idf()).replace("$id", i.getId());
			String modelBottom = new JSONObject(defaultInventoryContentBottom).toString(idf()).replace("$id", i.getId());

			if(ut != null)
			{
				i.setTop(true);
				pack.setResource("models/inventory/" + i.getId() + "_top.json", modelTop);
				pack.setResource("textures/inventories/" + i.getId() + "_top.png", ut);
				AllocatedNode idx = ass.allocateNormal("inventory/" + i.getId() + "_top");
				superInventories.put(idx.getSuperid(), i);
				i.setTypeTop(idx.getMaterial());
				i.setDurabilityTop((short) idx.getId());
				i.setSuperIDTop(idx.getSuperid());
				System.out.println("   Registered Inventory TOP " + i.getId() + " as " + idx.getMaterial().toString() + ":" + idx.getId());
			}

			if(ub != null)
			{
				i.setBottom(true);
				pack.setResource("models/inventory/" + i.getId() + "_bottom.json", modelBottom);
				pack.setResource("textures/inventories/" + i.getId() + "_bottom.png", ub);
				AllocatedNode idx = ass.allocateNormal("inventory/" + i.getId() + "_bottom");
				superInventories.put(idx.getSuperid(), i);
				i.setTypeBottom(idx.getMaterial());
				i.setDurabilityBottom((short) idx.getId());
				i.setSuperIDBottom(idx.getSuperid());
				System.out.println("   Registered Inventory BOTTOM " + i.getId() + " as " + idx.getMaterial().toString() + ":" + idx.getId());
			}
		}
	}

	private void processBlocks()
	{
		System.out.println("Registering " + blocks.size() + " Blocks");

		for(CustomBlock i : blocks)
		{
			if(i.getBlockType().equals(BlockRegistryType.BUILDING_BLOCK))
			{
				BlockType overriding = oass.allocate(i);

				if(overriding == null)
				{
					continue;
				}

				String ds = oass.adapt(overriding);
				String t = "/assets/textures/blocks/" + i.getId() + ".png";
				URL texture = i.getClass().getResource(t);

				if(texture != null)
				{
					pack.setResource("textures/blocks/" + ds + ".png", texture);
				}

				else
				{
					pack.setResource("textures/blocks/" + ds + ".png", missingTexture);
					System.out.println("  WARNING: " + i.getId() + " MISSING TEXTURE: " + t);
				}

				i.setDurabilityLock((short) -1);
				i.setType(overriding.getMaterial());
				i.setSuperID(msid--);
				i.setData(overriding.getData());
				System.out.println("  Registered BUILDING Block " + i.getId() + " to " + i.getType() + ":" + i.getData() + " wtx = " + ds);
			}

			else
			{
				ModelType rt = i.getRenderType();
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
						System.out.println("  WARNING: " + i.getId() + " MISSING TEXTURE: " + t);
					}
				}

				if(model != null)
				{
					pack.setResource("models/block/" + i.getId() + ".json", model);
				}

				else
				{
					String newModel = rt.getModelContent().replaceAll("\\Q$id\\E", i.getId());
					pack.setResource("models/block/" + i.getId() + ".json", new JSONObject(newModel).toString(idf()));
				}

				AllocatedNode idx = i.isShaded() ? ass.allocateShaded("block/" + i.getId()) : ass.allocateNormal("block/" + i.getId());
				i.setDurabilityLock((short) idx.getId());
				i.setType(idx.getMaterial());
				superBlocks.put(idx.getSuperid(), i);
				i.setSuperID(idx.getSuperid());
				i.setMatt(ass.getNameForMaterial(i.getType()));
				System.out.println("  Registered TILE Block " + i.getId() + " to " + i.getType() + ":" + i.getDurabilityLock());
			}
		}
	}

	private void processItems()
	{
		System.out.println("Registering " + items.size() + " Items");

		for(CustomItem i : items)
		{
			JSONObject o = new JSONObject(defaultItemContent);
			JSONObject t = o.getJSONObject("textures");

			for(int j = 0; j < i.getLayers(); j++)
			{
				URL url = i.getClass().getResource("/assets/textures/items/" + i.getId() + "_" + j + ".png");

				if(url == null && j == 0)
				{
					url = i.getClass().getResource("/assets/textures/items/" + i.getId() + ".png");
				}

				if(url == null)
				{
					System.out.println(" Unable to locate item texture " + i.getId() + "_" + j + ".png");
					url = missingTexture;
				}

				pack.setResource("textures/items/" + i.getId() + "_" + j + ".png", url);
				t.put("layer" + j, "items/" + i.getId() + "_" + j);
				o.put("textures", t);
			}

			pack.setResource("models/item/" + i.getId() + ".json", o.toString(idf()));
			AllocatedNode node = ass.allocateNormal("item/" + i.getId());
			System.out.println("  Registered Item " + i.getId() + " to " + node.getMaterial() + ":" + node.getId());
			superItems.put(node.getSuperid(), i);
			i.setType(node.getMaterial());
			i.setDurability((short) node.getId());
			i.setSuperID(node.getSuperid());
		}
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

	public static String read(URL url) throws IOException
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

	public GList<CustomBlock> getBlocks()
	{
		return blocks;
	}

	public GList<CustomInventory> getInventories()
	{
		return inventories;
	}

	public GList<CustomSound> getSounds()
	{
		return sounds;
	}

	public GList<CustomItem> getItems()
	{
		return items;
	}

	public GList<ICustomRecipe> getRecipes()
	{
		return recipes;
	}

	public GMap<Integer, CustomBlock> getSuperBlocks()
	{
		return superBlocks;
	}

	public GMap<Integer, CustomItem> getSuperItems()
	{
		return superItems;
	}

	public GMap<Integer, CustomInventory> getSuperInventories()
	{
		return superInventories;
	}

	public AllocationSpace ass()
	{
		return ass;
	}

	public GList<CustomAdvancement> getAdvancements()
	{
		return advancements;
	}

	public OverridedAllocationSpace getOass()
	{
		return oass;
	}
}
