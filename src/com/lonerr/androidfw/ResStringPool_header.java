package com.lonerr.androidfw;

import com.lonerr.utils.ByteArrayUtil;

public class ResStringPool_header {
	public ResStringPool_header(byte[] data, int off) {
		header = new ResChunk_header(data, off);
		stringCount = ByteArrayUtil.getInt(data, off += 8, false);
		styleCount = ByteArrayUtil.getInt(data, off += 4, false);
		flags = ByteArrayUtil.getInt(data, off += 4, false);
		stringsStart = ByteArrayUtil.getInt(data, off += 4, false);
		stylesStart = ByteArrayUtil.getInt(data, off += 4, false);
	}

	ResChunk_header header;

	// Number of strings in this pool (number of uint32_t indices that follow
	// in the data).
	int stringCount;

	// Number of style span arrays in the pool (number of uint32_t indices
	// follow the string indices).
	int styleCount;

	// Flags.
	// If set, the string index is sorted by the string values (based
	// on strcmp16()).
	public static final int SORTED_FLAG = 1 << 0;

	// String pool is encoded in UTF-8
	public static final int UTF8_FLAG = 1 << 8;
	int flags;

	// Index from header of the string data.
	int stringsStart;

	// Index from header of the style data.
	int stylesStart;
}
