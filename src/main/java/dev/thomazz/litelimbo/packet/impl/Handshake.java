package dev.thomazz.litelimbo.packet.impl;

import dev.thomazz.litelimbo.packet.Packet;
import dev.thomazz.litelimbo.packet.property.Version;
import dev.thomazz.litelimbo.packet.property.Direction;
import dev.thomazz.litelimbo.util.MinecraftBufferReader;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.Setter;

@Data
public class Handshake implements Packet {
	private Version version;
	@Setter
	private String address;
	private int port;
	private int nextStatus;

	@Override
	public void read(ByteBuf buf, Version version, Direction direction) {
		int realProtocolVersion = MinecraftBufferReader.readVarInt(buf);
		this.version = Version.fromId(realProtocolVersion);
		this.address = MinecraftBufferReader.readString(buf);
		this.port = buf.readUnsignedShort();
		this.nextStatus = MinecraftBufferReader.readVarInt(buf);
	}
}
