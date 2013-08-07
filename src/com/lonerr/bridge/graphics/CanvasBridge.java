package com.lonerr.bridge.graphics;

import java.util.HashMap;

import android.graphics.GraphicsJNI;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import com.lonerr.skia.core.SkBitmap;
import com.lonerr.skia.core.SkCanvas;
import com.lonerr.skia.core.SkIRect;
import com.lonerr.skia.core.SkMatrix;
import com.lonerr.skia.core.SkPaint;
import com.lonerr.skia.core.SkRect;
import com.lonerr.skia.core.SkRegion;
import com.lonerr.skia.core.SkXfermode;

public class CanvasBridge {
	private static HashMap<Integer, SkCanvas> skCanvasPool = new HashMap<Integer, SkCanvas>();
	public static SkCanvas getCanvas(int mNativeCanvas){
		return skCanvasPool.get(mNativeCanvas);
	}

	/**
	 * Free up as much memory as possible from private caches (e.g. fonts,
	 * images)
	 * 
	 * @hide
	 */
	public static void freeCaches() {
	}

	/**
	 * Free up text layout caches
	 * 
	 * @hide
	 */
	public static void freeTextLayoutCaches() {
	}

	public static int initRaster(int nativeBitmapOrZero) {
		SkBitmap skBitmap = BitmapBridge.getBitmap(nativeBitmapOrZero);
		SkCanvas skCanvas = skBitmap == null ? new SkCanvas() : new SkCanvas(
				skBitmap);
		skCanvasPool.put(skCanvas.hashCode(), skCanvas);
		return skCanvas.hashCode();
	}

	public static void native_setBitmap(int nativeCanvas, int nativeBitmap) {
		SkBitmap bitmap = BitmapBridge.getBitmap(nativeBitmap);
		if (bitmap != null) {
			skCanvasPool.get(nativeCanvas).setBitmapDevice(bitmap);
		} else {
			skCanvasPool.get(nativeCanvas).setDevice(null);
		}
	}

	public static int native_saveLayer(int nativeCanvas, RectF bounds,
			int nativePaint, int flags) {
		SkRect bounds_ = null;
		SkPaint paint = PaintBridge.getPaint(nativePaint);
		if (bounds != null) {
			bounds_ = new SkRect();
			GraphicsJNI.jrectf_to_rect(bounds, bounds_);
		}
		return skCanvasPool.get(nativeCanvas).saveLayer(bounds_, paint, flags);
	}

	public static int native_saveLayer(int nativeCanvas, float l, float t,
			float r, float b, int nativePaint, int flags) {
		SkRect bounds = new SkRect();
		bounds.set(l, t, r, b);
		SkPaint paint = PaintBridge.getPaint(nativePaint);
		return skCanvasPool.get(nativeCanvas).saveLayer(bounds, paint, flags);
	}

	public static int native_saveLayerAlpha(int nativeCanvas, RectF bounds,
			int alpha, int flags) {
		SkRect bounds_ = null;
		if (bounds != null) {
			GraphicsJNI.jrectf_to_rect(bounds, bounds_);
		}
		return skCanvasPool.get(nativeCanvas).saveLayerAlpha(bounds_, alpha,
				flags);
	}

	public static int native_saveLayerAlpha(int nativeCanvas, float l, float t,
			float r, float b, int alpha, int flags) {
		SkRect bounds = new SkRect();
		bounds.set(l, t, r, b);
		return skCanvasPool.get(nativeCanvas).saveLayerAlpha(bounds, alpha,
				flags);
	}

	public static void native_concat(int nCanvas, int nMatrix) {
		skCanvasPool.get(nCanvas).concat(MatrixBridge.getMatrix(nMatrix));
	}

	public static void native_setMatrix(int nCanvas, int nMatrix) {
		SkMatrix matrix = MatrixBridge.getMatrix(nMatrix);
		if (matrix == null) {
			skCanvasPool.get(nCanvas).resetMatrix();
		} else {
			skCanvasPool.get(nCanvas).setMatrix(matrix);
		}
	}

	public static boolean native_clipRect(int nCanvas, float left, float top,
			float right, float bottom, int op) {
		SkRect rect = new SkRect();
		rect.set(left, top, right, bottom);
		return skCanvasPool.get(nCanvas).clipRect(rect,RegionBridge.getSkOp(op), false);
	}

	public static boolean native_clipPath(int nativeCanvas, int nativePath,
			int regionOp) {
		return skCanvasPool.get(nativeCanvas).clipPath(
				PathBridge.getPath(nativePath), RegionBridge.getSkOp(regionOp),
				false);
	}

