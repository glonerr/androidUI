package com.lonerr.skia.core;

import com.lonerr.skia.effects.SkColorFilter;

public class SkPaint {

	public static final int kAntiAlias_Flag = 0x01;
	public static final int kFilterBitmap_Flag = 0x02;
	public static final int kDither_Flag = 0x04;
	public static final int kUnderlineText_Flag = 0x08;
	public static final int kStrikeThruText_Flag = 0x10;
	public static final int kFakeBoldText_Flag = 0x20;
	public static final int kLinearText_Flag = 0x40;
	public static final int kSubpixelText_Flag = 0x80;
	public static final int kDevKernText_Flag = 0x100;
	public static final int kLCDRenderText_Flag = 0x200;
	public static final int kEmbeddedBitmapText_Flag = 0x400;
	public static final int kAutoHinting_Flag = 0x800;
	public static final int kVerticalText_Flag = 0x1000;
	public static final int kGenA8FromLCD_Flag = 0x2000;
	public static final int kAllFlags = 0x3FFF;

	private static final float SkPaintDefaults_TextSize = 12;
	private static final float SkPaintDefaults_MiterLimit = 4;
	private static final int SkPaintDefaults_Flags = 0;
	private static final Hinting SkPaintDefaults_Hinting = Hinting.kNormal_Hinting;

	private int fColor;
	private SkTypeface fTypeface;
	private float fTextSize;
	private float fTextScaleX;
	private float fTextSkewX;
	private SkPathEffect fPathEffect;
	private SkShader fShader;
	private SkXfermode fXfermode;
	private SkMaskFilter fMaskFilter;
	private SkColorFilter fColorFilter;
	private SkRasterizer fRasterizer;
	private SkImageFilter fImageFilter;
	private float fWidth;
	private float fMiterLimit;
	private int fFlags;
	private Align fTextAlign;
	private Cap fCapType;
	private Join fJoinType;
	private Style fStyle;
	private TextEncoding fTextEncoding; // 3 values
	private Hinting fHinting;

	public enum TextEncoding {
		kUTF8_TextEncoding, // !< the text parameters are UTF8
		kUTF16_TextEncoding, // !< the text parameters are UTF16
		kGlyphID_TextEncoding // !< the text parameters are glyph indices
	};

	public enum Style {
		kFill_Style, // !< fill the geometry
		kStroke_Style, // !< stroke the geometry
		kStrokeAndFill_Style, // !< fill and stroke the geometry
		kStyleCount,
	};

	/**
	 * Cap enum specifies the settings for the paint's strokecap. This is the
	 * treatment that is applied to the beginning and end of each non-closed
	 * contour (e.g. lines).
	 */
	public enum Cap {
		kButt_Cap, // !< begin/end contours with no extension
		kRound_Cap, // !< begin/end contours with a semi-circle extension
		kSquare_Cap, // !< begin/end contours with a half square extension
		kCapCount, kDefault_Cap;
	};

	/**
	 * Join enum specifies the settings for the paint's strokejoin. This is the
	 * treatment that is applied to corners in paths and rectangles.
	 */
	public enum Join {
		kMiter_Join, // !< connect path segments with a sharp join
		kRound_Join, // !< connect path segments with a round join
		kBevel_Join, // !< connect path segments with a flat bevel join
		kJoinCount, kDefault_Join
	};

	public enum Align {
		kLeft_Align, kCenter_Align, kRight_Align,

		kAlignCount
	};

	/**
	 * Specifies the level of hinting to be performed. These names are taken
	 * from the Gnome/Cairo names for the same. They are translated into
	 * Freetype concepts the same as in cairo-ft-font.c: kNo_Hinting ->
	 * FT_LOAD_NO_HINTING kSlight_Hinting -> FT_LOAD_TARGET_LIGHT
	 * kNormal_Hinting -> <default, no option> kFull_Hinting -> <same as
	 * kNormalHinting, unless we are rendering subpixel glyphs, in which case
	 * TARGET_LCD or TARGET_LCD_V is used>
	 */
	public enum Hinting {
		kNo_Hinting, kSlight_Hinting, kNormal_Hinting, // !< this is the default
		kFull_Hinting
	};

	public SkPaint(int paint) {
		// TODO Auto-generated constructor stub
	}

