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
import com.volmit.fulcrum.resourcepack.ResourcePack;

public class BlockRegistry implements Listener
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
	private PredicateGenerator gen;

	public BlockRegistry()
	{
		blocks = new GList<ICustomBlock>();
		idblocks = new GMap<String, ICustomBlock>();
		shortid = new GMap<Short, String>();
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
		idblocks.clear();
		shortid.clear();
		defaultModelContent = read(defaultModel);
		defaultModelContentAll = read(defaultModelAll);
		defaultModelContentTop = read(defaultModelTop);
		defaultModelContentBottomTop = read(defaultModelBottomTop);
		defaultModelContentColumn = read(defaultModelColumn);
		defaultModelContentPedistal = read(defaultModelPedistal);
		pack.setResource("models/block/fulcrum_cube.json", cube);
		pack.setResource("models/block/fulcrum_cube_all.json", cubeAll);
		pack.setResource("models/block/fulcrum_cube_top.json", cubeTop);
		pack.setResource("models/block/fulcrum_cube_bottom_top.json", cubeBottomTop);
		pack.setResource("models/block/fulcrum_cube_column.json", cubeColumn);
		pack.setResource("models/block/fulcrum_pedistal.json", cubePedistal);
		pack.setResource("textures/blocks/mob_spawner.png", newSpawner);
		pack.setResource("textures/blocks/unknown.png", missingTexture);
		pack.setResource("textures/items/unknown.png", missingTexture);

		int max = 0;
		for(ICustomBlock i : blocks)
		{
			if(i.getRenderType().equals(BlockRenderType.ALL))
			{
				String t = "/assets/textures/blocks/" + i.getId() + ".png";
				String m = "/assets/models/block/" + i.getId() + ".json";
				URL texture = i.getClass().getResource(t);
				URL model = i.getClass().getResource(m);

				if(texture != null)
				{
					pack.setResource("textures/blocks/" + i.getId() + ".png", texture);
				}

				else
				{
					pack.setResource("textures/blocks/" + i.getId() + ".png", missingTexture);
					System.out.println("WARNING: " + i.getId() + " MISSING TEXTURE: " + t);
				}

				if(model != null)
				{
					pack.setResource("models/block/" + i.getId() + ".json", model);
				}

				else
				{
					String newModel = defaultModelContentAll.replaceAll("\\Q$id\\E", i.getId());
					pack.setResource("models/block/" + i.getId() + ".json", newModel);
				}
			}

			else if(i.getRenderType().equals(BlockRenderType.PEDISTAL))
			{
				String tu = "/assets/textures/blocks/" + i.getId() + "_top.png";
				String td = "/assets/textures/blocks/" + i.getId() + "_bottom.png";
				String tp = "/assets/textures/blocks/" + i.getId() + "_pillar.png";
				String m = "/assets/models/block/" + i.getId() + ".json";
				URL textureTop = i.getClass().getResource(tu);
				URL textureBottom = i.getClass().getResource(td);
				URL texturePillar = i.getClass().getResource(tp);
				URL model = i.getClass().getResource(m);

				if(textureTop != null)
				{
					pack.setResource("textures/blocks/" + i.getId() + "_top.png", textureTop);
				}

				else
				{
					pack.setResource("textures/blocks/" + i.getId() + "_top.png", missingTexture);
					System.out.println("WARNING: " + i.getId() + " MISSING TEXTURE: " + tu);
				}

				if(textureBottom != null)
				{
					pack.setResource("textures/blocks/" + i.getId() + "_bottom.png", textureBottom);
				}

				else
				{
					pack.setResource("textures/blocks/" + i.getId() + "_bottom.png", missingTexture);
					System.out.println("WARNING: " + i.getId() + " MISSING TEXTURE: " + td);
				}

				if(texturePillar != null)
				{
					pack.setResource("textures/blocks/" + i.getId() + "_pillar.png", texturePillar);
				}

				else
				{
					pack.setResource("textures/blocks/" + i.getId() + "_pillar.png", missingTexture);
					System.out.println("WARNING: " + i.getId() + " MISSING TEXTURE: " + tp);
				}

				if(model != null)
				{
					pack.setResource("models/block/" + i.getId() + ".json", model);
				}

				else
				{
					String newModel = defaultModelContentPedistal.replaceAll("\\Q$id\\E", i.getId());
					pack.setResource("models/block/" + i.getId() + ".json", newModel);
				}
			}

			else if(i.getRenderType().equals(BlockRenderType.MANUAL))
			{
				String tu = "/assets/textures/blocks/" + i.getId() + "_up.png";
				String td = "/assets/textures/blocks/" + i.getId() + "_down.png";
				String tn = "/assets/textures/blocks/" + i.getId() + "_north.png";
				String tw = "/assets/textures/blocks/" + i.getId() + "_west.png";
				String te = "/assets/textures/blocks/" + i.getId() + "_east.png";
				String ts = "/assets/textures/blocks/" + i.getId() + "_south.png";
				String m = "/assets/models/block/" + i.getId() + ".json";
				URL textureUp = i.getClass().getResource(tu);
				URL textureDown = i.getClass().getResource(td);
				URL textureNorth = i.getClass().getResource(tn);
				URL textureWest = i.getClass().getResource(tw);
				URL textureEast = i.getClass().getResource(te);
				URL textureSouth = i.getClass().getResource(ts);
				URL model = i.getClass().getResource(m);

				if(textureUp != null)
				{
					pack.setResource("textures/blocks/" + i.getId() + "_up.png", textureUp);
				}

				else
				{
					pack.setResource("textures/blocks/" + i.getId() + "_up.png", missingTexture);
					System.out.println("WARNING: " + i.getId() + " MISSING TEXTURE: " + tu);
				}

				if(textureDown != null)
				{
					pack.setResource("textures/blocks/" + i.getId() + "_down.png", textureDown);
				}

				else
				{
					pack.setResource("textures/blocks/" + i.getId() + "_down.png", missingTexture);
					System.out.println("WARNING: " + i.getId() + " MISSING TEXTURE: " + td);
				}

				if(textureNorth != null)
				{
					pack.setResource("textures/blocks/" + i.getId() + "_north.png", textureNorth);
				}

				else
				{
					pack.setResource("textures/blocks/" + i.getId() + "_north.png", missingTexture);
					System.out.println("WARNING: " + i.getId() + " MISSING TEXTURE: " + tn);
				}

				if(textureSouth != null)
				{
					pack.setResource("textures/blocks/" + i.getId() + "_south.png", textureSouth);
				}

				else
				{
					pack.setResource("textures/blocks/" + i.getId() + "_south.png", missingTexture);
					System.out.println("WARNING: " + i.getId() + " MISSING TEXTURE: " + ts);
				}

				if(textureEast != null)
				{
					pack.setResource("textures/blocks/" + i.getId() + "_east.png", textureEast);
				}

				else
				{
					pack.setResource("textures/blocks/" + i.getId() + "_east.png", missingTexture);
					System.out.println("WARNING: " + i.getId() + " MISSING TEXTURE: " + te);
				}

				if(textureWest != null)
				{
					pack.setResource("textures/blocks/" + i.getId() + "_west.png", textureWest);
				}

				else
				{
					pack.setResource("textures/blocks/" + i.getId() + "_west.png", missingTexture);
					System.out.println("WARNING: " + i.getId() + " MISSING TEXTURE: " + tw);
				}

				if(model != null)
				{
					pack.setResource("models/block/" + i.getId() + ".json", model);
				}

				else
				{
					String newModel = defaultModelContent.replaceAll("\\Q$id\\E", i.getId());
					pack.setResource("models/block/" + i.getId() + ".json", newModel);
				}
			}

			else if(i.getRenderType().equals(BlockRenderType.TOP_BOTTOM))
			{
				String tu = "/assets/textures/blocks/" + i.getId() + "_top.png";
				String td = "/assets/textures/blocks/" + i.getId() + "_bottom.png";
				String ts = "/assets/textures/blocks/" + i.getId() + "_side.png";
				String m = "/assets/models/block/" + i.getId() + ".json";
				URL textureUp = i.getClass().getResource(tu);
				URL textureDown = i.getClass().getResource(td);
				URL textureSide = i.getClass().getResource(ts);
				URL model = i.getClass().getResource(m);

				if(textureUp != null)
				{
					pack.setResource("textures/blocks/" + i.getId() + "_top.png", textureUp);
				}

				else
				{
					pack.setResource("textures/blocks/" + i.getId() + "_top.png", missingTexture);
					System.out.println("WARNING: " + i.getId() + " MISSING TEXTURE: " + tu);
				}

				if(textureDown != null)
				{
					pack.setResource("textures/blocks/" + i.getId() + "_bottom.png", textureDown);
				}

				else
				{
					pack.setResource("textures/blocks/" + i.getId() + "_bottom.png", missingTexture);
					System.out.println("WARNING: " + i.getId() + " MISSING TEXTURE: " + td);
				}

				if(textureSide != null)
				{
					pack.setResource("textures/blocks/" + i.getId() + "_side.png", textureSide);
				}

				else
				{
					pack.setResource("textures/blocks/" + i.getId() + "_side.png", missingTexture);
					System.out.println("WARNING: " + i.getId() + " MISSING TEXTURE: " + ts);
				}

				if(model != null)
				{
					pack.setResource("models/block/" + i.getId() + ".json", model);
				}

				else
				{
					String newModel = defaultModelContentBottomTop.replaceAll("\\Q$id\\E", i.getId());
					pack.setResource("models/block/" + i.getId() + ".json", newModel);
				}
			}

			else if(i.getRenderType().equals(BlockRenderType.TOP))
			{
				String tu = "/assets/textures/blocks/" + i.getId() + "_top.png";
				String ts = "/assets/textures/blocks/" + i.getId() + "_side.png";
				String m = "/assets/models/block/" + i.getId() + ".json";
				URL textureUp = i.getClass().getResource(tu);
				URL textureSide = i.getClass().getResource(ts);
				URL model = i.getClass().getResource(m);

				if(textureUp != null)
				{
					pack.setResource("textures/blocks/" + i.getId() + "_top.png", textureUp);
				}

				else
				{
					pack.setResource("textures/blocks/" + i.getId() + "_top.png", missingTexture);
					System.out.println("WARNING: " + i.getId() + " MISSING TEXTURE: " + tu);
				}

				if(textureSide != null)
				{
					pack.setResource("textures/blocks/" + i.getId() + "_side.png", textureSide);
				}

				else
				{
					pack.setResource("textures/blocks/" + i.getId() + "_side.png", missingTexture);
					System.out.println("WARNING: " + i.getId() + " MISSING TEXTURE: " + ts);
				}

				if(model != null)
				{
					pack.setResource("models/block/" + i.getId() + ".json", model);
				}

				else
				{
					String newModel = defaultModelContentTop.replaceAll("\\Q$id\\E", i.getId());
					pack.setResource("models/block/" + i.getId() + ".json", newModel);
				}
			}

			else if(i.getRenderType().equals(BlockRenderType.COLUMN))
			{
				String tu = "/assets/textures/blocks/" + i.getId() + "_end.png";
				String ts = "/assets/textures/blocks/" + i.getId() + "_side.png";
				String m = "/assets/models/block/" + i.getId() + ".json";
				URL textureUp = i.getClass().getResource(tu);
				URL textureSide = i.getClass().getResource(ts);
				URL model = i.getClass().getResource(m);

				if(textureUp != null)
				{
					pack.setResource("textures/blocks/" + i.getId() + "_end.png", textureUp);
				}

				else
				{
					pack.setResource("textures/blocks/" + i.getId() + "_end.png", missingTexture);
					System.out.println("WARNING: " + i.getId() + " MISSING TEXTURE: " + tu);
				}

				if(textureSide != null)
				{
					pack.setResource("textures/blocks/" + i.getId() + "_side.png", textureSide);
				}

				else
				{
					pack.setResource("textures/blocks/" + i.getId() + "_side.png", missingTexture);
					System.out.println("WARNING: " + i.getId() + " MISSING TEXTURE: " + ts);
				}

				if(model != null)
				{
					pack.setResource("models/block/" + i.getId() + ".json", model);
				}

				else
				{
					String newModel = defaultModelContentColumn.replaceAll("\\Q$id\\E", i.getId());
					pack.setResource("models/block/" + i.getId() + ".json", newModel);
				}
			}

			i.setDurabilityLock((short) gen.getModels().size());
			System.out.println("Registered BLOCK " + i.getId() + " to " + gen.getModels().size());
			gen.getModels().add("block/" + i.getId());
			idblocks.put(i.getId(), i);
			shortid.put(i.getDurabilityLock(), i.getId());
			max++;
		}

		gen.generate();
		pack.setResource("models/" + gen.modelSuperName() + ".json", gen.getParenter().toString(4));
		pack.setResource("models/" + gen.getModel() + ".json", gen.generateModel("items/diamond_hoe").toString(4));
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