	public static boolean native_clipRegion(int nativeCanvas, int nativeRegion,
			int regionOp) {
		return skCanvasPool.get(nativeCanvas).clipRegion(
				RegionBridge.getRegion(nativeRegion),
				RegionBridge.getSkOp(regionOp));
	}

	public static void nativeSetDrawFilter(int nativeCanvas, int nativeFilter) {
		skCanvasPool.get(nativeCanvas).setDrawFilter(
				DrawFilterBridge.getDrawFilter(nativeFilter));
	}

	public static boolean native_getClipBounds(int nativeCanvas, Rect bounds) {
		SkRect r = new SkRect();
		SkIRect ir = new SkIRect();
		boolean result = skCanvasPool.get(nativeCanvas).getClipBounds(r,
				SkCanvas.EdgeType.kBW_EdgeType);
		if (!result) {
			r.setEmpty();
		}
		r.round(ir);
		GraphicsJNI.copyRect(ir, bounds);
		return result;
	}

	public static void native_getCTM(int nativeCanvas, int nMatrix) {
		skCanvasPool.get(nativeCanvas).getTotalMatrix()
				.copyTo(MatrixBridge.getMatrix(nMatrix));
	}

	public static boolean native_quickReject(int nativeCanvas, RectF rect,
			int native_edgeType) {
		SkRect rect_ = new SkRect();
		GraphicsJNI.jrectf_to_rect(rect, rect_);
		return skCanvasPool.get(nativeCanvas).quickReject(rect_,
				getEdgeType(native_edgeType));
	}

	private static SkCanvas.EdgeType getEdgeType(int native_edgeType) {
		switch (native_edgeType) {
		case 0:
			return SkCanvas.EdgeType.kBW_EdgeType;
		case 1:
			return SkCanvas.EdgeType.kAA_EdgeType;
		default:
			return null;
		}
	}

	public static boolean native_quickReject(int nativeCanvas, int path,
			int native_edgeType) {
		return skCanvasPool.get(nativeCanvas).quickReject(
				PathBridge.getPath(path), getEdgeType(native_edgeType));
	}

	public static boolean native_quickReject(int nativeCanvas, float left,
			float top, float right, float bottom, int native_edgeType) {
		SkRect r = new SkRect();
		r.set(left, top, right, bottom);
		return skCanvasPool.get(nativeCanvas).quickReject(r,
				getEdgeType(native_edgeType));
	}

	public static void native_drawRGB(int nativeCanvas, int r, int g, int b) {
		skCanvasPool.get(nativeCanvas).drawARGB(0xFF, r, g, b);
	}

	public static void native_drawARGB(int nativeCanvas, int a, int r, int g,
			int b) {
		skCanvasPool.get(nativeCanvas).drawARGB(a, r, g, b);
	}

	public static void native_drawColor(int nativeCanvas, int color) {
		skCanvasPool.get(nativeCanvas).drawColor(color,
				SkXfermode.Mode.kSrcOver_Mode);
	}

	public static void native_drawColor(int nativeCanvas, int color, int mode) {
		skCanvasPool.get(nativeCanvas).drawColor(color, getXferMode(mode));
	}

	private static SkXfermode.Mode getXferMode(int mode) {
		switch (mode) {
		case 0:
			return SkXfermode.Mode.kClear_Mode;
		case 1:
			return SkXfermode.Mode.kSrc_Mode;
		case 2:
			return SkXfermode.Mode.kDst_Mode;
		case 3:
			return SkXfermode.Mode.kSrcOver_Mode;
		case 4:
			return SkXfermode.Mode.kDstOver_Mode;
		case 5:
			return SkXfermode.Mode.kSrcIn_Mode;
		case 6:
			return SkXfermode.Mode.kDstIn_Mode;
		case 7:
			return SkXfermode.Mode.kSrcOut_Mode;
		case 8:
			return SkXfermode.Mode.kDstOut_Mode;
		case 9:
			return SkXfermode.Mode.kSrcATop_Mode;
		case 10:
			return SkXfermode.Mode.kDstATop_Mode;
		case 11:
			return SkXfermode.Mode.kXor_Mode;
		case 12:
			return SkXfermode.Mode.kDarken_Mode;
		case 13:
			return SkXfermode.Mode.kLighten_Mode;
		case 14:
			return SkXfermode.Mode.kMultiply_Mode;
		case 15:
			return SkXfermode.Mode.kScreen_Mode;
		case 16:
			return null;
		case 17:
			return SkXfermode.Mode.kOverlay_Mode;

		default:
			return null;
		}
	}

