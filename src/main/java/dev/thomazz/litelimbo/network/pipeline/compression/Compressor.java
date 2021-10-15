package dev.thomazz.litelimbo.network.pipeline.compression;

import io.netty.buffer.ByteBuf;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Compressor {
	private final Deflater deflater;
	private final Inflater inflater;
	private byte[] zLibBuffer = new byte[8192]; // 8KB is zLib's default
	
	public Compressor(int compressionLevel) {
		this.deflater = new Deflater(compressionLevel);
		this.inflater = new Inflater();
	}

	public void compress(ByteBuf buf, ByteBuf compressed) {
		if (buf.hasArray()) {
			this.deflater.setInput(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
		} else {
			byte[] inData = new byte[buf.readableBytes()];
			buf.readBytes(inData);
			this.deflater.setInput(inData);
		}

		this.deflater.finish();

		while (!this.deflater.finished()) {
			int bytes = this.deflater.deflate(this.zLibBuffer);
			compressed.writeBytes(this.zLibBuffer, 0, bytes);
		}

		this.deflater.reset();
	}

	public void decompress(ByteBuf buf, ByteBuf decompressed, int decompressSize) throws DataFormatException {
		final int readable = buf.readableBytes();

		if (buf.hasArray()) {
			this.inflater.setInput(buf.array(), buf.arrayOffset() + buf.readerIndex(), readable);
		} else {
			byte[] inData = new byte[readable];
			buf.readBytes(inData);
			this.inflater.setInput(inData);
		}

		while (!this.inflater.finished() && this.inflater.getBytesRead() < readable) {
			int written = decompressed.readableBytes();
			if (written > decompressSize) {
				throw new DataFormatException("Data overflow: " + written + " : " + decompressSize);
			}

			int read = this.inflater.inflate(this.zLibBuffer);
			decompressed.writeBytes(this.zLibBuffer, 0, read);
		}

		this.inflater.reset();
	}
}
