package com.lonerr.skia.core;

public class SkGeometry {

	public static final int kCW_SkRotationDirection = 0;
	public static final int kCCW_SkRotationDirection = 1;
	public static final int kSkBuildQuadArcStorage = 17;

	public static final float[] gQuadCirclePts = new float[] {
			SkScalar.SK_Scalar1, 0, SkScalar.SK_Scalar1,
			SkScalar.SK_ScalarTanPIOver8, SkScalar.SK_ScalarRoot2Over2,
			SkScalar.SK_ScalarRoot2Over2, SkScalar.SK_ScalarTanPIOver8,
			SkScalar.SK_Scalar1, 0, SkScalar.SK_Scalar1,
			-SkScalar.SK_ScalarTanPIOver8, SkScalar.SK_Scalar1,
			-SkScalar.SK_ScalarRoot2Over2, SkScalar.SK_ScalarRoot2Over2,
			-SkScalar.SK_Scalar1, SkScalar.SK_ScalarTanPIOver8,
			-SkScalar.SK_Scalar1, 0, -SkScalar.SK_Scalar1,
			-SkScalar.SK_ScalarTanPIOver8, -SkScalar.SK_ScalarRoot2Over2,
			-SkScalar.SK_ScalarRoot2Over2, -SkScalar.SK_ScalarTanPIOver8,
			-SkScalar.SK_Scalar1, 0, -SkScalar.SK_Scalar1,
			SkScalar.SK_ScalarTanPIOver8, -SkScalar.SK_Scalar1,
			SkScalar.SK_ScalarRoot2Over2, -SkScalar.SK_ScalarRoot2Over2,
			SkScalar.SK_Scalar1, -SkScalar.SK_ScalarTanPIOver8,

			SkScalar.SK_Scalar1, 0 };
}
