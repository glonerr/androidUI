package com.lonerr.skia.core;

import java.awt.geom.AffineTransform;
import java.util.Arrays;

public class SkMatrix {

	private int fTypeMask;
	private float[] fMat = new float[MATRIX_SIZE];
	@SuppressWarnings("unused")
	private static final int kScalar1Int = 0x3f800000;
	@SuppressWarnings("unused")
	private static final int kPersp1Int = 0x3f800000;
	public static final int MATRIX_SIZE = 9;
	public static final int kMScaleX = 0x00;
	public static final int kMSkewX = 0x01;
	public static final int kMTransX = 0x02;
	public static final int kMSkewY = 0x03;
	public static final int kMScaleY = 0x04;
	public static final int kMTransY = 0x05;
	public static final int kMPersp0 = 0x06;
	public static final int kMPersp1 = 0x07;
	public static final int kMPersp2 = 0x08;
	@SuppressWarnings("unused")
	private static final int kTranslate_Shift = 0x00;
	@SuppressWarnings("unused")
	private static final int kScale_Shift = 0x01;
	@SuppressWarnings("unused")
	private static final int kAffine_Shift = 0x02;
	@SuppressWarnings("unused")
	private static final int kPerspective_Shift = 0x03;
	private static final int kRectStaysRect_Shift = 0x04;
	private static final int kIdentity_Mask = 0x00;
	private static final int kTranslate_Mask = 0x01; // !< set if the matrix has
														// translation
	private static final int kScale_Mask = 0x02; // !< set if the matrix has X
													// or Y scale
	private static final int kAffine_Mask = 0x04; // !< set if the matrix skews
													// or rotates
	private static final int kPerspective_Mask = 0x08; // set if the matrix is
														// in perspective
	private static final int kRectStaysRect_Mask = 0x10;
	private static final int kOnlyPerspectiveValid_Mask = 0x40;
	private static final int kUnknown_Mask = 0x80;
	private static final int kAllMasks = kTranslate_Mask | kScale_Mask
			| kAffine_Mask | kPerspective_Mask | kRectStaysRect_Mask;
	private static final float kMatrix22Elem = SkScalar.SK_Scalar1;

	public enum ScaleToFit {
		/**
		 * Scale in X and Y independently, so that src matches dst exactly. This
		 * may change the aspect ratio of the src.
		 */
		kFill_ScaleToFit,
		/**
		 * Compute a scale that will maintain the original src aspect ratio, but
		 * will also ensure that src fits entirely inside dst. At least one axis
		 * (X or Y) will fit exactly. kStart aligns the result to the left and
		 * top edges of dst.
		 */
		kStart_ScaleToFit,
		/**
		 * Compute a scale that will maintain the original src aspect ratio, but
		 * will also ensure that src fits entirely inside dst. At least one axis
		 * (X or Y) will fit exactly. The result is centered inside dst.
		 */
		kCenter_ScaleToFit,
		/**
		 * Compute a scale that will maintain the original src aspect ratio, but
		 * will also ensure that src fits entirely inside dst. At least one axis
		 * (X or Y) will fit exactly. kEnd aligns the result to the right and
		 * bottom edges of dst.
		 */
		kEnd_ScaleToFit
	};

	private boolean checkForZero(float x) {
		return x * x == 0;
	}

	private int computeTypeMask() {
		int mask = 0;
		if (fMat[kMPersp0] != 0 || fMat[kMPersp1] != 0
				|| (fMat[kMPersp2] - kMatrix22Elem) != 0) {
			mask |= kPerspective_Mask;
		}
		if (fMat[kMTransX] != 0 || fMat[kMTransY] != 0) {
			mask |= kTranslate_Mask;
		}
		int m00 = (int) Math.ceil(fMat[kMScaleX]);
		int m01 = (int) Math.ceil(fMat[kMSkewX]);
		int m10 = (int) Math.ceil(fMat[kMSkewY]);
		int m11 = (int) Math.ceil(fMat[kMScaleY]);

		if ((m01 | m10) != 0) {
			mask |= kAffine_Mask;
		}

		if (((m00 - (int) SkScalar.SK_Scalar1) | (m11 - (int) SkScalar.SK_Scalar1)) != 0) {
			mask |= kScale_Mask;
		}
		if ((mask & kPerspective_Mask) == 0) {
			// map non-zero to 1
			m00 = m00 != 0 ? 1 : 0;
			m01 = m01 != 0 ? 1 : 0;
			m10 = m10 != 0 ? 1 : 0;
			m11 = m11 != 0 ? 1 : 0;
			// record if the (p)rimary and (s)econdary diagonals are all 0 or
			// all non-zero (answer is 0 or 1)
			int dp0 = (m00 | m11) ^ 1; // true if both are 0
			int dp1 = m00 & m11; // true if both are 1
			int ds0 = (m01 | m10) ^ 1; // true if both are 0
			int ds1 = m01 & m10; // true if both are 1
			// return 1 if primary is 1 and secondary is 0 or
			// primary is 0 and secondary is 1
			mask |= ((dp0 & ds1) | (dp1 & ds0)) << kRectStaysRect_Shift;
		}
		return mask;
	}

	private int computePerspectiveTypeMask() {
		int mask = kOnlyPerspectiveValid_Mask | kUnknown_Mask;
		if (((int) Math.ceil(fMat[kMPersp0]) | (int) Math.ceil(fMat[kMPersp1]) | ((int) Math
				.ceil(fMat[kMPersp2]) - (int) SkScalar.SK_Scalar1)) != 0) {
			mask |= kPerspective_Mask;
		}
		return mask;
	}

	private int getPerspectiveTypeMaskOnly() {
		if ((fTypeMask & kUnknown_Mask) != 0
				&& (fTypeMask & kOnlyPerspectiveValid_Mask) == 0) {
			fTypeMask = computePerspectiveTypeMask();
		}
		return fTypeMask & 0xF;
	}

	private int getType() {
		if ((fTypeMask & kUnknown_Mask) != 0) {
			fTypeMask = computeTypeMask();
		}
		// only return the public masks
		return fTypeMask & 0xF;
	}

	private boolean isTriviallyIdentity() {
		if ((fTypeMask & kUnknown_Mask) != 0) {
			return false;
		}
		return ((fTypeMask & 0xF) == 0);
	}

	public void mapPtsProc(SkPoint[] dst, SkPoint[] src, int offsetDst,
			int offsetSrc, int count) {
		switch (getType() & kAllMasks) {
		case 0:
			Identity_pts(dst, src, offsetDst, offsetSrc, count);
			break;
		case 1:
			Trans_pts(dst, src, offsetDst, offsetSrc, count);
			break;
		case 2:
			Scale_pts(dst, src, offsetDst, offsetSrc, count);
			break;
		case 3:
			ScaleTrans_pts(dst, src, offsetDst, offsetSrc, count);
			break;
		case 4:
			Rot_pts(dst, src, offsetDst, offsetSrc, count);
			break;
		case 5:
			RotTrans_pts(dst, src, offsetDst, offsetSrc, count);
			break;
		case 6:
			Rot_pts(dst, src, offsetDst, offsetSrc, count);
			break;
		case 7:
			RotTrans_pts(dst, src, offsetDst, offsetSrc, count);
			break;
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
			Persp_pts(dst, src, offsetDst, offsetSrc, count);
			break;
		}
	}

