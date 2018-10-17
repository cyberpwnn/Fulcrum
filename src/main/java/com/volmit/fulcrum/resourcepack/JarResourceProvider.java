package com.volmit.fulcrum.resourcepack;

import java.io.InputStream;

public class JarResourceProvider implements ResourceProvider
{
	private Class<?> clazz;

	public JarResourceProvider(Class<?> clazz)
	{
		this.clazz = clazz;
	}

	@Override
	public InputStream read(String path)
	{
		return clazz.getResourceAsStream("/" + path);
	}
}
