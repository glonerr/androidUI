package com.lonerr.androidfw;

public class Res_value {
	// Contains no data.
	public static final int TYPE_NULL = 0x00;
	// The 'data' holds a ResTable_ref; a reference to another resource
	// table entry.
	public static final int TYPE_REFERENCE = 0x01;
	// The 'data' holds an attribute resource identifier.
	public static final int TYPE_ATTRIBUTE = 0x02;
	// The 'data' holds an index into the containing resource table's
	// global value string pool.
	public static final int TYPE_STRING = 0x03;
	// The 'data' holds a single-precision floating point number.
	public static final int TYPE_FLOAT = 0x04;
	// The 'data' holds a complex number encoding a dimension value;
	// such as "100in".
	public static final int TYPE_DIMENSION = 0x05;
	// The 'data' holds a complex number encoding a fraction of a
	// container.
	public static final int TYPE_FRACTION = 0x06;

	// Beginning of integer flavors...
	public static final int TYPE_FIRST_INT = 0x10;

	// The 'data' is a raw integer value of the form n..n.
	public static final int TYPE_INT_DEC = 0x10;
	// The 'data' is a raw integer value of the form 0xn..n.
	public static final int TYPE_INT_HEX = 0x11;
	// The 'data' is either 0 or 1; for input "false" or "true" respectively.
	public static final int TYPE_INT_BOOLEAN = 0x12;

	// Beginning of color integer flavors...
	public static final int TYPE_FIRST_COLOR_INT = 0x1c;

	// The 'data' is a raw integer value of the form #aarrggbb.
	public static final int TYPE_INT_COLOR_ARGB8 = 0x1c;
	// The 'data' is a raw integer value of the form #rrggbb.
	public static final int TYPE_INT_COLOR_RGB8 = 0x1d;
	// The 'data' is a raw integer value of the form #argb.
	public static final int TYPE_INT_COLOR_ARGB4 = 0x1e;
	// The 'data' is a raw integer value of the form #rgb.
	public static final int TYPE_INT_COLOR_RGB4 = 0x1f;

	// ...end of integer flavors.
	public static final int TYPE_LAST_COLOR_INT = 0x1f;

	// ...end of integer flavors.
	public static final int TYPE_LAST_INT = 0x1f;

	public int dataType;
	public int data;

}
