package dev.thomazz.litelimbo.network.pipeline.encryption;

import dev.thomazz.litelimbo.util.buffer.BufferAllocation;
import dev.thomazz.litelimbo.util.buffer.BufferType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MinecraftDecryption extends MessageToMessageDecoder<ByteBuf> {
	private final CipherHasher cipher;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
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
