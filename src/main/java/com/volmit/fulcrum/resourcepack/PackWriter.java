package com.volmit.fulcrum.resourcepack;

import java.io.File;
import java.io.InputStream;

import com.volmit.volume.lang.collections.GList;

public interface PackWriter
{
	public Properties getProperties();

	public void addProvider(ResourceProvider provider);

	public GList<ResourceProvider> getProviders();

	public InputStream provide(String path);

	public void write(ResourcePack pack, File destination);

	public static class Properties
	{
		public int compressionLevel = 3;
		public int jsonIndentation = 0;
		public int optimizePngCompressionLevel = 9;
		public boolean optimizePngRemoveGamma = true;
		public boolean optimizePngs = true;
		public boolean includeLogs = true;
		public boolean zipPack = true;
		public int threads = 8;
	}
}
