package com.lonerr.skia.core;

public class SkScalar {

	public static boolean SkScalarNearlyZero(float x, float tol) {
		return Math.abs(x) < tol;
	}

	public static final float SK_ScalarNearlyZero = SkScalar.SK_Scalar1
			/ (1 << 12);
	public static final float SK_Scalar1 = 1.0f;
	public static final float SK_ScalarSqrt2 = 1.41421356f;
	public static final float SK_ScalarTanPIOver8 = 0.414213562f;
	public static final float SK_ScalarRoot2Over2 = 0.707106781f;
	public static final int SK_MaxS16 = 32767;
	public static final int SK_MinS16 = -32767;
	public static final int SK_MaxS32 = 0x7FFFFFFF;
	public static final int SK_MinS32 = 0x80000001;
}
