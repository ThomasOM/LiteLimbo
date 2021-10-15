package dev.thomazz.litelimbo.packet.impl.login;

import dev.thomazz.litelimbo.packet.Packet;
import dev.thomazz.litelimbo.packet.property.Version;
import dev.thomazz.litelimbo.packet.property.Direction;
import dev.thomazz.litelimbo.util.Constants;
import dev.thomazz.litelimbo.util.MinecraftBufferReader;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionResponse implements Packet {
	private byte[] sharedSecret = Constants.EMPTY_BYTE_ARRAY;
	private byte[] verifyToken = Constants.EMPTY_BYTE_ARRAY;

	@Override
	public void read(ByteBuf buf, Version version, Direction direction) {
		if (version.compareTo(Version.MINECRAFT_1_8) >= 0) {
			this.sharedSecret = MinecraftBufferReader.readByteArray(buf, 256);
			this.verifyToken = MinecraftBufferReader.readByteArray(buf, 128);
		} else {
			this.sharedSecret = MinecraftBufferReader.readByteArray17(buf);
			this.verifyToken = MinecraftBufferReader.readByteArray17(buf);
		}
	}
}
