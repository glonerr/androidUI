package com.lonerr.skia.core;


public class SkShader {
	public enum TileMode {
		kClamp_TileMode, // !< replicate the edge color if the shader draws
							// outside of its original bounds
		kRepeat_TileMode, // !< repeat the shader's image horizontally and
							// vertically
		kMirror_TileMode, // !< repeat the shader's image horizontally and
							// vertically, alternating mirror images so that
							// adjacent images always seam

		kTileModeCount
	};

	// override these in your subclass

	// !< set if all of the colors will be opaque
	public static final int kOpaqueAlpha_Flag = 0x01,

	// ! set if this shader's shadeSpan16() method can be called
			kHasSpan16_Flag = 0x02,

			/**
			 * Set this bit if the shader's native data type is instrinsically
			 * 16 bit, meaning that calling the 32bit shadeSpan() entry point
			 * will mean the the impl has to up-sample 16bit data into 32bit.
			 * Used as a a means of clearing a dither request if the it will
			 * have no effect
			 */
			kIntrinsicly16_Flag = 0x04,

			/**
			 * set (after setContext) if the spans only vary in X (const in Y).
			 * e.g. an Nx1 bitmap that is being tiled in Y, or a linear-gradient
			 * that varies from left-to-right. This flag specifies this for
			 * shadeSpan().
			 */
			kConstInY32_Flag = 0x08,

			/**
			 * same as kConstInY32_Flag, but is set if this is true for
			 * shadeSpan16 which may not always be the case, since shadeSpan16
			 * may be predithered, which would mean it was not const in Y, even
			 * though the 32bit shadeSpan() would be const.
			 */
			kConstInY16_Flag = 0x10;

	public static SkShader CreateBitmapShader(SkBitmap bitmap,
			TileMode kclampTilemode, TileMode kclampTilemode2) {
		// TODO Auto-generated method stub
		return null;
	}
}