	private void Persp_pts(SkPoint[] dst, SkPoint[] src, int offsetDst,
			int offsetSrc, int count) {
		if (count > 0) {
			for (int i = 0, idst = offsetDst, isrc = offsetSrc; i < count; i++, idst++, isrc++) {
				float sy = src[isrc].fY;
				float sx = src[isrc].fX;
				float x = sx * fMat[kMScaleX] + sy * fMat[kMSkewX]
						+ fMat[kMTransX];
				float y = sx * fMat[kMSkewY] + sy * fMat[kMScaleY]
						+ fMat[kMTransY];
				float z = sx * fMat[kMPersp0] + sy * fMat[kMPersp1]
						+ fMat[kMPersp2];
				if (z != 0) {
					z = SkScalar.SK_Scalar1 / z;
				}
				dst[idst].fY = y * z;
				dst[idst].fX = x * z;
			}
		}
	}

	private void RotTrans_pts(SkPoint[] dst, SkPoint[] src, int offsetDst,
			int offsetSrc, int count) {
		if (count > 0) {
			float mx = fMat[kMScaleX];
			float my = fMat[kMScaleY];
			float kx = fMat[kMSkewX];
			float ky = fMat[kMSkewY];
			float tx = fMat[kMTransX];
			float ty = fMat[kMTransY];
			for (int i = 0, idst = offsetDst, isrc = offsetSrc; i < count; i++, idst++, isrc++) {
				float sy = src[isrc].fY;
				float sx = src[isrc].fX;
				dst[idst].fY = sx * ky + sy * my + ty;
				dst[idst].fX = sx * mx + sy * kx + tx;
			}
		}
	}

	private void Rot_pts(SkPoint[] dst, SkPoint[] src, int offsetDst,
			int offsetSrc, int count) {
		float mx = fMat[kMScaleX];
		float my = fMat[kMScaleY];
		float kx = fMat[kMSkewX];
		float ky = fMat[kMSkewY];
		for (int i = 0, idst = offsetDst, isrc = offsetSrc; i < count; i++, idst++, isrc++) {
			float sy = src[isrc].fY;
			float sx = src[isrc].fX;
			dst[idst].fY = sx * ky + sy * my;
			dst[idst].fX = sx * mx + sy * kx;
		}
	}

	private void ScaleTrans_pts(SkPoint[] dst, SkPoint[] src, int offsetDst,
			int offsetSrc, int count) {
		float mx = fMat[kMScaleX];
		float my = fMat[kMScaleY];
		float tx = fMat[kMTransX];
		float ty = fMat[kMTransY];
		for (int i = 0, idst = offsetDst, isrc = offsetSrc; i < count; i++, idst++, isrc++) {
			dst[idst].fY = src[isrc].fY * my + ty;
			dst[idst].fX = src[isrc].fX * mx + tx;
		}
	}

	private void Scale_pts(SkPoint[] dst, SkPoint[] src, int offsetDst,
			int offsetSrc, int count) {
		if (count > 0) {
			float mx = fMat[kMScaleX];
			float my = fMat[kMScaleY];
			for (int i = 0, idst = offsetDst, isrc = offsetSrc; i < count; i++, idst++, isrc++) {
				dst[idst].fY = src[isrc].fY * my;
				dst[idst].fX = src[isrc].fX * mx;
			}
		}
	}

	private void Trans_pts(SkPoint[] dst, SkPoint[] src, int offsetDst,
			int offsetSrc, int count) {
		if (count > 0) {
			float tx = fMat[kMTransX];
			float ty = fMat[kMTransY];
			for (int i = 0, idst = offsetDst, isrc = offsetSrc; i < count; i++, idst++, isrc++) {
				dst[idst].fY = src[isrc].fY + ty;
				dst[idst].fX = src[isrc].fX + tx;
			}
		}
	}

	private void Identity_pts(SkPoint[] dst, SkPoint[] src, int offsetDst,
			int offsetSrc, int count) {
		if (count > 0) {
			for (int i = 0, idst = offsetDst, isrc = offsetSrc; i < count; i++, idst++, isrc++) {
				dst[idst].fX = src[isrc].fX;
				dst[idst].fY = src[isrc].fY;
			}
		}
	}

	private void multiply(float[] a, float[] b) {
		float[] tmp = new float[MATRIX_SIZE];
		// first row
		tmp[0] = a[0] * b[0] + a[1] * b[3] + a[2] * b[6];
		tmp[1] = a[0] * b[1] + a[1] * b[4] + a[2] * b[7];
		tmp[2] = a[0] * b[2] + a[1] * b[5] + a[2] * b[8];
		// 2nd row
		tmp[3] = a[3] * b[0] + a[4] * b[3] + a[5] * b[6];
		tmp[4] = a[3] * b[1] + a[4] * b[4] + a[5] * b[7];
		tmp[5] = a[3] * b[2] + a[4] * b[5] + a[5] * b[8];
		// 3rd row
		tmp[6] = a[6] * b[0] + a[7] * b[3] + a[8] * b[6];
		tmp[7] = a[6] * b[1] + a[7] * b[4] + a[8] * b[7];
		tmp[8] = a[6] * b[2] + a[7] * b[5] + a[8] * b[8];
		System.arraycopy(tmp, 0, fMat, 0, MATRIX_SIZE);
	}

	private void orTypeMask(int mask) {
		fTypeMask = fTypeMask | mask;
	}

	private boolean poly_to_point(SkPoint pt, final SkPoint poly[], int count) {
		float x = 1, y = 1;
		SkPoint pt1 = new SkPoint(), pt2 = new SkPoint();

		if (count > 1) {
			pt1.fX = poly[1].fX - poly[0].fX;
			pt1.fY = poly[1].fY - poly[0].fY;
			y = SkPoint.length(pt1.fX, pt1.fY);
			if (checkForZero(y)) {
				return false;
			}
			switch (count) {
			case 2:
				break;
			case 3:
				pt2.fX = poly[0].fY - poly[2].fY;
				pt2.fY = poly[2].fX - poly[0].fX;
				x = (pt1.fX * pt2.fX + pt1.fY * pt2.fY) / y;
				break;
			default:
				pt2.fX = poly[0].fY - poly[3].fY;
				pt2.fY = poly[3].fX - poly[0].fX;
				x = (pt1.fX * pt2.fX + pt1.fY * pt2.fY) / y;
				break;
			}
		}
		pt.set(x, y);
		return true;
	}