	public SkPaint() {
		fTextSize = SkPaintDefaults_TextSize;
		fTextScaleX = SkScalar.SK_Scalar1;
		fColor = SkColor.SK_ColorBLACK;
		fMiterLimit = SkPaintDefaults_MiterLimit;
		fFlags = SkPaintDefaults_Flags;
		fCapType = Cap.kDefault_Cap;
		fJoinType = Join.kDefault_Join;
		fTextAlign = Align.kLeft_Align;
		fStyle = Style.kFill_Style;
		fTextEncoding = TextEncoding.kUTF8_TextEncoding;
		fHinting = SkPaintDefaults_Hinting;
	}

	public SkMaskFilter getMaskFilter() {
		return fMaskFilter;
	}

	public void setFilterBitmap(boolean doFilter) {
		setFlags(doFilter ? fFlags | kFilterBitmap_Flag : fFlags & ~kFilterBitmap_Flag);
	}

	public boolean isAntiAlias() {
		return (fFlags & kAntiAlias_Flag) != 0;
	}

	public void setAlpha(int a) {
		fColor = ((fColor & 0xff000000) | a << 24);
	}

	public void setDither(boolean doDither) {
		setFlags(doDither ? fFlags | kDither_Flag : fFlags & ~kDither_Flag);
	}

	public void setARGB(int a, int r, int g, int b) {
		setColor((a << 24) | (r << 16) | (g << 8) | (b << 0));
	}

	public SkImageFilter getImageFilter() {
		return fImageFilter;
	}

	public SkShader setShader(SkShader shader) {
		fShader = shader;
		return shader;
	}

	public SkRect canComputeFastBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	public SkRect computeFastBounds(SkRect bounds, SkRect bounds2) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTextEncoding(TextEncoding encoding) {
		fTextEncoding = encoding;
	}

	public void reset() {
		fTextSize = SkPaintDefaults_TextSize;
		fTextScaleX = SkScalar.SK_Scalar1;
		fColor = SkColor.SK_ColorBLACK;
		fMiterLimit = SkPaintDefaults_MiterLimit;
		fFlags = SkPaintDefaults_Flags;
		fCapType = Cap.kDefault_Cap;
		fJoinType = Join.kDefault_Join;
		fTextAlign = Align.kLeft_Align;
		fStyle = Style.kFill_Style;
		fTextEncoding = TextEncoding.kUTF8_TextEncoding;
		fHinting = SkPaintDefaults_Hinting;
	}

	public void copyTo(SkPaint dst) {
		// TODO Auto-generated method stub

	}

	public Style getStyle() {
		return fStyle;
	}

	public void setStyle(Style style) {
		fStyle = style;
	}

	public Cap getStrokeCap() {
		return fCapType;
	}

	public void setStrokeCap(Cap ct) {
		fCapType = ct;
	}

	public Join getStrokeJoin() {
		return fJoinType;
	}

	public void setStrokeJoin(Join jt) {
		fJoinType = jt;
	}

	public boolean getFillPath(SkPath src, SkPath dst) {
		SkPath effectPath = new SkPath(), strokePath = new SkPath();
		SkPath path = src;

		float width = getStrokeWidth();

		switch (getStyle()) {
		case kFill_Style:
			width = -1; // mark it as no-stroke
			break;
		case kStrokeAndFill_Style:
			if (width == 0) {
				width = -1; // mark it as no-stroke
			}
			break;
		case kStroke_Style:
			break;
		default:
			break;
		}

		if (getPathEffect() != null) {
			// lie to the pathEffect if our style is strokeandfill, so that it
			// treats us as just fill
			if (getStyle() == SkPaint.Style.kStrokeAndFill_Style) {
				width = -1; // mark it as no-stroke
			}

			if (getPathEffect().filterPath(effectPath, src, width)) {
				path = effectPath;
			}

			// restore the width if we earlier had to lie, and if we're still
			// set to no-stroke
			// note: if we're now stroke (width >= 0), then the pathEffect asked
			// for that change
			// and we want to respect that (i.e. don't overwrite their setting
			// for width)
			if (getStyle() == SkPaint.Style.kStrokeAndFill_Style && width < 0) {
				width = getStrokeWidth();
				if (width == 0) {
					width = -1;
				}
			}
		}

		if (width > 0 && !path.isEmpty()) {
			SkStroke stroker = new SkStroke(this, width);
			stroker.strokePath(path, strokePath);
			path = strokePath;
		}

		if (path == src) {
			dst = src;
		} else {
			dst.swap(path);
		}

		return width != 0; // return true if we're filled, or false if we're
							// hairline (width == 0)
	}

