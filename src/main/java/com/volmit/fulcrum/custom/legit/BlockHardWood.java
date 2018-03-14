package com.volmit.fulcrum.custom.legit;

import org.bukkit.SoundCategory;

import com.volmit.fulcrum.custom.CustomBlock;
import com.volmit.fulcrum.sfx.Audio;

public class BlockHardWood extends CustomBlock
{
	public BlockHardWood()
	{
		super("hard_wood");
		setName("Hard Wood");
		setBreakSound(new Audio().s("material.hardwood.break").vp(1f, 0.9f).c(SoundCategory.BLOCKS));
		setPlaceSound(new Audio().s("material.hardwood.place").vp(1f, 0.9f).c(SoundCategory.BLOCKS));
		setDigSound(new Audio().s("material.hardwood.dig").vp(0.4f, 1.2f).c(SoundCategory.BLOCKS));
		setStepSound(new Audio().s("material.hardwood.step").vp(0.4f, 1.2f).c(SoundCategory.BLOCKS));
	}
}
