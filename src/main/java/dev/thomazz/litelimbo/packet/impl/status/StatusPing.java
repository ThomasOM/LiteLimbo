package dev.thomazz.litelimbo.packet.impl.status;

import dev.thomazz.litelimbo.packet.Packet;
import dev.thomazz.litelimbo.packet.property.Version;
import dev.thomazz.litelimbo.packet.property.Direction;
import io.netty.buffer.ByteBuf;

public class StatusPing implements Packet {

	private long id;

	@Override
	public void read(ByteBuf buf, Version version, Direction direction) {
		this.id = buf.readLong();
	}

	@Override
	public void write(ByteBuf buf, Version version, Direction direction) {
		buf.writeLong(this.id);
	}
}
