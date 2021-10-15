package dev.thomazz.litelimbo;

import dev.thomazz.litelimbo.handler.PacketHandler;
import dev.thomazz.litelimbo.handler.handlers.LoginHandler;
import dev.thomazz.litelimbo.handler.handlers.StatusHandler;
import dev.thomazz.litelimbo.network.pipeline.encode.MinecraftDecoder;
import dev.thomazz.litelimbo.network.pipeline.encode.MinecraftEncoder;
import dev.thomazz.litelimbo.packet.property.Version;
import dev.thomazz.litelimbo.packet.impl.Handshake;
import dev.thomazz.litelimbo.packet.property.Protocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LiteNetManager extends SimpleChannelInboundHandler<Object> {
	private final LiteLimbo server;

	private Channel channel;
	private Protocol protocol;
	private Version version;
	private PacketHandler packetHandler;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);

		try {
			this.channel = ctx.channel();
			this.channel.config().setAutoRead(true);
			this.setProtocol(Protocol.HANDSHAKE);
		} catch (Throwable throwable) {
			LiteLimbo.LOGGER.error("Could not activate server channel!");
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, Object packet) {
		if (packet instanceof Handshake) {
			Handshake handshake = (Handshake) packet;
			this.setProtocolVersion(handshake.getVersion());

			switch (handshake.getNextStatus()) {
				case 1:
					this.setProtocol(Protocol.STATUS);
					this.setPacketHandler(new StatusHandler(this));
					return;
				case 2:
					this.setProtocol(Protocol.LOGIN);
					this.setPacketHandler(new LoginHandler(this));
					return;
				default:
					if (this.channel.isActive()) {
						this.channel.close();
					}

					LiteLimbo.LOGGER.warn("Received invalid handshake id!");
					throw new IllegalStateException("Unexpected value: " + handshake.getNextStatus());
			}
		}

		this.packetHandler.handlePacket(context, packet);
	}

	@Override
	public void channelInactive(ChannelHandlerContext context) throws Exception {
		this.server.unregisterKeepAlive(this);

		if (this.packetHandler != null) {
			String message = this.packetHandler.disconnectMessage();
			if (!message.isEmpty()) {
				LiteLimbo.LOGGER.info(message);
			}
		}

		super.channelInactive(context);
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
		this.channel.pipeline().get(MinecraftDecoder.class).setProtocol(protocol);
		this.channel.pipeline().get(MinecraftEncoder.class).setProtocol(protocol);
	}

	public void setPacketHandler(PacketHandler packetHandler) {
		this.packetHandler = packetHandler;

		String message = this.packetHandler.startMessage();
		if (!message.isEmpty()) {
			LiteLimbo.LOGGER.info(message);
		}
	}

	private void setProtocolVersion(Version version) {
		this.version = version;
		this.channel.pipeline().get(MinecraftDecoder.class).setVersion(version);
		this.channel.pipeline().get(MinecraftEncoder.class).setVersion(version);
	}
}
