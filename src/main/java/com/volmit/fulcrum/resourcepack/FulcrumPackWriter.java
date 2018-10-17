package com.volmit.fulcrum.resourcepack;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.zeroturnaround.zip.ZipUtil;

import com.googlecode.pngtastic.core.PngImage;
import com.googlecode.pngtastic.core.PngOptimizer;
import com.volmit.fulcrum.util.Environment;
import com.volmit.volume.bukkit.VolumePlugin;
import com.volmit.volume.lang.collections.GList;
import com.volmit.volume.lang.format.F;
import com.volmit.volume.lang.io.VIO;
import com.volmit.volume.lang.json.JSONObject;
import com.volmit.volume.math.Profiler;

public class FulcrumPackWriter implements PackWriter
{
	private String name;
	private final Properties properties;
	private final GList<ResourceProvider> providers;
	private PrintWriter pw;
	private long savings;
	private long fullsize;

	public FulcrumPackWriter()
	{
		properties = new Properties();
		providers = new GList<ResourceProvider>();
	}

	private void log(String t, String s)
	{
		if(pw != null)
		{
			pw.println("[" + t + "]: " + s);
		}

		VolumePlugin.vpi.getLogger().log(Level.INFO, "[" + t + "]: " + s);
	}

	private void f(String s)
	{
		log("FATAL", s);
	}

	private void l(String s)
	{
		log("INFO", s);
	}

	@Override
	public void addProvider(ResourceProvider provider)
	{
		getProviders().add(provider);
	}

	@Override
	public GList<ResourceProvider> getProviders()
	{
		return providers;
	}

	@Override
	public InputStream provide(String path)
	{
		for(ResourceProvider i : getProviders())
		{
			InputStream in = i.read(path);

			if(in != null)
			{
				return in;
			}
		}

		return null;
	}

	@Override
	public void write(ResourcePack pack, File destination)
	{
		Profiler pr = new Profiler();
		name = "pack-" + UUID.randomUUID().toString().split("-")[1];
		initLogging();
		pr.begin();
		zip(writeToCache(pack), destination);
		pr.end();
		l("---------------------------");
		l("Pack Size: " + F.fileSize(fullsize) + " optimized to " + F.fileSize(getProperties().zipPack ? destination.length() : (fullsize - savings)));
		l("Resources: " + pack.getResources().size());
		l("Elapsed: " + F.time(pr.getMilliseconds(), 0));
		finishLogging();
	}

	private void zip(File folder, File destination)
	{
		if(getProperties().zipPack)
		{
			ZipUtil.pack(folder, destination, getProperties().compressionLevel);
			l("Compressing Resource Pack");
		}
	}

	private void finishLogging()
	{
		if(pw != null)
		{
			pw.close();
		}
	}

	private void initLogging()
	{
		if(getProperties().includeLogs)
		{
			try
			{
				File f = new File(fileFor(name).getParentFile().getParentFile(), name + ".log");
				f.getParentFile().mkdirs();
				pw = new PrintWriter(new FileWriter(f), true);
			}

			catch(IOException e)
			{
				f("Failed to open log file.");
				e.printStackTrace();
			}
		}
	}

	private File writeToCache(ResourcePack pack)
	{
		Environment.clearCache(name);
		File f = Environment.getCache(name);
		writeJSON(pack.getPackMeta().realize(), fileFor("pack.mcmeta"));
		GList<Resource> q = pack.getResources().copy();
		ExecutorService es = Executors.newWorkStealingPool(Math.min(q.size(), getProperties().threads));
		l("Writing " + q.size() + " resources with " + Math.min(q.size(), getProperties().threads) + " threads.");

		while(!q.isEmpty())
		{
			Resource r = q.pop();

			try
			{
				es.submit(() -> write(r));
			}

			catch(RejectedExecutionException e)
			{
				try
				{
					Thread.sleep(5);
				}

				catch(InterruptedException e1)
				{
					e1.printStackTrace();
				}

				q.add(r);
			}

			catch(Throwable e)
			{
				f("Failed to queue/write " + r.getPath());
			}
		}

		es.shutdown();

		try
		{
			es.awaitTermination(30, TimeUnit.MINUTES);
		}

		catch(InterruptedException e)
		{
			e.printStackTrace();
		}

		l("Finished writing resources.");

		return f;
	}

	private void write(Resource i)
	{
		File f = fileFor(i.getPath());
		InputStream in = provide(i.getPath());

		if(in != null)
		{
			try
			{
				FileOutputStream fos = new FileOutputStream(f);
				long full = VIO.fullTransfer(in, fos, 8192);
				in.close();
				fos.close();
				l("Wrote " + F.fileSize(full) + " " + i.getPath());
				fullsize += f.length();

				if(i.getPath().toLowerCase().endsWith(".png") && getProperties().optimizePngs)
				{
					double size = f.length();
					InputStream pin = new BufferedInputStream(new FileInputStream(f));
					PngImage image = new PngImage(pin);
					PngOptimizer optimizer = new PngOptimizer();
					PngImage optimizedImage = optimizer.optimize(image, getProperties().optimizePngRemoveGamma, getProperties().optimizePngCompressionLevel);
					ByteArrayOutputStream optimizedBytes = new ByteArrayOutputStream();
					optimizedImage.writeDataOutputStream(optimizedBytes);
					optimizedImage.export(f.getAbsolutePath(), optimizedBytes.toByteArray());
					double newSize = f.length();
					double percentSmaller = (size - newSize) / size;
					savings += size - newSize;
					l("Optimized " + f.getName() + ", " + F.pc(percentSmaller, 0) + " smaller.");
				}
			}

			catch(Exception e)
			{
				f("Failed to write " + i.getPath() + " to " + f.getPath());
				e.printStackTrace();
			}
		}

		else
		{
			f("Failed to provide " + i.getPath());
		}
	}

	private File fileFor(String path)
	{
		File f = new File(Environment.getCache(name), path);
		f.getParentFile().mkdirs();
		return f;
	}

	private void writeJSON(JSONObject json, File f)
	{
		try
		{
			VIO.writeAll(f, json.toString(getProperties().jsonIndentation));
			fullsize += f.length();
			l("Wrote json to " + f.getName());
		}

		catch(IOException e)
		{
			f("Failed to write json to " + f.getPath());
			e.printStackTrace();
		}
	}

	@Override
	public Properties getProperties()
	{
		return properties;
	}
}
