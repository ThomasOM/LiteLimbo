package dev.thomazz.litelimbo.handler.handlers;

import dev.thomazz.litelimbo.LiteNetManager;
import dev.thomazz.litelimbo.handler.PacketHandler;
import dev.thomazz.litelimbo.packet.impl.game.Chat;
import dev.thomazz.litelimbo.util.ColorConverter;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayHandler implements PacketHandler {
	private final LiteNetManager netManager;
	private final String username;

	@Override
	public void handlePacket(ChannelHandlerContext ctx, Object packet) {
		if (packet instanceof Chat) {
			Chat chat = new Chat(ColorConverter.convert("&7You only hear a quiet echo..."));
			this.netManager.getChannel().writeAndFlush(chat);
		}
	}

	@Override
	public String startMessage() {
		return "Player '" + this.username + "' successfully logged in";
	}

	@Override
	public String disconnectMessage() {
		return "Player '" + this.username + "' disconnected";
	}
}
