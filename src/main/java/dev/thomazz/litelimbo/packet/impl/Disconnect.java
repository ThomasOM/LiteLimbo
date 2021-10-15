package dev.thomazz.litelimbo.packet.impl;

import dev.thomazz.litelimbo.packet.Packet;
import dev.thomazz.litelimbo.packet.property.Version;
import dev.thomazz.litelimbo.packet.property.Direction;
import dev.thomazz.litelimbo.util.MinecraftBufferReader;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Disconnect implements Packet {
	private String reason;

	public Disconnect(String reason) {
		this.reason = "{\"text\": \"" + reason + "\"}";
	}

	@Override
	public void write(ByteBuf buf, Version version, Direction direction) {
		MinecraftBufferReader.writeString(buf, this.reason);
	}
}
