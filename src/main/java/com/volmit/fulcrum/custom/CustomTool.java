package com.volmit.fulcrum.custom;

public class CustomTool extends CustomItem
{
	private String toolType;
	private int toolLevel;

	public CustomTool(String id)
	{
		super(id);
		setToolLevel(ToolLevel.WOOD);
		setToolType(ToolType.PICKAXE);
		setStackSize(1);
	}

	public String getToolType()
	{
		return toolType;
	}

	public void setToolType(String toolType)
	{
		this.toolType = toolType;
	}

	public int getToolLevel()
	{
		return toolLevel;
	}

	public void setToolLevel(int toolLevel)
	{
		this.toolLevel = toolLevel;
	}
}
