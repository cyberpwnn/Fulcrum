package com.volmit.fulcrum.images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;
import javax.naming.directory.InvalidAttributesException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;

import com.volmit.fulcrum.map.BakedImageRenderer;
import com.volmit.fulcrum.map.Papyrus;
import com.volmit.fulcrum.vfx.particle.ParticleSuspended;
import com.volmit.volume.lang.collections.GList;
import com.volmit.volume.lang.collections.GMap;
import com.volmit.volume.lang.collections.GSet;
import com.volmit.volume.lang.format.F;
import com.volmit.volume.math.M;

public class ImageBakery
{
	private static final GMap<String, BufferedImage> imgs = new GMap<String, BufferedImage>();
	private static final GMap<String, GMap<World, Papyrus>> renderers = new GMap<String, GMap<World, Papyrus>>();
	private static final GMap<Block, GSet<BlockFace>> textures = new GMap<Block, GSet<BlockFace>>();

	public static void removeTexture(Block block, BlockFace face)
	{
		ItemFrame frame = getFrame(block, face);

		if(frame == null)
		{
			return;
		}

		frame.setItem(new ItemStack(Material.AIR));
		frame.remove();
	}

	public static void setTexture(Block block, BlockFace face, String texture)
	{
		if(!block.getRelative(face).getType().equals(Material.AIR))
		{
			return;
		}

		if(!textures.containsKey(block))
		{
			textures.put(block, new GSet<BlockFace>());
		}

		if(!textures.get(block).contains(face))
		{
			placeFrame(block, face);
		}

		ItemFrame frame = getFrame(block, face);

		if(frame == null)
		{
			System.out.println("Failed to find");
			return;
		}

		frame.setItem(ImageBakery.getBakedMap(block.getWorld(), texture));
	}

	private static ItemFrame placeFrame(Block block, BlockFace face)
	{
		block.getRelative(face).setType(Material.ITEM_FRAME);
		ItemFrame i = (ItemFrame) block.getWorld().spawn(block.getRelative(face).getLocation(), ItemFrame.class);
		i.setFacingDirection(face);
		new ParticleSuspended().setDeep(true).play(i.getLocation());
		return i;
	}

	private static ItemFrame getFrame(Block block, BlockFace face)
	{
		for(Entity i : block.getWorld().getNearbyEntities(block.getRelative(face).getLocation().clone().add(0.5, 0.5, 0.5), 0.5, 0.5, 0.5))
		{
			if(i instanceof ItemFrame)
			{
				ItemFrame frame = (ItemFrame) i;
				if(frame.getAttachedFace().equals(face.getOppositeFace()))
				{
					frame.getLocation().getBlock().equals(block.getRelative(face));
					return frame;
				}
			}
		}

		return null;
	}

	public static GList<String> getImages()
	{
		return imgs.k();
	}

	public static ItemStack getBakedMap(World world, String id)
	{
		if(!getImages().contains(id))
		{
			id = "fulcrum:unknown";
		}

		if(!renderers.containsKey(id))
		{
			renderers.put(id, new GMap<World, Papyrus>());
		}

		if(!renderers.get(id).containsKey(world))
		{
			Papyrus p = new Papyrus(world);

			for(MapRenderer i : p.getMap().getRenderers())
			{
				p.getMap().removeRenderer(i);
			}

			p.getMap().addRenderer(new BakedImageRenderer(imgs.get(id)));
			renderers.get(id).put(world, p);
		}

		return renderers.get(id).get(world).makeMapItem();
	}

	public static void ingest(String id, Class<?> p, String resourceName) throws IOException, InvalidAttributesException
	{
		if(imgs.containsKey(id))
		{
			throw new InvalidAttributesException("Duplicate ID: " + id);
		}

		System.out.println("Loading Jar Resource: " + id + " from " + p.getName() + " (" + resourceName + ")");
		BufferedImage bu = ImageIO.read(p.getResourceAsStream("/" + resourceName));

		if(bu == null)
		{
			System.out.println("Unable to locate image: NULL");
		}

		imgs.put(id, bu);
	}

	public static void ingest(String id, File file) throws IOException, InvalidAttributesException
	{
		if(imgs.containsKey(id))
		{
			throw new InvalidAttributesException("Duplicate ID: " + id);
		}

		System.out.println("Loading Filesystem Resource: " + id + " (" + file.getAbsolutePath() + ")");

		BufferedImage bu = ImageIO.read(file);

		if(bu == null)
		{
			System.out.println("Unable to locate image: NULL");
		}

		imgs.put(id, bu);
	}

	public static void ingest(String id, InputStream is, File jar) throws IOException, InvalidAttributesException
	{
		if(imgs.containsKey(id))
		{
			throw new InvalidAttributesException("Duplicate ID: " + id);
		}

		System.out.println("Loading Filesystem Resource: " + id + " fron stream (" + jar.getAbsolutePath() + ")");

		BufferedImage bu = ImageIO.read(is);

		if(bu == null)
		{
			System.out.println("Unable to locate image: NULL");
		}

		imgs.put(id, bu);
	}

	public static void scan(File jar, String prefix) throws IOException
	{
		ZipFile zipFile = new ZipFile(jar);

		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		while(entries.hasMoreElements())
		{
			ZipEntry entry = entries.nextElement();

			if(entry.isDirectory())
			{
				continue;
			}

			if(entry.getName().endsWith(".png") || entry.getName().endsWith(".jpg") || entry.getName().endsWith(".jpeg"))
			{
				InputStream stream = zipFile.getInputStream(entry);

				try
				{
					ingest(prefix + ":" + entry.getName().replace(".png", "").replace(".jpg", "").replace(".jpeg", ""), stream, jar);
				}

				catch(InvalidAttributesException e)
				{
					e.printStackTrace();
				}

				stream.close();
			}
		}

		zipFile.close();
	}

	public static void compile()
	{
		int k = getImages().size() * Bukkit.getWorlds().size();
		int v = 0;
		long ms = M.ms();

		for(String i : getImages())
		{
			for(World j : Bukkit.getWorlds())
			{
				getBakedMap(j, i);
				v++;

				if(M.ms() - ms > 5000)
				{
					ms = M.ms();
					System.out.println("Compiling Renderers: " + F.pc((double) v / (double) k, 2));
				}
			}
		}
	}
}
