package dev.thomazz.litelimbo.packet;

import dev.thomazz.litelimbo.packet.property.Direction;
import dev.thomazz.litelimbo.packet.property.Version;
import io.netty.buffer.ByteBuf;

public interface Packet {
	default void read(ByteBuf buf, Version version, Direction direction) {
	}

	default void write(ByteBuf buf, Version version, Direction direction) {
	}
}