	private boolean proc(int index, SkPoint[] srcPt, SkMatrix dst, SkPoint scale) {
		switch (index) {
		case 0:
			float invScale = 1 / scale.fY;
			dst.fMat[kMScaleX] = (srcPt[1].fY - srcPt[0].fY) * invScale;
			dst.fMat[kMSkewY] = (srcPt[0].fX - srcPt[1].fX) * invScale;
			dst.fMat[kMPersp0] = 0;
			dst.fMat[kMSkewX] = (srcPt[1].fX - srcPt[0].fX) * invScale;
			dst.fMat[kMScaleY] = (srcPt[1].fY - srcPt[0].fY) * invScale;
			dst.fMat[kMPersp1] = 0;
			dst.fMat[kMTransX] = srcPt[0].fX;
			dst.fMat[kMTransY] = srcPt[0].fY;
			dst.fMat[kMPersp2] = 1;
			dst.setTypeMask(kUnknown_Mask);
			return true;
		case 1:
			invScale = 1 / scale.fX;
			dst.fMat[kMScaleX] = (srcPt[2].fX - srcPt[0].fX) * invScale;
			dst.fMat[kMSkewY] = (srcPt[2].fY - srcPt[0].fY) * invScale;
			dst.fMat[kMPersp0] = 0;

			invScale = 1 / scale.fY;
			dst.fMat[kMSkewX] = (srcPt[1].fX - srcPt[0].fX) * invScale;
			dst.fMat[kMScaleY] = (srcPt[1].fY - srcPt[0].fY) * invScale;
			dst.fMat[kMPersp1] = 0;

			dst.fMat[kMTransX] = srcPt[0].fX;
			dst.fMat[kMTransY] = srcPt[0].fY;
			dst.fMat[kMPersp2] = 1;
			dst.setTypeMask(kUnknown_Mask);
			return true;
		case 2:
			float a1,
			a2;
			float x0,
			y0,
			x1,
			y1,
			x2,
			y2;
			x0 = srcPt[2].fX - srcPt[0].fX;
			y0 = srcPt[2].fY - srcPt[0].fY;
			x1 = srcPt[2].fX - srcPt[1].fX;
			y1 = srcPt[2].fY - srcPt[1].fY;
			x2 = srcPt[2].fX - srcPt[3].fX;
			y2 = srcPt[2].fY - srcPt[3].fY;
			/* check if abs(x2) > abs(y2) */
			if (x2 > 0 ? y2 > 0 ? x2 > y2 : x2 > -y2 : y2 > 0 ? -x2 > y2
					: x2 < y2) {
				float denom = x1 * y2 / x2 - y1;
				if (checkForZero(denom)) {
					return false;
				}
				a1 = ((x0 - x1) * y2 / x2 - y0 + y1) / denom;
			} else {
				float denom = x1 - y1 * x2 / y2;
				if (checkForZero(denom)) {
					return false;
				}
				a1 = (x0 - x1 - (y0 - y1) * x2 / y2) / denom;
			}
			/* check if abs(x1) > abs(y1) */
			if (x1 > 0 ? y1 > 0 ? x1 > y1 : x1 > -y1 : y1 > 0 ? -x1 > y1
					: x1 < y1) {
				float denom = y2 - x2 * y1 / x1;
				if (checkForZero(denom)) {
					return false;
				}
				a2 = (y0 - y2 - (x0 - x2) * y1 / x1) / denom;
			} else {
				float denom = y2 * x1 / y1 - x2;
				if (checkForZero(denom)) {
					return false;
				}
				a2 = ((y0 - y2) * x1 / y1 - x0 + x2) / denom;
			}
			invScale = 1 / scale.fX;
			dst.fMat[kMScaleX] = (a2 * srcPt[3].fX + srcPt[3].fX - srcPt[0].fX)
					* invScale;
			dst.fMat[kMSkewY] = (a2 * srcPt[3].fY + srcPt[3].fY - srcPt[0].fY)
					* invScale;
			dst.fMat[kMPersp0] = a2 * invScale;
			invScale = 1 / scale.fY;
			dst.fMat[kMSkewX] = (a1 * srcPt[1].fX + srcPt[1].fX - srcPt[0].fX)
					* invScale;
			dst.fMat[kMScaleY] = (a1 * srcPt[1].fY + srcPt[1].fY - srcPt[0].fY)
					* invScale;
			dst.fMat[kMPersp1] = a1 * invScale;
			dst.fMat[kMTransX] = srcPt[0].fX;
			dst.fMat[kMTransY] = srcPt[0].fY;
			dst.fMat[kMPersp2] = 1;
			dst.setTypeMask(kUnknown_Mask);
			return true;
		}
		return false;
	}

	private void setTypeMask(int mask) {
		fTypeMask = mask;
	}

	private boolean skScalarNearlyZero(float x, float tolerance) {
		return Math.abs(x) < tolerance;
	}

	public static void putSkMatrix(SkMatrix matrix) {

	}

	public SkMatrix() {
		reset();
	}

	public void copyTo(SkMatrix skMatrix) {
		System.arraycopy(fMat, 0, skMatrix.fMat, 0, MATRIX_SIZE);
		skMatrix.fTypeMask = fTypeMask;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		SkMatrix object = (SkMatrix) obj;
		return fTypeMask == object.fTypeMask
				&& Arrays.equals(fMat, object.fMat);
	}

	public boolean hasPerspective() {
		return (getPerspectiveTypeMaskOnly() & kPerspective_Mask) != 0;
	}

	public boolean invert(SkMatrix inv) {
		boolean isPersp = hasPerspective();
		int shift = 0;
		double scale = sk_inv_determinant(fMat, isPersp, shift);
		if (scale == 0) { // underflow
			return false;
		}

		float[] tmp = new float[MATRIX_SIZE];

		if (isPersp) {
			shift = 61 - shift;
			tmp[kMScaleX] = (float) ((fMat[kMScaleY] * fMat[kMPersp2] - fMat[kMTransY]
					* fMat[kMPersp1]) * scale);
			tmp[kMSkewX] = (float) ((fMat[kMTransX] * fMat[kMPersp1] - fMat[kMSkewX]
					* fMat[kMPersp2]) * scale);
			tmp[kMTransX] = (float) ((fMat[kMSkewX] * fMat[kMTransY] - fMat[kMTransX]
					* fMat[kMScaleY]) * scale);
			tmp[kMSkewY] = (float) ((fMat[kMTransY] * fMat[kMPersp0] - fMat[kMSkewY]
					* fMat[kMPersp2]) * scale);
			tmp[kMScaleY] = (float) ((fMat[kMScaleX] * fMat[kMPersp2] - fMat[kMTransX]
					* fMat[kMPersp0]) * scale);
			tmp[kMTransY] = (float) ((fMat[kMTransX] * fMat[kMSkewY] - fMat[kMScaleX]
					* fMat[kMTransY]) * scale);
			tmp[kMPersp0] = (float) ((fMat[kMSkewY] * fMat[kMPersp1] - fMat[kMScaleY]
					* fMat[kMPersp0]) * scale);
			tmp[kMPersp1] = (float) ((fMat[kMSkewX] * fMat[kMPersp0] - fMat[kMScaleX]
					* fMat[kMPersp1]) * scale);
			tmp[kMPersp2] = (float) ((fMat[kMScaleX] * fMat[kMScaleY] - fMat[kMSkewX]
					* fMat[kMSkewY]) * scale);
			inv.fMat = tmp;
			inv.setTypeMask(kUnknown_Mask);
		} else { // not perspective
			tmp[kMScaleX] = (float) (fMat[kMScaleY] * scale);
			tmp[kMSkewX] = (float) (-fMat[kMSkewX] * scale);
			tmp[kMTransX] = (float) ((fMat[kMSkewX] * fMat[kMTransY] - fMat[kMScaleY]
					* fMat[kMTransX]) * scale);
			tmp[kMSkewY] = (float) (-fMat[kMSkewY] * scale);
			tmp[kMScaleY] = (float) (fMat[kMScaleX] * scale);
			tmp[kMTransY] = (float) ((fMat[kMSkewY] * fMat[kMTransX] - fMat[kMScaleX]
					* fMat[kMTransY]) * scale);
			tmp[kMPersp0] = 0;
			tmp[kMPersp1] = 0;
			tmp[kMPersp2] = kMatrix22Elem;
			inv.fMat = tmp;
			inv.setTypeMask(kUnknown_Mask | kOnlyPerspectiveValid_Mask);
		}
		return true;

		// Transform transform = new Transform(null);
		// transform.setElements(fMat[kMScaleX], fMat[kMSkewY], fMat[kMSkewX],
		// fMat[kMScaleY], fMat[kMTransX], fMat[kMTransY]);
		// transform.invert();
		// float[] elements = new float[6];
		// transform.getElements(elements);
		// inv.fMat[kMScaleX] = elements[0];
		// inv.fMat[kMSkewY] = elements[1];
		// inv.fMat[kMSkewX] = elements[2];
		// inv.fMat[kMScaleY] = elements[3];
		// inv.fMat[kMTransX] = elements[4];
		// inv.fMat[kMTransY] = elements[5];
		// inv.setTypeMask(kUnknown_Mask);
		// return true;
	}

