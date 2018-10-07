package com.volmit.fulcrum.resourcepack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.zeroturnaround.zip.ZipUtil;

import com.volmit.volume.lang.io.VIO;

public class ResourcePack extends Package
{
	public ResourcePack()
	{
		super();
	}

	public void write(File location, CompressionMode mode) throws IOException
	{
		switch(mode)
		{
			case COMPRESS:
				write(location, true, false, false, 1);
				break;
			case EDGY:
				write(location, true, true, true, 9);
				break;
			case MINIFIED_RAW:
				write(location, false, true, false, -1);
				break;
			case PRODUCTION:
				write(location, true, true, false, 4);
				break;
			case RAW:
				write(location, false, false, false, -1);
				break;
			default:
				break;
		}
	}

	public void write(File location, boolean compress, boolean minify, boolean optimizeImages, int level) throws IOException
	{
		if(location.exists())
		{
			if(location.isFile() && !compress)
			{
				throw new UnsupportedOperationException("Cannot write a folder to an existing file!");
			}

			if(location.isDirectory() && compress)
			{
				throw new UnsupportedOperationException("Cannot write a file to an existing folder!");
			}
		}

		if(compress)
		{
			File dir = new File(location.getParentFile(), "tmp-" + location.getName());
			write(dir, false, minify, optimizeImages, -1);
			ZipUtil.pack(dir, location, level);
		}

		else
		{
			for(String i : getResourcePaths())
			{
				File f = new File(location, i);
				write(f, getResource(i));
			}

			writeJSON(new File(location, "pack.mcmeta"), getMeta().toString(minify ? 0 : 4));
		}
	}

	private void writeJSON(File path, String json) throws IOException
	{
		PrintWriter pw = new PrintWriter(new FileWriter(path));
		pw.println(json);
		pw.close();
	}

	private void write(File f, PackResource r) throws IOException
	{
		f.getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(f);
		InputStream in = r.getInputStream();
		VIO.fullTransfer(in, fos, 8192);
		fos.close();
		in.close();
	}

	public void setResouce(KnownPath path, PackResource r)
	{
		setResouce(path.toString(), r);
	}
}
