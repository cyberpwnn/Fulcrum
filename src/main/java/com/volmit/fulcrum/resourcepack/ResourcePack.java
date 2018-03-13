package com.volmit.fulcrum.resourcepack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.regex.Matcher;

import com.volmit.fulcrum.lang.GMap;

public class ResourcePack
{
	private final PackMeta meta;
	private GMap<String, URL> copyResources;
	private GMap<String, String> writeResources;

	public ResourcePack()
	{
		meta = new PackMeta();
		copyResources = new GMap<String, URL>();
		writeResources = new GMap<String, String>();
	}

	public void setResource(String path, URL url)
	{
		copyResources.put(path, url);
	}

	public void setResource(String path, String content)
	{
		writeResources.put(path, content);
	}

	public PackMeta getMeta()
	{
		return meta;
	}

	public void writeToFolder(File f) throws IOException
	{
		writePackContent(new File(f, "pack.mcmeta"), getMeta().toString());
		writeResourceToFile(getMeta().getPackIcon(), new File(f, "pack.png"));

		for(String i : copyResources.k())
		{
			File destination = new File(f, "assets" + File.separator + "minecraft" + File.separator + i.replaceAll("/", Matcher.quoteReplacement(File.separator)));
			destination.getParentFile().mkdirs();
			writeResourceToFile(copyResources.get(i), destination);
		}

		for(String i : writeResources.k())
		{
			File destination = new File(f, "assets" + File.separator + "minecraft" + File.separator + i.replaceAll("/", Matcher.quoteReplacement(File.separator)));
			destination.getParentFile().mkdirs();
			writePackContent(destination, writeResources.get(i));
		}
	}

	private void writeResourceToFile(URL url, File f) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(f);
		InputStream in = url.openStream();
		byte[] buffer = new byte[1024];
		int read = 0;

		while((read = in.read(buffer)) != -1)
		{
			fos.write(buffer, 0, read);
		}

		fos.close();
		in.close();
		System.out.println("Wrote " + f.getPath());
	}

	private void writePackContent(File m, String content) throws IOException
	{
		m.createNewFile();
		PrintWriter pw = new PrintWriter(m);
		pw.println(content);
		pw.close();
		System.out.println("Wrote " + m.getPath());
	}
}
