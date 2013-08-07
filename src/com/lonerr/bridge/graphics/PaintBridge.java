package com.lonerr.bridge.graphics;

import java.util.HashMap;

import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import com.lonerr.skia.core.SkPaint;
import com.lonerr.skia.core.SkPaint.Cap;
import com.lonerr.skia.core.SkPaint.Join;
import com.lonerr.skia.core.SkPaint.Style;

public class PaintBridge {
	private static HashMap<Integer, SkPaint> skPaintPool = new HashMap<Integer, SkPaint>();

	public static SkPaint getPaint(int nativePaint) {
		return skPaintPool.get(nativePaint);
	}

	public static int native_init() {
		SkPaint obj = new SkPaint();
		defaultSettingsForAndroid(obj);
		skPaintPool.put(obj.hashCode(), obj);
		return obj.hashCode();
	}

	public static int native_initWithPaint(int paint) {
		SkPaint obj = new SkPaint(paint);
		skPaintPool.put(obj.hashCode(), obj);
		return obj.hashCode();
	}

	public static void native_reset(int object) {
		SkPaint obj = skPaintPool.get(object);
		obj.reset();
		defaultSettingsForAndroid(obj);
	}

	private static void defaultSettingsForAndroid(SkPaint obj) {
		obj.setTextEncoding(SkPaint.TextEncoding.kGlyphID_TextEncoding);
	}

	public static void native_set(int native_dst, int native_src) {
		SkPaint src = skPaintPool.get(native_src);
		SkPaint dst = skPaintPool.get(native_dst);
		src.copyTo(dst);
	}

	public static int native_getStyle(int native_object) {
		return getStyleInt(skPaintPool.get(native_object).getStyle());
	}

	private static int getStyleInt(Style style) {
		switch (style) {
		case kFill_Style:
			return 0;
		case kStroke_Style:
			return 1;
		case kStrokeAndFill_Style:
			return 2;
		default:
			return -1;
		}
	}

	public static void native_setStyle(int native_object, int style) {
		skPaintPool.get(native_object).setStyle(getStyle(style));
	}

	private static SkPaint.Style getStyle(int style) {
		switch (style) {
		case 0:
			return SkPaint.Style.kFill_Style;
		case 1:
			return SkPaint.Style.kStroke_Style;
		case 2:
			return SkPaint.Style.kStrokeAndFill_Style;
		default:
			return null;
		}
	}

	public static int native_getStrokeCap(int native_object) {
		return getCapInt(skPaintPool.get(native_object).getStrokeCap());
	}

	private static int getCapInt(Cap strokeCap) {
		switch (strokeCap) {
		case kButt_Cap:
			return 0;
		case kRound_Cap:
			return 1;
		case kSquare_Cap:
			return 2;
		default:
			return -1;
		}
	}

	public static void native_setStrokeCap(int native_object, int cap) {
		skPaintPool.get(native_object).setStrokeCap(getCap(cap));
	}

	private static SkPaint.Cap getCap(int cap) {
		switch (cap) {
		case 0:
			return SkPaint.Cap.kButt_Cap;
		case 1:
			return SkPaint.Cap.kRound_Cap;
		case 2:
			return SkPaint.Cap.kSquare_Cap;
		default:
			return null;
		}
	}

	public static int native_getStrokeJoin(int native_object) {
		return getJoinInt(skPaintPool.get(native_object).getStrokeJoin());
	}

	private static int getJoinInt(Join strokeJoin) {
		switch (strokeJoin) {
		case kMiter_Join:
			return 0;
		case kRound_Join:
			return 1;
		case kBevel_Join:
			return 2;
		default:
			return -1;
		}
	}

	public static void native_setStrokeJoin(int native_object, int join) {
		skPaintPool.get(native_object).setStrokeJoin(getJoin(join));
	}

	private static SkPaint.Join getJoin(int join) {
		switch (join) {
		case 0:
			return SkPaint.Join.kMiter_Join;
		case 1:
			return SkPaint.Join.kRound_Join;
		case 2:
			return SkPaint.Join.kBevel_Join;
		default:
			return null;
		}
	}

