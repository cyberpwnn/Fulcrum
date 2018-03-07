package com.volmit.fulcrum.map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import com.volmit.fulcrum.lang.GList;

public interface IPapyrus
{
	public MapView getView();

	public BufferedFrame getFrameBuffer();

	public void addRenderer(IRenderer renderer);

	public void clearRenderers();

	public GList<IRenderer> getRenderers();

	public void removeRenderer(IRenderer renderer);

	public void destroy();

	public ItemStack makeMapItem();
}
