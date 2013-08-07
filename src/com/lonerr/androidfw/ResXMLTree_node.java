package com.lonerr.androidfw;

import com.lonerr.utils.ByteArrayUtil;

public class ResXMLTree_node {

	public ResXMLTree_node(byte[] data, int dataStart) {
		header = new ResChunk_header(data, dataStart);
		lineNumber = ByteArrayUtil.getInt(data, dataStart += header.headerSize, false);
		comment = ByteArrayUtil.getInt(data, dataStart += 4, false);
	}

	ResChunk_header header;

	// Line number in original source file at which this element appeared.
	int lineNumber;

	// Optional XML comment that was associated with this element; -1 if none.
	int comment;
}
