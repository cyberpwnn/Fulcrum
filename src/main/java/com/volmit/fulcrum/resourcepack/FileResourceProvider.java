package com.volmit.fulcrum.resourcepack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileResourceProvider implements ResourceProvider
{
	private File basedir;

	public FileResourceProvider(File basedir)
	{
		this.basedir = basedir;
	}

	@Override
	public InputStream read(String path)
	{
		File f = new File(basedir, path);

		if(f.exists() && f.isFile())
		{
			try
			{
				return new FileInputStream(f);
			}

			catch(FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}

		return null;
	}
}
