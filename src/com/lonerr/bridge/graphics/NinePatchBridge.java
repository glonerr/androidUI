package com.lonerr.bridge.graphics;

import android.graphics.GraphicsJNI;
import android.graphics.NinePatchChunk;
import android.graphics.Rect;
import android.graphics.RectF;
import com.lonerr.skia.core.SkBitmap;
import com.lonerr.skia.core.SkCanvas;
import com.lonerr.skia.core.SkIRect;
import com.lonerr.skia.core.SkPaint;
import com.lonerr.skia.core.SkRect;
import com.lonerr.skia.core.SkRegion;

public class NinePatchBridge {
	public static boolean isNinePatchChunk(byte[] array) {
		if (null == array) {
			return false;
		}
		if (array.length < NinePatchChunk.CHUNK_SIZE) {
			return false;
		}
		NinePatchChunk chunk = NinePatchChunk.deserialize(array);
		byte wasDeserialized = chunk.wasDeserialized;
		return wasDeserialized != -1;
	}

	public static void validateNinePatchChunk(int bitmap, byte[] chunk) {
		if (chunk.length < NinePatchChunk.CHUNK_SIZE) {
			throw new RuntimeException("Array too small for chunk.");
		}
	}

	public static void nativeDraw(int canvas, RectF boundsRectF, int bitmap,
			byte[] chunkObj, int paint, int destDensity, int srcDensity) {
		SkRect bounds = new SkRect();
		GraphicsJNI.jrectf_to_rect(boundsRectF, bounds);
		draw(canvas, bounds, bitmap, chunkObj, paint, destDensity, srcDensity);
	}

	private static void draw(int nativeCanvas, SkRect bounds, int nativeBitmap,
			byte[] chunkObj, int nativePaint, int destDensity, int srcDensity) {
		NinePatchChunk chunk = NinePatchChunk.deserialize(chunkObj);
		SkCanvas canvas = CanvasBridge.getCanvas(nativeCanvas);
		SkBitmap bitmap = BitmapBridge.getBitmap(nativeBitmap);
		SkPaint paint = PaintBridge.getPaint(nativePaint);
		if (destDensity == srcDensity || destDensity == 0 || srcDensity == 0) {
			NinePatch_Draw(canvas, bounds, bitmap, chunk, paint, null);
		} else {
			canvas.save(SkCanvas.kMatrixClip_SaveFlag);
			float scale = destDensity / srcDensity;
			canvas.translate(bounds.fLeft, bounds.fTop);
			canvas.scale(scale, scale);
			bounds.fRight = (bounds.fRight - bounds.fLeft) / scale;
			bounds.fBottom = (bounds.fBottom - bounds.fTop) / scale;
			bounds.fLeft = bounds.fTop = 0;
			NinePatch_Draw(canvas, bounds, bitmap, chunk, paint, null);
			canvas.restore();
		}
	}

