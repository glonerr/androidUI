package com.lonerr.bridge.graphics;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.HashMap;

import android.graphics.GraphicsJNI;
import android.graphics.Rect;
import android.graphics.Region;
import com.lonerr.skia.core.SkIRect;
import com.lonerr.skia.core.SkRegion;
import com.lonerr.skia.core.SkRegion.Op;
import android.os.Parcel;

public class RegionBridge {
	private static HashMap<Integer, SkRegion> skMatrixPool = new HashMap<Integer, SkRegion>();

	public static Op getSkOp(int regionOp) {
		switch (regionOp) {
		case 0:
			return SkRegion.Op.kDifference_Op;
		case 1:
			return SkRegion.Op.kIntersect_Op;
		case 2:
			return SkRegion.Op.kUnion_Op;
		case 3:
			return SkRegion.Op.kXOR_Op;
		case 4:
			return SkRegion.Op.kReverseDifference_Op;
		case 5:
			return SkRegion.Op.kReplace_Op;
		default:
			return null;
		}
	}

	public static SkRegion getRegion(int nativeRegion) {
		return skMatrixPool.get(nativeRegion);
	}

	/**
	 * Return true if the region contains the specified point
	 * 
	 * @param mNativeRegion
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean contains(int mNativeRegion, int x, int y) {
		return skMatrixPool.get(mNativeRegion).contains(x, y);
	}

	/**
	 * Return true if this region is empty
	 * 
	 * @param mNativeRegion
	 */
	public static boolean isEmpty(int mNativeRegion) {
		return skMatrixPool.get(mNativeRegion).isEmpty();
	}

	/**
	 * Return true if the region contains a single rectangle
	 * 
	 * @param mNativeRegion
	 */
	public static boolean isRect(int mNativeRegion) {
		return skMatrixPool.get(mNativeRegion).isRect();
	}

	/**
	 * Return true if the region contains more than one rectangle
	 * 
	 * @param mNativeRegion
	 */
	public static boolean isComplex(int mNativeRegion) {
		return skMatrixPool.get(mNativeRegion).isComplex();
	}

	/**
	 * Return true if the region is a single rectangle (not complex) and it
	 * contains the specified rectangle. Returning false is not a guarantee that
	 * the rectangle is not contained by this region, but return true is a
	 * guarantee that the rectangle is contained by this region.
	 * 
	 * @param bottom2
	 */
	public static boolean quickContains(int mNativeRegion, int left, int top,
			int right, int bottom) {
		return skMatrixPool.get(mNativeRegion).quickContains(left, top, right,
				bottom);
	}

	/**
	 * Return true if the region is empty, or if the specified rectangle does
	 * not intersect the region. Returning false is not a guarantee that they
	 * intersect, but returning true is a guarantee that they do not.
	 * 
	 * @param bottom2
	 */
	public static boolean quickReject(int mNativeRegion, int left, int top,
			int right, int bottom) {
		SkIRect ir = new SkIRect();
		ir.set(left, top, right, bottom);
		return skMatrixPool.get(mNativeRegion).quickReject(ir);
	}

	/**
	 * Return true if the region is empty, or if the specified region does not
	 * intersect the region. Returning false is not a guarantee that they
	 * intersect, but returning true is a guarantee that they do not.
	 */
	public static boolean quickReject(Region rgn) {
		return skMatrixPool.get(rgn).quickReject(
				rgn == null ? null : skMatrixPool.get(rgn.mNativeRegion));
	}

	/**
	 * Set the dst region to the result of translating this region by [dx, dy].
	 * If this region is empty, then dst will be set to empty.
	 */
	public static void translate(int mNativeRegion, int dx, int dy, Region dst) {
		// TODO Auto-generated method stub
		skMatrixPool.get(mNativeRegion).translate(dx, dy,
				dst == null ? null : skMatrixPool.get(dst.mNativeRegion));
	}

	/**
	 * Set the dst region to the result of scaling this region by the given
	 * scale amount. If this region is empty, then dst will be set to empty.
	 * 
	 * @param scale2
	 * 
	 * @hide
	 */
	public static void scale(int mNativeRegion, float scale, Region dst) {
		SkRegion rgn = skMatrixPool.get(mNativeRegion);
		if (rgn == null)
			return;
		if (dst == null)
			return;
		SkRegion dstSR = skMatrixPool.get(dst.mNativeRegion);
		if (rgn.fArea.isEmpty()) {
			dstSR.fArea = new Area();
		} else {
			dstSR.fArea = new Area(rgn.fArea);
			AffineTransform mtx = new AffineTransform();
			mtx.scale(scale, scale);
			dstSR.fArea.transform(mtx);
		}
	}

	public static boolean nativeEquals(int r1, int r2) {
		return skMatrixPool.get(r1).equals(skMatrixPool.get(r2));
	}

	public static void nativeDestructor(int native_region) {
		skMatrixPool.remove(native_region);
	}

	public static boolean nativeSetRegion(int native_dst, int native_src) {
		SkRegion region = skMatrixPool.get(native_src);
		skMatrixPool.put(native_dst, region);
		return !region.isEmpty();
	}

	public static boolean nativeSetRect(int native_dst, int left, int top,
			int right, int bottom) {
		return skMatrixPool.get(native_dst).setRect(left, top, right, bottom);
	}

	public static boolean nativeSetPath(int native_dst, int native_path,
			int native_clip) {
		return skMatrixPool.get(native_dst).setPath(
				PathBridge.getPath(native_path), skMatrixPool.get(native_clip));
	}

	public static boolean nativeGetBounds(int native_region, Rect rect) {
		SkIRect r = skMatrixPool.get(native_region).getBounds();
		GraphicsJNI.copyRect(r, rect);
		return !r.isEmpty();
	}

	public static boolean nativeGetBoundaryPath(int native_region,
			int native_path) {
		return skMatrixPool.get(native_region).getBoundaryPath(
				PathBridge.getPath(native_path));
	}

	public static boolean nativeOp(int native_dst, int left, int top,
			int right, int bottom, int op) {
		SkIRect ir = new SkIRect();
		ir.set(left, top, right, bottom);
		return skMatrixPool.get(native_dst).op(ir, getSkOp(op));
	}

	public static boolean nativeOp(int native_dst, Rect rect, int region, int op) {
		SkIRect ir = new SkIRect();
		GraphicsJNI.copyRect(rect, ir);
		return skMatrixPool.get(native_dst).op(ir, skMatrixPool.get(region),
				getSkOp(op));
	}

	public static boolean nativeOp(int native_dst, int native_region1,
			int native_region2, int op) {
		return skMatrixPool.get(native_dst).op(
				skMatrixPool.get(native_region1),
				skMatrixPool.get(native_region2), getSkOp(op));
	}

	public static int nativeCreateFromParcel(Parcel p) {
		return -1;
	}

	public static boolean nativeWriteToParcel(int native_region, Parcel p) {
		return false;
	}

	public static String nativeToString(int native_region) {
		return skMatrixPool.get(native_region).toString();
	}

	public static int nativeConstructor() {
		SkRegion region = new SkRegion();
		skMatrixPool.put(region.hashCode(), region);
		return region.hashCode();
	}
}
