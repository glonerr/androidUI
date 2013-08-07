package com.lonerr.androidfw;

public class ResStringPool_span {

	public static final int END = 0xFFFFFFFF;

	// This is the name of the span -- that is, the name of the XML
	// tag that defined it. The special value END (0xFFFFFFFF) indicates
	// the end of an array of spans.
	public int name;

	// The range of characters in the string that this span applies to.
	int firstChar, lastChar;

}
