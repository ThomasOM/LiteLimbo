package dev.thomazz.litelimbo.network.pipeline.compression;

import dev.thomazz.litelimbo.util.MinecraftBufferReader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MinecraftCompressor extends MessageToByteEncoder<ByteBuf> {
	private final int threshold;
	private final Compressor compressor;

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) {
		int uncompressed = in.readableBytes();

		if (uncompressed <= this.threshold) {
			// No compression
			MinecraftBufferReader.writeVarInt(out, 0);
			out.writeBytes(in);
		} else {
			// Compression
			try {
				MinecraftBufferReader.writeVarInt(out, uncompressed);
				this.compressor.compress(in, out);
			} catch (Exception e) {
				out.release();
				throw e;
			} finally {
				in.release();
			}
		}
	}

	@Override
	protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) {
		int initialBufferSize = msg.readableBytes() <= this.threshold ? msg.readableBytes() + 1 : msg.readableBytes() / 3;
		return ctx.alloc().heapBuffer(initialBufferSize);
	}
}
