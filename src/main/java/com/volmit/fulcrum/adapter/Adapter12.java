package com.volmit.fulcrum.adapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftCreatureSpawner;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.volmit.dumpster.F;
import com.volmit.dumpster.GList;
import com.volmit.dumpster.GMap;
import com.volmit.dumpster.GSet;
import com.volmit.dumpster.M;
import com.volmit.dumpster.Profiler;
import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.bukkit.A;
import com.volmit.fulcrum.bukkit.Base64;
import com.volmit.fulcrum.bukkit.BlockType;
import com.volmit.fulcrum.bukkit.P;
import com.volmit.fulcrum.bukkit.PE;
import com.volmit.fulcrum.bukkit.S;
import com.volmit.fulcrum.bukkit.Task;
import com.volmit.fulcrum.bukkit.TaskLater;
import com.volmit.fulcrum.custom.AdvancementHolder;
import com.volmit.fulcrum.custom.AdvancementHolder.FrameType;
import com.volmit.fulcrum.custom.ContentManager;
import com.volmit.fulcrum.custom.ContentRegistry;
import com.volmit.fulcrum.custom.CustomBlock;
import com.volmit.fulcrum.custom.ToolLevel;
import com.volmit.fulcrum.custom.ToolType;
import com.volmit.fulcrum.lang.C;
import com.volmit.fulcrum.resourcepack.ResourcePack;
import com.volmit.fulcrum.world.scm.GhostWorld;

import net.minecraft.server.v1_12_R1.BiomeBase;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.ChatComponentText;
import net.minecraft.server.v1_12_R1.ChatMessageType;
import net.minecraft.server.v1_12_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_12_R1.ChunkSection;
import net.minecraft.server.v1_12_R1.IBlockData;
import net.minecraft.server.v1_12_R1.MojangsonParseException;
import net.minecraft.server.v1_12_R1.MojangsonParser;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import net.minecraft.server.v1_12_R1.PacketPlayOutCollect;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntity;
import net.minecraft.server.v1_12_R1.PacketPlayOutMapChunk;
import net.minecraft.server.v1_12_R1.PacketPlayOutMultiBlockChange;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_12_R1.PacketPlayOutTileEntityData;
import net.minecraft.server.v1_12_R1.PacketPlayOutUnloadChunk;
import net.minecraft.server.v1_12_R1.SoundEffectType;
import net.minecraft.server.v1_12_R1.TileEntity;
import net.minecraft.server.v1_12_R1.TileEntityMobSpawner;

public final class Adapter12 implements IAdapter
{
	private GhostWorld world;
	private GMap<Chunk, GSet<Location>> update;
	private GMap<Chunk, GSet<Integer>> dirty;
	private GList<Block> physics;
	private GMap<Block, Double> blockDamage;
	private GSet<Chunk> drop;
	private GSet<Chunk> udrop;
	private GMap<Material, Double> blockHardness;
	private GMap<Material, Integer> blockMinimums;
	private GMap<Material, String> blockEffectives;
	private GMap<Material, net.minecraft.server.v1_12_R1.Material> blockMaterials;
	private long processed = 0;
	private boolean push;

	public Adapter12()
	{
		Fulcrum.register(this);
		world = new GhostWorld();
		physics = new GList<Block>();
		update = new GMap<Chunk, GSet<Location>>();
		dirty = new GMap<Chunk, GSet<Integer>>();
		push = false;
		drop = new GSet<Chunk>();
		udrop = new GSet<Chunk>();
		blockHardness = new GMap<Material, Double>();
		blockMinimums = new GMap<Material, Integer>();
		blockEffectives = new GMap<Material, String>();
		blockMaterials = new GMap<Material, net.minecraft.server.v1_12_R1.Material>();
		blockDamage = new GMap<Block, Double>();

		new Task(0)
		{
			@Override
			public void run()
			{
				onTick();

				if(processed == 0)
				{
					return;
				}

				System.out.println("Processed " + F.f(processed) + " block changes");
				processed = 0;
			}
		};
	}

	@EventHandler
	public void on(BlockBreakEvent e)
	{
		drop.add(e.getBlock().getChunk());
	}

	@EventHandler
	public void on(BlockPlaceEvent e)
	{
		drop.add(e.getBlock().getChunk());
	}

	@EventHandler
	public void on(ChunkUnloadEvent e)
	{
		udrop.add(e.getChunk());
	}

	@EventHandler
	public void on(BlockPhysicsEvent e)
	{
		drop.add(e.getBlock().getChunk());
	}

	@EventHandler
	public void on(BlockFromToEvent e)
	{
		drop.add(e.getBlock().getChunk());
	}

	@EventHandler
	public void on(BlockBurnEvent e)
	{
		drop.add(e.getBlock().getChunk());
	}

	@EventHandler
	public void on(BlockFadeEvent e)
	{
		drop.add(e.getBlock().getChunk());
	}

	@EventHandler
	public void on(BlockGrowEvent e)
	{
		drop.add(e.getBlock().getChunk());
	}

	@EventHandler
	public void on(BlockFormEvent e)
	{
		drop.add(e.getBlock().getChunk());
	}

	@EventHandler
	public void on(BlockIgniteEvent e)
	{
		drop.add(e.getBlock().getChunk());
	}

	@EventHandler
	public void on(BlockPistonExtendEvent e)
	{
		drop.add(e.getBlock().getChunk());

		for(Block i : e.getBlocks())
		{
			drop.add(i.getChunk());
		}
	}

	@EventHandler
	public void on(BlockPistonRetractEvent e)
	{
		drop.add(e.getBlock().getChunk());

		for(Block i : e.getBlocks())
		{
			drop.add(i.getChunk());
		}
	}

	@EventHandler
	public void on(BlockExplodeEvent e)
	{
		drop.add(e.getBlock().getChunk());

		for(Block i : e.blockList())
		{
			drop.add(i.getChunk());
		}
	}

