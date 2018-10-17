package com.volmit.fulcrum.resourcepack;

import java.io.InputStream;

@FunctionalInterface
public interface ResourceProvider
{
	public InputStream read(String path);
}