	private double sk_inv_determinant(float[] mat, boolean isPerspective,
			int shift) {
		double det;

		if (isPerspective) {
			det = mat[kMScaleX]
					* ((double) mat[kMScaleY] * mat[kMPersp2] - (double) mat[kMTransY]
							* mat[kMPersp1])
					+ mat[kMSkewX]
					* ((double) mat[kMTransY] * mat[kMPersp0] - (double) mat[kMSkewY]
							* mat[kMPersp2])
					+ mat[kMTransX]
					* ((double) mat[kMSkewY] * mat[kMPersp1] - (double) mat[kMScaleY]
							* mat[kMPersp0]);
		} else {
			det = (double) mat[kMScaleX] * mat[kMScaleY]
					- (double) mat[kMSkewX] * mat[kMSkewY];
		}

		// Since the determinant is on the order of the cube of the matrix
		// members,
		// compare to the cube of the default nearly-zero constant (although an
		// estimate of the condition number would be better if it wasn't so
		// expensive).
		if (SkScalar.SkScalarNearlyZero((float) det,
				SkScalar.SK_ScalarNearlyZero * SkScalar.SK_ScalarNearlyZero
						* SkScalar.SK_ScalarNearlyZero)) {
			return 0;
		}
		return 1.0 / det;
	}

	public boolean isIdentity() {
		return getType() == 0;
	}

	public void mapPoints(SkPoint[] dst, SkPoint[] src, int count) {
		mapPtsProc(dst, src, 0, 0, count);
	}
	
	public void mapPtsProc(float[] dst, float[] src, int offsetDst,
			int offsetSrc, int count) {
		switch (getType() & kAllMasks) {
		case 0:
			Identity_pts(dst, src, offsetDst, offsetSrc, count);
			break;
		case 1:
			Trans_pts(dst, src, offsetDst, offsetSrc, count);
			break;
		case 2:
			Scale_pts(dst, src, offsetDst, offsetSrc, count);
			break;
		case 3:
			ScaleTrans_pts(dst, src, offsetDst, offsetSrc, count);
			break;
		case 4:
			Rot_pts(dst, src, offsetDst, offsetSrc, count);
			break;
		case 5:
			RotTrans_pts(dst, src, offsetDst, offsetSrc, count);
			break;
		case 6:
			Rot_pts(dst, src, offsetDst, offsetSrc, count);
			break;
		case 7:
			RotTrans_pts(dst, src, offsetDst, offsetSrc, count);
			break;
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
			Persp_pts(dst, src, offsetDst, offsetSrc, count);
			break;
		}
	}

	private void Persp_pts(float[] dst, float[] src, int offsetDst,
			int offsetSrc, int count) {
		if (count > 0) {
			for (int i = 0, idst = offsetDst, isrc = offsetSrc; i < count; i++, idst+=2, isrc+=2) {
				float sy = src[isrc+1];
				float sx = src[isrc];
				float x = sx * fMat[kMScaleX] + sy * fMat[kMSkewX]
						+ fMat[kMTransX];
				float y = sx * fMat[kMSkewY] + sy * fMat[kMScaleY]
						+ fMat[kMTransY];
				float z = sx * fMat[kMPersp0] + sy * fMat[kMPersp1]
						+ fMat[kMPersp2];
				if (z != 0) {
					z = SkScalar.SK_Scalar1 / z;
				}
				dst[idst+1] = y * z;
				dst[idst] = x * z;
			}
		}
	}

	private void RotTrans_pts(float[] dst, float[] src, int offsetDst,
			int offsetSrc, int count) {
		if (count > 0) {
			float mx = fMat[kMScaleX];
			float my = fMat[kMScaleY];
			float kx = fMat[kMSkewX];
			float ky = fMat[kMSkewY];
			float tx = fMat[kMTransX];
			float ty = fMat[kMTransY];
			for (int i = 0, idst = offsetDst, isrc = offsetSrc; i < count; i++, idst+=2, isrc+=2) {
				float sy = src[isrc+1];
				float sx = src[isrc];
				dst[idst+1] = sx * ky + sy * my + ty;
				dst[idst] = sx * mx + sy * kx + tx;
			}
		}
	}

	private void Rot_pts(float[] dst, float[] src, int offsetDst,
			int offsetSrc, int count) {
		float mx = fMat[kMScaleX];
		float my = fMat[kMScaleY];
		float kx = fMat[kMSkewX];
		float ky = fMat[kMSkewY];
		for (int i = 0, idst = offsetDst, isrc = offsetSrc; i < count; i++, idst+=2, isrc+=2) {
			float sy = src[isrc+1];
			float sx = src[isrc];
			dst[idst+1] = sx * ky + sy * my;
			dst[idst] = sx * mx + sy * kx;
		}
	}

	private void ScaleTrans_pts(float[] dst, float[] src, int offsetDst,
			int offsetSrc, int count) {
		float mx = fMat[kMScaleX];
		float my = fMat[kMScaleY];
		float tx = fMat[kMTransX];
		float ty = fMat[kMTransY];
		for (int i = 0, idst = offsetDst, isrc = offsetSrc; i < count; i++, idst+=2, isrc+=2) {
			dst[idst+1] = src[isrc+1] * my + ty;
			dst[idst] = src[isrc] * mx + tx;
		}
	}

	private void Scale_pts(float[] dst, float[] src, int offsetDst,
			int offsetSrc, int count) {
		if (count > 0) {
			float mx = fMat[kMScaleX];
			float my = fMat[kMScaleY];
			for (int i = 0, idst = offsetDst, isrc = offsetSrc; i < count; i++, idst+=2, isrc+=2) {
				dst[idst+1] = src[isrc+1] * my;
				dst[idst] = src[isrc] * mx;
			}
		}
	}

	private void Trans_pts(float[] dst, float[] src, int offsetDst,
			int offsetSrc, int count) {
		if (count > 0) {
			float tx = fMat[kMTransX];
			float ty = fMat[kMTransY];
			for (int i = 0, idst = offsetDst, isrc = offsetSrc; i < count; i++, idst+=2, isrc+=2) {
				dst[idst+1] = src[isrc+1] + ty;
				dst[idst] = src[isrc] + tx;
			}
		}
	}

	private void Identity_pts(float[] dst, float[] src, int offsetDst,
			int offsetSrc, int count) {
		if (count > 0) {
			for (int i = 0, idst = offsetDst, isrc = offsetSrc; i < count; i++, idst+=2, isrc+=2) {
				dst[idst] = src[isrc];
				dst[idst+1] = src[isrc+1];
			}
		}
	}