	private void onTick()
	{
		for(Chunk i : drop)
		{
			world.drop(i);
		}

		for(Chunk i : udrop)
		{
			world.drop(i);
		}

		udrop.clear();
		drop.clear();

		for(Chunk i : update.k())
		{
			if(update.get(i).size() > 24)
			{
				for(Location j : update.get(i))
				{
					makeDirty(i, getSection(j.getBlockY()));
				}

				continue;
			}

			if(update.get(i).size() > 1)
			{
				try
				{
					sendMultiBlockChange(i, new GList<Location>(update.get(i)));
					continue;
				}

				catch(NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e)
				{
					System.out.println("Failed to send multiblock change, sending block changes instead.");
					e.printStackTrace();
				}
			}

			for(Player j : P.getPlayersWithinViewOf(i))
			{
				for(Location k : update.get(i))
				{
					sendBlockChange(k, new BlockType(k.getBlock()), j);
				}
			}
		}

		for(Chunk i : dirty.k())
		{
			boolean[] bits = new boolean[16];
			Arrays.fill(bits, false);

			for(int j : dirty.get(i))
			{
				bits[j] = true;
			}

			sendChunkSection(i, getBitMask(bits));
		}

		long nsx = M.ns();
		long nf = 1500000;

		if(physics.size() > 50000)
		{
			nf += 5000000;
		}

		while(!physics.isEmpty() && M.ns() - nsx < nf)
		{
			applyPhysics(physics.pop());
		}

		dirty.clear();
		update.clear();
		popPhysics();
	}

	@Override
	public int getBiomeId(Biome biome)
	{
		BiomeBase mcBiome = CraftBlock.biomeToBiomeBase((Biome) biome);
		return mcBiome != null ? BiomeBase.a((BiomeBase) mcBiome) : 0;
	}

	@Override
	public Biome getBiome(int id)
	{
		BiomeBase mcBiome = BiomeBase.getBiome((int) id);
		return CraftBlock.biomeBaseToBiome((BiomeBase) mcBiome);
	}

