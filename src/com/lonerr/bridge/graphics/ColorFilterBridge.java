package com.lonerr.bridge.graphics;

import java.util.HashMap;

import com.lonerr.skia.effects.SkColorFilter;

public class ColorFilterBridge {
	private static HashMap<Integer, SkColorFilter> skColorFilterPool = new HashMap<Integer, SkColorFilter>();
	public static SkColorFilter getColorFilter(int nativeFilter) {
		return skColorFilterPool.get(nativeFilter);
	}
}
