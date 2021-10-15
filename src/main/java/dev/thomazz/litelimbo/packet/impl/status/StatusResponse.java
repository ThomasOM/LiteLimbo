package dev.thomazz.litelimbo.packet.impl.status;

import dev.thomazz.litelimbo.packet.Packet;
import dev.thomazz.litelimbo.packet.property.Version;
import dev.thomazz.litelimbo.packet.property.Direction;
import dev.thomazz.litelimbo.util.MinecraftBufferReader;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class StatusResponse implements Packet {

	private String response;

	@Override
	public void write(ByteBuf buf, Version version, Direction direction) {
		MinecraftBufferReader.writeString(buf, this.response);
	}
}
