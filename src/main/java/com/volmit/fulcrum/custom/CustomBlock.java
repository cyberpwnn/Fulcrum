package com.volmit.fulcrum.custom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.volmit.dumpster.GSet;
import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.bukkit.BlockType;
import com.volmit.fulcrum.lang.C;
import com.volmit.fulcrum.sfx.Audible;
import com.volmit.fulcrum.sfx.Audio;

public class CustomBlock implements ICustom
{
	private Audible breakSound;
	private Audible placeSound;
	private Audible digSound;
	private Audible pickupSound;
	private Audible stepSound;
	private ModelType renderType;
	private BlockRegistryType blockRegistryType;
	private BlockRenderType blockRenderType;
	private String name;
	private final String id;
	private short durabilityLock;
	private Material material;
	private boolean shaded;
	private int sid;
	private String matt;
	private boolean ee;
	private int stackSize;
	private String toolType;
	private int minimumToolLevel;
	private double hardness;
	private byte data;
	private GSet<BlockFlag> flags;

	public CustomBlock(String id)
	{
		flags = new GSet<BlockFlag>();
		data = -1;
		hardness = 0.5;
		toolType = ToolType.HAND;
		minimumToolLevel = ToolLevel.HAND;
		stackSize = 64;
		ee = false;
		this.id = id;
		sid = 0;
		setName("fulcrum:" + id);
		shaded = false;
		matt = "";
		renderType = ModelType.CUBE_ALL;
		pickupSound = ContentManager.getPickupSound();
		blockRegistryType = BlockRegistryType.TILE_BLOCK;
		blockRenderType = BlockRenderType.NORMAL;

		CustomBlock ci = ContentManager.getBlock(id);

		if(ci != null)
		{
			setType(ci.getType());
			setDurabilityLock(ci.getDurabilityLock());
			setSuperID(ci.getSuperID());
		}
	}

	public void setHardnessLike(Material m)
	{
		setHardness(BlockHardness.getHardness(m));
	}

	public void setEffectiveToolLike(Material m)
	{
		setToolType(BlockHardness.getEffectiveTool(m));
	}

	public void setPropertiesLike(Material m)
	{
		setHardnessLike(m);
		setEffectiveToolLike(m);
	}

	public void flag(BlockFlag... flags)
	{
		for(BlockFlag i : flags)
		{
			this.flags.add(i);
		}
	}

	public void addFlag(BlockFlag flag)
	{
		flag(flag);
	}

	public void addFlags(BlockFlag... flags)
	{
		flag(flags);
	}

	public boolean hasFlag(BlockFlag flag)
	{
		return flags.contains(flag);
	}

	public GSet<BlockFlag> getFlags()
	{
		return flags;
	}

	public void setFlags(GSet<BlockFlag> flags)
	{
		this.flags = flags;
	}

	public void setBlockRegistryType(BlockRegistryType blockRegistryType)
	{
		this.blockRegistryType = blockRegistryType;
	}

	public void setSid(int sid)
	{
		this.sid = sid;
	}

	public void setEe(boolean ee)
	{
		this.ee = ee;
	}

	public ItemStack onDrop()
	{
		return getItem(1);
	}

	public void onUpdate(Block block)
	{

	}

	public void onViewTicked(Player player, Block block)
	{

	}

	public void onPickedUp(Player player, Item item, boolean cancel)
	{

	}

	public void onPlaced(Player player, Block block, Block against, BlockFace on, boolean cancel)
	{

	}

	public void onBroke(Player player, Block block, boolean cancel)
	{

	}

	public void onStartDig(Player player, Block block, boolean cancel)
	{

	}

	public void onCancelDig(Player player, Block block)
	{

	}

	public void setSound(MultiCustomSound s)
	{
		s.applyToBlock(this);
	}

	public String getToolType()
	{
		return toolType;
	}

	public void setToolType(String toolType)
	{
		this.toolType = toolType;
	}

	public int getMinimumToolLevel()
	{
		return minimumToolLevel;
	}

	public void setMinimumToolLevel(int minimumToolLevel)
	{
		this.minimumToolLevel = minimumToolLevel;
	}

	public double getHardness()
	{
		return hardness;
	}

	public void setHardness(double hardness)
	{
		this.hardness = hardness;
	}

	public Audible getDigSound()
	{
		return digSound;
	}

	public void setDigSound(Audible digSound)
	{
		this.digSound = digSound;
	}

	public void setDigSound(CustomSound s)
	{
		setDigSound(new Audio(s));
	}

	public Audible getPickupSound()
	{
		return pickupSound;
	}

	public void setPickupSound(Audible pickupSound)
	{
		this.pickupSound = pickupSound;
	}

	public void setPickupSound(CustomSound pickupSound)
	{
		setPickupSound(new Audio(pickupSound));
	}

