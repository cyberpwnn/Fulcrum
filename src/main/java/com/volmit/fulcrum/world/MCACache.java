package com.volmit.fulcrum.world;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.bukkit.TICK;
import com.volmit.fulcrum.data.cluster.ClusterBoolean;
import com.volmit.fulcrum.data.cluster.ClusterDouble;
import com.volmit.fulcrum.data.cluster.ClusterFloat;
import com.volmit.fulcrum.data.cluster.ClusterInt;
import com.volmit.fulcrum.data.cluster.ClusterLong;
import com.volmit.fulcrum.data.cluster.ClusterStringList;
import com.volmit.fulcrum.data.cluster.DataCluster;
import com.volmit.fulcrum.data.cluster.ICluster;
import com.volmit.volume.lang.collections.GList;
import com.volmit.volume.lang.collections.GMap;
import com.volmit.volume.lang.collections.GSet;

public class MCACache implements Listener
{
	private GMap<MCAKey, GMap<ChunkKey, GMap<BlockKey, GMap<String, DataCluster>>>> blockMap;
	private GMap<MCAKey, GMap<ChunkKey, GMap<String, DataCluster>>> chunkMap;
	private GMap<String, GMap<String, DataCluster>> worldMap;

	public MCACache()
	{
		Bukkit.getPluginManager().registerEvents(this, Fulcrum.instance);
		blockMap = new GMap<MCAKey, GMap<ChunkKey, GMap<BlockKey, GMap<String, DataCluster>>>>();
		chunkMap = new GMap<MCAKey, GMap<ChunkKey, GMap<String, DataCluster>>>();
		worldMap = new GMap<String, GMap<String, DataCluster>>();
	}

	public int size()
	{
		return blockSize() + chunkSize() + worldSize();
	}

	public int blockSize()
	{
		int v = 0;

		for(MCAKey i : blockMap.k())
		{
			for(ChunkKey j : blockMap.get(i).k())
			{
				for(BlockKey k : blockMap.get(i).get(j).k())
				{
					v += blockMap.get(i).get(j).get(k).size();
				}
			}
		}

		return v;
	}

	public int chunkSize()
	{
		int v = 0;

		for(MCAKey i : chunkMap.k())
		{
			for(ChunkKey j : chunkMap.get(i).k())
			{
				v += chunkMap.get(i).get(j).size();
			}
		}

		return v;
	}

	public int worldSize()
	{
		int v = 0;

		for(String i : worldMap.k())
		{
			v += worldMap.get(i).size();
		}

		return v;
	}

	public void push(Block block, String key, DataCluster cc)
	{
		MCAKey mca = new MCAKey(block.getChunk());
		ChunkKey ck = new ChunkKey(block.getChunk());
		BlockKey bk = new BlockKey(block);

		if(!blockMap.containsKey(mca))
		{
			blockMap.put(mca, new GMap<ChunkKey, GMap<BlockKey, GMap<String, DataCluster>>>());
		}

		if(!blockMap.get(mca).containsKey(ck))
		{
			blockMap.get(mca).put(ck, new GMap<BlockKey, GMap<String, DataCluster>>());
		}

		if(!blockMap.get(mca).get(ck).containsKey(bk))
		{
			blockMap.get(mca).get(ck).put(bk, new GMap<String, DataCluster>());
		}

		blockMap.get(mca).get(ck).get(bk).put(key, cc);
	}

	public void push(Chunk chunk, String key, DataCluster cc)
	{
		MCAKey mca = new MCAKey(chunk);
		ChunkKey ck = new ChunkKey(chunk);

		if(!chunkMap.containsKey(mca))
		{
			chunkMap.put(mca, new GMap<ChunkKey, GMap<String, DataCluster>>());
		}

		if(!chunkMap.get(mca).containsKey(ck))
		{
			chunkMap.get(mca).put(ck, new GMap<String, DataCluster>());
		}

		chunkMap.get(mca).get(ck).put(key, cc);
	}

	public void push(World world, String key, DataCluster cc)
	{
		if(!worldMap.containsKey(world.getName()))
		{
			worldMap.put(world.getName(), new GMap<String, DataCluster>());
		}

		worldMap.get(world.getName()).put(key, cc);
	}

