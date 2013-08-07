package com.lonerr.skia.core;

public class SkRect {
	public float fLeft, fTop, fRight, fBottom;

	public void set(float left, float top, float right, float bottom) {
		fLeft = left;
		fTop = top;
		fRight = right;
		fBottom = bottom;
	}

	public void setEmpty() {
		fLeft = fTop = fRight = fBottom = 0;
	}

	public void iset(int left, int top, int right, int bottom) {
		fLeft = left;
		fTop = top;
		fRight = right;
		fBottom = bottom;
	}

	public boolean isEmpty() {
		 return fLeft >= fRight || fTop >= fBottom; 
	}

	public void roundOut(SkIRect dst) {
		dst.set((int) Math.floor(fLeft), (int) Math.floor(fTop),
				(int) Math.ceil(fRight), (int) Math.ceil(fBottom));
	}

	public void sort() {
		if (fLeft > fRight) {
			float tmp = fRight;
			fRight = fLeft;
			fLeft = tmp;
		}
		if (fTop > fBottom) {
			float tmp = fRight;
			fBottom = fTop;
			fTop = tmp;
		}
	}

	public void toQuad(SkPoint[] quad) {
		quad[0] = new SkPoint();
		quad[0].set(fLeft, fTop);
		quad[1] = new SkPoint();
		quad[1].set(fRight, fTop);
		quad[2] = new SkPoint();
		quad[2].set(fRight, fBottom);
		quad[3] = new SkPoint();
		quad[3].set(fLeft, fBottom);
	}

	@Override
	public String toString() {
		return "SkRect:[" + fLeft + "," + fTop + "-" + fRight + "," + fBottom
				+ "]";
	}

	public void set(SkPoint[] quad, int count) {
		float l = quad[0].fX, t = quad[0].fY, r = quad[0].fX, b = quad[0].fY;
		for (int i = 1; i < count; i++) {
			if (quad[i].fX < l)
				l = quad[i].fX;
			else if (quad[i].fX > r)
				r = quad[i].fX;
			if (quad[i].fY < t)
				t = quad[i].fY;
			else if (quad[i].fY > b)
				b = quad[i].fY;
		}
		fLeft = l;
		fRight = r;
		fTop = t;
		fBottom = b;
	}

	public float width() {
		return fRight - fLeft;
	}

	public float height() {
		return fBottom - fTop;
	}

	public boolean isFinite() {
		float value = fLeft * 0 + fTop * 0 + fRight * 0 + fBottom * 0;
		// value is either NaN or it is finite (zero).
		// value==value will be true iff value is not NaN
		return value == value;
	}

	public static boolean Intersects(SkRect a, SkRect b) {
		return !a.isEmpty() && !b.isEmpty()
				&& // check for empties
				a.fLeft < b.fRight && b.fLeft < a.fRight && a.fTop < b.fBottom
				&& b.fTop < a.fBottom;
	}

	public void toSkPoint2(SkPoint[] srcP) {
		srcP[0] = new SkPoint();
		srcP[0].fX = fLeft;
		srcP[0].fY = fTop;
		srcP[1] = new SkPoint();
		srcP[1].fX = fRight;
		srcP[1].fY = fBottom;
	}

	public void round(SkIRect ir) {
		ir.fLeft = SkMath.sk_float_round2int(fLeft);
		ir.fTop = SkMath.sk_float_round2int(fTop);
		ir.fRight = SkMath.sk_float_round2int(fRight);
		ir.fBottom = SkMath.sk_float_round2int(fBottom);
	}

	public boolean intersect(SkRect r) {
		return intersect(r.fLeft, r.fTop, r.fRight, r.fBottom);
	}

	private boolean intersect(float left, float top, float right, float bottom) {
		if (left < right && top < bottom && !isEmpty()
				&& // check for empties
				fLeft < right && left < fRight && fTop < bottom
				&& top < fBottom) {
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

	public void set(SkIRect src) {
		fLeft = src.fLeft;
		fTop = src.fTop;
		fRight = src.fRight;
		fBottom = src.fBottom;
	}

	public boolean intersect(SkRect a, SkRect b) {
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

	public boolean contains(SkRect r) {
		return !r.isEmpty() && !isEmpty() && fLeft <= r.fLeft && fTop <= r.fTop
				&& fRight >= r.fRight && fBottom >= r.fBottom;
	}

	public void set(SkRect src) {
		fLeft = src.fLeft;
		fTop = src.fTop;
		fRight = src.fRight;
		fBottom = src.fBottom;
	}

	public float centerX() {
		return 0.5f * (fLeft + fRight);
	}

	public float centerY() {
		return 0.5f * (fTop + fBottom);
	}

	public void set(float[] pts, int count) {
		if (count > 0) {
			float l, t, r, b;
			l = r = pts[0];
			t = b = pts[1];
			for (int i = 1; i < count; i++) {
				int index = i << 1;
				float x = pts[index];
				float y = pts[index + 1];
				if (x < l)
					l = x;
				else if (x > r)
					r = x;
				if (y < t)
					t = y;
				else if (y > b)
					b = y;
			}
			set(l, t, r, b);
		}
	}

	public void offset(float dx, float dy) {
		fLeft += dx;
		fTop += dy;
		fRight += dx;
		fBottom += dy;
	}

}
