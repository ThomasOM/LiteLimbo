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

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionRequest implements Packet {
	private String serverId = "";
	private byte[] publicKey = Constants.EMPTY_BYTE_ARRAY;
	private byte[] verifyToken = Constants.EMPTY_BYTE_ARRAY;

	@Override
	public void write(ByteBuf buf, Version version, Direction direction) {
		MinecraftBufferReader.writeString(buf, this.serverId);

		if (version.compareTo(Version.MINECRAFT_1_8) >= 0) {
			MinecraftBufferReader.writeByteArray(buf, this.publicKey);
			MinecraftBufferReader.writeByteArray(buf, this.verifyToken);
		} else {
			MinecraftBufferReader.writeByteArray17(this.publicKey, buf, false);
			MinecraftBufferReader.writeByteArray17(this.verifyToken, buf, false);
		}
	}
}
