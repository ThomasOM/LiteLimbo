package dev.thomazz.litelimbo.network.pipeline.encode;

import dev.thomazz.litelimbo.LiteLimbo;
import dev.thomazz.litelimbo.packet.Packet;
import dev.thomazz.litelimbo.packet.PacketProvider;
import dev.thomazz.litelimbo.packet.PacketRegistry;
import dev.thomazz.litelimbo.packet.property.Version;
import dev.thomazz.litelimbo.packet.property.Direction;
import dev.thomazz.litelimbo.packet.property.Protocol;
import dev.thomazz.litelimbo.util.MinecraftBufferReader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

public class MinecraftDecoder extends MessageToMessageDecoder<ByteBuf> {
	private final Direction direction;

	private Protocol protocol = Protocol.HANDSHAKE;
	private Version version;
	private PacketProvider provider;

	public MinecraftDecoder(Direction direction) {
		this.direction = direction;
		this.setVersion(Version.earliest());
	}

	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
		if (!msg.isReadable()) {
			return;
		}

		ByteBuf slice = msg.slice();

		int packetId = MinecraftBufferReader.readVarInt(msg);
		Packet packet = this.provider.fromId(packetId);
		if (packet == null) {
			msg.skipBytes(msg.readableBytes());
			out.add(slice.retain());
		} else {
			try {
				packet.read(msg, this.version, this.direction);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (msg.isReadable()) {
				LiteLimbo.LOGGER.error("didn't read enough bytes in packet (id " + packet + ")");
				return;
			}

			out.add(packet);
		}
	}

	public void setVersion(Version version) {
		this.version = version;
		this.provider = PacketRegistry.getProvider(this.direction, this.version, this.protocol);
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
		this.setVersion(this.version);
	}
}
