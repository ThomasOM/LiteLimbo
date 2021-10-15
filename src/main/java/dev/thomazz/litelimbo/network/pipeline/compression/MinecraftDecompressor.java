package dev.thomazz.litelimbo.network.pipeline.compression;

import dev.thomazz.litelimbo.util.MinecraftBufferReader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MinecraftDecompressor extends MessageToMessageDecoder<ByteBuf> {
	private static final int UNCOMPRESSED_LIMIT = 8 * 1024 * 1024; // 8MB
	private final int threshold;
	private final Compressor compressor;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int decompressedSize = MinecraftBufferReader.readVarInt(in);

		// No decompression needed for these
		if (decompressedSize == 0) {
			out.add(in.retain());
			return;
		}

		if (decompressedSize >= MinecraftDecompressor.UNCOMPRESSED_LIMIT) {
			throw new CorruptedFrameException("Packet exceeded uncompressed limit: " +
					decompressedSize + " : " + MinecraftDecompressor.UNCOMPRESSED_LIMIT);
		}

		if (decompressedSize <= this.threshold) {
			throw new CorruptedFrameException("Encountered invalid decompressed packet size: " +
					decompressedSize + " : " + this.threshold);
		}

		ByteBuf decompressed = ctx.alloc().heapBuffer(decompressedSize);

		// Decompression
		try {
			this.compressor.decompress(in, decompressed, decompressedSize);
			out.add(decompressed);
		} catch (Exception e) {
			decompressed.release();
			throw e;
		} finally {
			in.release();
		}
	}
}
