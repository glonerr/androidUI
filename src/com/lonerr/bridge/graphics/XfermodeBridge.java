package com.lonerr.bridge.graphics;

import java.util.HashMap;

import com.lonerr.skia.core.SkXfermode;

public class XfermodeBridge {
	private static HashMap<Integer, SkXfermode> skXfermodePool = new HashMap<Integer, SkXfermode>();
	public static SkXfermode getXfermode(int nativeFilter) {
		return skXfermodePool.get(nativeFilter);
	}
}