	public boolean mapRect(SkRect dst, final SkRect src) {
		if (rectStaysRect()) {
			SkPoint[] srcP = new SkPoint[2];
			SkPoint[] dstP = new SkPoint[2];
			src.toSkPoint2(srcP);
			dst.toSkPoint2(dstP);
			mapPoints(dstP, srcP, 2);
			dst.fLeft = dstP[0].fX;
			dst.fTop = dstP[0].fY;
			dst.fRight = dstP[1].fX;
			dst.fBottom = dstP[1].fY;
			dst.sort();
			return true;
		} else {
			SkPoint[] quad = new SkPoint[4];
			src.toQuad(quad);
			mapPoints(quad, quad, 4);
			dst.set(quad, 4);
			return false;
		}
	}

	public float mapRadius(float radius) {
		SkPoint[] vec = new SkPoint[2];
		vec[0] = new SkPoint();
		vec[0].set(radius, 0);
		vec[1] = new SkPoint();
		vec[1].set(0, radius);
		mapVectors(vec, 2);
		float d0 = vec[0].length();
		float d1 = vec[1].length();
		return (float) Math.sqrt(d0 * d1);
	}

	public void mapVectors(SkPoint[] dst, SkPoint[] src, int count) {
		if (hasPerspective()) {
			SkPoint origin = new SkPoint();
			mapXYProc(0, 0, origin);
			for (int i = count - 1; i >= 0; --i) {
				SkPoint tmp = new SkPoint();
				mapXYProc(src[i].fX, src[i].fY, tmp);
				dst[i].set(tmp.fX - origin.fX, tmp.fY - origin.fY);
			}
		} else {
			SkMatrix tmp = new SkMatrix();
			copyTo(tmp);
			tmp.fMat[kMTransX] = tmp.fMat[kMTransY] = 0;
			tmp.clearTypeMask(kTranslate_Mask);
			tmp.mapPoints(dst, src, count);
		}
	}

	private void clearTypeMask(int mask) {
		fTypeMask &= ~mask;
	}

	private void mapVectors(SkPoint[] vec, int count) {
		mapVectors(vec, vec, count);
	}

	private void mapXYProc(float x, float y, SkPoint result) {
		switch (getType() & kAllMasks) {
		case 0:
			Identity_xy(x, y, result);
			break;
		case 1:
			Trans_xy(x, y, result);
			break;
		case 2:
			Scale_xy(x, y, result);
			break;
		case 3:
			ScaleTrans_xy(x, y, result);
			break;
		case 4:
			Rot_xy(x, y, result);
			break;
		case 5:
			RotTrans_xy(x, y, result);
			break;
		case 6:
			Rot_xy(x, y, result);
			break;
		case 7:
			RotTrans_xy(x, y, result);
			break;
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
			Persp_xy(x, y, result);
			break;
		}
	}

	private void Persp_xy(float sx, float sy, SkPoint pt) {
		float x = sx * fMat[kMScaleX] + sy * fMat[kMSkewX] + fMat[kMTransX];
		float y = sx * fMat[kMSkewY] + sy * fMat[kMScaleY] + fMat[kMTransY];
		float z = sx * fMat[kMPersp0] + sy * fMat[kMPersp1] + fMat[kMPersp2];
		if (z != 0) {
			z = SkScalar.SK_Scalar1 / z;
		}
		pt.fX = x * z;
		pt.fY = y * z;
	}

	private void RotTrans_xy(float sx, float sy, SkPoint pt) {
		pt.fX = sx * fMat[kMScaleX] + sy * fMat[kMSkewX] + fMat[kMTransX];
		pt.fY = sx * fMat[kMSkewY] + sy + fMat[kMScaleY] + fMat[kMTransY];
	}

	private void Rot_xy(float sx, float sy, SkPoint pt) {
		pt.fX = sx * fMat[kMScaleX] + sy * fMat[kMSkewX] + fMat[kMTransX];
		pt.fY = sx * fMat[kMSkewY] + sy * fMat[kMScaleY] + fMat[kMTransY];
	}

	private void ScaleTrans_xy(float sx, float sy, SkPoint pt) {
		pt.fX = sx * fMat[kMScaleX] + fMat[kMTransX];
		pt.fY = sy * fMat[kMScaleY] + fMat[kMTransY];
	}

	private void Scale_xy(float sx, float sy, SkPoint pt) {
		pt.fX = sx * fMat[kMScaleX];
		pt.fY = sy * fMat[kMScaleY];
	}

	private void Trans_xy(float sx, float sy, SkPoint pt) {
		pt.fX = sx + fMat[kMTransX];
		pt.fY = sy + fMat[kMTransY];
	}

	private void Identity_xy(float sx, float sy, SkPoint pt) {
		pt.fX = sx;
		pt.fY = sy;
	}

	public boolean preConcat(SkMatrix mat) {
		return mat.isIdentity() || setConcat(this, mat);
	}

	public boolean preRotate(float degrees) {
		// SkMatrix m = new SkMatrix();
		// m.setRotate(degrees);
		// return preConcat(m);
		float[] fMat = new float[MATRIX_SIZE];
		double radians = Math.toRadians(degrees);
		float sinV = (float) Math.sin(radians);
		float cosV = (float) Math.cos(radians);
		fMat[kMScaleX] = cosV;
		fMat[kMSkewX] = -sinV;
		fMat[kMTransX] = 0;
		fMat[kMSkewY] = sinV;
		fMat[kMScaleY] = cosV;
		fMat[kMTransY] = 0;
		fMat[kMPersp0] = fMat[kMPersp1] = 0;
		fMat[kMPersp2] = kMatrix22Elem;
		this.fMat = getMultiply(this.fMat, fMat);
		setTypeMask(kUnknown_Mask);
		return true;
	}

	public boolean preRotate(float degrees, float px, float py) {
		// SkMatrix m = new SkMatrix();
		// m.setRotate(degrees, px, py);
		// return preConcat(m);
		float[] fMat = new float[MATRIX_SIZE];
		double radians = Math.toRadians(degrees);
		float sinV = (float) Math.sin(radians);
		float cosV = (float) Math.cos(radians);
		float oneMinusCosV = SkScalar.SK_Scalar1 - cosV;
		fMat[kMScaleX] = cosV;
		fMat[kMSkewX] = -sinV;
		fMat[kMTransX] = sinV * py + oneMinusCosV * px;
		fMat[kMSkewY] = sinV;
		fMat[kMScaleY] = cosV;
		fMat[kMTransY] = -sinV * px + oneMinusCosV * py;
		fMat[kMPersp0] = fMat[kMPersp1] = 0;
		fMat[kMPersp2] = kMatrix22Elem;
		this.fMat = getMultiply(this.fMat, fMat);
		setTypeMask(kUnknown_Mask);
		return true;
	}

	public boolean preScale(float sx, float sy) {
		if (SkScalar.SK_Scalar1 == sx && SkScalar.SK_Scalar1 == sy) {
			return true;
		}
		fMat[kMScaleX] = fMat[kMScaleX] * sx;
		fMat[kMSkewY] = fMat[kMSkewY] * sx;
		fMat[kMPersp0] = fMat[kMPersp0] * sx;
		fMat[kMSkewX] = fMat[kMSkewX] * sy;
		fMat[kMScaleY] = fMat[kMScaleY] * sy;
		fMat[kMPersp1] = fMat[kMPersp1] * sy;
		orTypeMask(kScale_Mask);
		return true;
	}

