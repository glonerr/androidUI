package com.lonerr.skia.core;

public class SkIPoint {
	public int fX;
	public int fY;

	public void setZero() {
		fX = fY = 0;
	}

	public void set(int x, int y) {
		fX = x;
		fY = y;
	}

	public int x() {
		// TODO Auto-generated method stub
		return fX;
	}

	public int y() {
		// TODO Auto-generated method stub
		return fY;
	}
}
