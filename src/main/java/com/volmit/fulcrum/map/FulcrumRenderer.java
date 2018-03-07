package com.volmit.fulcrum.map;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class FulcrumRenderer extends MapRenderer
{
	protected byte[][] bakedImage;

	public FulcrumRenderer()
	{
		bakedImage = new byte[128][128];
		fill(FrameColor.BLACK);
	}

	public void fill(byte color)
	{
		for(int i = 0; i < 128; i++)
		{
			for(int j = 0; j < 128; j++)
			{
				bakedImage[i][j] = color;
			}
		}
	}

	@Override
	public void render(MapView map, MapCanvas canvas, Player player)
	{
		for(int i = 0; i < 128; i++)
		{
			for(int j = 0; j < 128; j++)
			{
				canvas.setPixel(i, j, bakedImage[i][j]);
			}
		}
	}
}