	private static void NinePatch_Draw(SkCanvas canvas, SkRect bounds,
			SkBitmap bitmap, NinePatchChunk chunk, SkPaint paint,
			SkRegion outRegion) {
		if (canvas != null
				&& canvas.quickReject(bounds, SkCanvas.EdgeType.kBW_EdgeType)) {
			return;
		}

		if (null == paint) {
			// matches default dither in NinePatchDrawable.java.
			paint = new SkPaint();
			paint.setDither(true);
		}

		// if our SkCanvas were back by GL we should enable this and draw this
		// as
		// a mesh, which will be faster in most cases.
		// if (false) {
		// SkNinePatch.DrawMesh(canvas, bounds, bitmap, chunk.xDivs,
		// chunk.numXDivs, chunk.yDivs, chunk.numYDivs, paint);
		// return;
		// }

		if (bounds.isEmpty()
				|| bitmap.width() == 0
				|| bitmap.height() == 0
				|| (paint != null && paint.getXfermode() == null && paint
						.getAlpha() == 0)) {
			return;
		}

		// after the lock, it is valid to check getPixels()
		if (bitmap.getPixels() == null)
			return;

		boolean hasXfer = paint.getXfermode() != null;
		SkRect dst = new SkRect();
		SkIRect src = new SkIRect();

		int x0 = chunk.xDivs[0];
		int y0 = chunk.yDivs[0];
		int initColor = paint.getColor();
		byte numXDivs = chunk.numXDivs;
		byte numYDivs = chunk.numYDivs;
		int i;
		int j;
		int colorIndex = 0;
		int color;
		boolean xIsStretchable;
		boolean initialXIsStretchable = (x0 == 0);
		boolean yIsStretchable = (y0 == 0);
		int bitmapWidth = bitmap.width();
		int bitmapHeight = bitmap.height();

		float[] dstRights = new float[numXDivs + 1];// (SkScalar*)
													// alloca((numXDivs + 1) *
													// sizeof(SkScalar));
		boolean dstRightsHaveBeenCached = false;

		int numStretchyXPixelsRemaining = 0;
		for (i = 0; i < numXDivs; i += 2) {
			numStretchyXPixelsRemaining += chunk.xDivs[i + 1] - chunk.xDivs[i];
		}
		int numFixedXPixelsRemaining = bitmapWidth
				- numStretchyXPixelsRemaining;
		int numStretchyYPixelsRemaining = 0;
		for (i = 0; i < numYDivs; i += 2) {
			numStretchyYPixelsRemaining += chunk.yDivs[i + 1] - chunk.yDivs[i];
		}
		int numFixedYPixelsRemaining = bitmapHeight
				- numStretchyYPixelsRemaining;

		src.fTop = 0;
		dst.fTop = bounds.fTop;
		// The first row always starts with the top being at y=0 and the bottom
		// being either yDivs[1] (if yDivs[0]=0) of yDivs[0]. In the former case
		// the first row is stretchable along the Y axis, otherwise it is fixed.
		// The last row always ends with the bottom being bitmap.height and the
		// top
		// being either yDivs[numYDivs-2] (if yDivs[numYDivs-1]=bitmap.height)
		// or
		// yDivs[numYDivs-1]. In the former case the last row is stretchable
		// along
		// the Y axis, otherwise it is fixed.
		//
		// The first and last columns are similarly treated with respect to the
		// X
		// axis.
		//
		// The above is to help explain some of the special casing that goes on
		// the
		// code below.

		// The initial yDiv and whether the first row is considered stretchable
		// or
		// not depends on whether yDiv[0] was zero or not.
		for (j = yIsStretchable ? 1 : 0; j <= numYDivs
				&& src.fTop < bitmapHeight; j++, yIsStretchable = !yIsStretchable) {
			src.fLeft = 0;
			dst.fLeft = bounds.fLeft;
			if (j == numYDivs) {
				src.fBottom = bitmapHeight;
				dst.fBottom = bounds.fBottom;
			} else {
				src.fBottom = chunk.yDivs[j];
				int srcYSize = src.fBottom - src.fTop;
				if (yIsStretchable) {
					dst.fBottom = dst.fTop
							+ calculateStretch(bounds.fBottom, dst.fTop,
									srcYSize, numStretchyYPixelsRemaining,
									numFixedYPixelsRemaining);
					numStretchyYPixelsRemaining -= srcYSize;
				} else {
					dst.fBottom = dst.fTop + srcYSize;
					numFixedYPixelsRemaining -= srcYSize;
				}
			}

			xIsStretchable = initialXIsStretchable;
			// The initial xDiv and whether the first column is considered
			// stretchable or not depends on whether xDiv[0] was zero or not.
			for (i = xIsStretchable ? 1 : 0; i <= numXDivs
					&& src.fLeft < bitmapWidth; i++, xIsStretchable = !xIsStretchable) {
				color = chunk.colors[colorIndex++];
				if (i == numXDivs) {
					src.fRight = bitmapWidth;
					dst.fRight = bounds.fRight;
				} else {
					src.fRight = chunk.xDivs[i];
					if (dstRightsHaveBeenCached) {
						dst.fRight = dstRights[i];
					} else {
						int srcXSize = src.fRight - src.fLeft;
						if (xIsStretchable) {
							dst.fRight = dst.fLeft
									+ calculateStretch(bounds.fRight,
											dst.fLeft, srcXSize,
											numStretchyXPixelsRemaining,
											numFixedXPixelsRemaining);
							numStretchyXPixelsRemaining -= srcXSize;
						} else {
							dst.fRight = dst.fLeft + srcXSize;
							numFixedXPixelsRemaining -= srcXSize;
						}
						dstRights[i] = dst.fRight;
					}
				}
				// If this horizontal patch is too small to be displayed, leave
				// the destination left edge where it is and go on to the next
				// patch
				// in the source.
				if (src.fLeft >= src.fRight) {
					src.fLeft = src.fRight;
					continue;
				}
				// Make sure that we actually have room to draw any bits
				if (dst.fRight <= dst.fLeft || dst.fBottom <= dst.fTop) {
					src.fLeft = src.fRight;
					dst.fLeft = dst.fRight;
					continue;
				}
				// If this patch is transparent, skip and don't draw.
				if (color == NinePatchChunk.TRANSPARENT_COLOR && !hasXfer) {
					if (outRegion != null) {
						// if (outRegion == null) {
						// outRegion = new SkRegion();
						// }
						SkIRect idst = new SkIRect();
						dst.round(idst);
						// ALOGI("Adding trans rect: (%d,%d)-(%d,%d)\n",
						// idst.fLeft, idst.fTop, idst.fRight, idst.fBottom);
						outRegion.op(idst, SkRegion.Op.kUnion_Op);
					}
					src.fLeft = src.fRight;
					dst.fLeft = dst.fRight;
					continue;
				}
				if (canvas != null) {
					drawStretchyPatch(canvas, src, dst, bitmap, paint,
							initColor, color, hasXfer);
				}
				src.fLeft = src.fRight;
				dst.fLeft = dst.fRight;
			}
			src.fTop = src.fBottom;
			dst.fTop = dst.fBottom;
			dstRightsHaveBeenCached = true;
		}
	}