	public DataCluster pull(Block block, String key) throws IOException
	{
		MCAKey mca = new MCAKey(block.getChunk());
		ChunkKey ck = new ChunkKey(block.getChunk());
		BlockKey bk = new BlockKey(block);

		if(!blockMap.containsKey(mca) || !blockMap.get(mca).containsKey(ck) || !blockMap.get(mca).get(ck).containsKey(bk) || !blockMap.get(mca).get(ck).get(bk).containsKey(key))
		{
			readMCA(mca);
		}

		if(!blockMap.containsKey(mca) || !blockMap.get(mca).containsKey(ck) || !blockMap.get(mca).get(ck).containsKey(bk) || !blockMap.get(mca).get(ck).get(bk).containsKey(key))
		{
			return new DataCluster();
		}

		return blockMap.get(mca).get(ck).get(bk).get(key);
	}

	public DataCluster pull(Chunk chunk, String key) throws IOException
	{
		MCAKey mca = new MCAKey(chunk);
		ChunkKey ck = new ChunkKey(chunk);

		if(!chunkMap.containsKey(mca) || !chunkMap.get(mca).containsKey(ck) || !chunkMap.get(mca).get(ck).containsKey(key))
		{
			readMCA(mca);
		}

		if(!chunkMap.containsKey(mca) || !chunkMap.get(mca).containsKey(ck) || !chunkMap.get(mca).get(ck).containsKey(key))
		{
			return new DataCluster();
		}

		return chunkMap.get(mca).get(ck).get(key);
	}

	public DataCluster pull(World world, String key) throws IOException
	{
		if(!worldMap.containsKey(world.getName()) || worldMap.get(world.getName()).containsKey(key))
		{
			readWorld(world.getName());
		}

		if(!worldMap.containsKey(world.getName()) || worldMap.get(world.getName()).containsKey(key))
		{
			return new DataCluster();
		}

		return worldMap.get(world.getName()).get(key);
	}

	public void flush() throws IOException
	{
		saveAll();
		blockMap.clear();
		chunkMap.clear();
		worldMap.clear();
	}

	public void flush(MCAKey key) throws IOException
	{
		writeMCA(key);
		blockMap.remove(key);
		chunkMap.remove(key);
	}

	public void saveAll() throws IOException
	{
		GSet<MCAKey> keys = new GSet<MCAKey>();
		keys.addAll(blockMap.k());
		keys.addAll(chunkMap.k());

		for(MCAKey i : keys)
		{
			writeMCA(i);
		}

		for(String i : worldMap.k())
		{
			writeWorld(i);
		}
	}

	public boolean hasMCA(MCAKey key)
	{
		File f = new File(Bukkit.getWorld(key.getWorld()).getWorldFolder(), "fulcrum");
		File d = new File(f, key.getX() + "." + key.getZ() + ".rtg");

		return d.exists();
	}

	public boolean hasWorld(String world)
	{
		File d = new File(new File(Bukkit.getWorld(world).getWorldFolder(), "fulcrum"), world + ".rtg");

		return d.exists();
	}

	public void readWorld(String world) throws IOException
	{
		File d = new File(new File(Bukkit.getWorld(world).getWorldFolder(), "fulcrum"), world + ".rtg");

		if(!d.exists())
		{
			return;
		}

		FileInputStream fin = new FileInputStream(d);
		GZIPInputStream gzi = new GZIPInputStream(fin);
		DataInputStream din = new DataInputStream(gzi);

		long size = din.readLong();

		for(int i = 0; i < size; i++)
		{
			String name = din.readUTF();
			int length = din.readInt();
			byte[] data = new byte[length];
			din.read(data, 0, length);
			DataCluster cc = load(data);

			if(!worldMap.containsKey(world))
			{
				worldMap.put(world, new GMap<String, DataCluster>());
			}

			worldMap.get(world).put(name, cc);
		}

		din.close();
	}

