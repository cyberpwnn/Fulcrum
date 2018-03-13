package com.volmit.fulcrum.custom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.lang.F;
import com.volmit.fulcrum.lang.GList;
import com.volmit.fulcrum.lang.GMap;
import com.volmit.fulcrum.lang.JSONArray;
import com.volmit.fulcrum.lang.JSONObject;
import com.volmit.fulcrum.resourcepack.ResourcePack;

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
	private URL sounds;
	private URL newSpawner;
	private String defaultModelContent;
	private String defaultModelContentAll;
	private String defaultModelContentTop;
	private String defaultModelContentBottomTop;
	private String defaultModelContentColumn;
	private String defaultModelContentPedistal;
	private GList<ICustomBlock> blocks;
	private GMap<String, ICustomBlock> idblocks;
	private GMap<Short, String> shortid;
	private JSONObject soundContent;
	private GList<SoundModification> soundMods;
	private GList<CustomSound> registerSounds;
	private PredicateGenerator gen;

	public ContentRegistry()
	{
		soundMods = new GList<SoundModification>();
		registerSounds = new GList<CustomSound>();
		blocks = new GList<ICustomBlock>();
		idblocks = new GMap<String, ICustomBlock>();
		shortid = new GMap<Short, String>();
		sounds = Fulcrum.class.getResource("/assets/sounds.json");
		cube = Fulcrum.class.getResource("/assets/models/block/fulcrum_cube.json");
		cubeAll = Fulcrum.class.getResource("/assets/models/block/fulcrum_cube_all.json");
		cubeBottomTop = Fulcrum.class.getResource("/assets/models/block/fulcrum_cube_bottom_top.json");
		cubeColumn = Fulcrum.class.getResource("/assets/models/block/fulcrum_cube_column.json");
		cubeTop = Fulcrum.class.getResource("/assets/models/block/fulcrum_cube_top.json");
		cubePedistal = Fulcrum.class.getResource("/assets/models/block/fulcrum_pedestal.json");
		defaultModel = Fulcrum.class.getResource("/assets/models/block/default_cube.json");
		defaultModelAll = Fulcrum.class.getResource("/assets/models/block/default_cube_all.json");
		defaultModelBottomTop = Fulcrum.class.getResource("/assets/models/block/default_cube_bottom_top.json");
		defaultModelColumn = Fulcrum.class.getResource("/assets/models/block/default_cube_column.json");
		defaultModelTop = Fulcrum.class.getResource("/assets/models/block/default_cube_top.json");
		defaultModelPedistal = Fulcrum.class.getResource("/assets/models/block/default_pedistal.json");
		newSpawner = Fulcrum.class.getResource("/assets/textures/blocks/mob_spawner.png");
		missingTexture = Fulcrum.class.getResource("/assets/textures/blocks/unknown.png");
		gen = new PredicateGenerator(Material.DIAMOND_HOE, "item/diamond_hoe");
		Fulcrum.register(this);
		makeSilent("block.metal.break");
		makeSilent("block.metal.fall");
		makeSilent("block.metal.hit");
		makeSilent("block.metal.place");
		makeSilent("block.metal.step");
		makeSilent("block.cloth.break");
		makeSilent("block.cloth.fall");
		makeSilent("block.cloth.hit");
		makeSilent("block.cloth.place");
		makeSilent("block.cloth.step");
		makeSilent("block.glsss.break");
		makeSilent("block.glsss.fall");
		makeSilent("block.glsss.hit");
		makeSilent("block.glsss.place");
		makeSilent("block.glsss.step");
		makeSilent("block.gravel.break");
		makeSilent("block.gravel.fall");
		makeSilent("block.gravel.hit");
		makeSilent("block.gravel.place");
		makeSilent("block.gravel.step");
		makeSilent("block.grass.break");
		makeSilent("block.grass.fall");
		makeSilent("block.grass.hit");
		makeSilent("block.grass.place");
		makeSilent("block.grass.step");
		makeSilent("block.ladder.break");
		makeSilent("block.ladder.fall");
		makeSilent("block.ladder.hit");
		makeSilent("block.ladder.place");
		makeSilent("block.ladder.step");
		makeSilent("block.sand.break");
		makeSilent("block.sand.fall");
		makeSilent("block.sand.hit");
		makeSilent("block.sand.place");
		makeSilent("block.sand.step");
		makeSilent("block.snow.break");
		makeSilent("block.snow.fall");
		makeSilent("block.snow.hit");
		makeSilent("block.snow.place");
		makeSilent("block.snow.step");
		makeSilent("block.stone.break");
		makeSilent("block.stone.fall");
		makeSilent("block.stone.hit");
		makeSilent("block.stone.place");
		makeSilent("block.stone.step");
	}

	public String getAudibleVersion(String soundNode)
	{
		return soundNode + ".restored";
	}

	public void makeSilent(String soundNode)
	{
		addSoundModification(new SoundModification()
		{
			@Override
			public JSONObject modify(JSONObject sounds)
			{
				if(sounds.has(soundNode))
				{
					JSONObject o = sounds.getJSONObject(soundNode);
					JSONObject ox = new JSONObject();
					JSONArray ja = new JSONArray();
					ja.put("fulcrum/silent");

					for(String i : o.keySet())
					{
						ox.put(i, o.get(i));
					}

					o.put("sounds", ja);
					sounds.put(soundNode, o);
					sounds.put(soundNode + ".restored", ox);
				}

				return sounds;
			}
		});
	}

	public void registerSound(CustomSound s)
	{
		registerSounds.add(s);
	}

	public void addSoundModification(SoundModification mod)
	{
		soundMods.add(mod);
	}

	public void set(Location l, String id)
	{
		ICustomBlock block = idblocks.get(id);

		if(block != null)
		{
			block.set(l);
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

	public void compileResources(ResourcePack pack) throws IOException
	{
		soundContent = new JSONObject(read(sounds));

		for(CustomSound i : registerSounds)
		{
			for(URL j : i.getSoundPaths().k())
			{
				pack.setResource("sounds/" + i.getSoundPaths().get(j), j);
			}

			soundMods.add(new SoundModification()
			{
				@Override
				public JSONObject modify(JSONObject sounds)
				{
					i.toJson(sounds);

					return sounds;
				}
			});
		}

		for(SoundModification i : soundMods)
		{
			soundContent = i.modify(soundContent);
		}

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
		pack.setResource("sounds.json", soundContent.toString(4));
		pack.setResource("sounds/fulcrum/silent.ogg", Fulcrum.class.getResource("/assets/sounds/fulcrum/silent.ogg"));
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