	public static void native_drawPaint(int nativeCanvas, int paint) {
		skCanvasPool.get(nativeCanvas).drawPaint(PaintBridge.getPaint(paint));
	}

	public static void native_drawLine(int nativeCanvas, float startX,
			float startY, float stopX, float stopY, int paint) {
		skCanvasPool.get(nativeCanvas).drawLines(startX, startY, stopX, stopY,
				PaintBridge.getPaint(paint));
	}

	public static void native_drawRect(int nativeCanvas, RectF rect, int paint) {
		SkRect rect_ = new SkRect();
        GraphicsJNI.jrectf_to_rect(rect, rect_);
		skCanvasPool.get(nativeCanvas).drawRect(rect_,
				PaintBridge.getPaint(paint));
	}

	public static void native_drawRect(int nativeCanvas, float left, float top,
			float right, float bottom, int paint) {
		skCanvasPool.get(nativeCanvas).drawRectCoords(left, top, right, bottom,
				PaintBridge.getPaint(paint));
	}

	public static void native_drawOval(int nativeCanvas, RectF src, int paint) {
		SkRect oval = new SkRect();
		GraphicsJNI.jrectf_to_rect(src, oval);
		skCanvasPool.get(nativeCanvas).drawOval(oval,
				PaintBridge.getPaint(paint));
	}

	public static void native_drawCircle(int nativeCanvas, float cx, float cy,
			float radius, int paint) {
		skCanvasPool.get(nativeCanvas).drawCircle(cx, cy, radius,
				PaintBridge.getPaint(paint));
	}

	public static void native_drawArc(int nativeCanvas, RectF joval,
			float startAngle, float sweepAngle, boolean useCenter, int paint) {
		SkRect oval = new SkRect();
		GraphicsJNI.jrectf_to_rect(joval, oval);
		skCanvasPool.get(nativeCanvas).drawArc(oval, startAngle, sweepAngle,
				useCenter, PaintBridge.getPaint(paint));
	}

	public static void native_drawRoundRect(int nativeCanvas, RectF jrect,
			float rx, float ry, int paint) {
		SkRect rect = new SkRect();
		GraphicsJNI.jrectf_to_rect(jrect, rect);
		skCanvasPool.get(nativeCanvas).drawRoundRect(rect, rx, ry,
				PaintBridge.getPaint(paint));
	}

	public static void native_drawPath(int nativeCanvas, int path, int paint) {
		skCanvasPool.get(nativeCanvas).drawPath(PathBridge.getPath(path),
				PaintBridge.getPaint(paint));
	}

	public static void native_drawBitmap(int nativeCanvas, int bitmap,
			float left, float top, int nPaint, int canvasDensity,
			int screenDensity, int bitmapDensity) {
		SkCanvas canvas = skCanvasPool.get(nativeCanvas);
		SkPaint paint = PaintBridge.getPaint(nPaint);
		if (canvasDensity == bitmapDensity || canvasDensity == 0
				|| bitmapDensity == 0) {
			if (screenDensity != 0 && screenDensity != bitmapDensity) {
				SkPaint filteredPaint = new SkPaint();
				if (paint != null) {
					filteredPaint = paint;
				}
				filteredPaint.setFilterBitmap(true);
				canvas.drawBitmap(BitmapBridge.getBitmap(bitmap), left, top,
						filteredPaint);
			} else {
				canvas.drawBitmap(BitmapBridge.getBitmap(bitmap), left, top,
						paint);
			}
		} else {
			canvas.save(SkCanvas.kMatrixClip_SaveFlag);
			float scale = canvasDensity / (float) bitmapDensity;
			canvas.translate(left, top);
			canvas.scale(scale, scale);
			SkPaint filteredPaint = new SkPaint();
			if (paint != null) {
				filteredPaint = paint;
			}
			filteredPaint.setFilterBitmap(true);
			canvas.drawBitmap(BitmapBridge.getBitmap(bitmap), 0, 0, filteredPaint);
			canvas.restore();
		}
	}

	public static void native_drawBitmap(int canvas, int bitmap, Rect srcIRect,
			RectF dstf, int paint, int screenDensity, int bitmapDensity) {
		SkRect dst = new SkRect();
		GraphicsJNI.jrectf_to_rect(dstf, dst);
		doDrawBitmap(canvas, bitmap, srcIRect, dst, paint, screenDensity,
				bitmapDensity);
	}