	public static boolean native_getFillPath(int native_object, int src, int dst) {
		return skPaintPool.get(native_object).getFillPath(PathBridge.getPath(src), PathBridge.getPath(dst));
	}

	public static int native_setShader(int native_object, int shader) {
		return skPaintPool.get(native_object).setShader(ShaderBridge.getShader(shader)).hashCode();
	}

	public static int native_setColorFilter(int native_object, int filter) {
		return skPaintPool.get(native_object).setColorFilter(ColorFilterBridge.getColorFilter(filter)).hashCode();
	}

	public static int native_setXfermode(int native_object, int xfermode) {
		return skPaintPool.get(native_object).setXfermode(XfermodeBridge.getXfermode(xfermode)).hashCode();
	}

	public static int native_setPathEffect(int native_object, int effect) {
		return skPaintPool.get(native_object).setPathEffect(PathEffectBridge.getPathEffect(effect)).hashCode();
	}

	public static int native_setMaskFilter(int native_object, int maskfilter) {
		return skPaintPool.get(native_object).setMaskFilter(MaskFilterBridge.getMaskFilter(maskfilter)).hashCode();
	}

	public static int native_setTypeface(int native_object, int typeface) {
		return skPaintPool.get(native_object).setTypeface(TypefaceBridge.getTypeface(typeface)).hashCode();
	}

	public static int native_setRasterizer(int native_object, int rasterizer) {
		return skPaintPool.get(native_object).setRasterizer(RasterizerBridge.getRasterizer(rasterizer)).hashCode();
	}

	public static int native_getTextAlign(int native_object) {
		return skPaintPool.get(native_object).getTextAlign().hashCode();
	}

	public static void native_setTextAlign(int native_object, int align) {
	}

	public static void native_setTextLocale(int native_object, String locale) {
	}

	public static int native_getTextWidths(int native_object, char[] text, int index, int count, float[] widths) {
		return -1;
	}

	public static int native_getTextWidths(int native_object, String text, int start, int end, float[] widths) {
		return -1;
	}

	public static int native_getTextGlyphs(int native_object, String text, int start, int end, int contextStart,
			int contextEnd, int flags, char[] glyphs) {
		return -1;
	}

	public static float native_getTextRunAdvances(int native_object, char[] text, int index, int count,
			int contextIndex, int contextCount, int flags, float[] advances, int advancesIndex, int reserved) {
		return -1;
	}

	public static float native_getTextRunAdvances(int native_object, String text, int start, int end, int contextStart,
			int contextEnd, int flags, float[] advances, int advancesIndex, int reserved) {
		return -1;
	}

	public static int native_getTextRunCursor(int native_object, char[] text, int contextStart, int contextLength,
			int flags, int offset, int cursorOpt) {
		return -1;
	}

	public static int native_getTextRunCursor(int native_object, String text, int contextStart, int contextEnd,
			int flags, int offset, int cursorOpt) {
		return -1;
	}

	public static void native_getTextPath(int native_object, int bidiFlags, char[] text, int index, int count, float x,
			float y, int path) {

	}

	public static void native_getTextPath(int native_object, int bidiFlags, String text, int start, int end, float x,
			float y, int path) {
	}

	public static void nativeGetStringBounds(int nativePaint, String text, int start, int end, Rect bounds) {
	}

	public static void nativeGetCharArrayBounds(int nativePaint, char[] text, int index, int count, Rect bounds) {
	}

	public static void finalizer(int nativePaint) {
	}

	/**
	 * Return the paint's flags. Use the Flag enum to test flag values.
	 * 
	 * @param mNativePaint
	 * 
	 * @return the paint's flags (see enums ending in _Flag for bit masks)
	 */
	public static int getFlags(int native_object) {
		return skPaintPool.get(native_object).getFlags();
	}

