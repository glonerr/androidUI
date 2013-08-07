package com.lonerr.bridge.graphics;

import java.util.HashMap;

import android.graphics.GraphicsJNI;
import android.graphics.RectF;
import com.lonerr.skia.core.SkMatrix;
import com.lonerr.skia.core.SkMatrix.ScaleToFit;
import com.lonerr.skia.core.SkPoint;
import com.lonerr.skia.core.SkRect;

public class MatrixBridge {
	private static HashMap<Integer, SkMatrix> skMatrixPool = new HashMap<Integer, SkMatrix>();

	public static int native_create(int nMatrix) {
		SkMatrix obj = new SkMatrix();
		SkMatrix src = skMatrixPool.get(nMatrix);
		if (src != null)
			src.copyTo(obj);
		else
			obj.reset();
		skMatrixPool.put(obj.hashCode(), obj);
		return obj.hashCode();
	}

	public static boolean native_isIdentity(int native_object) {
		return skMatrixPool.get(native_object).isIdentity();
	}

	public static boolean native_rectStaysRect(int native_object) {
		return skMatrixPool.get(native_object).rectStaysRect();
	}

	public static void native_reset(int native_object) {
		skMatrixPool.get(native_object).reset();
	}

	public static void native_set(int native_object, int other) {
		skMatrixPool.get(other).copyTo(skMatrixPool.get(native_object));
	}

	public static void native_setTranslate(int native_object, float dx, float dy) {
		skMatrixPool.get(native_object).setTranslate(dx, dy);
	}

	public static void native_setScale(int native_object, float sx, float sy,
			float px, float py) {
		skMatrixPool.get(native_object).setScale(sx, sy, px, py);
	}

	public static void native_setScale(int native_object, float sx, float sy) {
		skMatrixPool.get(native_object).setScale(sx, sy);
	}

	public static void native_setRotate(int native_object, float degrees,
			float px, float py) {
		skMatrixPool.get(native_object).setRotate(degrees, px, py);
	}

	public static void native_setRotate(int native_object, float degrees) {
		skMatrixPool.get(native_object).setRotate(degrees);
	}

	public static void native_setSinCos(int native_object, float sinV,
			float cosV, float px, float py) {
		skMatrixPool.get(native_object).setSinCos(sinV, cosV, px, py);
	}

	public static void native_setSinCos(int native_object, float sinV,
			float cosV) {
		skMatrixPool.get(native_object).setSinCos(sinV, cosV);
	}

	public static void native_setSkew(int native_object, float kx, float ky,
			float px, float py) {
		skMatrixPool.get(native_object).setSkew(kx, ky, px, py);
	}

	public static void native_setSkew(int native_object, float kx, float ky) {
		skMatrixPool.get(native_object).setSkew(kx, ky);
	}

	public static boolean native_setConcat(int native_object, int a, int b) {
		return skMatrixPool.get(native_object).setConcat(skMatrixPool.get(a),
				skMatrixPool.get(b));
	}

	public static boolean native_preTranslate(int native_object, float dx,
			float dy) {
		return skMatrixPool.get(native_object).preTranslate(dx, dy);
	}

	public static boolean native_preScale(int native_object, float sx,
			float sy, float px, float py) {
		return skMatrixPool.get(native_object).preScale(sx, sy, px, py);
	}

	public static boolean native_preScale(int native_object, float sx, float sy) {
		return skMatrixPool.get(native_object).preScale(sx, sy);
	}

	public static boolean native_preRotate(int native_object, float degrees,
			float px, float py) {
		return skMatrixPool.get(native_object).preRotate(degrees, px, py);
	}

	public static boolean native_preRotate(int native_object, float degrees) {
		return skMatrixPool.get(native_object).preRotate(degrees);
	}

	public static boolean native_preSkew(int native_object, float kx, float ky,
			float px, float py) {
		return skMatrixPool.get(native_object).preSkew(kx, ky, px, py);
	}

	public static boolean native_preSkew(int native_object, float kx, float ky) {
		return skMatrixPool.get(native_object).preSkew(kx, ky);
	}

	public static boolean native_preConcat(int native_object, int other_matrix) {
		return skMatrixPool.get(native_object).preConcat(
				skMatrixPool.get(other_matrix));
	}

	public static boolean native_postTranslate(int native_object, float dx,
			float dy) {
		return skMatrixPool.get(native_object).postTranslate(dx, dy);
	}

	public static boolean native_postScale(int native_object, float sx,
			float sy, float px, float py) {
		return skMatrixPool.get(native_object).postScale(sx, sy, px, py);
	}

	public static boolean native_postScale(int native_object, float sx, float sy) {
		return skMatrixPool.get(native_object).postScale(sx, sy);
	}

