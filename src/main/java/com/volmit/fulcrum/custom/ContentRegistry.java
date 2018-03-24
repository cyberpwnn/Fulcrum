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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.google.common.io.Files;
import com.volmit.dumpster.F;
import com.volmit.dumpster.GList;
import com.volmit.dumpster.GMap;
import com.volmit.dumpster.JSONArray;
import com.volmit.dumpster.JSONException;
import com.volmit.dumpster.JSONObject;
import com.volmit.dumpster.Profiler;
import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.bukkit.P;
import com.volmit.fulcrum.bukkit.R;
import com.volmit.fulcrum.bukkit.TaskLater;
import com.volmit.fulcrum.event.ContentRecipeRegistryEvent;
import com.volmit.fulcrum.event.ContentRegistryEvent;
import com.volmit.fulcrum.resourcepack.ResourcePack;

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
	private URL soundHide;
	private URL soundBell;
	private URL soundWoosh;
	private URL soundSilent;
	private String defaultInventoryContentTop;
	private String defaultInventoryContentBottom;
	private String defaultItemContent;
	private String defaultModelContent;
	private String defaultModelContentAll;
	private String defaultModelContentTop;
	private String defaultModelContentBottomTop;
	private String defaultModelContentColumn;
	private String defaultModelContentPedistal;
	private GList<CustomBlock> blocks;
	private GList<CustomInventory> inventories;
	private GList<CustomSound> sounds;
	private GList<CustomItem> items;
	private GList<ICustomRecipe> recipes;
	private GMap<Integer, CustomBlock> superBlocks;
	private GMap<Integer, CustomItem> superItems;
	private GMap<Integer, CustomInventory> superInventories;
	private AllocationSpace ass;
	private String rid;
	private ResourcePack pack;

	public ContentRegistry()
	{
		superBlocks = new GMap<Integer, CustomBlock>();
		superItems = new GMap<Integer, CustomItem>();
		superInventories = new GMap<Integer, CustomInventory>();

		inventories = new GList<CustomInventory>();
		sounds = new GList<CustomSound>();
		items = new GList<CustomItem>();
		blocks = new GList<CustomBlock>();
		recipes = new GList<ICustomRecipe>();
		Fulcrum.register(this);
	}

	public void registerRecipe(ICustomRecipe r)
	{
		recipes.add(r);
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

	public void registerInventory(CustomInventory i)
	{
		inventories.add(i);
	}

	public void compileResources() throws IOException, NoSuchAlgorithmException
	{
		Profiler pr = new Profiler();
		pr.begin();
		Registrar rr = new Registrar();
		ContentRegistryEvent e = new ContentRegistryEvent(rr);
		Fulcrum.callEvent(e);

		if(rr.connect(this))
		{
			loadResources();
			processItems();
			processBlocks();
			processInventories();
			processSounds();
			buildPredicates();
			mergeResources();
		}

		processRecipes();
		pr.end();
		System.out.println("Items: " + F.f(items.size()));
		System.out.println("Blocks: " + F.f(blocks.size()));
		System.out.println("Sounds: " + F.f(sounds.size()));
		System.out.println("Inventories: " + F.f(inventories.size()));
		System.out.println(F.f(pack.size()) + " Resources compiled in " + F.time(pr.getMilliseconds(), 0));
	}

	private void mergeResources() throws IOException, NoSuchAlgorithmException
	{
		rid = UUID.randomUUID().toString().replaceAll("-", "");
		File fpack = new File(Fulcrum.server.getRoot(), rid + ".zip");
		byte[] hash = pack.writeToArchive(fpack);
		System.out.println("RESULTS:\n" + ass.toString());
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
			pack.setResource("models/" + ass.getNormalSuperModelName(i) + ".json", ox[1].toString());
			pack.setResource("models/" + ass.getNormalModelName(i) + ".json", ox[0].toString());
		}

		for(Material i : ass.getIordb())
		{
			JSONObject[] ox = ass.generateShadedModel(i);
			pack.setResource("models/" + ass.getShadedSuperModelName(i) + ".json", ox[1].toString());
			pack.setResource("models/" + ass.getShadedModelName(i) + ".json", ox[0].toString());
		}
	}

	private void loadResources() throws IOException
	{
		pack = new ResourcePack();
		soundHide = R.getURL("/assets/sounds/fulcrum/hide.ogg");
		soundSilent = R.getURL("/assets/sounds/fulcrum/silent.ogg");
		soundWoosh = R.getURL("/assets/sounds/fulcrum/woosh.ogg");
		soundBell = R.getURL("/assets/sounds/fulcrum/bell.ogg");
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
		sounds.removeDuplicates();
		blocks.removeDuplicates();
		inventories.removeDuplicates();
		items.removeDuplicates();
		pack.setResource("sounds/fulcrum/woosh.ogg", soundWoosh);
		pack.setResource("sounds/fulcrum/hide.ogg", soundHide);
		pack.setResource("sounds/fulcrum/silent.ogg", soundSilent);
		pack.setResource("sounds/fulcrum/bell.ogg", soundBell);
		pack.setResource("models/inventory/fulcrum_top.json", new JSONObject(read(inventoryTop)).toString());
		pack.setResource("models/inventory/fulcrum_bottom.json", new JSONObject(read(inventoryBottom)).toString());
		pack.setResource("models/block/fulcrum_cube.json", new JSONObject(read(cube)).toString());
		pack.setResource("models/block/fulcrum_cube_all.json", new JSONObject(read(cubeAll)).toString());
		pack.setResource("models/block/fulcrum_cube_top.json", new JSONObject(read(cubeTop)).toString());
		pack.setResource("models/block/fulcrum_cube_bottom_top.json", new JSONObject(read(cubeBottomTop)).toString());
		pack.setResource("models/block/fulcrum_cube_column.json", new JSONObject(read(cubeColumn)).toString());
		pack.setResource("models/block/fulcrum_pedistal.json", new JSONObject(read(cubePedistal)).toString());
		pack.setResource("textures/blocks/mob_spawner.png", newSpawner);
		pack.setResource("textures/blocks/unknown.png", missingTexture);
		pack.setResource("textures/items/unknown.png", missingTexture);
	}

	private void processSounds() throws JSONException, IOException
	{
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
				soundx.put(i.replace("block.", "m.block."), old);
				soundx.put(i, mod);
				System.out.println("  Remapped Sound " + i.replace("block.", "m.block."));
			}

			if(i.equalsIgnoreCase("item.hoe.till"))
			{
				JSONObject mod = new JSONObject(desound.getJSONObject(i).toString());
				JSONObject old = new JSONObject(desound.getJSONObject(i).toString());
				mod.put("sounds", new JSONArray("[" + F.repeat("\"fulcrum/silent\",", 5000) + "]"));
				soundx.put("m.item.hoe.till", old);
				soundx.put(i, mod);
				System.out.println("  Remapped Sound " + "m.item.hoe.till");
			}

			if(i.equalsIgnoreCase("entity.item.pickup"))
			{
				JSONObject mod = new JSONObject(desound.getJSONObject(i).toString());
				JSONObject old = new JSONObject(desound.getJSONObject(i).toString());
				mod.put("sounds", new JSONArray("[" + F.repeat("\"fulcrum/silent\",", 5000) + "]"));
				soundx.put("m.entity.item.pickup", old);
				soundx.put(i, mod);
				System.out.println("  Remapped Sound " + "m.entity.item.pickup");
			}

			if(i.equalsIgnoreCase("item.armor.equip_leather"))
			{
				JSONObject mod = new JSONObject(desound.getJSONObject(i).toString());
				JSONObject old = new JSONObject(desound.getJSONObject(i).toString());
				mod.put("sounds", new JSONArray("[" + F.repeat("\"fulcrum/silent\",", 5000) + "]"));
				soundx.put("m.item.armor.equip_leather", old);
				soundx.put(i, mod);
				System.out.println("  Remapped Sound " + "m.item.armor.equip_leather");
			}

			if(i.equalsIgnoreCase("ui.toast.challenge_complete"))
			{
				JSONObject mod = new JSONObject(desound.getJSONObject(i).toString());
				JSONObject old = new JSONObject(desound.getJSONObject(i).toString());
				mod.put("sounds", new JSONArray("[" + F.repeat("\"fulcrum/bell\",", 5000) + "]"));
				soundx.put("m.ui.toast.challenge_complete", old);
				soundx.put(i, mod);
				System.out.println("  Remapped Sound " + "m.ui.toast.challenge_complete");
			}

			if(i.equalsIgnoreCase("ui.toast.in"))
			{
				JSONObject mod = new JSONObject(desound.getJSONObject(i).toString());
				JSONObject old = new JSONObject(desound.getJSONObject(i).toString());
				mod.put("sounds", new JSONArray("[" + F.repeat("\"fulcrum/woosh\",", 5000) + "]"));
				soundx.put("m.ui.toast.in", old);
				soundx.put(i, mod);
				System.out.println("  Remapped Sound " + "m.ui.toast.in");
			}

			if(i.equalsIgnoreCase("ui.toast.out"))
			{
				JSONObject mod = new JSONObject(desound.getJSONObject(i).toString());
				JSONObject old = new JSONObject(desound.getJSONObject(i).toString());
				mod.put("sounds", new JSONArray("[" + F.repeat("\"fulcrum/hide\",", 5000) + "]"));
				soundx.put("m.ui.toast.out", old);
				soundx.put(i, mod);
				System.out.println("  Remapped Sound " + "m.ui.toast.out");
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

		pack.setResource("sounds.json", soundx.toString());
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

			String modelTop = new JSONObject(defaultInventoryContentTop).toString(0).replace("$id", i.getId());
			String modelBottom = new JSONObject(defaultInventoryContentBottom).toString(0).replace("$id", i.getId());

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
		for(CustomBlock i : blocks)
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
			superBlocks.put(idx.getSuperid(), i);
			i.setSuperID(idx.getSuperid());
			i.setMatt(ass.getNameForMaterial(i.getType()));
		}
	}

	private void processItems()
	{
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

	private String read(URL url) throws IOException
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
}
