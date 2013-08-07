package android.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// See "frameworks/base/include/utils/ResourceTypes.h" for the format of
// NinePatch chunk.
public class NinePatchChunk {

	public static final int NO_COLOR = 0x00000001;
	public static final int TRANSPARENT_COLOR = 0x00000000;
	public static final int CHUNK_SIZE = 32;

	public int paddingLeft, paddingTop, paddingRight, paddingBottom;
	public byte numXDivs;
	public byte numYDivs;
	public byte mColorNum;

	public int xDivs[];
	public int yDivs[];
	public int colors[];
	public int size;
	public byte wasDeserialized;

	private static void readIntArray(int[] data, ByteBuffer buffer) {
		for (int i = 0, n = data.length; i < n; ++i) {
			data[i] = buffer.getInt();
		}
	}

	private static void checkDivCount(int length) {
		if (length == 0 || (length & 0x01) != 0) {
			throw new RuntimeException("invalid nine-patch: " + length);
		}
	}

	public static NinePatchChunk deserialize(byte[] data) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(data).order(
				ByteOrder.BIG_ENDIAN);

		NinePatchChunk chunk = new NinePatchChunk();
		chunk.wasDeserialized = byteBuffer.get();
		chunk.xDivs = new int[chunk.numXDivs = byteBuffer.get()];
		chunk.yDivs = new int[chunk.numYDivs = byteBuffer.get()];
		chunk.colors = new int[chunk.mColorNum = byteBuffer.get()];

		checkDivCount(chunk.xDivs.length);
		checkDivCount(chunk.yDivs.length);

		// skip 8 bytes
		byteBuffer.getInt();
		byteBuffer.getInt();

		chunk.paddingLeft = byteBuffer.getInt();
		chunk.paddingRight = byteBuffer.getInt();
		chunk.paddingTop = byteBuffer.getInt();
		chunk.paddingBottom = byteBuffer.getInt();

		// skip 4 bytes
		byteBuffer.getInt();

		readIntArray(chunk.xDivs, byteBuffer);
		readIntArray(chunk.yDivs, byteBuffer);
		readIntArray(chunk.colors, byteBuffer);
		chunk.size = data.length;
		return chunk;
	}

	public static byte[] serialize(NinePatchChunk chunk) {
		byte[] data = new byte[chunk.size];
		ByteBuffer byteBuffer = ByteBuffer.wrap(data).order(
				ByteOrder.BIG_ENDIAN);
		byteBuffer.put(chunk.wasDeserialized);
		
		byteBuffer.put(chunk.numXDivs);
		byteBuffer.put(chunk.numYDivs);
		byteBuffer.put(chunk.mColorNum);

		byteBuffer.putInt(0);
		byteBuffer.putInt(0);

		byteBuffer.putInt(chunk.paddingLeft);
		byteBuffer.putInt(chunk.paddingRight);
		byteBuffer.putInt(chunk.paddingTop);
		byteBuffer.putInt(chunk.paddingBottom);

		byteBuffer.putInt(0);

		for (int i : chunk.xDivs) {
			byteBuffer.putInt(i);
		}
		for (int i : chunk.yDivs) {
			byteBuffer.putInt(i);
		}
		for (int i : chunk.colors) {
			byteBuffer.putInt(i);
		}
		return data;
	}

	public int serializedSize() {
		return size;
	}
}