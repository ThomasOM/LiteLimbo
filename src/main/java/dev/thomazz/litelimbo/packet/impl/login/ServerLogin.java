package dev.thomazz.litelimbo.packet.impl.login;

import dev.thomazz.litelimbo.packet.Packet;
import dev.thomazz.litelimbo.packet.property.Version;
import dev.thomazz.litelimbo.packet.property.Direction;
import dev.thomazz.litelimbo.util.MinecraftBufferReader;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ServerLogin implements Packet {
	@Getter
	private String username;

	@Override
	public void read(ByteBuf buf, Version version, Direction direction) {
		this.username = MinecraftBufferReader.readString(buf, 16);
	}
}