	/**
	 * Set the paint's flags. Use the Flag enum to specific flag values.
	 * 
	 * @param flags
	 *            The new flag bits for the paint
	 * @param flags2
	 */
	public static void setFlags(int native_object, int flags) {
		skPaintPool.get(native_object).setFlags(flags);
	}

	/**
	 * Return the paint's hinting mode. Returns either {@link #HINTING_OFF} or
	 * {@link #HINTING_ON}.
	 */
	public static int getHinting(int native_object) {
		return skPaintPool.get(native_object).getHinting().hashCode();
	}

	/**
	 * Set the paint's hinting mode. May be either {@link #HINTING_OFF} or
	 * {@link #HINTING_ON}.
	 */
	public static void setHinting(int native_object, int mode) {
		skPaintPool.get(native_object).setHinting(
				mode == 0 ? SkPaint.Hinting.kNo_Hinting : SkPaint.Hinting.kSlight_Hinting);
	}

	/**
	 * Helper for setFlags(), setting or clearing the ANTI_ALIAS_FLAG bit
	 * AntiAliasing smooths out the edges of what is being drawn, but is has no
	 * impact on the interior of the shape. See setDither() and
	 * setFilterBitmap() to affect how colors are treated.
	 * 
	 * @param aa
	 *            true to set the antialias bit in the flags, false to clear it
	 */
	public static void setAntiAlias(int native_object, boolean aa) {
		skPaintPool.get(native_object).setAntiAlias(aa);
	}

	/**
	 * Helper for setFlags(), setting or clearing the DITHER_FLAG bit Dithering
	 * affects how colors that are higher precision than the device are
	 * down-sampled. No dithering is generally faster, but higher precision
	 * colors are just truncated down (e.g. 8888 -> 565). Dithering tries to
	 * distribute the error inherent in this process, to reduce the visual
	 * artifacts.
	 * 
	 * @param dither
	 *            true to set the dithering bit in flags, false to clear it
	 */
	public static void setDither(int native_object, boolean dither) {
		skPaintPool.get(native_object).setDither(dither);
	}

	/**
	 * Helper for setFlags(), setting or clearing the LINEAR_TEXT_FLAG bit
	 * 
	 * @param linearText
	 *            true to set the linearText bit in the paint's flags, false to
	 *            clear it.
	 */
	@Deprecated
	public static void setLinearText(int native_object, boolean linearText) {
		skPaintPool.get(native_object).setLinearText(linearText);
	}

	/**
	 * Helper for setFlags(), setting or clearing the SUBPIXEL_TEXT_FLAG bit
	 * 
	 * @param subpixelText
	 *            true to set the subpixelText bit in the paint's flags, false
	 *            to clear it.
	 */
	public static void setSubpixelText(int native_object, boolean subpixelText) {
		skPaintPool.get(native_object).setSubpixelText(subpixelText);
	}

	/**
	 * Helper for setFlags(), setting or clearing the UNDERLINE_TEXT_FLAG bit
	 * 
	 * @param underlineText
	 *            true to set the underlineText bit in the paint's flags, false
	 *            to clear it.
	 */
	public static void setUnderlineText(int native_object, boolean underlineText) {
		skPaintPool.get(native_object).setUnderlineText(underlineText);
	}

	/**
	 * Helper for setFlags(), setting or clearing the STRIKE_THRU_TEXT_FLAG bit
	 * 
	 * @param strikeThruText
	 *            true to set the strikeThruText bit in the paint's flags, false
	 *            to clear it.
	 */
	public static void setStrikeThruText(int native_object, boolean strikeThruText) {
		skPaintPool.get(native_object).setStrikeThruText(strikeThruText);
	}

	/**
	 * Helper for setFlags(), setting or clearing the FAKE_BOLD_TEXT_FLAG bit
	 * 
	 * @param fakeBoldText
	 *            true to set the fakeBoldText bit in the paint's flags, false
	 *            to clear it.
	 */
	public static void setFakeBoldText(int native_object, boolean fakeBoldText) {
		skPaintPool.get(native_object).setFakeBoldText(fakeBoldText);
	}

