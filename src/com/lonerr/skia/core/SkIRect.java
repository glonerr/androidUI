package com.lonerr.skia.core;

public class SkIRect {
	public int fLeft;
	public int fTop;
	public int fRight;
	public int fBottom;

	public void set(int left, int top, int right, int bottom) {
		fLeft = left;
		fTop = top;
		fRight = right;
		fBottom = bottom;
	}

	public void setEmpty() {
		fLeft = fTop = fRight = fBottom = 0;
	}

	public int width() {
		return fRight - fLeft;
	}

	public int height() {
		return fBottom - fTop;
	}

	public boolean intersect(SkIRect r) {
		return intersect(r.fLeft, r.fTop, r.fRight, r.fBottom);
	}

	private boolean intersect(int left, int top, int right, int bottom) {
		if (left < right && top < bottom && !isEmpty() && fLeft < right
				&& left < fRight && fTop < bottom && top < fBottom) {
			if (fLeft < left)
				fLeft = left;
			if (fTop < top)
				fTop = top;
			if (fRight > right)
				fRight = right;
			if (fBottom > bottom)
				fBottom = bottom;
			return true;
		}
		return false;
	}

	public boolean isEmpty() {
		return fLeft >= fRight || fTop >= fBottom;
	}

	public static boolean Intersects(SkIRect a, SkIRect b) {
		return !a.isEmpty() && !b.isEmpty()
				&& // check for empties
				a.fLeft < b.fRight && b.fLeft < a.fRight && a.fTop < b.fBottom
				&& b.fTop < a.fBottom;
	}

	public void set(SkIRect bounds) {
		fLeft = bounds.fLeft;
		fTop = bounds.fTop;
		fRight = bounds.fRight;
		fBottom = bounds.fBottom;
	}

	public boolean intersect(SkIRect a, SkIRect b) {
		if (!a.isEmpty() && !b.isEmpty() && a.fLeft < b.fRight
				&& b.fLeft < a.fRight && a.fTop < b.fBottom
				&& b.fTop < a.fBottom) {
			fLeft = Math.max(a.fLeft, b.fLeft);
			fTop = Math.max(a.fTop, b.fTop);
			fRight = Math.max(a.fRight, b.fRight);
			fBottom = Math.max(a.fBottom, b.fBottom);
			return true;
		}
		return false;
	}

	public boolean contains(int left, int top, int right, int bottom) {
        return  left < right && top < bottom && isEmpty() && // check for empties
                fLeft <= left && fTop <= top &&
                fRight >= right && fBottom >= bottom;
    }

	public boolean contains(SkIRect r) {
		return !r.isEmpty() && !isEmpty()
				&& // check for empties
				fLeft <= r.fLeft && fTop <= r.fTop && fRight >= r.fRight
				&& fBottom >= r.fBottom;
	}
}