	public static void native_drawBitmap(int canvas, int bitmap, Rect srcIRect,
			Rect dstIRect, int paint, int screenDensity, int bitmapDensity) {
		SkRect dst = new SkRect();
		GraphicsJNI.jrect_to_rect(dstIRect, dst);
		doDrawBitmap(canvas, bitmap, srcIRect, dst, paint, screenDensity,
				bitmapDensity);
	}

	private static void doDrawBitmap(int nCanvas, int nBitmap, Rect srcIRect,
			SkRect dst, int nPaint, int screenDensity, int bitmapDensity) {
		SkIRect src = new SkIRect(), srcPtr = null;
		SkPaint paint = PaintBridge.getPaint(nPaint);
		SkCanvas canvas = skCanvasPool.get(nCanvas);
		SkBitmap bitmap = BitmapBridge.getBitmap(nBitmap);
		if (srcIRect != null) {
			GraphicsJNI.copyRect(srcIRect, src);
			srcPtr = src;
		}

		if (screenDensity != 0 && screenDensity != bitmapDensity) {
			SkPaint filteredPaint = new SkPaint();
			if (paint != null) {
				filteredPaint = paint;
			}
			filteredPaint.setFilterBitmap(true);
			canvas.drawBitmapRect(bitmap, srcPtr, dst, filteredPaint);
		} else {
			canvas.drawBitmapRect(bitmap, srcPtr, dst, paint);
		}
	}

	public static void native_drawBitmap(int nativeCanvas, int[] colors,
			int offset, int stride, float x, float y, int width, int height,
			boolean hasAlpha, int paint) {
		SkBitmap bitmap = new SkBitmap();
		bitmap.setConfig(hasAlpha ? SkBitmap.Config.kARGB_8888_Config
				: SkBitmap.Config.kRGB_565_Config, width, height, 0);
		SkCanvas canvas = skCanvasPool.get(nativeCanvas);
		bitmap.setPixels(colors, offset, stride, (int)x, (int)y, width, height);
		canvas.drawBitmap(bitmap, x, y, PaintBridge.getPaint(paint));
	}

	public static void nativeDrawBitmapMatrix(int nCanvas, int nBitmap,
			int nMatrix, int nPaint) {
		skCanvasPool.get(nCanvas).drawBitmapMatrix(
				BitmapBridge.getBitmap(nBitmap),
				MatrixBridge.getMatrix(nMatrix), PaintBridge.getPaint(nPaint));
	}

	public static void nativeDrawBitmapMesh(int nCanvas, int nBitmap,
			int meshWidth, int meshHeight, float[] verts, int vertOffset,
			int[] colors, int colorOffset, int nPaint) {
	}

	public static void nativeDrawVertices(int nCanvas, int mode, int n,
			float[] verts, int vertOffset, float[] texs, int texOffset,
			int[] colors, int colorOffset, short[] indices, int indexOffset,
			int indexCount, int nPaint) {
	}

	public static void native_drawText(int canvas, char[] text, int index,
			int count, float x, float y, int flags, int paint) {
		drawTextWithGlyphs(canvas, text, index, count, x, y, flags, paint);
	}

	private static void drawTextWithGlyphs(int nativeCanvas, char[] text,
			int index, int count, float x, float y, int flags, int paint) {
		// TODO Auto-generated method stub

	}

	public static void native_drawText(int canvas, String text, int start,
			int end, float x, float y, int flags, int paint) {
		int count = end - start;
		drawTextWithGlyphs(canvas, text.toCharArray(), start, count, x, y,
				flags, paint);
	}

	public static void native_drawTextRun(int canvas, String text, int start,
			int end, int contextStart, int contextEnd, float x, float y,
			int dirFlags, int paint) {
		int count = end - start;
		int contextCount = contextEnd - contextStart;
		drawTextWithGlyphs(canvas, text.toCharArray(), start - contextStart,
				count, contextCount, x, y, dirFlags, paint);
	}

	public static void native_drawTextRun(int canvas, char[] chars, int index,
			int count, int contextIndex, int contextCount, float x, float y,
			int dirFlags, int paint) {
		drawTextWithGlyphs(canvas, chars, index - contextIndex, count,
				contextCount, x, y, dirFlags, paint);
	}

	private static void drawTextWithGlyphs(int canvas, char[] chars, int i,
			int count, int contextCount, float x, float y, int dirFlags,
			int paint) {
		// TODO Auto-generated method stub

	}

