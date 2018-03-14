package com.volmit.fulcrum.bukkit;

import java.net.URL;

import com.volmit.fulcrum.Fulcrum;

public class R
{
	public static boolean exists(Class<?> c, String file)
	{
		return getURL(c, file) != null;
	}

	public static URL getURL(Class<?> c, String file)
	{
		return c.getResource(file);
	}

	public static boolean exists(String file)
	{
		return getURL(file) != null;
	}

	public static URL getURL(String file)
	{
		return Fulcrum.class.getResource(file);
	}
}
