package com.volmit.fulcrum.util;

import java.io.File;

import com.volmit.volume.bukkit.util.net.Protocol;
import com.volmit.volume.lang.io.VIO;

public class Environment
{
	public static File getCache(String name)
	{
		File f = new File("caches", name);
		f.mkdirs();
		return f;
	}

	public static void clearCache(String name)
	{
		VIO.delete(getCache(name));
	}

	public static int getOptimalPackVersion()
	{
		if(Protocol.B1_6_1.to(Protocol.R1_8_9).contains(Protocol.getProtocolVersion()))
		{
			return 1;
		}

		if(Protocol.R1_9.to(Protocol.R1_10_2).contains(Protocol.getProtocolVersion()))
		{
			return 2;
		}

		if(Protocol.R1_11.to(Protocol.R1_12_2).contains(Protocol.getProtocolVersion()))
		{
			return 3;
		}

		if(Protocol.R1_13.to(Protocol.LATEST).contains(Protocol.getProtocolVersion()))
		{
			return 4;
		}

		return 3;
	}
}
