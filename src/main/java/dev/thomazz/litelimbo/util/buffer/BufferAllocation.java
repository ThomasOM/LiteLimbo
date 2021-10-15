package dev.thomazz.litelimbo.util.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BufferAllocation {
	// WARNING: Automatically retains reference counter
	public ByteBuf tryTypeConversion(ByteBuf buf, ByteBufAllocator allocator, BufferType type) {
		if (type == BufferAllocation.getBufferType(buf)) {
			return buf.retain();
		}

		int capacity = buf.readableBytes();
		ByteBuf newBuf = BufferAllocation.createFromType(allocator, type, capacity);
		newBuf.writeBytes(buf);
		return newBuf;
	}

	private ByteBuf createFromType(ByteBufAllocator allocator, BufferType type, int capacity) {
		ByteBuf buf;
		switch (type) {
			default:
			case HEAP:
				buf = allocator.heapBuffer(capacity);
				break;
			case DIRECT:
				buf = allocator.directBuffer(capacity);
		}

		return buf;
	}

	public BufferType getBufferType(ByteBuf buf) {
		for (BufferType type : BufferType.values()) {
			if (type.satisfies(buf)) {
				return type;
			}
		}

		throw new RuntimeException("Unknown buffer type..?");
	}
}