	@Override
	public BlockType getBlock(Location location)
	{
		return new BlockType(location);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void setBlock(Location l, BlockType m)
	{
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();
		net.minecraft.server.v1_12_R1.World w = ((CraftWorld) l.getWorld()).getHandle();
		net.minecraft.server.v1_12_R1.Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
		BlockPosition bp = new BlockPosition(x, y, z);
		int combined = m.getMaterial().getId() + (m.getData() << 12);
		IBlockData ibd = net.minecraft.server.v1_12_R1.Block.getByCombinedId(combined);
		chunk.a(bp, ibd);
		makeDirty(l);
		processed++;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void setBlockNoPacket(Location l, BlockType m)
	{
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();
		net.minecraft.server.v1_12_R1.World w = ((CraftWorld) l.getWorld()).getHandle();
		net.minecraft.server.v1_12_R1.Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
		BlockPosition bp = new BlockPosition(x, y, z);
		int combined = m.getMaterial().getId() + (m.getData() << 12);
		IBlockData ibd = net.minecraft.server.v1_12_R1.Block.getByCombinedId(combined);
		chunk.a(bp, ibd);
	}

	@Override
	public void makeDirty(Location l)
	{
		if(!update.containsKey(l.getChunk()))
		{
			update.put(l.getChunk(), new GSet<Location>());
		}

		update.get(l.getChunk()).add(l);

		if(isPushingPhysics())
		{
			queueUpdate(l.getBlock());
		}
	}

	@Override
	public void makeDirty(Chunk c, int section)
	{
		if(!dirty.containsKey(c))
		{
			dirty.put(c, new GSet<Integer>());
		}

		dirty.get(c).add(section);
	}

	@Override
	public void makeDirty(Chunk c)
	{
		boolean[] s = getValidSections(c);

		for(int i = 0; i < s.length; i++)
		{
			if(s[i])
			{
				makeDirty(c, i);
			}
		}
	}

	@Override
	public void sendChunkSection(Chunk c, int bitmask)
	{
		for(Player i : P.getPlayersWithinViewOf(c))
		{
			sendChunkSection(c, bitmask, i);
		}
	}

	@Override
	public void sendChunkSection(Chunk c, int bitmask, Player p)
	{
		PacketPlayOutMapChunk map = new PacketPlayOutMapChunk(((CraftChunk) c).getHandle(), bitmask);
		sendPacket(p, map);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void sendBlockChange(Location l, BlockType t, Player player)
	{
		player.sendBlockChange(l, t.getMaterial(), t.getData());
	}

	@Override
	public void sendMultiBlockChange(Chunk c, GList<Location> points) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException
	{
		PacketPlayOutMultiBlockChange mb = new PacketPlayOutMultiBlockChange();
		PacketPlayOutMultiBlockChange.MultiBlockChangeInfo[] chs = new PacketPlayOutMultiBlockChange.MultiBlockChangeInfo[points.size()];
		ChunkCoordIntPair cp = new ChunkCoordIntPair(c.getX(), c.getZ());
		Field fcp = PacketPlayOutMultiBlockChange.class.getDeclaredField("a");
		Field fchs = PacketPlayOutMultiBlockChange.class.getDeclaredField("b");
		fcp.setAccessible(true);
		fchs.setAccessible(true);

		for(int i = 0; i < points.size(); i++)
		{
			net.minecraft.server.v1_12_R1.Block b = CraftMagicNumbers.getBlock((CraftBlock) points.get(i).getBlock());
			IBlockData d = b.getBlockData();
			int x = points.get(i).getBlockX();
			int y = points.get(i).getBlockY();
			int z = points.get(i).getBlockZ();
			x &= 15;
			z &= 15;
			chs[i] = mb.new MultiBlockChangeInfo((short) (x << 12 | z << 8 | y), d);
		}

		fcp.set(mb, cp);
		fchs.set(mb, chs);

		sendPacket(c, mb);
	}

	private int getBitMask(boolean[] modifiedSections)
	{
		int bitMask = 0;

		for(int section = 0; section < modifiedSections.length; section++)
		{
			if(modifiedSections[section])
			{
				bitMask += 1 << section;
			}
		}

		return bitMask;
	}

	private int getSection(int y)
	{
		return y >> 4;
	}

	@Override
	public int getBitmask(Chunk c)
	{
		return getBitMask(getValidSections(c));
	}

	@Override
	public boolean[] getValidSections(Chunk c)
	{
		boolean[] f = new boolean[16];
		Arrays.fill(f, false);

		for(ChunkSection i : ((CraftChunk) c).getHandle().getSections())
		{
			if(i == null)
			{
				continue;
			}

			System.out.println(i.getYPosition() / 16);
			f[i.getYPosition() / 16] = true;
		}

		return f;
	}

	@Override
	public void notifyEntity(Entity e)
	{
		for(Player i : P.getPlayersWithinViewOf(e.getLocation().getChunk()))
		{
			notifyEntity(e, i);
		}
	}

	@Override
	public void sendUnload(Chunk c)
	{
		for(Player i : P.getPlayersWithinViewOf(c))
		{
			sendUnload(c, i);
		}
	}

	@Override
	public void sendReload(Chunk c)
	{
		sendUnload(c);
		boolean[] bits = new boolean[16];
		Arrays.fill(bits, true);
		sendChunkSection(c, getBitMask(bits));
	}

	@Override
	public void makeFullyDirty(Chunk c)
	{
		for(int i = 0; i < 16; i++)
		{
			makeDirty(c, i);
		}
	}

	@Override
	public void sendReload(Chunk c, Player p)
	{
		sendUnload(c, p);
		boolean[] bits = new boolean[16];
		Arrays.fill(bits, true);
		sendChunkSection(c, getBitMask(bits), p);
	}

	@Override
	public void notifyEntity(Entity e, Player p)
	{
		PacketPlayOutEntity px = new PacketPlayOutEntity(e.getEntityId());
		sendPacket(p, px);
	}

	@Override
	public void sendUnload(Chunk c, Player p)
	{
		PacketPlayOutUnloadChunk px = new PacketPlayOutUnloadChunk(c.getX(), c.getZ());
		sendPacket(p, px);
	}

	@Override
	public void setBiome(World w, int x, int z, Biome b)
	{
		w.setBiome(x, z, b);
		makeFullyDirty(w.getChunkAt(w.getBlockAt(x, 0, z)));
	}

	@Override
	public void applyPhysics(Block bfg)
	{
		net.minecraft.server.v1_12_R1.Block b = CraftMagicNumbers.getBlock((CraftBlock) bfg);
		int x = bfg.getX();
		int y = bfg.getY();
		int z = bfg.getZ();
		BlockPosition bp = new BlockPosition(x, y, z);
		CraftWorld w = (CraftWorld) bfg.getWorld();
		net.minecraft.server.v1_12_R1.World v = (net.minecraft.server.v1_12_R1.World) w.getHandle();
		v.update(bp, b, true);
	}

	@Override
	public ItemStack getSkull(String uri) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		ItemStack localItemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		ItemMeta localItemMeta = localItemStack.getItemMeta();
		localItemMeta.setDisplayName("skull");
		GameProfile localGameProfile = new GameProfile(UUID.randomUUID(), null);
		byte[] arrayOfByte = Base64.encodeBytesToBytes(String.format("{textures:{SKIN:{url:\"%s\"}}}", new Object[] {uri}).getBytes());
		localGameProfile.getProperties().put("textures", new Property("textures", new String(arrayOfByte)));
		Field localField = null;
		localField = localItemMeta.getClass().getDeclaredField("profile");
		localField.setAccessible(true);
		localField.set(localItemMeta, localGameProfile);
		localItemStack.setItemMeta(localItemMeta);

		return localItemStack;
	}

	@Override
	public void makeSectionDirty(Location l)
	{
		makeDirty(l.getChunk(), l.getBlockY() >> 4);
	}

	@Override
	public void queueUpdate(Block b)
	{
		physics.add(b);
	}

	@Override
	public void pushPhysics()
	{
		push = true;
	}

	@Override
	public boolean isPushingPhysics()
	{
		return push;
	}

	@Override
	public void popPhysics()
	{
		push = false;
	}

	@Override
	public BlockType getBlockAsync(Location l)
	{
		try
		{
			BlockType b = world.get(l);

			if(b.getMaterial().equals(Material.MOB_SPAWNER))
			{
				try
				{
					// FUCKING UNSAFE
					if(ContentManager.isCustom(l.getBlock()))
					{
						CustomBlock cb = ContentManager.getBlock(l.getBlock());
						b.setCmat(cb.getMaterial());
						b.setCdur(cb.getDurabilityLock());
						b.setCustom(true);
					}
				}

				catch(Exception e)
				{

				}

				return b;
			}
		}

		catch(Exception e)
		{

		}

		return new BlockType();
	}

	@Override
	public int getGhostSize()
	{
		return world.size();
	}

	@Override
	public void updateBlockData(Location block, String mojangson, boolean notify)
	{
		try
		{
			BlockPosition pos = new BlockPosition(block.getBlockX(), block.getBlockY(), block.getBlockZ());
			net.minecraft.server.v1_12_R1.World nmsworld = ((CraftWorld) block.getWorld()).getHandle();
			IBlockData blockData = nmsworld.getType(pos);
			TileEntity tile = nmsworld.getTileEntity(pos);
			NBTTagCompound nbt = tile.save(new NBTTagCompound());
			nbt.g();
			NBTTagCompound parsedNBT = null;
			parsedNBT = MojangsonParser.parse(mojangson);
			nbt.a(parsedNBT);
			nbt.setInt("x", pos.getX());
			nbt.setInt("y", pos.getY());
			nbt.setInt("z", pos.getZ());
			tile.load(nbt);

			if(notify)
			{
				tile.update();
				nmsworld.notify(pos, blockData, blockData, 3);
				resetSpawnerRotation(block);
			}
		}

		catch(MojangsonParseException e)
		{
			System.out.println(e.getMessage());
		}

		catch(Exception e)
		{

		}
	}

	@Override
	public void updateBlockData(Location block, String mojangson)
	{
		updateBlockData(block, mojangson, false);
	}

	@Override
	public void sendActionBar(String s, Player player)
	{
		sendPacket(player, new PacketPlayOutChat(new ChatComponentText(s), ChatMessageType.GAME_INFO));
	}

	@Override
	public void sendTitle(String title, String subtitle, int i, int s, int o, Player p)
	{
		p.sendTitle(title, subtitle, i, s, o);
	}

	@Override
	public void sendTabHeaderFooter(String header, String footer, Player p)
	{
		PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

		try
		{
			PacketPlayOutPlayerListHeaderFooter.class.getField("a").set(packet, new ChatComponentText(header));
			PacketPlayOutPlayerListHeaderFooter.class.getField("b").set(packet, new ChatComponentText(footer));
		}

		catch(IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
		{
			e.printStackTrace();
		}

		sendPacket(p, packet);
	}

	@Override
	public void sendMessage(String s, Player p)
	{
		sendPacket(p, new PacketPlayOutChat(new ChatComponentText(s), ChatMessageType.CHAT));
	}

	@Override
	public void sendSystemMessage(String s, Player p)
	{
		sendPacket(p, new PacketPlayOutChat(new ChatComponentText(s), ChatMessageType.SYSTEM));
	}

	@Override
	public void sendPacket(Player p, Object packet)
	{
		((CraftPlayer) p).getHandle().playerConnection.sendPacket((Packet<?>) packet);
	}

	@Override
	public void sendPacket(Location l, Object packet)
	{
		sendPacket(l.getChunk(), packet);
	}

	@Override
	public void sendPacket(World world, Object packet)
	{
		for(Player i : world.getPlayers())
		{
			sendPacket(i, packet);
		}
	}

	@Override
	public void sendPacket(Object packet)
	{
		for(Player i : P.onlinePlayers())
		{
			sendPacket(i, packet);
		}
	}

	@Override
	public void sendPacket(Chunk c, Object packet)
	{
		for(Player i : P.getPlayersWithinViewOf(c))
		{
			sendPacket(i, packet);
		}
	}

	@Override
	public void sendResourcePackPacket(Player p, String url)
	{
		p.setResourcePack(url);
	}

	@Override
	public void sendResourcePackPacket(Player p, String url, byte[] hash)
	{
		p.setResourcePack(url, hash);
	}

	@Override
	public void sendResourcePack(Player p, String url)
	{
		sendResourcePackPrepare(p, new Runnable()
		{
			@Override
			public void run()
			{
				sendResourcePackPacket(p, url);
			}
		});
	}

	@Override
	public void sendResourcePack(Player p, ResourcePack pack)
	{
		new A()
		{
			@Override
			public void run()
			{
				String fn = UUID.randomUUID().toString().replaceAll("-", "") + ".zip";
				File f = new File(Fulcrum.server.getRoot(), fn);
				String uurl = getServerPublicAddress();
				System.out.println(p.getName() + " -> " + p.getAddress().getAddress().getHostAddress());

				if(p.getAddress().getAddress().getHostAddress().equals("127.0.0.1"))
				{
					System.out.println("Client is on the same network as the server. Setting url to local");

					try
					{
						uurl = Inet4Address.getLocalHost().getHostAddress();
					}

					catch(UnknownHostException e)
					{
						e.printStackTrace();
					}
				}

				String url = uurl;
				try
				{
					pack.writeToArchive(f);

					new S()
					{
						@Override
						public void run()
						{
							sendResourcePackPrepare(p, new Runnable()
							{
								@Override
								public void run()
								{
									sendResourcePackPacket(p, "http://" + url + ":" + Fulcrum.server.getPort() + "/" + fn);
									System.out.println("Sending " + p + " DYNAMIC pack @ " + "http://" + url + ":" + Fulcrum.server.getPort() + "/" + fn);
								}
							});
						}
					};
				}

				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		};
	}

	@Override
	public void sendResourcePackWeb(Player p, String pack)
	{
		new A()
		{
			@Override
			public void run()
			{
				String uurl = getServerPublicAddress();
				System.out.println(p.getName() + " -> " + p.getAddress().getAddress().getHostAddress());

				if(p.getAddress().getAddress().getHostAddress().equals("127.0.0.1"))
				{
					System.out.println("Client is on the same network as the server. Setting url to local");

					try
					{
						uurl = Inet4Address.getLocalHost().getHostAddress();
					}

					catch(UnknownHostException e)
					{
						e.printStackTrace();
					}
				}

				String url = uurl;

				try
				{
					new S()
					{
						@Override
						public void run()
						{
							sendResourcePackPrepare(p, new Runnable()
							{
								@Override
								public void run()
								{
									sendResourcePackPacket(p, "http://" + url + ":" + Fulcrum.server.getPort() + "/" + pack);
									System.out.println("Sending " + p + " DYNAMIC pack @ " + "http://" + url + ":" + Fulcrum.server.getPort() + "/" + pack);
								}
							});
						}
					};
				}

				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		};
	}

	@Override
	public void sendResourcePackPrepare(Player p, Runnable r)
	{
		PE.BLINDNESS.a(300).d(12000).apply(p);
		PE.SLOW.a(1000).d(12000).apply(p);
		PE.SLOW_DIGGING.a(1000).d(12000).apply(p);
		Location lx = p.getLocation().clone();
		Location la = p.getLocation().clone();
		lx.setDirection(new Vector(0, 1, 0));
		boolean[] fx = {false};
		boolean[] fa = {false};
		boolean f = p.isFlying();
		boolean faf = p.getAllowFlight();
		p.setAllowFlight(true);
		p.setFlying(true);

		new Task(0)
		{
			@Override
			public void run()
			{
				if(fx[0])
				{
					p.closeInventory();
					p.closeInventory();
					p.closeInventory();
					p.removePotionEffect(PotionEffectType.BLINDNESS);
					p.removePotionEffect(PotionEffectType.SLOW);
					p.removePotionEffect(PotionEffectType.SLOW_DIGGING);

					new TaskLater(20)
					{
						@Override
						public void run()
						{
							if(!fa[0])
							{
								sendAdvancementIntense(p, new ItemStack(Material.NETHER_STAR), C.GREEN + "Resources Loaded");
							}

							else
							{
								p.sendTitle("    ", C.RED + "Resources Failed to Load", 2, 20, 5);
							}
						}
					};

					p.setFlying(f);
					p.setAllowFlight(faf);
					cancel();
					p.teleport(la);
					p.resetPlayerTime();
					return;
				}

				p.setPlayerTime(13686, false);
				p.teleport(lx);
			}
		};
		sendAdvancementSubtle(p, new ItemStack(Material.FIREBALL), C.GREEN + "Compiling Resource Pack");

		new TaskLater(20)
		{
			@Override
			public void run()
			{
				r.run();
				Fulcrum.register(new Listener()
				{
					@EventHandler
					public void on(PlayerResourcePackStatusEvent e)
					{
						if(e.getPlayer().equals(p))
						{
							if(e.getStatus().equals(Status.ACCEPTED))
							{
								new TaskLater(20)
								{
									@Override
									public void run()
									{
										sendAdvancementSubtle(p, new ItemStack(Material.FIREBALL), C.GREEN + "Accepted Resource Pack\n" + C.BOLD + "Sending...");
									}
								};
							}

							if(e.getStatus().equals(Status.FAILED_DOWNLOAD))
							{
								Fulcrum.unregister(this);
								new TaskLater(5)
								{
									@Override
									public void run()
									{
										sendAdvancementSubtle(p, new ItemStack(Material.FIREBALL), C.RED + "Resource Pack " + C.BOLD + "FAILED...");
										fx[0] = true;
										fa[0] = true;
									}
								};
							}

							if(e.getStatus().equals(Status.DECLINED))
							{
								p.kickPlayer("In multiplayer options re-allow resource packs.");
							}

							if(e.getStatus().equals(Status.SUCCESSFULLY_LOADED))
							{
								Fulcrum.unregister(this);

								new TaskLater(5)
								{
									@Override
									public void run()
									{
										fx[0] = true;
										fa[0] = false;
									}
								};
							}
						}
					}

				});
			}
		};
	}

	@Override
	public String getServerPublicAddress()
	{
		try
		{
			BufferedReader pr = new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com/").openStream()));
			String address = pr.readLine();

			pr.close();

			return address;
		}

		catch(Exception e)
		{
			return null;
		}
	}

	@Override
	public int getSpawnerType(Location block)
	{
		if(block.getBlock().getType().equals(Material.MOB_SPAWNER))
		{
			return getBlockData(block);
		}

		return -1;
	}

	@Override
	public void setSpawnerType(Location block, int id)
	{
		setSpawnerType(block, ContentManager.getBlock(id).getMatt(), ContentManager.getBlock(id).getDurabilityLock(), ContentManager.getBlock(id).isEnchanted());
	}

	public int getBlockData(Location block)
	{
		try
		{
			BlockPosition pos = new BlockPosition(block.getBlockX(), block.getBlockY(), block.getBlockZ());
			net.minecraft.server.v1_12_R1.World nmsworld = ((CraftWorld) block.getWorld()).getHandle();
			TileEntity tile = nmsworld.getTileEntity(pos);
			NBTTagCompound nbt = tile.save(new NBTTagCompound());
			nbt.g();
			NBTTagCompound nbtsd = nbt.getCompound("SpawnData");
			nbtsd.g();
			NBTTagList list = nbtsd.getList("ArmorItems", 10);
			NBTTagCompound f = list.get(3);
			f.g();

			short d = f.getShort("Damage");
			String mat = f.getString("id");
			Material m = null;

			for(Material i : ContentManager.r().ass().getMattx().k())
			{
				if(("minecraft:" + ContentManager.r().ass().getMattx().get(i)).equals(mat))
				{
					m = i;
				}
			}

			if(m == null)
			{
				return -3;
			}

			for(CustomBlock i : ContentManager.getBlocks())
			{
				if(i.getType().equals(m) && i.getDurabilityLock() == d)
				{
					return i.getSuperID();
				}
			}
		}

		catch(Exception e)
		{

		}

		return -2;
	}

	@Override
	public void setSpawnerType(Location block, String mat, short dmg, boolean enchanted)
	{
		setBlockNoPacket(block, new BlockType(Material.MOB_SPAWNER));
		CreatureSpawner s = ((CreatureSpawner) block.getBlock().getState());
		s.setRequiredPlayerRange(0);
		updateBlockData(block, "{RequiredPlayerRange:0s}");
		updateBlockData(block, "{SpawnData:{id:\"minecraft:armor_stand\",Invisible:0,Marker:1}}");
		updateBlockData(block, "{SpawnData:{Invisible:1b,NoBasePlate:1b,ShowArms:0b,ArmorItems:[{id:\"\",Count:0},{id:\"\",Count:0},{id:\"\",Count:0},{id:\"minecraft:" + mat + "\",Count:1b,Damage:" + dmg + "s,tag:{Unbreakable:1" + (enchanted ? ",ench:[{id:0,lvl:0}]" : "") + "}}]}}", true);
	}

	@Override
	public void resetSpawnerRotation(Location block)
	{
		BlockPosition pos = new BlockPosition(block.getBlockX(), block.getBlockY(), block.getBlockZ());
		net.minecraft.server.v1_12_R1.World nmsworld = ((CraftWorld) block.getWorld()).getHandle();
		TileEntity tile = nmsworld.getTileEntity(pos);
		NBTTagCompound nbt = tile.save(new NBTTagCompound());
		NBTTagCompound ee = new NBTTagCompound();

		nbt.g();
		ee.g();

		PacketPlayOutTileEntityData bc = new PacketPlayOutTileEntityData(pos, 1, nbt);

		sendBlockChange(block, new BlockType(Material.AIR), P.getAnyPlayer());
		sendBlockChange(block, new BlockType(Material.MOB_SPAWNER), P.getAnyPlayer());
		sendPacket(bc);
	}

	@Override
	public void hideSpawner(Location block)
	{
		sendBlockChange(block, new BlockType(Material.AIR), P.getAnyPlayer());
	}

	@Override
	public void showSpawner(Location block)
	{
		sendBlockChange(block, new BlockType(Material.MOB_SPAWNER), P.getAnyPlayer());
		BlockPosition pos = new BlockPosition(block.getBlockX(), block.getBlockY(), block.getBlockZ());
		net.minecraft.server.v1_12_R1.World nmsworld = ((CraftWorld) block.getWorld()).getHandle();
		TileEntity tile = nmsworld.getTileEntity(pos);
		NBTTagCompound nbt = tile.save(new NBTTagCompound());
		NBTTagCompound ee = new NBTTagCompound();
		nbt.g();
		ee.g();
		PacketPlayOutTileEntityData bc = new PacketPlayOutTileEntityData(pos, 1, nbt);
		sendPacket(bc);
	}

	@Override
	public void pickup(Entity who, Entity item)
	{
		int c = 1;

		if(item instanceof Item)
		{
			c = ((Item) item).getItemStack().getAmount();
		}

		PacketPlayOutCollect p = new PacketPlayOutCollect(item.getEntityId(), who.getEntityId(), c);
		sendPacket(item.getLocation(), p);
	}

	@Override
	public void sendCrack(Block b, Entity e, double progress)
	{
		sendCrack(b, e.getEntityId(), progress);
	}

	@Override
	public void sendCrack(Block b, int eid, double progress)
	{
		BlockPosition bp = new BlockPosition(b.getX(), b.getY(), b.getZ());
		PacketPlayOutBlockBreakAnimation bx = new PacketPlayOutBlockBreakAnimation(eid, bp, (byte) (progress * 9.0));
		sendPacket(b.getLocation(), bx);
	}

	@Override
	public void damageBlock(Block b, double percent)
	{
		if(!blockDamage.containsKey(b))
		{
			blockDamage.put(b, percent);
		}

		blockDamage.put(b, blockDamage.get(b) + percent);
	}

	@Override
	public void brokedBlock(Block b)
	{
		blockDamage.remove(b);
	}

	@Override
	public boolean shouldBeBroken(Block b)
	{
		return isBeingBroken(b) && getBreakProgress(b) > 1;
	}

	@Override
	public boolean isBeingBroken(Block b)
	{
		return blockDamage.containsKey(b);
	}

	@Override
	public double getBreakProgress(Block b)
	{
		return isBeingBroken(b) ? blockDamage.get(b) : -1;
	}

	@Override
	public boolean canPlace(Player player, Block target)
	{
		Location center = target.getLocation().clone().add(0.5, 0.5, 0.5);

		for(Entity i : center.getWorld().getNearbyEntities(center, 0.5, 0.5, 0.5))
		{
			if(i instanceof LivingEntity)
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public void sendAdvancementIntense(Player p, ItemStack is, String text)
	{
		AdvancementHolder a = new AdvancementHolder(UUID.randomUUID().toString());
		a.withToast(true);
		a.withDescription("Go away");
		a.withFrame(FrameType.CHALLANGE);
		a.withAnnouncement(false);
		a.withTitle(text);
		a.withTrigger("minecraft:impossible");
		a.withIcon(is.getData());
		a.withBackground("minecraft:textures/blocks/bedrock.png");
		a.loadAdvancement();
		a.sendPlayer(p);

		new TaskLater(1)
		{
			@Override
			public void run()
			{
				a.delete(p);
			}
		};
	}

	@Override
	public void sendAdvancementSubtle(Player p, ItemStack is, String text)
	{
		AdvancementHolder a = new AdvancementHolder(UUID.randomUUID().toString());
		a.withToast(true);
		a.withDescription("Go away");
		a.withFrame(FrameType.GOAL);
		a.withAnnouncement(false);
		a.withTitle(text);
		a.withTrigger("minecraft:impossible");
		a.withIcon(is.getData());
		a.withBackground("minecraft:textures/blocks/bedrock.png");
		a.loadAdvancement();
		a.sendPlayer(p);

		new TaskLater(1)
		{
			@Override
			public void run()
			{
				a.delete(p);
			}
		};
	}

	@Override
	public GList<Location> getSpawners(Chunk c)
	{
		GList<Location> locations = new GList<Location>();
		net.minecraft.server.v1_12_R1.Chunk chunk = ((CraftChunk) c).getHandle();

		for(TileEntity i : chunk.tileEntities.values())
		{
			if(i instanceof TileEntityMobSpawner)
			{
				BlockPosition pos = i.getPosition();
				locations.add(new Location(c.getWorld(), pos.getX(), pos.getY(), pos.getZ()));
			}
		}

		return locations;
	}

	@Override
	public boolean isTileEntity(Block b)
	{
		if(b.getState().getClass().equals(CraftCreatureSpawner.class))
		{
			return false;
		}

		return !b.getState().getClass().equals(CraftBlockState.class);
	}

	@Override
	public PotionEffect getGlowEffect(DyeColor color)
	{
		return new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, true, color.getColor());
	}

	@Override
	public boolean isStone(Material type)
	{
		return CraftMagicNumbers.getBlock(type).getStepSound().equals(SoundEffectType.d);
	}

	@Override
	public boolean isGlass(Material type)
	{
		return CraftMagicNumbers.getBlock(type).getStepSound().equals(SoundEffectType.f);
	}

	@Override
	public boolean isMetal(Material type)
	{
		return CraftMagicNumbers.getBlock(type).getStepSound().equals(SoundEffectType.e);
	}

	@Override
	public boolean isCloth(Material type)
	{
		return CraftMagicNumbers.getBlock(type).getStepSound().equals(SoundEffectType.g);
	}

	@Override
	public void stopDigging(Block block, Player p)
	{
		PE.SLOW_DIGGING.a(-1).d(3).apply(p);
	}

	@Override
	public void forceSwing(Player p, Player ob)
	{
		PacketPlayOutAnimation a = new PacketPlayOutAnimation(((CraftPlayer) p).getHandle(), 0);
		sendPacket(p.getLocation(), a);
	}

	@Override
	public double getHardness(Material t)
	{
		return blockHardness.containsKey(t) ? blockHardness.get(t) : -1;
	}

	@Override
	public double getHardness(Block t)
	{
		return getHardness(t.getType());
	}

	@SuppressWarnings("deprecation")
	@Override
	public int cacheBlockData(ContentRegistry r)
	{
		try
		{
			Profiler p = new Profiler();
			p.begin();
			blockMinimums = new GMap<Material, Integer>();
			blockEffectives = new GMap<Material, String>();
			blockMaterials = new GMap<Material, net.minecraft.server.v1_12_R1.Material>();
			blockHardness = new GMap<Material, Double>();

			try
			{
				Field strength = net.minecraft.server.v1_12_R1.Block.class.getDeclaredField("strength");
				Field material = net.minecraft.server.v1_12_R1.Block.class.getDeclaredField("material");
				strength.setAccessible(true);
				material.setAccessible(true);

				for(Material i : Material.values())
				{
					if(i.getId() > 255)
					{
						continue;
					}

					net.minecraft.server.v1_12_R1.Block block = net.minecraft.server.v1_12_R1.Block.REGISTRY.getId(i.getId());

					if(block == null)
					{
						continue;
					}

					try
					{
						int level = 0;
						String type = ToolType.HAND;
						blockHardness.put(i, (double) strength.getFloat(block));
						blockMaterials.put(i, (net.minecraft.server.v1_12_R1.Material) material.get(block));
						net.minecraft.server.v1_12_R1.Material m = blockMaterials.get(i);

						if(m.equals(net.minecraft.server.v1_12_R1.Material.WOOD))
						{
							type = ToolType.AXE;
						}

						else if(m.equals(net.minecraft.server.v1_12_R1.Material.BANNER))
						{
							type = ToolType.AXE;
						}

						else if(m.equals(net.minecraft.server.v1_12_R1.Material.CLAY))
						{
							type = ToolType.SHOVEL;
						}

						else if(m.equals(net.minecraft.server.v1_12_R1.Material.CLOTH))
						{
							type = ToolType.SHEARS;
						}

						else if(m.equals(net.minecraft.server.v1_12_R1.Material.CORAL))
						{
							type = ToolType.AXE;
						}

						else if(m.equals(net.minecraft.server.v1_12_R1.Material.DRAGON_EGG))
						{
							type = ToolType.PICKAXE;
						}

						else if(m.equals(net.minecraft.server.v1_12_R1.Material.EARTH))
						{
							type = ToolType.SHOVEL;
						}

						else if(m.equals(net.minecraft.server.v1_12_R1.Material.ICE))
						{
							type = ToolType.PICKAXE;
						}

						else if(m.equals(net.minecraft.server.v1_12_R1.Material.LEAVES))
						{
							type = ToolType.SHEARS;
						}

						else if(m.equals(net.minecraft.server.v1_12_R1.Material.ORE))
						{
							type = ToolType.PICKAXE;
						}

						else if(m.equals(net.minecraft.server.v1_12_R1.Material.PACKED_ICE))
						{
							type = ToolType.PICKAXE;
						}

						else if(m.equals(net.minecraft.server.v1_12_R1.Material.PISTON))
						{
							type = ToolType.PICKAXE;
						}

						else if(m.equals(net.minecraft.server.v1_12_R1.Material.PUMPKIN))
						{
							type = ToolType.AXE;
						}

						else if(m.equals(net.minecraft.server.v1_12_R1.Material.SAND))
						{
							type = ToolType.SHOVEL;
						}

						else if(m.equals(net.minecraft.server.v1_12_R1.Material.SNOW_BLOCK))
						{
							type = ToolType.SHOVEL;
						}

						else if(m.equals(net.minecraft.server.v1_12_R1.Material.SNOW_LAYER))
						{
							type = ToolType.SHOVEL;
						}

						else if(m.equals(net.minecraft.server.v1_12_R1.Material.STONE))
						{
							type = ToolType.PICKAXE;
						}

						else if(m.equals(net.minecraft.server.v1_12_R1.Material.WEB))
						{
							type = ToolType.SWORD;
						}

						else if(m.equals(net.minecraft.server.v1_12_R1.Material.WOOD))
						{
							type = ToolType.AXE;
						}

						else if(m.equals(net.minecraft.server.v1_12_R1.Material.WOOL))
						{
							type = ToolType.SHEARS;
						}

						if(!type.equals(ToolType.HAND) && !type.equals(ToolType.SHEARS))
						{
							switch(i)
							{
								case ANVIL:
									level = ToolLevel.WOOD;
									break;
								case BEACON:
									level = ToolLevel.WOOD;
									break;
								case BLACK_GLAZED_TERRACOTTA:
									level = ToolLevel.WOOD;
									break;
								case BLACK_SHULKER_BOX:
									level = ToolLevel.WOOD;
									break;
								case BLUE_GLAZED_TERRACOTTA:
									level = ToolLevel.WOOD;
									break;
								case BLUE_SHULKER_BOX:
									level = ToolLevel.WOOD;
									break;
								case BONE_BLOCK:
									level = ToolLevel.WOOD;
									break;
								case BREWING_STAND:
									level = ToolLevel.WOOD;
									break;
								case BRICK:
									level = ToolLevel.WOOD;
									break;
								case BRICK_STAIRS:
									level = ToolLevel.WOOD;
									break;
								case BROWN_GLAZED_TERRACOTTA:
									level = ToolLevel.WOOD;
									break;
								case BROWN_SHULKER_BOX:
									level = ToolLevel.WOOD;
									break;
								case BURNING_FURNACE:
									level = ToolLevel.WOOD;
									break;
								case CAULDRON:
									level = ToolLevel.WOOD;
									break;
								case COAL_BLOCK:
									level = ToolLevel.STONE;
									break;
								case COAL_ORE:
									level = ToolLevel.WOOD;
									break;
								case COBBLESTONE:
									level = ToolLevel.WOOD;
									break;
								case COBBLESTONE_STAIRS:
									level = ToolLevel.WOOD;
									break;
								case COBBLE_WALL:
									level = ToolLevel.WOOD;
									break;
								case CONCRETE:
									level = ToolLevel.WOOD;
									break;
								case CYAN_GLAZED_TERRACOTTA:
									level = ToolLevel.WOOD;
									break;
								case CYAN_SHULKER_BOX:
									level = ToolLevel.WOOD;
									break;
								case DAYLIGHT_DETECTOR:
									level = ToolLevel.WOOD;
									break;
								case DAYLIGHT_DETECTOR_INVERTED:
									level = ToolLevel.WOOD;
									break;
								case DIAMOND_BLOCK:
									level = ToolLevel.STONE;
									break;
								case DIAMOND_ORE:
									level = ToolLevel.IRON;
									break;
								case DISPENSER:
									level = ToolLevel.WOOD;
									break;
								case DRAGON_EGG:
									level = ToolLevel.WOOD;
									break;
								case DROPPER:
									level = ToolLevel.WOOD;
									break;
								case EMERALD_BLOCK:
									level = ToolLevel.STONE;
									break;
								case EMERALD_ORE:
									level = ToolLevel.IRON;
									break;
								case ENCHANTMENT_TABLE:
									level = ToolLevel.WOOD;
									break;
								case ENDER_CHEST:
									level = ToolLevel.WOOD;
									break;
								case ENDER_PORTAL_FRAME:
									level = ToolLevel.WOOD;
									break;
								case ENDER_STONE:
									level = ToolLevel.WOOD;
									break;
								case END_BRICKS:
									level = ToolLevel.WOOD;
									break;
								case END_GATEWAY:
									level = ToolLevel.WOOD;
									break;
								case FLOWER_POT:
									level = ToolLevel.WOOD;
									break;
								case FROSTED_ICE:
									level = ToolLevel.WOOD;
									break;
								case FURNACE:
									level = ToolLevel.WOOD;
									break;
								case GOLD_BLOCK:
									level = ToolLevel.STONE;
									break;
								case GOLD_ORE:
									level = ToolLevel.IRON;
									break;
								case GRAY_GLAZED_TERRACOTTA:
									level = ToolLevel.WOOD;
									break;
								case GRAY_SHULKER_BOX:
									level = ToolLevel.WOOD;
									break;
								case GREEN_GLAZED_TERRACOTTA:
									level = ToolLevel.WOOD;
									break;
								case GREEN_SHULKER_BOX:
									level = ToolLevel.WOOD;
									break;
								case HARD_CLAY:
									level = ToolLevel.WOOD;
									break;
								case HOPPER:
									level = ToolLevel.WOOD;
									break;
								case IRON_BLOCK:
									level = ToolLevel.STONE;
									break;
								case IRON_DOOR_BLOCK:
									level = ToolLevel.STONE;
									break;
								case IRON_FENCE:
									level = ToolLevel.WOOD;
									break;
								case IRON_ORE:
									level = ToolLevel.STONE;
									break;
								case IRON_PLATE:
									level = ToolLevel.WOOD;
									break;
								case IRON_TRAPDOOR:
									level = ToolLevel.WOOD;
									break;
								case JUKEBOX:
									level = ToolLevel.WOOD;
									break;
								case LAPIS_BLOCK:
									level = ToolLevel.STONE;
									break;
								case LAPIS_ORE:
									level = ToolLevel.STONE;
									break;
								case LIGHT_BLUE_GLAZED_TERRACOTTA:
									level = ToolLevel.WOOD;
									break;
								case LIGHT_BLUE_SHULKER_BOX:
									level = ToolLevel.WOOD;
									break;
								case LIME_GLAZED_TERRACOTTA:
									level = ToolLevel.WOOD;
									break;
								case LIME_SHULKER_BOX:
									level = ToolLevel.WOOD;
									break;
								case MAGENTA_GLAZED_TERRACOTTA:
									level = ToolLevel.WOOD;
									break;
								case MAGENTA_SHULKER_BOX:
									level = ToolLevel.WOOD;
									break;
								case MAGMA:
									level = ToolLevel.WOOD;
									break;
								case MOB_SPAWNER:
									level = ToolLevel.WOOD;
									break;
								case MOSSY_COBBLESTONE:
									level = ToolLevel.WOOD;
									break;
								case NETHERRACK:
									level = ToolLevel.WOOD;
									break;
								case NETHER_BRICK:
									level = ToolLevel.WOOD;
									break;
								case NETHER_BRICK_ITEM:
									level = ToolLevel.WOOD;
									break;
								case NETHER_BRICK_STAIRS:
									level = ToolLevel.WOOD;
									break;
								case NETHER_FENCE:
									level = ToolLevel.WOOD;
									break;
								case NOTE_BLOCK:
									level = ToolLevel.WOOD;
									break;
								case OBSERVER:
									level = ToolLevel.WOOD;
									break;
								case OBSIDIAN:
									level = ToolLevel.WOOD;
									break;
								case ORANGE_GLAZED_TERRACOTTA:
									level = ToolLevel.WOOD;
									break;
								case ORANGE_SHULKER_BOX:
									level = ToolLevel.WOOD;
									break;
								case PINK_GLAZED_TERRACOTTA:
									level = ToolLevel.WOOD;
									break;
								case PINK_SHULKER_BOX:
									level = ToolLevel.WOOD;
									break;
								case PISTON_BASE:
									level = ToolLevel.WOOD;
									break;
								case PISTON_EXTENSION:
									level = ToolLevel.WOOD;
									break;
								case PISTON_MOVING_PIECE:
									level = ToolLevel.WOOD;
									break;
								case PISTON_STICKY_BASE:
									level = ToolLevel.WOOD;
									break;
								case PRISMARINE:
									level = ToolLevel.WOOD;
									break;
								case PRISMARINE_CRYSTALS:
									level = ToolLevel.WOOD;
									break;
								case PRISMARINE_SHARD:
									level = ToolLevel.WOOD;
									break;
								case PURPLE_GLAZED_TERRACOTTA:
									level = ToolLevel.WOOD;
									break;
								case PURPLE_SHULKER_BOX:
									level = ToolLevel.WOOD;
									break;
								case PURPUR_BLOCK:
									level = ToolLevel.WOOD;
									break;
								case PURPUR_DOUBLE_SLAB:
									level = ToolLevel.WOOD;
									break;
								case PURPUR_PILLAR:
									level = ToolLevel.WOOD;
									break;
								case PURPUR_SLAB:
									level = ToolLevel.WOOD;
									break;
								case PURPUR_STAIRS:
									level = ToolLevel.WOOD;
									break;
								case QUARTZ_BLOCK:
									level = ToolLevel.STONE;
									break;
								case QUARTZ_ORE:
									level = ToolLevel.WOOD;
									break;
								case QUARTZ_STAIRS:
									level = ToolLevel.WOOD;
									break;
								case REDSTONE_BLOCK:
									level = ToolLevel.STONE;
									break;
								case REDSTONE_LAMP_OFF:
									level = ToolLevel.WOOD;
									break;
								case REDSTONE_LAMP_ON:
									level = ToolLevel.WOOD;
									break;
								case REDSTONE_ORE:
									level = ToolLevel.IRON;
									break;
								case RED_GLAZED_TERRACOTTA:
									level = ToolLevel.WOOD;
									break;
								case RED_MUSHROOM:
									level = ToolLevel.WOOD;
									break;
								case RED_NETHER_BRICK:
									level = ToolLevel.WOOD;
									break;
								case RED_SANDSTONE:
									level = ToolLevel.WOOD;
									break;
								case RED_SANDSTONE_STAIRS:
									level = ToolLevel.WOOD;
									break;
								case RED_SHULKER_BOX:
									level = ToolLevel.WOOD;
									break;
								case SANDSTONE:
									level = ToolLevel.WOOD;
									break;
								case SANDSTONE_STAIRS:
									level = ToolLevel.WOOD;
									break;
								case SEA_LANTERN:
									level = ToolLevel.WOOD;
									break;
								case SHULKER_SHELL:
									level = ToolLevel.WOOD;
									break;
								case SILVER_GLAZED_TERRACOTTA:
									level = ToolLevel.WOOD;
									break;
								case SILVER_SHULKER_BOX:
									level = ToolLevel.WOOD;
									break;
								case SMOOTH_BRICK:
									level = ToolLevel.WOOD;
									break;
								case SMOOTH_STAIRS:
									level = ToolLevel.WOOD;
									break;
								case SNOW:
									level = ToolLevel.WOOD;
									break;
								case SNOW_BLOCK:
									level = ToolLevel.WOOD;
									break;
								case STAINED_CLAY:
									level = ToolLevel.WOOD;
									break;
								case STEP:
									level = ToolLevel.WOOD;
									break;
								case STONE:
									level = ToolLevel.WOOD;
									break;
								case STONE_PLATE:
									level = ToolLevel.WOOD;
									break;
								case STONE_SLAB2:
									level = ToolLevel.WOOD;
									break;
								case WEB:
									level = ToolLevel.WOOD;
									break;
								case WHITE_GLAZED_TERRACOTTA:
									level = ToolLevel.WOOD;
									break;
								case WHITE_SHULKER_BOX:
									level = ToolLevel.WOOD;
									break;
								case YELLOW_GLAZED_TERRACOTTA:
									level = ToolLevel.WOOD;
									break;
								case YELLOW_SHULKER_BOX:
									level = ToolLevel.WOOD;
									break;
								default:
									break;
							}
						}

						blockMinimums.put(i, level);
						blockEffectives.put(i, type);
						r.o("Identified " + i.name() + " HARDNESS: " + blockHardness.get(i) + " EFFECTIVE: " + blockEffectives.get(i) + " MINLEVEL: " + blockMinimums.get(i));
					}

					catch(Throwable e)
					{
						e.printStackTrace();
					}
				}
			}

			catch(Throwable e)
			{
				e.printStackTrace();
			}

			p.end();
			r.i("Cached " + F.f(blockHardness.size()) + " vanilla block types in " + F.time(p.getMilliseconds(), 1));
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return blockHardness.size();
	}

	@Override
	public int getMinimumLevel(Block b)
	{
		return blockMinimums.containsKey(b.getType()) ? blockMinimums.get(b.getType()) : 0;
	}

	@Override
	public String getEffectiveTool(Block b)
	{
		String v = blockEffectives.get(b.getType());
		return v == null ? ToolType.HAND : v;
	}

	@Override
	public boolean shouldDigFaster(Block b, String tool)
	{
		return getEffectiveTool(b).equals(tool);
	}
}
