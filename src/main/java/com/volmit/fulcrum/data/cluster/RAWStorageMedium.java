package com.volmit.fulcrum.data.cluster;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.volmit.dumpster.GList;

public class RAWStorageMedium implements IStorageMethod<ByteBuffer>
{
	@Override
	public ByteBuffer save(DataCluster c)
	{
		try
		{
			ByteArrayOutputStream boas = new ByteArrayOutputStream();
			GZIPOutputStream gzo = new GZIPOutputStream(boas);
			DataOutputStream dos = new DataOutputStream(gzo);
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
			return ByteBuffer.wrap(boas.toByteArray());
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public DataCluster load(ByteBuffer t)
	{
		try
		{
			ByteArrayInputStream bin = new ByteArrayInputStream(t.array());
			GZIPInputStream gzi = new GZIPInputStream(bin);
			DataInputStream din = new DataInputStream(gzi);
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

		catch(IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