	public boolean preScale(float sx, float sy, float px, float py) {
		// SkMatrix m = new SkMatrix();
		// m.setScale(sx, sy, px, py);
		// return preConcat(m);
		if (SkScalar.SK_Scalar1 == sx && SkScalar.SK_Scalar1 == sy) {
			return true;
		}
		float[] fMat = new float[MATRIX_SIZE];
		fMat[kMScaleX] = sx;
		fMat[kMScaleY] = sy;
		fMat[kMTransX] = px - sx * px;
		fMat[kMTransY] = py - sy * py;
		fMat[kMPersp2] = kMatrix22Elem;
		fMat[kMSkewX] = fMat[kMSkewY] = fMat[kMPersp0] = fMat[kMPersp1] = 0;
		this.fMat = getMultiply(this.fMat, fMat);
		setTypeMask(kUnknown_Mask);
		return true;
	}

	public boolean preSkew(float sx, float sy) {
		// SkMatrix m = new SkMatrix();
		// m.setSkew(sx, sy);
		// return preConcat(m);
		float[] fMat = new float[MATRIX_SIZE];
		fMat[kMScaleX] = SkScalar.SK_Scalar1;
		fMat[kMSkewX] = sx;
		fMat[kMTransX] = 0;
		fMat[kMSkewY] = sy;
		fMat[kMScaleY] = SkScalar.SK_Scalar1;
		fMat[kMTransY] = 0;
		fMat[kMPersp0] = fMat[kMPersp1] = 0;
		fMat[kMPersp2] = kMatrix22Elem;
		this.fMat = getMultiply(this.fMat, fMat);
		setTypeMask(kUnknown_Mask);
		return true;
	}

	public boolean preSkew(float sx, float sy, float px, float py) {
		// SkMatrix m = new SkMatrix();
		// m.setSkew(sx, sy, px, py);
		// return preConcat(m);
		float[] fMat = new float[MATRIX_SIZE];
		fMat[kMScaleX] = SkScalar.SK_Scalar1;
		fMat[kMSkewX] = sx;
		fMat[kMTransX] = -sx * py;
		fMat[kMSkewY] = sy;
		fMat[kMScaleY] = SkScalar.SK_Scalar1;
		fMat[kMTransY] = -sy * px;
		fMat[kMPersp0] = fMat[kMPersp1] = 0;
		fMat[kMPersp2] = kMatrix22Elem;
		this.fMat = getMultiply(this.fMat, fMat);
		setTypeMask(kUnknown_Mask);
		return true;
	}

	public boolean preTranslate(float dx, float dy) {
		// if (hasPerspective()) {
		// SkMatrix m = new SkMatrix();
		// m.setTranslate(dx, dy);
		// return preConcat(m);
		// }
		// if (dx != 0 || dy != 0) {
		// fMat[kMTransX] += fMat[kMScaleX] * dx + fMat[kMSkewX] * dy;
		// fMat[kMTransY] += fMat[kMSkewY] * dx + fMat[kMScaleY] * dy;
		// setTypeMask(kUnknown_Mask | kOnlyPerspectiveValid_Mask);
		// }
		// return true;
		if (hasPerspective()) {
			if (dx != 0 || dy != 0) {
				fMat[kMTransX] = dx;
				fMat[kMTransY] = dy;
				fMat[kMScaleX] = fMat[kMScaleY] = SkScalar.SK_Scalar1;
				fMat[kMSkewX] = fMat[kMSkewY] = fMat[kMPersp0] = fMat[kMPersp1] = 0;
				fMat[kMPersp2] = kMatrix22Elem;
				this.fMat = getMultiply(this.fMat, fMat);
			} else {
				reset();
			}
			setTypeMask(kUnknown_Mask);
		}
		if (dx != 0 || dy != 0) {
			fMat[kMTransX] += fMat[kMScaleX] * dx + fMat[kMSkewX] * dy;
			fMat[kMTransY] += fMat[kMSkewY] * dx + fMat[kMScaleY] * dy;
			setTypeMask(kUnknown_Mask | kOnlyPerspectiveValid_Mask);
		}
		return true;
	}

	public boolean postConcat(SkMatrix mat) {
		return mat.isIdentity() || setConcat(mat, this);
	}

	public boolean postScale(float sx, float sy) {
		// if (SK_Scalar1 == sx && SK_Scalar1 == sy) {
		// return true;
		// }
		// SkMatrix m = new SkMatrix();
		// m.setScale(sx, sy);
		// return postConcat(m);
		if (SkScalar.SK_Scalar1 == sx && SkScalar.SK_Scalar1 == sy) {
			return true;
		}
		float[] fMat = new float[MATRIX_SIZE];
		fMat[kMScaleX] = sx;
		fMat[kMScaleY] = sy;
		fMat[kMPersp2] = kMatrix22Elem;
		fMat[kMTransX] = fMat[kMTransY] = fMat[kMSkewX] = fMat[kMSkewY] = fMat[kMPersp0] = fMat[kMPersp1] = 0;
		this.fMat = getMultiply(fMat, this.fMat);
		setTypeMask(kUnknown_Mask);
		return true;
	}

	public boolean postScale(float sx, float sy, float px, float py) {
		// if (SK_Scalar1 == sx && SK_Scalar1 == sy) {
		// return true;
		// }
		// SkMatrix m = new SkMatrix();
		// m.setScale(sx, sy, px, py);
		// return postConcat(m);
		if (SkScalar.SK_Scalar1 == sx && SkScalar.SK_Scalar1 == sy) {
			return true;
		}
		float[] fMat = new float[MATRIX_SIZE];
		fMat[kMScaleX] = sx;
		fMat[kMScaleY] = sy;
		fMat[kMTransX] = px - sx * px;
		fMat[kMTransY] = py - sy * py;
		fMat[kMPersp2] = kMatrix22Elem;
		fMat[kMSkewX] = fMat[kMSkewY] = fMat[kMPersp0] = fMat[kMPersp1] = 0;
		this.fMat = getMultiply(fMat, this.fMat);
		setTypeMask(kUnknown_Mask);
		return true;
	}

	public boolean postRotate(float degrees) {
		// SkMatrix m = new SkMatrix();
		// m.setRotate(degrees);
		// return postConcat(m);
		float[] fMat = new float[MATRIX_SIZE];
		double radians = Math.toRadians(degrees);
		float sinV = (float) Math.sin(radians);
		float cosV = (float) Math.cos(radians);
		fMat[kMScaleX] = cosV;
		fMat[kMSkewX] = -sinV;
		fMat[kMTransX] = 0;
		fMat[kMSkewY] = sinV;
		fMat[kMScaleY] = cosV;
		fMat[kMTransY] = 0;
		fMat[kMPersp0] = fMat[kMPersp1] = 0;
		fMat[kMPersp2] = kMatrix22Elem;
		this.fMat = getMultiply(fMat, this.fMat);
		setTypeMask(kUnknown_Mask);
		return true;
	}

