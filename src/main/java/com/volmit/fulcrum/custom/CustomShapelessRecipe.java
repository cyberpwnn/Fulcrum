package com.volmit.fulcrum.custom;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.volmit.dumpster.GList;
import com.volmit.fulcrum.bukkit.BlockType;

public class CustomShapelessRecipe implements ICustomRecipe
{
	private ItemStack result;
	private GList<ItemStack> ingredients;

	public CustomShapelessRecipe(ItemStack result)
	{
		this.result = result;
		ingredients = new GList<ItemStack>();
	}

	public CustomShapelessRecipe(CustomItem result, int count)
	{
		this(result.getItem(count));
	}

	public CustomShapelessRecipe(CustomItem result)
	{
		this(result, 1);
	}

	public CustomShapelessRecipe(CustomBlock result, int count)
	{
		this(result.getItem(count));
	}

	public CustomShapelessRecipe(CustomBlock result)
	{
		this(result, 1);
	}

	@Override
	public ItemStack getResult()
	{
		return result;
	}

	public CustomShapelessRecipe addIngredient(Material material)
	{
		return addIngredient(new BlockType(material));
	}

	@SuppressWarnings("deprecation")
	public CustomShapelessRecipe addIngredient(BlockType t)
	{
		ItemStack is = new ItemStack(t.getMaterial());

		if(t.getData() != 0)
		{
			MaterialData da = is.getData();
			da.setData(t.getData());
			is.setData(da);
		}

		return addIngredient(is);
	}

	public CustomShapelessRecipe addIngredient(ItemStack is)
	{
		if(is.getAmount() > 1)
		{
			for(int i = 0; i < is.getAmount(); i++)
			{
				ItemStack iv = is.clone();
				iv.setAmount(1);
				addIngredient(iv);
			}
		}

		else
		{
			ingredients.add(is);
		}

		return this;
	}

	public CustomShapelessRecipe addIngredient(CustomItem item)
	{
		return addIngredient(item, 1);
	}

	public CustomShapelessRecipe addIngredient(CustomBlock block)
	{
		return addIngredient(block, 1);
	}

	public CustomShapelessRecipe addIngredient(CustomItem item, int c)
	{
		return addIngredient(item.getItem(c));
	}

	public CustomShapelessRecipe addIngredient(CustomBlock block, int c)
	{
		return addIngredient(block.getItem(c));
	}

	public GList<ItemStack> getIngredients()
	{
		return ingredients;
	}

	public CustomShapelessRecipe setResult(ItemStack result)
	{
		this.result = result;
		return this;
	}
}