	public CustomBlock(String id, ModelType renderType)
	{
		this(id);
		this.renderType = renderType;
	}

	public void setBreakSound(Audible breakSound)
	{
		this.breakSound = breakSound;
	}

	public void setPlaceSound(Audible placeSound)
	{
		this.placeSound = placeSound;
	}

	public void setStepSound(Audible stepSound)
	{
		this.stepSound = stepSound;
	}

	public void setBreakSound(CustomSound breakSound)
	{
		setBreakSound(new Audio(breakSound));
	}

	public void setPlaceSound(CustomSound placeSound)
	{
		setPlaceSound(new Audio(placeSound));
	}

	public void setStepSound(CustomSound stepSound)
	{
		setStepSound(new Audio(stepSound));
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Audible getBreakSound()
	{
		return breakSound;
	}

	public Audible getPlaceSound()
	{
		return placeSound;
	}

	public Audible getStepSound()
	{
		return stepSound;
	}

	public String getName()
	{
		return name;
	}

	public String getId()
	{
		return id;
	}

	public void setDurabilityLock(short d)
	{
		this.durabilityLock = d;
	}

	public short getDurabilityLock()
	{
		return durabilityLock;
	}

	public ItemStack getItem()
	{
		return getItem(1);
	}

	@SuppressWarnings("deprecation")
	public ItemStack getItem(int count)
	{
		ItemStack is;

		if(ContentManager.isOverrided(this))
		{
			is = new ItemStack(getType(), count, (short) 0, getData());
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(C.RESET + getName());
			im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			im.addItemFlags(ItemFlag.HIDE_DESTROYS);
			im.addItemFlags(ItemFlag.HIDE_PLACED_ON);
			im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
			im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			is.setItemMeta(im);
		}

		else
		{
			is = new ItemStack(getType());
			is.setAmount(count);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(getName());
			is.setDurability(getDurabilityLock());
			im.setUnbreakable(true);

			if(ee)
			{
				im.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0, true);
			}

			im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			im.addItemFlags(ItemFlag.HIDE_DESTROYS);
			im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			im.addItemFlags(ItemFlag.HIDE_PLACED_ON);
			im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
			im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			is.setItemMeta(im);
		}

		return is;
	}

	public void set(Location location)
	{
		set(location, true);
	}

	@SuppressWarnings("deprecation")
	public void set(Location location, boolean ph)
	{
		if(getType() == null)
		{
			CustomBlock ci = ContentManager.getBlock(id);

			if(ci != null)
			{
				setType(ci.getType());
				setDurabilityLock(ci.getDurabilityLock());
				setSuperID(ci.getSuperID());
				setData(ci.getData());
			}
		}

		if(blockRegistryType.equals(BlockRegistryType.BUILDING_BLOCK))
		{
			location.getBlock().setTypeIdAndData(getType().getId(), getData(), ph);
		}

		else
		{
			Fulcrum.adapter.setSpawnerType(location, getMatt(), getDurabilityLock(), ee);
		}
	}

	public ModelType getRenderType()
	{
		return renderType;
	}

	public void setRenderType(ModelType renderType)
	{
		this.renderType = renderType;
	}

	public Material getType()
	{
		return material;
	}

	public void setType(Material type)
	{
		material = type;
	}

	public boolean isShaded()
	{
		return shaded;
	}

	public Material getMaterial()
	{
		return material;
	}

	public void setMaterial(Material material)
	{
		this.material = material;
	}

	public void setShaded(boolean shaded)
	{
		this.shaded = shaded;
	}

	public int getSuperID()
	{
		return sid;
	}

	public void setSuperID(int f)
	{
		sid = f;
	}

	public String getMatt()
	{
		return matt;
	}

	public void setMatt(String matt)
	{
		this.matt = matt;
	}

	public void setEnchanted(boolean boolean1)
	{
		ee = boolean1;
	}

	public boolean isEnchanted()
	{
		return ee;
	}

	public void setStackSize(int size)
	{
		this.stackSize = size;
	}

	public int getStackSize()
	{
		return stackSize;
	}

	public BlockRegistryType getBlockType()
	{
		return blockRegistryType;
	}

	public void setBlockType(BlockRegistryType blockType)
	{
		this.blockRegistryType = blockType;
	}

	public BlockRenderType getBlockRenderType()
	{
		return blockRenderType;
	}

	public void setBlockRenderType(BlockRenderType blockRenderType)
	{
		this.blockRenderType = blockRenderType;
	}

	public void setData(byte data)
	{
		this.data = data;
	}

	public BlockRegistryType getBlockRegistryType()
	{
		return blockRegistryType;
	}

	public int getSid()
	{
		return sid;
	}

	public boolean isEe()
	{
		return ee;
	}

	public byte getData()
	{
		return data;
	}

	public BlockType getAsType()
	{
		return new BlockType(getType(), getData());
	}
}
