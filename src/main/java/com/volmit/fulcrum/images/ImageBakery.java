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

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;

import com.volmit.fulcrum.lang.GList;
import com.volmit.fulcrum.lang.GMap;
import com.volmit.fulcrum.map.BakedImageRenderer;
import com.volmit.fulcrum.map.Papyrus;

public class ImageBakery
{
	private static final GMap<String, BufferedImage> imgs = new GMap<String, BufferedImage>();
	private static final GMap<String, GMap<World, Papyrus>> renderers = new GMap<String, GMap<World, Papyrus>>();

	public static GList<String> getImages()
	{
		return imgs.k();
	}

	public static ItemStack getBakedMap(World world, String id)
	{
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
}
