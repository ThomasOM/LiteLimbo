package dev.thomazz.litelimbo.network.pipeline.encode;

import dev.thomazz.litelimbo.packet.Packet;
import dev.thomazz.litelimbo.packet.PacketProvider;
import dev.thomazz.litelimbo.packet.PacketRegistry;
import dev.thomazz.litelimbo.packet.property.Version;
import dev.thomazz.litelimbo.packet.property.Direction;
import dev.thomazz.litelimbo.packet.property.Protocol;
import dev.thomazz.litelimbo.util.MinecraftBufferReader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MinecraftEncoder extends MessageToByteEncoder<Packet> {
	private final Direction direction;

	private Protocol protocol = Protocol.HANDSHAKE;
	private Version version;
	private PacketProvider provider;

	public MinecraftEncoder(Direction direction) {
		this.direction = direction;
		this.setVersion(Version.earliest());
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf byteBuf) {
		int packetId = this.provider.getId(packet);
		MinecraftBufferReader.writeVarInt(byteBuf, packetId);
		packet.write(byteBuf, this.version, this.direction);
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
