package com.volmit.fulcrum.map;

import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;

import com.volmit.fulcrum.bukkit.TICK;

public class BakedImageRenderer extends FulcrumRenderer
{
	public BakedImageRenderer(BufferedImage img)
	{
		super();
		BufferedFrame bu = new BufferedFrame(img.getWidth(), img.getHeight());
		BufferedFrame doubleBuffer = null;
		bu.fromBufferedImage(img);
		double xScale = 128.0 / (double) img.getWidth();
		double yScale = 128.0 / (double) img.getHeight();
		doubleBuffer = bu.scale(xScale, yScale, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		bakedImage = doubleBuffer.bake();
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
