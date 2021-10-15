package dev.thomazz.litelimbo.network.pipeline.encryption;

import dev.thomazz.litelimbo.LiteLimbo;
import io.netty.buffer.ByteBuf;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;

public class CipherHasher {
	private final Cipher cipher;

	public CipherHasher(int mode, SecretKey key) {
		try {
			this.cipher = Cipher.getInstance("AES/CFB8/NoPadding");
			this.cipher.init(mode, key, new IvParameterSpec(key.getEncoded()));
		} catch (Exception e) {
			LiteLimbo.LOGGER.error("Could not create cipher");
			throw new RuntimeException(e);
		}
	}

	public void hash(ByteBuf buf) {
		if (!buf.hasArray()) {
			throw new IllegalArgumentException("Direct buffer passed to hash function");
		}

		int readable = buf.readableBytes();
		int offset = buf.arrayOffset() + buf.readerIndex();

		try {
			this.cipher.update(buf.array(), offset, readable, buf.array(), offset);
		} catch (ShortBufferException ignored) {
			// Should never happen because we're using AES
		}
	}
}