	public boolean postRotate(float degrees, float px, float py) {
		// SkMatrix m = new SkMatrix();
		// m.setRotate(degrees, px, py);
		// return postConcat(m);
		float[] fMat = new float[MATRIX_SIZE];
		double radians = Math.toRadians(degrees);
		float sinV = (float) Math.sin(radians);
		float cosV = (float) Math.cos(radians);
		float oneMinusCosV = SkScalar.SK_Scalar1 - cosV;
		fMat[kMScaleX] = cosV;
		fMat[kMSkewX] = -sinV;
		fMat[kMTransX] = sinV * py + oneMinusCosV * px;
		fMat[kMSkewY] = sinV;
		fMat[kMScaleY] = cosV;
		fMat[kMTransY] = -sinV * px + oneMinusCosV * py;
		fMat[kMPersp0] = fMat[kMPersp1] = 0;
		fMat[kMPersp2] = kMatrix22Elem;
		this.fMat = getMultiply(fMat, this.fMat);
		setTypeMask(kUnknown_Mask);
		return true;
	}

	private float[] getMultiply(float[] a, float[] b) {
		float[] tmp = new float[MATRIX_SIZE];
		// first row
		tmp[0] = a[0] * b[0] + a[1] * b[3] + a[2] * b[6];
		tmp[1] = a[0] * b[1] + a[1] * b[4] + a[2] * b[7];
		tmp[2] = a[0] * b[2] + a[1] * b[5] + a[2] * b[8];
		// 2nd row
		tmp[3] = a[3] * b[0] + a[4] * b[3] + a[5] * b[6];
		tmp[4] = a[3] * b[1] + a[4] * b[4] + a[5] * b[7];
		tmp[5] = a[3] * b[2] + a[4] * b[5] + a[5] * b[8];
		// 3rd row
		tmp[6] = a[6] * b[0] + a[7] * b[3] + a[8] * b[6];
		tmp[7] = a[6] * b[1] + a[7] * b[4] + a[8] * b[7];
		tmp[8] = a[6] * b[2] + a[7] * b[5] + a[8] * b[8];
		return tmp;
		// System.arraycopy(tmp, 0, fMat, 0, MATRIX_SIZE);
	}

	public boolean postSkew(float sx, float sy) {
		// SkMatrix m = new SkMatrix();
		// m.setSkew(sx, sy);
		// return postConcat(m);
		float[] fMat = new float[MATRIX_SIZE];
		fMat[kMScaleX] = SkScalar.SK_Scalar1;
		fMat[kMSkewX] = sx;
		fMat[kMTransX] = 0;
		fMat[kMSkewY] = sy;
		fMat[kMScaleY] = SkScalar.SK_Scalar1;
		fMat[kMTransY] = 0;
		fMat[kMPersp0] = fMat[kMPersp1] = 0;
		fMat[kMPersp2] = kMatrix22Elem;
		this.fMat = getMultiply(fMat, this.fMat);
		setTypeMask(kUnknown_Mask);
		return true;
	}

	public boolean postSkew(float sx, float sy, float px, float py) {
		// SkMatrix m = new SkMatrix();
		// m.setSkew(sx, sy, px, py);
		// return postConcat(m);
		float[] fMat = new float[MATRIX_SIZE];
		fMat[kMScaleX] = SkScalar.SK_Scalar1;
		fMat[kMSkewX] = sx;
		fMat[kMTransX] = -sx * py;
		fMat[kMSkewY] = sy;
		fMat[kMScaleY] = SkScalar.SK_Scalar1;
		fMat[kMTransY] = -sy * px;
		fMat[kMPersp0] = fMat[kMPersp1] = 0;
		fMat[kMPersp2] = kMatrix22Elem;
		this.fMat = getMultiply(fMat, this.fMat);
		setTypeMask(kUnknown_Mask);
		return true;
	}

	public boolean postTranslate(float dx, float dy) {
		// if (hasPerspective()) {
		// SkMatrix m = new SkMatrix();
		// m.setTranslate(dx, dy);
		// return postConcat(m);
		// }
		// if (dx != 0 || dy != 0) {
		// fMat[kMTransX] += dx;
		// fMat[kMTransY] += dy;
		// setTypeMask(kUnknown_Mask | kOnlyPerspectiveValid_Mask);
		// }
		// return true;
		if (hasPerspective()) {
			if (dx != 0 || dy != 0) {
				fMat[kMTransX] = dx;
				fMat[kMTransY] = dy;
				fMat[kMScaleX] = fMat[kMScaleY] = SkScalar.SK_Scalar1;
				fMat[kMSkewX] = fMat[kMSkewY] = fMat[kMPersp0] = fMat[kMPersp1] = 0;
				fMat[kMPersp2] = kMatrix22Elem;
				this.fMat = getMultiply(fMat, this.fMat);
			} else {
				reset();
			}
			setTypeMask(kUnknown_Mask);
		}
		if (dx != 0 || dy != 0) {
			fMat[kMTransX] += dx;
			fMat[kMTransY] += dy;
			setTypeMask(kUnknown_Mask | kOnlyPerspectiveValid_Mask);
		}
		return true;
	}

	public boolean rectStaysRect() {
		if ((fTypeMask & kUnknown_Mask) != 0) {
			fTypeMask = this.computeTypeMask();
		}
		return (fTypeMask & kRectStaysRect_Mask) != 0;
	}

	public void reset() {
		fMat[kMScaleX] = fMat[kMScaleY] = SkScalar.SK_Scalar1;
		fMat[kMSkewX] = fMat[kMSkewY] = fMat[kMTransX] = fMat[kMTransY] = fMat[kMPersp0] = fMat[kMPersp1] = 0;
		fMat[kMPersp2] = kMatrix22Elem;
		setTypeMask(kIdentity_Mask | kRectStaysRect_Mask);
	}

	public void setScale(float sx, float sy) {
		if (SkScalar.SK_Scalar1 == sx && SkScalar.SK_Scalar1 == sy) {
			reset();
		} else {
			fMat[kMScaleX] = sx;
			fMat[kMScaleY] = sy;
			fMat[kMPersp2] = kMatrix22Elem;
			fMat[kMTransX] = fMat[kMTransY] = fMat[kMSkewX] = fMat[kMSkewY] = fMat[kMPersp0] = fMat[kMPersp1] = 0;
			setTypeMask(kScale_Mask | kRectStaysRect_Mask);
		}
	}

	public void setScale(float sx, float sy, float px, float py) {
		if (SkScalar.SK_Scalar1 == sx && SkScalar.SK_Scalar1 == sy) {
			reset();
		} else {
			fMat[kMScaleX] = sx;
			fMat[kMScaleY] = sy;
			fMat[kMTransX] = px - sx * px;
			fMat[kMTransY] = py - sy * py;
			fMat[kMPersp2] = kMatrix22Elem;
			fMat[kMSkewX] = fMat[kMSkewY] = fMat[kMPersp0] = fMat[kMPersp1] = 0;
			setTypeMask(kScale_Mask | kTranslate_Mask | kRectStaysRect_Mask);
		}
	}

	public void setRotate(float degrees) {
		double radians = Math.toRadians(degrees);
		setSinCos((float) Math.sin(radians), (float) Math.cos(radians));
	}

	public void setRotate(float degrees, float px, float py) {
		double radians = Math.toRadians(degrees);
		setSinCos((float) Math.sin(radians), (float) Math.cos(radians), px, py);
	}

