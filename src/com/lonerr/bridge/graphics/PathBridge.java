package com.lonerr.bridge.graphics;

import java.util.HashMap;

import android.graphics.GraphicsJNI;
import android.graphics.RectF;
import com.lonerr.skia.core.SkPath;
import com.lonerr.skia.core.SkPath.Direction;
import com.lonerr.skia.core.SkRect;

public class PathBridge {
	private static HashMap<Integer, SkPath> skPathPool = new HashMap<Integer, SkPath>();

	public static SkPath getPath(int nativePath) {
		return skPathPool.get(nativePath);
	}

	public static int init1() {
		SkPath path = new SkPath();
		skPathPool.put(path.hashCode(), path);
		return path.hashCode();
	}

	public static int init2(int nPath) {
		SkPath path = new SkPath(skPathPool.get(nPath));
		skPathPool.put(path.hashCode(), path);
		return path.hashCode();
	}

	public static void native_reset(int nPath) {
		skPathPool.get(nPath).reset();
	}

	public static void native_rewind(int nPath) {
		skPathPool.get(nPath).rewind();
	}

	public static void native_set(int native_dst, int native_src) {
		skPathPool.put(native_dst, skPathPool.get(native_src));
	}

	public static int native_getFillType(int nPath) {
		return skPathPool.get(nPath).getFillType();
	}

	public static void native_setFillType(int nPath, int ft) {
		skPathPool.get(nPath).setFillType(ft);
	}

	public static boolean native_isEmpty(int nPath) {
		return skPathPool.get(nPath).isEmpty();
	}

	public static boolean native_isRect(int nPath, RectF rect) {
		SkRect rect_ = new SkRect();
		boolean result = skPathPool.get(nPath).isRect(rect_);
		GraphicsJNI.copyRect(rect_, rect);
		return result;
	}

	public static void native_computeBounds(int nPath, RectF bounds) {
		final SkRect bounds_ = skPathPool.get(nPath).getBounds();
		GraphicsJNI.copyRect(bounds_, bounds);
	}

	public static void native_incReserve(int nPath, int extraPtCount) {
		skPathPool.get(nPath).incReserve(extraPtCount);
	}

	public static void native_moveTo(int nPath, float x, float y) {
		skPathPool.get(nPath).moveTo(x, y);
	}

	public static void native_rMoveTo(int nPath, float dx, float dy) {
		skPathPool.get(nPath).rMoveTo(dx, dy);
	}

	public static void native_lineTo(int nPath, float x, float y) {
		skPathPool.get(nPath).lineTo(x, y);
	}

	public static void native_rLineTo(int nPath, float dx, float dy) {
		skPathPool.get(nPath).rLineTo(dx, dy);
	}

	public static void native_quadTo(int nPath, float x1, float y1, float x2,
			float y2) {
		skPathPool.get(nPath).quadTo(x1, y1, x2, y2);
	}

	public static void native_rQuadTo(int nPath, float dx1, float dy1,
			float dx2, float dy2) {
		skPathPool.get(nPath).rQuadTo(dx1, dy1, dx2, dy2);
	}

	public static void native_cubicTo(int nPath, float x1, float y1, float x2,
			float y2, float x3, float y3) {
		skPathPool.get(nPath).cubicTo(x1, y1, x2, y2, x3, y3);
	}

	public static void native_rCubicTo(int nPath, float x1, float y1, float x2,
			float y2, float x3, float y3) {
		skPathPool.get(nPath).rCubicTo(x1, y1, x2, y2, x3, y3);
	}

	public static void native_arcTo(int nPath, RectF oval, float startAngle,
			float sweepAngle, boolean forceMoveTo) {
		SkRect oval_ = new SkRect();
		GraphicsJNI.jrectf_to_rect(oval, oval_);
		skPathPool.get(nPath).arcTo(oval_, startAngle, sweepAngle, forceMoveTo);
	}

	public static void native_close(int nPath) {
		skPathPool.get(nPath).close();
	}

	public static void native_addRect(int nPath, RectF rect, int dir) {
		SkRect rect_ = new SkRect();
		GraphicsJNI.jrectf_to_rect(rect, rect_);
		skPathPool.get(nPath).addRect(rect_, getDir(dir));
	}

	private static Direction getDir(int dir) {
		switch (dir) {
		case 0:
			return SkPath.Direction.kCW_Direction;
		case 1:
			return SkPath.Direction.kCCW_Direction;
		}
		return null;
	}

	public static void native_addRect(int nPath, float left, float top,
			float right, float bottom, int dir) {
		skPathPool.get(nPath).addRect(left, top, right, bottom, getDir(dir));
	}

	public static void native_addOval(int nPath, RectF oval, int dir) {
		SkRect oval_ = new SkRect();
		GraphicsJNI.jrectf_to_rect(oval, oval_);
		skPathPool.get(nPath).addOval(oval_, getDir(dir));
	}

	public static void native_addCircle(int nPath, float x, float y,
			float radius, int dir) {
		skPathPool.get(nPath).addCircle(x, y, radius, getDir(dir));
	}

	public static void native_addArc(int nPath, RectF oval, float startAngle,
			float sweepAngle) {
		SkRect oval_ = new SkRect();
		GraphicsJNI.jrectf_to_rect(oval, oval_);
		skPathPool.get(nPath).addArc(oval_, startAngle, sweepAngle);
	}

	public static void native_addRoundRect(int nPath, RectF rect, float rx,
			float ry, int dir) {
		SkRect rect_ = new SkRect();
		GraphicsJNI.jrectf_to_rect(rect, rect_);
		skPathPool.get(nPath).addRoundRect(rect_, rx, ry, getDir(dir));
	}

	public static void native_addRoundRect(int nPath, RectF rect,
			float[] array, int dir) {
		SkRect rect_ = new SkRect();
		GraphicsJNI.jrectf_to_rect(rect, rect_);
		skPathPool.get(nPath).addRoundRect(rect_, array, getDir(dir));
	}

	public static void native_addPath(int nPath, int src, float dx, float dy) {
		skPathPool.get(nPath).addPath(skPathPool.get(src), dx, dy);
	}

	public static void native_addPath(int nPath, int src) {
		skPathPool.get(nPath).addPath(skPathPool.get(src));
	}

	public static void native_addPath(int nPath, int src, int matrix) {
		skPathPool.get(nPath).addPath(skPathPool.get(src),
				MatrixBridge.getMatrix(matrix));
	}

	public static void native_offset(int nPath, float dx, float dy, int dst) {
		skPathPool.get(nPath).offset(dx, dy, skPathPool.get(dst));
	}

	public static void native_offset(int nPath, float dx, float dy) {
		skPathPool.get(nPath).offset(dx, dy);
	}

	public static void native_setLastPoint(int nPath, float dx, float dy) {
		skPathPool.get(nPath).setLastPt(dx, dy);
	}

	public static void native_transform(int nPath, int matrix, int dst) {
		skPathPool.get(nPath).transform(MatrixBridge.getMatrix(matrix),skPathPool.get(dst));
	}

	public static void native_transform(int nPath, int matrix) {
		skPathPool.get(nPath).transform(MatrixBridge.getMatrix(matrix));
	}

	public static void finalizer(int nPath) {
		skPathPool.remove(nPath);
	}
}