	public void writeWorld(String world) throws IOException
	{
		File d = new File(new File(Bukkit.getWorld(world).getWorldFolder(), "fulcrum"), world + ".rtg");

		if(!d.exists())
		{
			d.createNewFile();
		}

		FileOutputStream fos = new FileOutputStream(d);
		GZIPOutputStream gzo = new GZIPOutputStream(fos);
		DataOutputStream dos = new DataOutputStream(gzo);

		if(worldMap.containsKey(world))
		{
			dos.writeLong(worldMap.get(world).size());

			for(String i : worldMap.get(world).k())
			{
				dos.writeUTF(i);
				byte[] data = save(worldMap.get(world).get(i));
				dos.writeInt(data.length);
				dos.write(data);
			}
		}

		else
		{
			dos.writeLong(0);
		}

		dos.close();
	}

	public void readMCA(MCAKey key) throws IOException
	{
		try
		{
			File f = new File(Bukkit.getWorld(key.getWorld()).getWorldFolder(), "fulcrum");
			File d = new File(f, key.getX() + "." + key.getZ() + ".rtg");

			if(!d.exists())
			{
				return;
			}

			FileInputStream fin = new FileInputStream(d);
			GZIPInputStream gzi = new GZIPInputStream(fin);
			DataInputStream din = new DataInputStream(gzi);

			long blockSize = din.readLong();

			for(int i = 0; i < blockSize; i++)
			{
				int x = din.readInt();
				int y = din.readInt();
				int z = din.readInt();
				String category = din.readUTF();
				int length = din.readInt();
				byte[] data = new byte[length];
				din.read(data, 0, length);
				ChunkKey ck = new ChunkKey(key.getWorld(), x >> 4, z >> 4);
				BlockKey bk = new BlockKey(key.getWorld(), x, y, z);
				DataCluster cc = load(data);

				if(!blockMap.containsKey(key))
				{
					blockMap.put(key, new GMap<ChunkKey, GMap<BlockKey, GMap<String, DataCluster>>>());
				}

				if(!blockMap.get(key).containsKey(ck))
				{
					blockMap.get(key).put(ck, new GMap<BlockKey, GMap<String, DataCluster>>());
				}

				if(!blockMap.get(key).get(ck).containsKey(bk))
				{
					blockMap.get(key).get(ck).put(bk, new GMap<String, DataCluster>());
				}

				blockMap.get(key).get(ck).get(bk).put(category, cc);
			}

			long chunkSize = din.readLong();

			for(int i = 0; i < chunkSize; i++)
			{
				int x = din.readInt();
				int z = din.readInt();
				String category = din.readUTF();
				int length = din.readInt();
				byte[] data = new byte[length];
				din.read(data, 0, length);
				ChunkKey ck = new ChunkKey(key.getWorld(), x, z);
				DataCluster cc = load(data);

				if(!chunkMap.containsKey(key))
				{
					chunkMap.put(key, new GMap<ChunkKey, GMap<String, DataCluster>>());
				}

				if(!chunkMap.get(key).containsKey(ck))
				{
					chunkMap.get(key).put(ck, new GMap<String, DataCluster>());
				}

				chunkMap.get(key).get(ck).put(category, cc);
			}

			din.close();
		}

		catch(EOFException e)
		{
			System.out.println("Fulcrum failed to read ghost data at MCA " + key.getX() + " " + key.getZ() + " deleting.");
		}
	}

