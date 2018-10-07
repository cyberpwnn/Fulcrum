package com.volmit.fulcrum.resourcepack;

import java.io.InputStream;

public interface PackResource
{
	public static final String ASSETS_MINECRAFT = "assets/minecraft";

	public InputStream getInputStream();
}
