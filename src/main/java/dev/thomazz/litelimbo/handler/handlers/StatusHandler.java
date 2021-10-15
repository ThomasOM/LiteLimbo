package dev.thomazz.litelimbo.handler.handlers;

import dev.thomazz.litelimbo.LiteNetManager;
import dev.thomazz.litelimbo.handler.PacketHandler;
import dev.thomazz.litelimbo.packet.impl.status.StatusPing;
import dev.thomazz.litelimbo.packet.impl.status.StatusResponse;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StatusHandler implements PacketHandler {
	private final LiteNetManager netManager;

	@Override
	public void handlePacket(ChannelHandlerContext ctx, Object packet) {
		if (packet instanceof StatusPing) {
			this.netManager.getChannel().writeAndFlush(packet);
		} else {
			String motd = this.netManager.getServer().getMotdMessage();
			this.netManager.getChannel().writeAndFlush(
					new StatusResponse("{\"version\":{\"name\":\"LiteLimbo\"," + "\"protocol\":" +
							this.netManager.getVersion().getProtocolId() + "}," +
							"\"players\":{\"max\":0,\"online\":0,\"sample\":[]}," +
							"\"description\":{\"text\":\"" + motd + "\"}}")
			);
		}
	}
}