	public SkPathEffect getPathEffect() {
		return fPathEffect;
	}

	public SkColorFilter setColorFilter(SkColorFilter filter) {
		fColorFilter = filter;
		return filter;
	}

	public SkXfermode setXfermode(SkXfermode mode) {
		fXfermode = mode;
		return mode;
	}

	public SkPathEffect setPathEffect(SkPathEffect effect) {
		fPathEffect = effect;
		return effect;
	}

	public SkMaskFilter setMaskFilter(SkMaskFilter filter) {
		fMaskFilter = filter;
		return filter;
	}

	public SkTypeface setTypeface(SkTypeface font) {
		fTypeface = font;
		return font;
	}

	public SkRasterizer setRasterizer(SkRasterizer r) {
		fRasterizer = r;
		return r;
	}

	public Align getTextAlign() {
		return fTextAlign;
	}

	public int getFlags() {
		return fFlags;
	}

	public void setFlags(int flags) {
		fFlags = flags;
	}

	public Hinting getHinting() {
		return fHinting;
	}

	public void setHinting(Hinting hintingLevel) {
		fHinting = hintingLevel;
	}

	public void setAntiAlias(boolean doAA) {
		setFlags(doAA ? fFlags | kAntiAlias_Flag : fFlags & ~kAntiAlias_Flag);
	}

	@Deprecated
	public void setLinearText(boolean doLinearText) {
		setFlags(doLinearText ? fFlags | kLinearText_Flag : fFlags & ~kLinearText_Flag);
	}

	public void setSubpixelText(boolean doSubpixel) {
		setFlags(doSubpixel ? fFlags | kSubpixelText_Flag : fFlags & ~kSubpixelText_Flag);
	}

	public void setUnderlineText(boolean underlineText) {
		setFlags(underlineText ? fFlags | kUnderlineText_Flag : fFlags & ~kUnderlineText_Flag);
	}

	public void setStrikeThruText(boolean doStrikeThru) {
		setFlags(doStrikeThru ? fFlags | kStrikeThruText_Flag : fFlags & ~kStrikeThruText_Flag);
	}

	public void setFakeBoldText(boolean doFakeBold) {
		setFlags(doFakeBold ? fFlags | kFakeBoldText_Flag : fFlags & ~kFakeBoldText_Flag);
	}

	public int getColor() {
		return fColor;
	}

	public void setColor(int color) {
		fColor = color;
	}

	public int getAlpha() {
		return (fColor >> 24) & 0xff;
	}

	public float getStrokeWidth() {
		return fWidth;
	}

	public void setStrokeWidth(float width) {
		if (width >= 0) {
			fWidth = width;
		}
	}

	public float getStrokeMiter() {
		return fMiterLimit;
	}

	public void setStrokeMiter(float limit) {
		if (limit >= 0) {
			fMiterLimit = limit;
		}
	}

	public void nSetShadowLayer(float radius, float dx, float dy, int color) {
		// TODO Auto-generated method stub

	}

	public float getTextSize() {
		return fTextSize;
	}

	public void setTextSize(float textSize) {
		// TODO Auto-generated method stub

	}

	public float getTextScaleX() {
		return fTextScaleX;
	}

	public void setTextScaleX(float scaleX) {
		fTextScaleX = scaleX;
	}

	public float getTextSkewX() {
		return fTextSkewX;
	}

	public void setTextSkewX(float skewX) {
		fTextSkewX = skewX;
	}

	public SkXfermode getXfermode() {
		return fXfermode;
	}

	public SkTypeface getTypeface() {
		return fTypeface;
	}

	public SkShader getShader() {
		return fShader;
	}

	public SkColorFilter getColorFilter() {
		return fColorFilter;
	}

	public SkRasterizer getRasterizer() {
		return fRasterizer;
	}

	public TextEncoding getTextEncoding() {
		return fTextEncoding;
	}
}
