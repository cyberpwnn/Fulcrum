package com.volmit.fulcrum.net;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.bukkit.S;
import com.volmit.fulcrum.custom.TinyProtocol;
import com.volmit.fulcrum.event.PlayerBlockEvent;
import com.volmit.fulcrum.event.PlayerCancelledDiggingEvent;
import com.volmit.fulcrum.event.PlayerFinishedDiggingEvent;
import com.volmit.fulcrum.event.PlayerStartDiggingEvent;
import com.volmit.volume.lang.collections.GMap;

import net.minecraft.server.v1_12_R1.PacketPlayInBlockDig;

public class NetworkManager
{
	private TinyProtocol proto;
	public static GMap<Block, Integer> forceProgress = new GMap<Block, Integer>();

	public NetworkManager()
	{
		proto = new TinyProtocol(Fulcrum.instance)
		{
			@Override
			public Object onPacketOutAsync(Player reciever, Object packet)
			{
				return super.onPacketOutAsync(reciever, packet);
			}

			@Override
			public Object onPacketInAsync(Player sender, Object packet)
			{
				if(packet instanceof PacketPlayInBlockDig)
				{
					PacketPlayInBlockDig dig = ((PacketPlayInBlockDig) packet);
					Location l = new Location(sender.getWorld(), dig.a().getX(), dig.a().getY(), dig.a().getZ());
					Block b = l.getBlock();
					BlockFace f = null;

					switch(dig.b())
					{
						case DOWN:
							f = BlockFace.DOWN;
							break;
						case EAST:
							f = BlockFace.EAST;
							break;
						case NORTH:
							f = BlockFace.NORTH;
							break;
						case SOUTH:
							f = BlockFace.SOUTH;
							break;
						case UP:
							f = BlockFace.UP;
							break;
						case WEST:
							f = BlockFace.WEST;
							break;
						default:
							break;
					}

					PlayerBlockEvent ex = null;

					switch(dig.c())
					{
						case ABORT_DESTROY_BLOCK:
							ex = new PlayerCancelledDiggingEvent(sender, b, f);
							break;
						case START_DESTROY_BLOCK:
							ex = new PlayerStartDiggingEvent(sender, b, f);
							break;
						case STOP_DESTROY_BLOCK:
							ex = new PlayerFinishedDiggingEvent(sender, b, f);
							break;
						default:
							break;
					}

					if(ex != null)
					{
						Event ev = ex;

						new S()
						{
							@Override
							public void run()
							{
								Fulcrum.callEvent(ev);
							}
						};
					}
				}

				return super.onPacketInAsync(sender, packet);
			}
		};

	}

	public void close()
	{
		proto.close();
	}
}
