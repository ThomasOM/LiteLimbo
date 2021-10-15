package dev.thomazz.litelimbo.packet.impl.game;

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
public class KeepAlive implements Packet {
	private long id;

	@Override
	public void write(ByteBuf buf, Version version, Direction direction) {
		if (version.compareTo(Version.MINECRAFT_1_12_2) >= 0) {
			buf.writeLong(this.id);
		} else if (version.compareTo(Version.MINECRAFT_1_8) >= 0) {
			MinecraftBufferReader.writeVarInt(buf, (int) this.id);
		} else {
			buf.writeInt((int) this.id);
		}
	}
}
