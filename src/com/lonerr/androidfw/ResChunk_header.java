package com.lonerr.androidfw;

import com.lonerr.utils.ByteArrayUtil;

public class ResChunk_header {
	public static final int RESCHUNK_HEADER_SIZE = 8;
	public ResChunk_header(byte[] data, int off) {
		type = ByteArrayUtil.getShort(data,off,false);
		headerSize = ByteArrayUtil.getShort(data,off+=2,false);
		size = ByteArrayUtil.getInt(data,off+=2,false);
	}

	// Type identifier for this chunk. The meaning of this value depends
	// on the containing chunk.
	short type;

	// Size of the chunk header (in bytes). Adding this value to
	// the address of the chunk allows you to find its associated data
	// (if any).
	short headerSize;

	// Total size of this chunk (in bytes). This is the chunkSize plus
	// the size of any data associated with the chunk. Adding this value
	// to the chunk allows you to completely skip its contents (including
	// any child chunks). If this value is the same as chunkSize, there is
	// no data associated with the chunk.
	int size;
}