	/**
	 * Helper for setFlags(), setting or clearing the FILTER_BITMAP_FLAG bit.
	 * Filtering affects the sampling of bitmaps when they are transformed.
	 * Filtering does not affect how the colors in the bitmap are converted into
	 * device pixels. That is dependent on dithering and xfermodes.
	 * 
	 * @param filter
	 *            true to set the FILTER_BITMAP_FLAG bit in the paint's flags,
	 *            false to clear it.
	 */
	public static void setFilterBitmap(int native_object, boolean filter) {
		skPaintPool.get(native_object).setFilterBitmap(filter);
	}

	/**
	 * Return the paint's color. Note that the color is a 32bit value containing
	 * alpha as well as r,g,b. This 32bit value is not premultiplied, meaning
	 * that its alpha can be any value, regardless of the values of r,g,b. See
	 * the Color class for more details.
	 * 
	 * @return the paint's color (and alpha).
	 */
	public static int getColor(int native_object) {
		return skPaintPool.get(native_object).getColor();
	}

	/**
	 * Set the paint's color. Note that the color is an int containing alpha as
	 * well as r,g,b. This 32bit value is not premultiplied, meaning that its
	 * alpha can be any value, regardless of the values of r,g,b. See the Color
	 * class for more details.
	 * 
	 * @param color
	 *            The new color (including alpha) to set in the paint.
	 */
	public static void setColor(int native_object, int color) {
		skPaintPool.get(native_object).setColor(color);
	}

	/**
	 * Helper to getColor() that just returns the color's alpha value. This is
	 * the same as calling getColor() >>> 24. It always returns a value between
	 * 0 (completely transparent) and 255 (completely opaque).
	 * 
	 * @return the alpha component of the paint's color.
	 */
	public static int getAlpha(int native_object) {
		return skPaintPool.get(native_object).getAlpha();
	}

	/**
	 * Helper to setColor(), that only assigns the color's alpha value, leaving
	 * its r,g,b values unchanged. Results are undefined if the alpha value is
	 * outside of the range [0..255]
	 * 
	 * @param a
	 *            set the alpha component [0..255] of the paint's color.
	 */
	public static void setAlpha(int native_object, int a) {
		skPaintPool.get(native_object).setAlpha(a);
	}

	/**
	 * Return the width for stroking.
	 * <p />
	 * A value of 0 strokes in hairline mode. Hairlines always draws a single
	 * pixel independent of the canva's matrix.
	 * 
	 * @return the paint's stroke width, used whenever the paint's style is
	 *         Stroke or StrokeAndFill.
	 */
	public static float getStrokeWidth(int native_object) {
		return skPaintPool.get(native_object).getStrokeWidth();
	}

	/**
	 * Set the width for stroking. Pass 0 to stroke in hairline mode. Hairlines
	 * always draws a single pixel independent of the canva's matrix.
	 * 
	 * @param width
	 *            set the paint's stroke width, used whenever the paint's style
	 *            is Stroke or StrokeAndFill.
	 */
	public static void setStrokeWidth(int native_object, float width) {
		skPaintPool.get(native_object).setStrokeWidth(width);
	}

	/**
	 * Return the paint's stroke miter value. Used to control the behavior of
	 * miter joins when the joins angle is sharp.
	 * 
	 * @return the paint's miter limit, used whenever the paint's style is
	 *         Stroke or StrokeAndFill.
	 */
	public static float getStrokeMiter(int native_object) {
		return skPaintPool.get(native_object).getStrokeMiter();
	}

	/**
	 * Set the paint's stroke miter value. This is used to control the behavior
	 * of miter joins when the joins angle is sharp. This value must be >= 0.
	 * 
	 * @param miter
	 *            set the miter limit on the paint, used whenever the paint's
	 *            style is Stroke or StrokeAndFill.
	 */
	public static void setStrokeMiter(int native_object, float miter) {
		skPaintPool.get(native_object).setStrokeMiter(miter);
	}

	public static void nSetShadowLayer(int native_object, float radius, float dx, float dy, int color) {
		skPaintPool.get(native_object).nSetShadowLayer(radius, dx, dy, color);
	}

