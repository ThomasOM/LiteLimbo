package dev.thomazz.litelimbo.packet.impl.login;

import dev.thomazz.litelimbo.packet.Packet;
import dev.thomazz.litelimbo.packet.property.Version;
import dev.thomazz.litelimbo.packet.property.Direction;
import dev.thomazz.litelimbo.util.MinecraftBufferReader;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerLoginSuccess implements Packet {
	private UUID uuid;
	private String username;

	@Override
	public void write(ByteBuf buf, Version version, Direction direction) {
		if (version.compareTo(Version.MINECRAFT_1_16) >= 0) {
			MinecraftBufferReader.writeUuidIntArray(buf, uuid);
		} else if (version.compareTo(Version.MINECRAFT_1_7_6) >= 0) {
			MinecraftBufferReader.writeString(buf, this.uuid.toString());
		} else {
			MinecraftBufferReader.writeString(buf, this.uuid.toString().replace("-", ""));
		}

		MinecraftBufferReader.writeString(buf, this.username);
	}
}
