package com.volmit.fulcrum.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
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

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.bukkit.Base64;
import com.volmit.fulcrum.bukkit.BlockType;
import com.volmit.fulcrum.bukkit.P;
import com.volmit.fulcrum.bukkit.Task;
import com.volmit.fulcrum.lang.F;
import com.volmit.fulcrum.lang.GList;
import com.volmit.fulcrum.lang.GMap;
import com.volmit.fulcrum.lang.GSet;
import com.volmit.fulcrum.lang.M;
import com.volmit.fulcrum.world.scm.GhostWorld;

import net.minecraft.server.v1_12_R1.BiomeBase;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_12_R1.ChunkSection;
import net.minecraft.server.v1_12_R1.IBlockData;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntity;
import net.minecraft.server.v1_12_R1.PacketPlayOutMapChunk;
import net.minecraft.server.v1_12_R1.PacketPlayOutMultiBlockChange;
import net.minecraft.server.v1_12_R1.PacketPlayOutUnloadChunk;

public final class Adapter12 implements IAdapter
{
	private GhostWorld world;
	private GMap<Chunk, GSet<Location>> update;
	private GMap<Chunk, GSet<Integer>> dirty;
	private GList<Block> physics;
	private GSet<Chunk> drop;
	private GSet<Chunk> udrop;
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

	@Override
	public void sendResourcePack(Player p, String url)
	{
		p.setResourcePack(url);
		Fulcrum.register(new Listener()
		{
			@EventHandler
			public void on(PlayerResourcePackStatusEvent e)
			{
				if(e.getPlayer().equals(p))
				{
					if(e.getStatus().equals(Status.ACCEPTED))
					{
						p.sendMessage("Good Boy");
					}

					if(e.getStatus().equals(Status.FAILED_DOWNLOAD))
					{
						p.kickPlayer("Stop using TimeWarnerCable");
					}

					if(e.getStatus().equals(Status.DECLINED))
					{
						p.kickPlayer("In multiplayer options re-allow resource packs.");
					}

					if(e.getStatus().equals(Status.SUCCESSFULLY_LOADED))
					{
						p.sendMessage("All set.");
						Fulcrum.unregister(this);
					}
				}
			}
		});
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
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(map);
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

		for(Player i : P.getPlayersWithinViewOf(c))
		{
			((CraftPlayer) i).getHandle().playerConnection.sendPacket(mb);
		}
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
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(px);
	}

	@Override
	public void sendUnload(Chunk c, Player p)
	{
		PacketPlayOutUnloadChunk px = new PacketPlayOutUnloadChunk(c.getX(), c.getZ());
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(px);
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
		return world.get(l);
	}

	@Override
	public int getGhostSize()
	{
		return world.size();
	}
}
