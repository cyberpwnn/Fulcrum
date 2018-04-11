package com.volmit.fulcrum.custom;

import org.bukkit.inventory.ItemStack;

public class CustomSmeltingRecipe implements ICustomRecipe
{
	private ItemStack result;
	private ItemStack ingredient;

	public CustomSmeltingRecipe(ItemStack ingredient, ItemStack result)
	{
		this.result = result;
		this.ingredient = ingredient;
	}

	@Override
	public ItemStack getResult()
	{
		return result;
	}

	public ItemStack getIngredient()
	{
		return ingredient;
	}

	public void setIngredient(ItemStack ingredient)
	{
		this.ingredient = ingredient;
	}

	public void setResult(ItemStack result)
	{
		this.result = result;
	}
}
