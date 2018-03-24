package com.volmit.fulcrum.custom;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.volmit.dumpster.GMap;
import com.volmit.fulcrum.bukkit.BlockType;

public class CustomShapedRecipe implements ICustomRecipe
{
	private ItemStack result;
	private GMap<Character, ItemStack> ingredients;
	private String[] pattern;

	public CustomShapedRecipe(ItemStack result, String... pattern)
	{
		this.result = result;
		ingredients = new GMap<Character, ItemStack>();
		this.pattern = pattern;
	}

	public CustomShapedRecipe(CustomItem result, int count, String... pattern)
	{
		this(result.getItem(count), pattern);
	}

	public CustomShapedRecipe(CustomItem result, String... pattern)
	{
		this(result, 1, pattern);
	}

	public CustomShapedRecipe(CustomBlock result, int count, String... pattern)
	{
		this(result.getItem(count), pattern);
	}

	public CustomShapedRecipe(CustomBlock result, String... pattern)
	{
		this(result, 1, pattern);
	}

	@Override
	public ItemStack getResult()
	{
		return result;
	}

	public CustomShapedRecipe addIngredient(String charx, Material material)
	{
		return addIngredient(charx, new BlockType(material));
	}

	@SuppressWarnings("deprecation")
	public CustomShapedRecipe addIngredient(String charx, BlockType t)
	{
		ItemStack is = new ItemStack(t.getMaterial());

		if(t.getData() != 0)
		{
			MaterialData da = is.getData();
			da.setData(t.getData());
			is.setData(da);
		}

		return addIngredient(charx, is);
	}

	public CustomShapedRecipe addIngredient(String charx, ItemStack is)
	{
		ingredients.put(charx.charAt(0), is);
		return this;
	}

	public CustomShapedRecipe addIngredient(String charx, CustomItem item)
	{
		return addIngredient(charx, item.getItem());
	}

	public CustomShapedRecipe addIngredient(String charx, CustomBlock block)
	{
		return addIngredient(charx, block.getItem());
	}

	public GMap<Character, ItemStack> getIngredients()
	{
		return ingredients;
	}

	public CustomShapedRecipe setResult(ItemStack result)
	{
		this.result = result;
		return this;
	}

	public String[] getPattern()
	{
		return pattern;
	}
}
