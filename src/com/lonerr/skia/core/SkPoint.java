package com.lonerr.skia.core;

import com.lonerr.skia.core.SkScalar;

public class SkPoint {

	public float fX;
	public float fY;
	
	private int fColor;

	public void setZero() {
		fX = fY = 0;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[" + fX + "," + fY + "]";
	}

	public void set(float x, float y) {
		fX = x;
		fY = y;
	}

	public static float length(float dx, float dy) {
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	public float length() {
		return (float) Math.sqrt(fX * fX + fY * fY);
	}

	public boolean equalsWithinTolerance(SkPoint v, float tol) {
		return SkScalar.SkScalarNearlyZero(fX - v.fX, tol)
				&& SkScalar.SkScalarNearlyZero(fY - v.fY, tol);
	}

	public static float DotProduct(SkPoint uStart, SkPoint uStop) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static float CrossProduct(SkPoint uStart, SkPoint uStop) {
		// TODO Auto-generated method stub
		return 0;
	}
}