	/**
	 * Return the paint's text size.
	 * 
	 * @return the paint's text size.
	 */
	public static float getTextSize(int native_object) {
		return skPaintPool.get(native_object).getTextSize();
	}

	/**
	 * Set the paint's text size. This value must be > 0
	 * 
	 * @param textSize
	 *            set the paint's text size.
	 */
	public static void setTextSize(int native_object, float textSize) {
		skPaintPool.get(native_object).setTextSize(textSize);
	}

	/**
	 * Return the paint's horizontal scale factor for text. The default value is
	 * 1.0.
	 * 
	 * @return the paint's scale factor in X for drawing/measuring text
	 */
	public static float getTextScaleX(int native_object) {
		return skPaintPool.get(native_object).getTextScaleX();
	}

	/**
	 * Set the paint's horizontal scale factor for text. The default value is
	 * 1.0. Values > 1.0 will stretch the text wider. Values < 1.0 will stretch
	 * the text narrower.
	 * 
	 * @param scaleX
	 *            set the paint's scale in X for drawing/measuring text.
	 */
	public static void setTextScaleX(int native_object, float scaleX) {
		skPaintPool.get(native_object).setTextScaleX(scaleX);
	}

	/**
	 * Return the paint's horizontal skew factor for text. The default value is
	 * 0.
	 * 
	 * @return the paint's skew factor in X for drawing text.
	 */
	public static float getTextSkewX(int native_object) {
		return skPaintPool.get(native_object).getTextSkewX();
	}

	/**
	 * Set the paint's horizontal skew factor for text. The default value is 0.
	 * For approximating oblique text, use values around -0.25.
	 * 
	 * @param skewX
	 *            set the paint's skew factor in X for drawing text.
	 */
	public static void setTextSkewX(int native_object, float skewX) {
		skPaintPool.get(native_object).setTextSkewX(skewX);
	}

	/**
	 * Return the distance above (negative) the baseline (ascent) based on the
	 * current typeface and text size.
	 * 
	 * @return the distance above (negative) the baseline (ascent) based on the
	 *         current typeface and text size.
	 */
	public static float ascent(int native_object) {
		return 0;
	}

	/**
	 * Return the distance below (positive) the baseline (descent) based on the
	 * current typeface and text size.
	 * 
	 * @return the distance below (positive) the baseline (descent) based on the
	 *         current typeface and text size.
	 */
	public static float descent(int native_object) {
		return 0;
	}

	/**
	 * Return the font's recommended interline spacing, given the Paint's
	 * settings for typeface, textSize, etc. If metrics is not null, return the
	 * fontmetric values in it.
	 * 
	 * @param metrics
	 *            If this object is not null, its fields are filled with the
	 *            appropriate values given the paint's text attributes.
	 * @return the font's recommended interline spacing.
	 */
	public static float getFontMetrics(int native_object, FontMetrics metrics) {
		return 0;
	}

	/**
	 * Return the font's interline spacing, given the Paint's settings for
	 * typeface, textSize, etc. If metrics is not null, return the fontmetric
	 * values in it. Note: all values have been converted to integers from
	 * floats, in such a way has to make the answers useful for both spacing and
	 * clipping. If you want more control over the rounding, call
	 * getFontMetrics().
	 * 
	 * @return the font's interline spacing.
	 */
	public static int getFontMetricsInt(int native_object, FontMetricsInt fmi) {
		return 0;
	}

	public static float native_measureText(int native_object, char[] text, int index, int count) {
		return 0;
	}

	public static float native_measureText(int native_object, String text, int start, int end) {
		return 0;
	}

	public static float native_measureText(int native_object, String text) {
		return 0;
	}

	public static int native_breakText(int native_object, char[] text, int index, int count, float maxWidth,
			float[] measuredWidth) {
		return 0;
	}

	public static int native_breakText(int native_object, String text, boolean measureForwards, float maxWidth,
			float[] measuredWidth) {
		return 0;
	}
}
