package dev.thomazz.litelimbo.handler;

import io.netty.channel.ChannelHandlerContext;

public interface PacketHandler {
	void handlePacket(ChannelHandlerContext ctx, Object packet);

	default String startMessage() {
		return "";
	}

	default String disconnectMessage() {
		return "";
	}
}
