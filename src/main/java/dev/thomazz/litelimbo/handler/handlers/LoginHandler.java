package dev.thomazz.litelimbo.handler.handlers;

import dev.thomazz.litelimbo.LiteLimbo;
import dev.thomazz.litelimbo.LiteNetManager;
import dev.thomazz.litelimbo.network.pipeline.compression.Compressor;
import dev.thomazz.litelimbo.network.pipeline.encryption.CipherHasher;
import dev.thomazz.litelimbo.handler.PacketHandler;
import dev.thomazz.litelimbo.network.pipeline.compression.MinecraftDecompressor;
import dev.thomazz.litelimbo.network.pipeline.compression.MinecraftCompressor;
import dev.thomazz.litelimbo.network.pipeline.encryption.MinecraftDecryption;
import dev.thomazz.litelimbo.network.pipeline.encryption.MinecraftEncryption;
import dev.thomazz.litelimbo.packet.impl.Disconnect;
import dev.thomazz.litelimbo.packet.impl.game.Chat;
import dev.thomazz.litelimbo.packet.impl.game.JoinGame;
import dev.thomazz.litelimbo.packet.impl.game.Position;
import dev.thomazz.litelimbo.packet.impl.login.EncryptionRequest;
import dev.thomazz.litelimbo.packet.impl.login.EncryptionResponse;
import dev.thomazz.litelimbo.packet.impl.login.ServerLogin;
import dev.thomazz.litelimbo.packet.impl.login.ServerLoginSuccess;
import dev.thomazz.litelimbo.packet.impl.login.SetCompression;
import dev.thomazz.litelimbo.packet.property.Protocol;
import dev.thomazz.litelimbo.packet.property.Version;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import java.security.Key;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginHandler implements PacketHandler {
	private final LiteNetManager netManager;
	private String username = "";

	@Override
	public void handlePacket(ChannelHandlerContext ctx, Object packet) {
		Channel channel = this.netManager.getChannel();
		LiteLimbo server = this.netManager.getServer();

		if (packet instanceof ServerLogin) {
			ServerLogin serverLogin = (ServerLogin) packet;
			this.username = serverLogin.getUsername();

			EncryptionRequest request = new EncryptionRequest(
					"",
					server.getKeyPair().getPublic().getEncoded(),
					server.getVerify()
			);

			channel.writeAndFlush(request);
		}

		if (packet instanceof EncryptionResponse) {
			EncryptionResponse response = (EncryptionResponse) packet;
			SecretKey loginKey = this.generateSecret(server.getKeyPair().getPrivate(), response.getSharedSecret());
			this.setupEncryption(channel, loginKey);

			// Only use compression in 1.8+
			if (this.netManager.getVersion().compareTo(Version.MINECRAFT_1_8) >= 0) {
				SetCompression compression = new SetCompression(256);
				channel.writeAndFlush(compression).addListener(compress -> {
					this.setCompression(channel, compression.getThreshold());
					this.loginSequence(server, channel);
				});
			} else {
				this.loginSequence(server, channel);
			}
		}

		if (packet instanceof Disconnect) {
			server.unregisterKeepAlive(this.netManager);
			if (channel.isActive()) {
				channel.close();
			}
		}
	}

	@Override
	public String disconnectMessage() {
		return !this.username.isEmpty() ? "Player '" + this.username + "' disconnected" : "";
	}

	private void loginSequence(LiteLimbo server, Channel channel) {
		ServerLoginSuccess loginSuccess = new ServerLoginSuccess(UUID.randomUUID(), this.username);
		channel.writeAndFlush(loginSuccess).addListener(login -> {
			this.netManager.setProtocol(Protocol.PLAY);
			this.netManager.setPacketHandler(new PlayHandler(this.netManager, this.username));

			JoinGame joinGame = new JoinGame(
					0,
					(short) 2,
					1,
					0L,
					(short) 0,
					(short) 1,
					"DEFAULT",
					1,
					true,
					false);

			channel.writeAndFlush(joinGame);

			Position position = new Position(0, 0, 0, 0, 0, false, Collections.emptySet(), 0);

			// Welcome message after teleport
			channel.writeAndFlush(position).addListener(pos -> {
				Chat chat = new Chat(server.getWelcomeMessage());
				channel.writeAndFlush(chat);
			});

			server.registerKeepAlive(this.netManager);
		});
	}

	private SecretKey generateSecret(PrivateKey privateKey, byte[] bytes) {
		return new SecretKeySpec(this.decrypt(privateKey, bytes), "AES");
	}

	private byte[] decrypt(Key key, byte[] bytes) {
		try {
			Cipher cipher = Cipher.getInstance(key.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(bytes);
		} catch (Exception ignored) {
			throw new RuntimeException("Cipher error");
		}
	}

	private void setupEncryption(Channel channel, SecretKey secretKey) {
		try {
			CipherHasher decryptionCipher = new CipherHasher(Cipher.DECRYPT_MODE, secretKey);
			CipherHasher encryptionCipher = new CipherHasher(Cipher.ENCRYPT_MODE, secretKey);

			ChannelPipeline pipeline = channel.pipeline();
			pipeline.addBefore("frame-decoder", "encryption-decoder", new MinecraftDecryption(decryptionCipher));
			pipeline.addBefore("length-encoder", "encryption-encoder", new MinecraftEncryption(encryptionCipher));
		} catch (Exception e) {
			LiteLimbo.LOGGER.error("Could not set up encryption!");
			e.printStackTrace();
		}
	}

	private void setCompression(Channel channel, int threshold) {
		if (threshold == -1) {
			channel.pipeline().remove("mc-decompressor");
			channel.pipeline().remove("mc-compressor");
			return;
		}

		Compressor compressor = new Compressor(4);
		MinecraftDecompressor decoder = new MinecraftDecompressor(threshold, compressor);
		MinecraftCompressor encoder = new MinecraftCompressor(threshold, compressor);

		channel.pipeline().addBefore("mc-decoder", "mc-decompressor", decoder);
		channel.pipeline().addBefore("mc-encoder", "mc-compressor", encoder);
	}
}
