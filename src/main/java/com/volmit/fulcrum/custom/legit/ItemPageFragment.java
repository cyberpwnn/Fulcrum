package com.volmit.fulcrum.custom.legit;

import com.volmit.fulcrum.custom.CustomItem;

public class ItemPageFragment extends CustomItem
{
	public ItemPageFragment()
	{
		super("page_fragment");
		setName("Page Fragment");
		setStackSize(16);
		setPickupSound(new SoundPickupPaper());
	}
}
