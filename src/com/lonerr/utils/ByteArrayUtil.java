package com.lonerr.utils;

public class ByteArrayUtil {

	public static short getShort(byte[] data, int i, boolean bigEndian) {
		if (bigEndian)
			return (short) ((data[i] << 8) + (data[i + 1] & 0xff));
		else
			return (short) ((data[i + 1] << 8) + (data[i] & 0xff));
	}

	public static int getInt(byte[] data, int i, boolean bigEndian) {
		if (bigEndian)
			return ((data[i] << 24) + ((data[i + 1] & 0xff) << 16) + ((data[i + 2] & 0xff) << 8) + (data[i + 3] & 0xff));
		else
			return ((data[i + 3] << 24) + ((data[i + 2] & 0xff) << 16) + ((data[i + 1] & 0xff) << 8) + (data[i] & 0xff));
	}

}
