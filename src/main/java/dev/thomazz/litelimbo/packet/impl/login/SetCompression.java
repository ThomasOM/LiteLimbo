package dev.thomazz.litelimbo.packet.impl.login;

import dev.thomazz.litelimbo.packet.Packet;
import dev.thomazz.litelimbo.packet.property.Version;
import dev.thomazz.litelimbo.packet.property.Direction;
import dev.thomazz.litelimbo.util.MinecraftBufferReader;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SetCompression implements Packet {
	private int threshold;

	@Override
	public void write(ByteBuf buf, Version version, Direction direction) {
		MinecraftBufferReader.writeVarInt(buf, this.threshold);
	}
}
