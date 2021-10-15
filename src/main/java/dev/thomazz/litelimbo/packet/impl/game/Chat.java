package dev.thomazz.litelimbo.packet.impl.game;

import dev.thomazz.litelimbo.packet.Packet;
import dev.thomazz.litelimbo.packet.property.Version;
import dev.thomazz.litelimbo.packet.property.Direction;
import dev.thomazz.litelimbo.util.MinecraftBufferReader;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Chat implements Packet {
	private String message;
	private byte type;
	private UUID sender;

	public Chat(String message) {
		this.message = "{\"text\": \"" + message + "\"}";
		this.type = (byte) 0;
	}

	@Override
	public void read(ByteBuf buf, Version version, Direction direction) {
		this.message = MinecraftBufferReader.readString(buf);

		if (direction == Direction.CLIENTBOUND && version.compareTo(Version.MINECRAFT_1_8) >= 0) {
			this.type = buf.readByte();
		}

		if (version.compareTo(Version.MINECRAFT_1_16) >= 0) {
			this.sender = MinecraftBufferReader.readUuid(buf);
		}
	}

	@Override
	public void write(ByteBuf buf, Version version, Direction direction) {
		MinecraftBufferReader.writeString(buf, this.message);

		if (direction == Direction.CLIENTBOUND && version.compareTo(Version.MINECRAFT_1_8) >= 0) {
			buf.writeByte(this.type);
		}

		if (version.compareTo(Version.MINECRAFT_1_16) >= 0) {
			 MinecraftBufferReader.writeUuid(buf, this.sender);
		}
	}
}