	public void setSinCos(float sinV, float cosV) {
		fMat[kMScaleX] = cosV;
		fMat[kMSkewX] = -sinV;
		fMat[kMTransX] = 0;
		fMat[kMSkewY] = sinV;
		fMat[kMScaleY] = cosV;
		fMat[kMTransY] = 0;
		fMat[kMPersp0] = fMat[kMPersp1] = 0;
		fMat[kMPersp2] = kMatrix22Elem;
		setTypeMask(kUnknown_Mask | kOnlyPerspectiveValid_Mask);
	}

	public void setSinCos(float sinV, float cosV, float px, float py) {
		final float oneMinusCosV = SkScalar.SK_Scalar1 - cosV;
		fMat[kMScaleX] = cosV;
		fMat[kMSkewX] = -sinV;
		fMat[kMTransX] = sinV * py + oneMinusCosV * px;
		fMat[kMSkewY] = sinV;
		fMat[kMScaleY] = cosV;
		fMat[kMTransY] = -sinV * px + oneMinusCosV * py;
		fMat[kMPersp0] = fMat[kMPersp1] = 0;
		fMat[kMPersp2] = kMatrix22Elem;
		setTypeMask(kUnknown_Mask | kOnlyPerspectiveValid_Mask);
	}

	public void setSkew(float sx, float sy) {
		fMat[kMScaleX] = SkScalar.SK_Scalar1;
		fMat[kMSkewX] = sx;
		fMat[kMTransX] = 0;
		fMat[kMSkewY] = sy;
		fMat[kMScaleY] = SkScalar.SK_Scalar1;
		fMat[kMTransY] = 0;
		fMat[kMPersp0] = fMat[kMPersp1] = 0;
		fMat[kMPersp2] = kMatrix22Elem;
		setTypeMask(kUnknown_Mask | kOnlyPerspectiveValid_Mask);
	}

	public void setSkew(float sx, float sy, float px, float py) {
		fMat[kMScaleX] = SkScalar.SK_Scalar1;
		fMat[kMSkewX] = sx;
		fMat[kMTransX] = -sx * py;
		fMat[kMSkewY] = sy;
		fMat[kMScaleY] = SkScalar.SK_Scalar1;
		fMat[kMTransY] = -sy * px;
		fMat[kMPersp0] = fMat[kMPersp1] = 0;
		fMat[kMPersp2] = kMatrix22Elem;
		setTypeMask(kUnknown_Mask | kOnlyPerspectiveValid_Mask);
	}

	public void setTranslate(float dx, float dy) {
		if (dx != 0 || dy != 0) {
			fMat[kMTransX] = dx;
			fMat[kMTransY] = dy;
			fMat[kMScaleX] = fMat[kMScaleY] = SkScalar.SK_Scalar1;
			fMat[kMSkewX] = fMat[kMSkewY] = fMat[kMPersp0] = fMat[kMPersp1] = 0;
			fMat[kMPersp2] = kMatrix22Elem;
			setTypeMask(kTranslate_Mask | kRectStaysRect_Mask);
		} else {
			reset();
		}
	}

	public boolean setConcat(SkMatrix a, SkMatrix b) {
		int aType = a.getPerspectiveTypeMaskOnly();
		int bType = b.getPerspectiveTypeMaskOnly();
		if (a.isTriviallyIdentity()) {
			b.copyTo(this);
		} else if (b.isTriviallyIdentity()) {
			a.copyTo(this);
		} else {
			multiply(a.fMat, b.fMat);
			if (((aType | bType) & kPerspective_Mask) != 0) {
				setTypeMask(kUnknown_Mask);
			} else {
				setTypeMask(kUnknown_Mask | kOnlyPerspectiveValid_Mask);
			}

		}
		return true;
	}

	public boolean setRectToRect(SkRect src, SkRect dst, ScaleToFit scaleToFit) {
		if (src.isEmpty()) {
			reset();
			return false;
		}

		if (dst.isEmpty()) {
			setTypeMask(kScale_Mask | kRectStaysRect_Mask);
		} else {
			float tx, sx = dst.width() / src.width();
			float ty, sy = dst.height() / src.height();
			boolean xLarger = false;

			if (scaleToFit != ScaleToFit.kFill_ScaleToFit) {
				if (sx > sy) {
					xLarger = true;
					sx = sy;
				} else {
					sy = sx;
				}
			}

			tx = dst.fLeft - src.fLeft * sx;
			ty = dst.fTop - src.fTop * sy;
			if (scaleToFit == ScaleToFit.kCenter_ScaleToFit
					|| scaleToFit == ScaleToFit.kEnd_ScaleToFit) {
				float diff;
				if (xLarger) {
					diff = dst.width() - src.width() / sy;
				} else {
					diff = dst.height() - src.height() / sy;
				}
				if (scaleToFit == ScaleToFit.kCenter_ScaleToFit) {
					diff = diff * 0.5f;
				}
				if (xLarger) {
					tx += diff;
				} else {
					ty += diff;
				}
			}
			fMat[kMScaleX] = sx;
			fMat[kMScaleY] = sy;
			fMat[kMTransX] = tx;
			fMat[kMTransY] = ty;
			fMat[kMSkewX] = fMat[kMSkewY] = fMat[kMPersp0] = fMat[kMPersp1] = 0;
			setTypeMask(kScale_Mask | kTranslate_Mask | kRectStaysRect_Mask);
		}
		// shared cleanup
		fMat[kMPersp2] = kMatrix22Elem;
		return true;
	}

	public boolean setPolyToPoly(SkPoint[] src, SkPoint[] dst, int count) {
		if (count > 4) {
			return false;
		}
		if (count == 0) {
			reset();
			return true;
		}
		if (count == 1) {
			setTranslate(dst[0].fX - src[0].fX, dst[0].fY - src[0].fY);
			return true;
		}
		SkPoint scale = new SkPoint();
		if (!poly_to_point(scale, src, count)
				|| skScalarNearlyZero(scale.fX, SkScalar.SK_ScalarNearlyZero)
				|| skScalarNearlyZero(scale.fY, SkScalar.SK_ScalarNearlyZero)) {
			return false;
		}
		int index = count - 2;
		SkMatrix tempMap = new SkMatrix(), result = new SkMatrix();
		tempMap.setTypeMask(kUnknown_Mask);
		if (!proc(index, src, tempMap, scale)) {
			return false;
		}
		if (!tempMap.invert(result)) {
			return false;
		}
		if (!proc(index, dst, tempMap, scale)) {
			return false;
		}
		if (!result.setConcat(tempMap, result)) {
			return false;
		}
		result.copyTo(this);
		return true;
	}

	public void setValues(float[] values) {
		System.arraycopy(values, 0, fMat, 0, MATRIX_SIZE);
		setTypeMask(kUnknown_Mask);
	}

	public void getValues(float[] values) {
		System.arraycopy(fMat, 0, values, 0, MATRIX_SIZE);
	}

	public void mapPoints(SkPoint[] pts, int count) {
		mapPoints(pts, pts, count);
	}

	public void mapPoints(float[] fs, int count) {
		mapPoints(fs, fs, count);
	}
	
	public void mapPoints(float[] dst,float[] src, int count) {
		mapPtsProc(dst, src, 0, 0, count);
	}

	public AffineTransform getAffineTransform() {
		AffineTransform transform = new AffineTransform(
				fMat[SkMatrix.kMScaleX], fMat[SkMatrix.kMSkewY],
				fMat[SkMatrix.kMSkewX], fMat[SkMatrix.kMScaleY],
				fMat[SkMatrix.kMTransX], fMat[SkMatrix.kMTransY]);
		return transform;
	}

}