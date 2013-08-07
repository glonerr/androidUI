package com.lonerr.bridge.graphics;

import java.util.HashMap;

import com.lonerr.skia.core.SkShader;

public class ShaderBridge {
	private static HashMap<Integer, SkShader> skShaderPool = new HashMap<Integer, SkShader>();

	public static SkShader getShader(int nativePaint) {
		return skShaderPool.get(nativePaint);
	}
}	
