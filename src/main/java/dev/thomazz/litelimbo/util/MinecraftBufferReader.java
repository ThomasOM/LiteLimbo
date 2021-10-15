package dev.thomazz.litelimbo.util;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.EncoderException;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;

@UtilityClass
public class MinecraftBufferReader {
	private static final int FORGE_MAX_ARRAY_LENGTH = Integer.MAX_VALUE & 0x1FFF9A; // For 1.7 support

	public int readVarInt(ByteBuf buf) {
		int i = 0;
		int j = 0;
		while (true) {
			int k = buf.readByte();
			i |= (k & 0x7F) << j++ * 7;
			if (j > 5) {
				throw new RuntimeException("VarInt too big");
			}
			if ((k & 0x80) != 128) {
				break;
			}
		}
		return i;
	}

	public void writeVarInt(ByteBuf buf, int value) {
		while (true) {
			if ((value & 0xFFFFFF80) == 0) {
				buf.writeByte(value);
				return;
			}
			buf.writeByte(value & 0x7F | 0x80);
			value >>>= 7;
		}
	}

	public String readString(ByteBuf buf) {
		return readString(buf, 65536); // 64KB max string size
	}

	public String readString(ByteBuf buf, int cap) {
		int length = readVarInt(buf);
		Preconditions.checkArgument(length >= 0, "Got a negative-length string (%s)", length);
		Preconditions.checkArgument(length <= cap * 4, "Bad string size (got %s, maximum is %s)", length, cap);
		Preconditions.checkState(buf.isReadable(length), "Trying to read a string that is too long (wanted %s, only have %s)", length, buf.readableBytes());
		String str = buf.toString(buf.readerIndex(), length, StandardCharsets.UTF_8);
		buf.skipBytes(length);
		Preconditions.checkState(str.length() <= cap, "Got a too-long string (got %s, max %s)", str.length(), cap);
		return str;
	}

	public void writeString(ByteBuf buf, CharSequence str) {
		int size = ByteBufUtil.utf8Bytes(str);
		MinecraftBufferReader.writeVarInt(buf, size);
		ByteBufUtil.writeUtf8(buf, str);
	}

	public byte[] readByteArray(ByteBuf buf, int cap) {
		int length = readVarInt(buf);
		Preconditions.checkArgument(length >= 0, "Got a negative-length array (%s)", length);
		Preconditions.checkArgument(length <= cap, "Bad array size (got %s, maximum is %s)", length, cap);
		Preconditions.checkState(buf.isReadable(length), "Trying to read an array that is too long (wanted %s, only have %s)", length, buf.readableBytes());
		byte[] array = new byte[length];
		buf.readBytes(array);
		return array;
	}

	public void writeByteArray(ByteBuf buf, byte[] array) {
		MinecraftBufferReader.writeVarInt(buf, array.length);
		buf.writeBytes(array);
	}

	public byte[] readByteArray17(ByteBuf buf) {
		int len = readExtendedForgeShort(buf);
		Preconditions.checkArgument(len <= (FORGE_MAX_ARRAY_LENGTH), "Cannot receive array longer than %s (got %s bytes)", MinecraftBufferReader.FORGE_MAX_ARRAY_LENGTH, len);
		byte[] ret = new byte[len];
		buf.readBytes(ret);
		return ret;
	}

	public void writeByteArray17(byte[] b, ByteBuf buf, boolean allowExtended) {
		if (allowExtended) {
			Preconditions.checkArgument(b.length <= (FORGE_MAX_ARRAY_LENGTH), "Cannot send array longer than %s (got %s bytes)", MinecraftBufferReader.FORGE_MAX_ARRAY_LENGTH, b.length);
		} else {
			Preconditions.checkArgument(b.length <= Short.MAX_VALUE, "Cannot send array longer than Short.MAX_VALUE (got %s bytes)", b.length);
		}

		MinecraftBufferReader.writeExtendedForgeShort(buf, b.length);
		buf.writeBytes(b);
	}

	public int readExtendedForgeShort(ByteBuf buf) {
		int low = buf.readUnsignedShort();
		int high = 0;
		if ((low & 0x8000) != 0) {
			low = low & 0x7FFF;
			high = buf.readUnsignedByte();
		}
		return ((high & 0xFF) << 15) | low;
	}

	public void writeExtendedForgeShort(ByteBuf buf, int toWrite) {
		int low = toWrite & 0x7FFF;
		int high = (toWrite & 0x7F8000) >> 15;
		if (high != 0) {
			low = low | 0x8000;
		}
		buf.writeShort(low);
		if (high != 0) {
			buf.writeByte(high);
		}
	}

	public void writeCompoundTag(ByteBuf buf, CompoundBinaryTag compoundTag) {
		try {
			BinaryTagIO.writer().write(compoundTag, (DataOutput) new ByteBufOutputStream(buf));
		} catch (IOException e) {
			throw new EncoderException("Unable to encode NBT CompoundTag");
		}
	}

	public void writeStringArray(ByteBuf buf, String[] stringArray) {
		MinecraftBufferReader.writeVarInt(buf, stringArray.length);
		for (String s : stringArray) {
			MinecraftBufferReader.writeString(buf, s);
		}
	}

	public void writeUuidIntArray(ByteBuf buf, UUID uuid) {
		buf.writeInt((int) (uuid.getMostSignificantBits() >> 32));
		buf.writeInt((int) uuid.getMostSignificantBits());
		buf.writeInt((int) (uuid.getLeastSignificantBits() >> 32));
		buf.writeInt((int) uuid.getLeastSignificantBits());
	}

	public UUID readUuid(ByteBuf buf) {
		long msb = buf.readLong();
		long lsb = buf.readLong();
		return new UUID(msb, lsb);
	}

	public void writeUuid(ByteBuf buf, UUID uuid) {
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
	}
}
