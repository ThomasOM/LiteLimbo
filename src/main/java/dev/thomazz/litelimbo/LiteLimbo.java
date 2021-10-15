package dev.thomazz.litelimbo;

import dev.thomazz.litelimbo.network.Connection;
import dev.thomazz.litelimbo.network.pipeline.encode.MinecraftDecoder;
import dev.thomazz.litelimbo.network.pipeline.encode.MinecraftEncoder;
import dev.thomazz.litelimbo.network.pipeline.frame.FrameDecoder;
import dev.thomazz.litelimbo.network.pipeline.frame.FrameEncoder;
import dev.thomazz.litelimbo.packet.PacketRegistry;
import dev.thomazz.litelimbo.packet.impl.game.KeepAlive;
import dev.thomazz.litelimbo.packet.property.Direction;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.flow.FlowControlHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class LiteLimbo extends Connection {
	public static final Logger LOGGER = LoggerFactory.getLogger(LiteLimbo.class);
	private static final Random RANDOM = new Random();

	private final Collection<LiteNetManager> aliveManagers = new CopyOnWriteArrayList<>();
	private final KeyPair keyPair;
	private final byte[] verify;
	private boolean alive = true;

	private String welcomeMessage;
	private String motdMessage;

	public LiteLimbo() {
		PacketRegistry.init();

		this.keyPair = this.generateKeypair();
		this.verify = new byte[4];
		LiteLimbo.RANDOM.nextBytes(this.verify);
	}

	private KeyPair generateKeypair() {
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(1024);
			return generator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			LiteLimbo.LOGGER.error("Key pair generation failed!");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void start(InetSocketAddress address) {
		super.start(address);

		Thread keepAliveThread = new Thread("Limbo-KeepAlive-Thread") {
			private long keepAliveCounter = 0;

			@Override
			public void run() {
				while(LiteLimbo.this.alive) {
					LiteLimbo.this.aliveManagers.forEach(manager ->
							LiteLimbo.this.sendKeepAlive(manager.getChannel(), this.keepAliveCounter++)
					);

					try {
						Thread.sleep(3000);
					} catch (InterruptedException ignored) {
					}
				}
			}
		};

		// Prevent from stopping JVM close
		keepAliveThread.setDaemon(true);
		keepAliveThread.start();
	}

	@Override
	public void die() {
		LiteLimbo.LOGGER.info("Stopping LiteLimbo");
		this.alive = false;
		super.die();
	}

	private void sendKeepAlive(Channel channel, long id) {
		if (channel != null && channel.isActive()) {
			KeepAlive keepAlive = new KeepAlive(id);
			channel.writeAndFlush(keepAlive);
		}
	}

	@Override
	protected ChannelInitializer<Channel> channelInitializer() {
		return new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) {
				ChannelPipeline pipeline = channel.pipeline();

				pipeline.addLast("timeout", new ReadTimeoutHandler(30));
				pipeline.addLast("frame-decoder", new FrameDecoder());
				pipeline.addLast("length-encoder", new FrameEncoder());
				pipeline.addLast("flow-handler", new FlowControlHandler()); // Ensures only one message is read at a time
				pipeline.addLast("mc-decoder", new MinecraftDecoder(Direction.SERVERBOUND));
				pipeline.addLast("mc-encoder", new MinecraftEncoder(Direction.CLIENTBOUND));

				LiteNetManager packetHandler = new LiteNetManager(LiteLimbo.this);
				pipeline.addLast("packet-handler", packetHandler);
			}
		};
	}

	public void registerKeepAlive(LiteNetManager manager) {
		this.aliveManagers.add(manager);
	}

	public void unregisterKeepAlive(LiteNetManager manager) {
		this.aliveManagers.remove(manager);
	}

	public LiteLimbo welcomeMessage(String message) {
		this.welcomeMessage = message;
		return this;
	}

	public LiteLimbo motdMessage(String message) {
		this.motdMessage = message;
		return this;
	}
}
