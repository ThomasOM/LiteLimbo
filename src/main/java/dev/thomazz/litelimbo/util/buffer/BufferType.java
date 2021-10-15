package dev.thomazz.litelimbo.util.buffer;

import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BufferType {
	HEAP(ByteBuf::hasArray),
	DIRECT(ByteBuf::hasMemoryAddress);

	private final BufferCondition condition;

	public boolean satisfies(ByteBuf buf) {
		return this.condition.satisfies(buf);
	}

	private interface BufferCondition {
		boolean satisfies(ByteBuf buf);
	}
}
