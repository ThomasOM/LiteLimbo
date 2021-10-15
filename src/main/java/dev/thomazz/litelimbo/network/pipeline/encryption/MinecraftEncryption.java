package dev.thomazz.litelimbo.network.pipeline.encryption;

import dev.thomazz.litelimbo.util.buffer.BufferAllocation;
import dev.thomazz.litelimbo.util.buffer.BufferType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MinecraftEncryption extends MessageToMessageEncoder<ByteBuf> {
	private final CipherHasher cipher;

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)  {
		ByteBuf swapped = BufferAllocation.tryTypeConversion(in, ctx.alloc(), BufferType.HEAP).slice();

		try {
			this.cipher.hash(swapped);
			out.add(swapped);
		} catch (Exception e) {
			swapped.release();
			throw e;
		}
	}
}
