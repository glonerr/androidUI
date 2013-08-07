package com.lonerr.bridge.graphics;

import java.util.HashMap;

import com.lonerr.skia.core.SkMaskFilter;

public class MaskFilterBridge {
	private static HashMap<Integer, SkMaskFilter> skMaskFilterPool = new HashMap<Integer, SkMaskFilter>();

	public static SkMaskFilter getMaskFilter(int nativeMaskFilter) {
		return skMaskFilterPool.get(nativeMaskFilter);
	}
}