	public void writeMCA(MCAKey key) throws IOException
	{
		File f = new File(Bukkit.getWorld(key.getWorld()).getWorldFolder(), "fulcrum");
		File d = new File(f, key.getX() + "." + key.getZ() + ".rtg");
		d.getParentFile().mkdirs();

		if(!d.exists())
		{
			d.createNewFile();
		}

		FileOutputStream fos = new FileOutputStream(d);
		GZIPOutputStream gzo = new GZIPOutputStream(fos);
		DataOutputStream dos = new DataOutputStream(gzo);

		long blockSize = 0;
		long chunkSize = 0;

		if(blockMap.containsKey(key))
		{
			for(ChunkKey i : blockMap.get(key).k())
			{
				for(BlockKey j : blockMap.get(key).get(i).k())
				{
					for(String k : blockMap.get(key).get(i).get(j).k())
					{
						if(!blockMap.get(key).get(i).get(j).get(k).isEmpty())
						{
							blockSize++;
						}
					}
				}
			}

			dos.writeLong(blockSize);

			for(ChunkKey i : blockMap.get(key).k())
			{
				for(BlockKey j : blockMap.get(key).get(i).k())
				{
					for(String k : blockMap.get(key).get(i).get(j).k())
					{
						DataCluster cc = blockMap.get(key).get(i).get(j).get(k);
						byte[] raw = save(cc);
						dos.writeInt(j.getX());
						dos.writeInt(j.getY());
						dos.writeInt(j.getZ());
						dos.writeUTF(k);
						dos.writeInt(raw.length);
						dos.write(raw);
					}
				}
			}
		}

		else
		{
			dos.writeLong(0);
		}

		if(chunkMap.containsKey(key))
		{
			for(ChunkKey i : chunkMap.get(key).k())
			{
				for(String j : chunkMap.get(key).get(i).k())
				{
					if(!chunkMap.get(key).get(i).get(j).isEmpty())
					{
						chunkSize++;
					}
				}
			}

			dos.writeLong(chunkSize);

			for(ChunkKey i : chunkMap.get(key).k())
			{
				for(String j : chunkMap.get(key).get(i).k())
				{
					DataCluster cc = chunkMap.get(key).get(i).get(j);
					byte[] raw = save(cc);
					dos.writeInt(i.getX());
					dos.writeInt(i.getZ());
					dos.writeUTF(j);
					dos.writeInt(raw.length);
					dos.write(raw);
				}
			}
		}

		else
		{
			dos.writeLong(0);
		}

		dos.close();
	}

	public byte[] save(DataCluster c) throws IOException
	{
		ByteArrayOutputStream boas = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(boas);
		dos.writeInt(c.k().size());

		for(String i : c.k())
		{
			dos.writeUTF(c.get(i).getTypeKey() + "@" + i);

			if(c.get(i) instanceof ClusterBoolean)
			{
				dos.writeBoolean(c.getBoolean(i));
			}

			else if(c.get(i) instanceof ClusterInt)
			{
				dos.writeInt(c.getInt(i));
			}

			else if(c.get(i) instanceof ClusterLong)
			{
				dos.writeLong(c.getLong(i));
			}

			else if(c.get(i) instanceof ClusterFloat)
			{
				dos.writeFloat(c.getFloat(i));
			}

			else if(c.get(i) instanceof ClusterDouble)
			{
				dos.writeDouble(c.getDouble(i));
			}

			else if(c.get(i) instanceof ClusterStringList)
			{
				@SuppressWarnings("unchecked")
				List<String> s = (List<String>) c.get(i).get();
				dos.writeInt(s.size());

				for(String j : s)
				{
					dos.writeUTF(j);
				}
			}

			else
			{
				dos.writeUTF(c.get(i).write());
			}
		}

		dos.close();
		return boas.toByteArray();
	}

	public DataCluster load(byte[] t) throws IOException
	{
		ByteArrayInputStream bin = new ByteArrayInputStream(t);
		DataInputStream din = new DataInputStream(bin);
		DataCluster c = new DataCluster();
		int size = din.readInt();

		for(int v = 0; v < size; v++)
		{
			String i = din.readUTF();
			String ct = i.split("@")[0];
			String key = i.split("@")[1];
			ICluster cx = DataCluster.getClusterType(ct);

			if(cx instanceof ClusterBoolean)
			{
				cx.set(din.readBoolean());
			}

			else if(cx instanceof ClusterInt)
			{
				cx.set(din.readInt());
			}

			else if(cx instanceof ClusterLong)
			{
				cx.set(din.readLong());
			}

			else if(cx instanceof ClusterFloat)
			{
				cx.set(din.readFloat());
			}

			else if(cx instanceof ClusterDouble)
			{
				cx.set(din.readDouble());
			}

			else if(cx instanceof ClusterStringList)
			{
				List<String> s = new GList<String>();
				int si = din.readInt();

				for(int ix = 0; ix < si; ix++)
				{
					s.add(din.readUTF());
				}

				cx.set(s);
			}

			else
			{
				cx.read(din.readUTF());
			}

			c.put(key, cx);
		}

		return c;
	}

	public void tick()
	{
		if(TICK.tick % 200 == 0 && size() > 1000)
		{
			try
			{
				flush();
			}

			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@EventHandler
	public void on(WorldSaveEvent e) throws IOException
	{
		flush();
	}

	public void on(WorldUnloadEvent e) throws IOException
	{
		flush();
	}
}
