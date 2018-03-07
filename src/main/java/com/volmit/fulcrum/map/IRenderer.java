package com.volmit.fulcrum.map;

import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;

public interface IRenderer
{
	public void draw(BufferedFrame frame, MapCanvas c, MapView v);
}