	public static boolean native_postRotate(int native_object, float degrees,
			float px, float py) {
		return skMatrixPool.get(native_object).postRotate(degrees, px, py);
	}

	public static boolean native_postRotate(int native_object, float degrees) {
		return skMatrixPool.get(native_object).postRotate(degrees);
	}

	public static boolean native_postSkew(int native_object, float kx,
			float ky, float px, float py) {
		return skMatrixPool.get(native_object).postSkew(kx, ky, px, py);
	}

	public static boolean native_postSkew(int native_object, float kx, float ky) {
		return skMatrixPool.get(native_object).postSkew(kx, ky);
	}

	public static boolean native_postConcat(int native_object, int other_matrix) {
		return skMatrixPool.get(native_object).postConcat(
				skMatrixPool.get(other_matrix));
	}

	public static boolean native_setRectToRect(int native_object, RectF srcf,
			RectF dstf, int stf) {
		SkRect src = new SkRect();
		SkRect dst = new SkRect();
		GraphicsJNI.jrectf_to_rect(srcf, src);
		GraphicsJNI.jrectf_to_rect(dstf, dst);
		return skMatrixPool.get(native_object).setRectToRect(src, dst, getSTF(stf));
	}

	private static SkMatrix.ScaleToFit getSTF(int stf) {
		switch (stf) {
		case 0:
			return ScaleToFit.kFill_ScaleToFit;
		case 1:
			return ScaleToFit.kStart_ScaleToFit;
		case 2:
			return ScaleToFit.kCenter_ScaleToFit;
		case 3:
			return ScaleToFit.kEnd_ScaleToFit;
		default:
			return null;
		}
	}

	public static boolean native_setPolyToPoly(int native_object, float[] src,
			int srcIndex, float[] dst, int dstIndex, int ptCount) {
		SkPoint[] srcPt = new SkPoint[4], dstPt = new SkPoint[4];
		for (int i = 0; i < ptCount; i++) {
			srcPt[i] = new SkPoint();
			dstPt[i] = new SkPoint();
			int x = i << 1;
			int y = x + 1;
			srcPt[i].set(src[x + srcIndex], src[y + srcIndex]);
			dstPt[i].set(dst[x + dstIndex], dst[y + dstIndex]);
		}
		return skMatrixPool.get(native_object).setPolyToPoly(srcPt, dstPt,
				ptCount);
	}

	public static boolean native_invert(int native_object, int inverse) {
		return skMatrixPool.get(native_object)
				.invert(skMatrixPool.get(inverse));
	}

	public static void native_mapPoints(int native_object, float[] dst,
			int dstIndex, float[] src, int srcIndex, int ptCount, boolean isPts) {
		SkPoint[] dstArray = new SkPoint[ptCount];
		SkPoint[] srcArray = new SkPoint[ptCount];
		for (int i = srcIndex, count = srcIndex + ptCount; i < count; i++) {
			srcArray[i] = new SkPoint();
			dstArray[i] = new SkPoint();
			srcArray[i].set(src[i << 1], src[(i << 1) + 1]);
		}
		if (isPts)
			skMatrixPool.get(native_object).mapPoints(dstArray, srcArray,
					ptCount);
		else
			skMatrixPool.get(native_object).mapVectors(dstArray, srcArray,
					ptCount);
		for (int i = dstIndex, count = dstIndex + ptCount; i < count; i++) {
			dst[i << 1] = dstArray[i].fX;
			dst[(i << 1) + 1] = dstArray[i].fY;
		}
	}

	public static boolean native_mapRect(int native_object, RectF dstf,
			RectF srcf) {
		SkRect dst = new SkRect();
		SkRect src = new SkRect();
		GraphicsJNI.jrectf_to_rect(srcf, src);
		GraphicsJNI.jrectf_to_rect(dstf, dst);
		boolean result =  skMatrixPool.get(native_object).mapRect(dst, src);
		GraphicsJNI.copyRect(dst, dstf);
		return result;
	}

	public static float native_mapRadius(int native_object, float radius) {
		return skMatrixPool.get(native_object).mapRadius(radius);
	}

	public static void native_getValues(int native_object, float[] values) {
		skMatrixPool.get(native_object).getValues(values);
	}

	public static void native_setValues(int native_object, float[] values) {
		skMatrixPool.get(native_object).setValues(values);
	}

	public static boolean native_equals(int native_a, int native_b) {
		return skMatrixPool.get(native_a).equals(skMatrixPool.get(native_b));
	}

	public static void finalizer(int native_instance) {
		skMatrixPool.remove(native_instance);
	}
	
	public static SkMatrix getMatrix(int nMatrix){
		return skMatrixPool.get(nMatrix);
	}
}