	public static void native_drawPosText(int nativeCanvas, char[] text,
			int index, int count, float[] pos, int paint) {

	}

	public static void native_drawPosText(int nativeCanvas, String text,
			float[] pos, int paint) {
	}

	public static void native_drawTextOnPath(int nativeCanvas, char[] text,
			int index, int count, int path, float hOffset, float vOffset,
			int bidiFlags, int paint) {
	}

	public static void native_drawTextOnPath(int nativeCanvas, String text,
			int path, float hOffset, float vOffset, int flags, int paint) {
	}

	public static void native_drawPicture(int nativeCanvas, int nativePicture) {
		skCanvasPool.get(nativeCanvas).drawPicture(
				PictureBridge.getPicture(nativePicture));
	}

	public static void finalizer(int nativeCanvas) {
		skCanvasPool.remove(nativeCanvas);
	}

	public static boolean isOpaque(int mNativeCanvas) {
		// TODO Auto-generated method stub
		return false;
	}

	public static int getWidth(int mNativeCanvas) {
		// TODO Auto-generated method stub
		return skCanvasPool.get(mNativeCanvas).getDevice().accessBitmap(false)
				.width();
	}

	public static int getHeight(int mNativeCanvas) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static int saveAll(int mNativeCanvas) {
		return skCanvasPool.get(mNativeCanvas).save(
				SkCanvas.kMatrixClip_SaveFlag);
	}

	public static int save(int mNativeCanvas, int saveFlags) {
		return skCanvasPool.get(mNativeCanvas).save(saveFlags);
	}

	public static void restore(int mNativeCanvas) {
		SkCanvas canvas = skCanvasPool.get(mNativeCanvas);
		if (canvas.getSaveCount() <= 1) { // cannot restore anymore
			return;
		}
		canvas.restore();
	}

	public static int getSaveCount(int mNativeCanvas) {
		return skCanvasPool.get(mNativeCanvas).getSaveCount();
	}

	public static void restoreToCount(int mNativeCanvas, int restoreCount) {
		SkCanvas canvas = skCanvasPool.get(mNativeCanvas);
		if (canvas.getSaveCount() <= 1) { // cannot restore anymore
			return;
		}
		canvas.restoreToCount(restoreCount);
	}

	public static void translate(int mNativeCanvas, float dx, float dy) {
		skCanvasPool.get(mNativeCanvas).translate(dx, dy);
	}

	public static void scale(int mNativeCanvas, float sx, float sy) {
		skCanvasPool.get(mNativeCanvas).scale(sx, sy);
	}

	public static void rotate(int mNativeCanvas, float degrees) {
		skCanvasPool.get(mNativeCanvas).rotate(degrees);
	}

	public static void skew(int mNativeCanvas, float sx, float sy) {
		skCanvasPool.get(mNativeCanvas).skew(sx, sy);
	}

	public static boolean clipRect(int mNativeCanvas, RectF rect) {
		SkCanvas canvas = skCanvasPool.get(mNativeCanvas);
		if (canvas == null || rect == null)
			return false;
		SkRect tmp = new SkRect();
		GraphicsJNI.jrectf_to_rect(rect, tmp);
		return canvas.clipRect(tmp, SkRegion.Op.kIntersect_Op, false);
	}

	public static boolean clipRect(int mNativeCanvas, Rect rect) {
		SkCanvas canvas = skCanvasPool.get(mNativeCanvas);
		if (canvas == null || rect == null)
			return false;
		SkRect tmp = new SkRect();
		GraphicsJNI.jrect_to_rect(rect, tmp);
		return canvas.clipRect(tmp, SkRegion.Op.kIntersect_Op, false);
	}

	public static boolean clipRect(int mNativeCanvas, float left, float top,
			float right, float bottom) {
		SkRect rect = new SkRect();
		rect.set(left, top, right, bottom);
		return skCanvasPool.get(mNativeCanvas).clipRect(rect,
				SkRegion.Op.kIntersect_Op, false);
	}

	public static void drawPoints(int mNativeCanvas, float[] pts, int offset,
			int count, Paint paint) {
		// TODO Auto-generated method stub

	}

	public static void drawPoint(int mNativeCanvas, float x, float y,
			Paint paint) {
		// TODO Auto-generated method stub

	}

	public static void drawLines(int mNativeCanvas, float[] pts, int offset,
			int count, Paint paint) {
		// TODO Auto-generated method stub

	}
}
