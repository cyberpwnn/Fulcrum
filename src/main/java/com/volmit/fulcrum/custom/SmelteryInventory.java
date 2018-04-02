package com.volmit.fulcrum.custom;

public class SmelteryInventory extends CustomInventory
{
	public SmelteryInventory()
	{
		super("smeltery");
	}

	public static class Controller extends ManagedInventory
	{
		public Controller()
		{
			super(new SmelteryInventory());
			supportSlot(1); // input 1
			supportSlot(11); // input 2
			supportSlot(19); // input 3
			supportSlot(13); // output
		}
	}
}
