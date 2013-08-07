package com.lonerr.androidfw;

public class ResXMLTree_header {
	public ResXMLTree_header(byte[] data, int off, int size) {
		header = new ResChunk_header(data, off);
	}

	public ResChunk_header header;
}
