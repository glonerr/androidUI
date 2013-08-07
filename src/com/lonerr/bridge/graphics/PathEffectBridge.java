package com.lonerr.bridge.graphics;

import java.util.HashMap;

import com.lonerr.skia.core.SkPathEffect;

public class PathEffectBridge {
	private static HashMap<Integer, SkPathEffect> skPathEffectPool = new HashMap<Integer, SkPathEffect>();
	public static SkPathEffect getPathEffect(int pathEffect) {
		return skPathEffectPool.get(pathEffect);
	}
}