	private static void drawStretchyPatch(SkCanvas canvas, SkIRect src,
			SkRect dst, SkBitmap bitmap, SkPaint paint, int initColor,
			int colorHint, boolean hasXfer) {
		if (colorHint != NinePatchChunk.NO_COLOR) {
			paint.setColor(modAlpha(colorHint, paint.getAlpha()));
			canvas.drawRect(dst, paint);
			paint.setColor(initColor);
		} else if (src.width() == 1 && src.height() == 1) {
			int c = bitmap.getPixel(src.fLeft, src.fTop);
			if (0 != c || hasXfer) {
				int prev = paint.getColor();
				paint.setColor(c);
				canvas.drawRect(dst, paint);
				paint.setColor(prev);
			}
		} else {
			canvas.drawBitmapRect(bitmap, src, dst, paint);
		}
	}

	private static int modAlpha(int c, int alpha) {
		int scale = alpha + (alpha >> 7);
		int a = ((c >> 24) & 0xFF) * scale >> 8;
		return (c & 0x00FFFFFF) | (a << 24);
	}

	private static float calculateStretch(float boundsLimit,
			float startingPoint, int srcSpace, int numStrechyPixelsRemaining,
			int numFixedPixelsRemaining) {
		float spaceRemaining = boundsLimit - startingPoint;
		float stretchySpaceRemaining = spaceRemaining - numFixedPixelsRemaining;
		return srcSpace * stretchySpaceRemaining / numStrechyPixelsRemaining;
	}

	public static void nativeDraw(int canvas, Rect boundsRect, int bitmap,
			byte[] chunkObj, int paint, int destDensity, int srcDensity) {
		SkRect bounds = new SkRect();
		GraphicsJNI.jrect_to_rect(boundsRect, bounds);
		draw(canvas, bounds, bitmap, chunkObj, paint, destDensity, srcDensity);
	}

	public static int nativeGetTransparentRegion(int bitmap, byte[] chunk,
			Rect boundsRect) {
		SkRect bounds = new SkRect();
		GraphicsJNI.jrect_to_rect(boundsRect, bounds);
		return -1;
	}
}
