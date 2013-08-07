package com.lonerr.skia.core;

import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class SkPath {
	private static final float CUBIC_ARC_FACTOR = ((SkScalar.SK_ScalarSqrt2 - SkScalar.SK_Scalar1) * 4 / 3);
	private int fFillType;
	private GeneralPath fPath = new GeneralPath();

	private float mLastX = 0;
	private float mLastY = 0;

	public SkPath() {
	}

	public SkPath(final SkPath src) {
		setFillType(src.fFillType);
		fPath.append(src.fPath, false);
	}

	public enum Direction {
		/** clockwise direction for adding closed contours */
		kCW_Direction,
		/** counter-clockwise direction for adding closed contours */
		kCCW_Direction
	};

	public void addRect(SkRect rect, Direction dir) {
		addRect(rect.fLeft, rect.fTop, rect.fRight, rect.fBottom, dir);
	}

	public void addPath(final SkPath path, float dx, float dy) {
		SkMatrix matrix = new SkMatrix();
		matrix.setTranslate(dx, dy);
		addPath(path, matrix);
	}

	public void addPath(SkPath path, SkMatrix matrix) {
		AffineTransform transform = matrix.getAffineTransform();
		if (transform != null) {
			fPath.append(path.fPath.getPathIterator(transform), false);
		} else {
			fPath.append(fPath, false);
		}
	}

	public void close() {
		fPath.closePath();
	}

	public void addRect(float left, float top, float right, float bottom,
			Direction dir) {
		moveTo(left, top);
		if (dir == Direction.kCCW_Direction) {
			lineTo(left, bottom);
			lineTo(right, bottom);
			lineTo(right, top);
		} else {
			lineTo(right, top);
			lineTo(right, bottom);
			lineTo(left, bottom);
		}
		close();
		resetLastPointFromPath();
	}

	private void resetLastPointFromPath() {
		Point2D last = fPath.getCurrentPoint();
		mLastX = (float) last.getX();
		mLastY = (float) last.getY();
	}

	public void moveTo(float x, float y) {
		fPath.moveTo(mLastX = x, mLastY = y);
	}

	public void transform(SkMatrix matrix, SkPath dst) {
		if (!matrix.hasPerspective()) {
			GeneralPath newPath = new GeneralPath();
			PathIterator iterator = fPath.getPathIterator(matrix
					.getAffineTransform());
			newPath.append(iterator, false /* connect */);
			if (dst != null) {
				dst.fPath = newPath;
			} else {
				fPath = newPath;
			}
		}
	}

	public SkRect getBounds() {
		Rectangle2D rect = fPath.getBounds2D();
		SkRect bounds = new SkRect();
		bounds.set((float) rect.getMinX(), (float) rect.getMinY(),
				(float) rect.getMaxX(), (float) rect.getMaxY());
		return bounds;
	}

	public void reset() {
		fPath.reset();
	}

	public boolean isEmpty() {
		return fPath.getCurrentPoint() == null;
	}

	public boolean isInverseFillType() {
		return (fFillType & 2) != 0;
	}

	public void rewind() {
		fPath.reset();
	}

	public int getFillType() {
		return fFillType;
	}

	public void setFillType(int ft) {
		fFillType = ft;
		fPath.setWindingRule(getWindingRule(ft));
	}

	private int getWindingRule(int ft) {
		switch (ft) {
		case 0:
		case 2:
			return GeneralPath.WIND_NON_ZERO;
		case 1:
		case 3:
			return GeneralPath.WIND_EVEN_ODD;
		}
		return 0;
	}

	public boolean isRect(SkRect rect) {
		Area area = new Area(fPath);
		if (area.isRectangular()) {
			if (rect != null) {
				rect = getBounds();
			}
			return true;
		}
		return false;
	}

	public void incReserve(int inc) {
	}

	public void rMoveTo(float x, float y) {
		x += mLastX;
		y += mLastY;
		fPath.moveTo(mLastX = x, mLastY = y);
	}

	public void rLineTo(float x, float y) {
		if (isEmpty()) {
			fPath.moveTo(mLastX = 0, mLastY = 0);
		}
		x += mLastX;
		y += mLastY;
		fPath.lineTo(mLastX = x, mLastY = y);
	}

	public void quadTo(float x1, float y1, float x2, float y2) {
		fPath.quadTo(x1, y1, mLastX = x2, mLastY = y2);
	}

	public void rQuadTo(float x1, float y1, float x2, float y2) {
		if (isEmpty()) {
			fPath.moveTo(mLastX = 0, mLastY = 0);
		}
		x1 += mLastX;
		y1 += mLastY;
		x2 += mLastX;
		y2 += mLastY;
		fPath.quadTo(x1, y1, mLastX = x2, mLastY = y2);
	}

	public void cubicTo(float x1, float y1, float x2, float y2, float x3,
			float y3) {
		fPath.curveTo(x1, y1, x2, y2, mLastX = x3, mLastY = y3);
	}

	public void rCubicTo(float x1, float y1, float x2, float y2, float x3,
			float y3) {
		if (isEmpty()) {
			fPath.moveTo(mLastX = 0, mLastY = 0);
		}
		x1 += mLastX;
		y1 += mLastY;
		x2 += mLastX;
		y2 += mLastY;
		x3 += mLastX;
		y3 += mLastY;
		fPath.curveTo(x1, y1, x2, y2, mLastX = x3, mLastY = y3);
	}

	public void arcTo(SkRect oval, float startAngle, float sweepAngle,
			boolean forceMoveTo) {
		Arc2D arc = new Arc2D.Float(oval.fLeft, oval.fTop, oval.width(),
				oval.height(), -startAngle, -sweepAngle, Arc2D.OPEN);
		fPath.append(arc, true /* connect */);
		resetLastPointFromPath();
	}

	public void addOval(SkRect oval, Direction dir) {
		float cx = oval.centerX();
		float cy = oval.centerY();
		float rx = 0.5f * oval.width();
		float ry = 0.5f * oval.height();

		// float sx = rx * CUBIC_ARC_FACTOR;
		// float sy = ry * CUBIC_ARC_FACTOR;
		// moveTo(cx + rx, cy);
		// if (dir == Direction.kCCW_Direction) {
		// cubicTo(cx + rx, cy - sy, cx + sx, cy - ry, cx, cy - ry);
		// cubicTo(cx - sx, cy - ry, cx - rx, cy - sy, cx - rx, cy);
		// cubicTo(cx - rx, cy + sy, cx - sx, cy + ry, cx, cy + ry);
		// cubicTo(cx + sx, cy + ry, cx + rx, cy + sy, cx + rx, cy);
		// } else {
		// cubicTo(cx + rx, cy + sy, cx + sx, cy + ry, cx, cy + ry);
		// cubicTo(cx - sx, cy + ry, cx - rx, cy + sy, cx - rx, cy);
		// cubicTo(cx - rx, cy - sy, cx - sx, cy - ry, cx, cy - ry);
		// cubicTo(cx + sx, cy - ry, cx + rx, cy - sy, cx + rx, cy);
		// }

		float sx = rx * SkScalar.SK_ScalarTanPIOver8;
		float sy = ry * SkScalar.SK_ScalarTanPIOver8;
		float mx = rx * SkScalar.SK_ScalarRoot2Over2;
		float my = ry * SkScalar.SK_ScalarRoot2Over2;

		/*
		 * To handle imprecision in computing the center and radii, we revert to
		 * the provided bounds when we can (i.e. use oval.fLeft instead of
		 * cx-rx) to ensure that we don't exceed the oval's bounds *ever*, since
		 * we want to use oval for our fast-bounds, rather than have to
		 * recompute it.
		 */
		float L = oval.fLeft; // cx - rx
		float T = oval.fTop; // cy - ry
		float R = oval.fRight; // cx + rx
		float B = oval.fBottom; // cy + ry

		moveTo(R, cy);
		if (dir == Direction.kCCW_Direction) {
			quadTo(R, cy - sy, cx + mx, cy - my);
			quadTo(cx + sx, T, cx, T);
			quadTo(cx - sx, T, cx - mx, cy - my);
			quadTo(L, cy - sy, L, cy);
			quadTo(L, cy + sy, cx - mx, cy + my);
			quadTo(cx - sx, B, cx, B);
			quadTo(cx + sx, B, cx + mx, cy + my);
			quadTo(R, cy + sy, R, cy);
		} else {
			quadTo(R, cy + sy, cx + mx, cy + my);
			quadTo(cx + sx, B, cx, B);
			quadTo(cx - sx, B, cx - mx, cy + my);
			quadTo(L, cy + sy, L, cy);
			quadTo(L, cy - sy, cx - mx, cy - my);
			quadTo(cx - sx, T, cx, T);
			quadTo(cx + sx, T, cx + mx, cy - my);
			quadTo(R, cy - sy, R, cy);
		}
		close();
	}

	public void addCircle(float x, float y, float r, Direction dir) {
		if (r > 0) {
			SkRect rect = new SkRect();
			rect.set(x - r, y - r, x + r, y + r);
			addOval(rect, dir);
		}
	}

	public void addArc(SkRect oval, float startAngle, float sweepAngle) {
		fPath.append(
				new Arc2D.Float(oval.fLeft, oval.fTop, oval.width(), oval
						.height(), -startAngle, -sweepAngle, Arc2D.OPEN), false);
	}

	public void addRoundRect(SkRect rect, float rx, float ry, Direction dir) {
		float w = rect.width();
		float halfW = w * 0.5f;
		float h = rect.height();
		float halfH = h * 0.5f;

		if (halfW <= 0 || halfH <= 0) {
			return;
		}

		boolean skip_hori = rx >= halfW;
		boolean skip_vert = ry >= halfH;

		if (skip_hori && skip_vert) {
			addOval(rect, dir);
			return;
		}

		// SkAutoPathBoundsUpdate apbu(this, rect);

		if (skip_hori) {
			rx = halfW;
		} else if (skip_vert) {
			ry = halfH;
		}

		float sx = rx * CUBIC_ARC_FACTOR;
		float sy = ry * CUBIC_ARC_FACTOR;

		incReserve(17);
		moveTo(rect.fRight - rx, rect.fTop);
		if (dir == Direction.kCCW_Direction) {
			if (!skip_hori) {
				lineTo(rect.fLeft + rx, rect.fTop); // top
			}
			cubicTo(rect.fLeft + rx - sx, rect.fTop, rect.fLeft, rect.fTop + ry
					- sy, rect.fLeft, rect.fTop + ry); // top-left
			if (!skip_vert) {
				lineTo(rect.fLeft, rect.fBottom - ry); // left
			}
			cubicTo(rect.fLeft, rect.fBottom - ry + sy, rect.fLeft + rx - sx,
					rect.fBottom, rect.fLeft + rx, rect.fBottom); // bot-left
			if (!skip_hori) {
				lineTo(rect.fRight - rx, rect.fBottom); // bottom
			}
			cubicTo(rect.fRight - rx + sx, rect.fBottom, rect.fRight,
					rect.fBottom - ry + sy, rect.fRight, rect.fBottom - ry); // bot-right
			if (!skip_vert) {
				lineTo(rect.fRight, rect.fTop + ry);
			}
			cubicTo(rect.fRight, rect.fTop + ry - sy, rect.fRight - rx + sx,
					rect.fTop, rect.fRight - rx, rect.fTop); // top-right
		} else {
			cubicTo(rect.fRight - rx + sx, rect.fTop, rect.fRight, rect.fTop
					+ ry - sy, rect.fRight, rect.fTop + ry); // top-right
			if (!skip_vert) {
				lineTo(rect.fRight, rect.fBottom - ry);
			}
			cubicTo(rect.fRight, rect.fBottom - ry + sy, rect.fRight - rx + sx,
					rect.fBottom, rect.fRight - rx, rect.fBottom); // bot-right
			if (!skip_hori) {
				lineTo(rect.fLeft + rx, rect.fBottom); // bottom
			}
			cubicTo(rect.fLeft + rx - sx, rect.fBottom, rect.fLeft,
					rect.fBottom - ry + sy, rect.fLeft, rect.fBottom - ry); // bot-left
			if (!skip_vert) {
				lineTo(rect.fLeft, rect.fTop + ry); // left
			}
			cubicTo(rect.fLeft, rect.fTop + ry - sy, rect.fLeft + rx - sx,
					rect.fTop, rect.fLeft + rx, rect.fTop); // top-left
			if (!skip_hori) {
				lineTo(rect.fRight - rx, rect.fTop); // top
			}
		}
		close();
	}

	public void addRoundRect(SkRect rect, float[] rad, Direction dir) {
		if (rect.isEmpty()) {
			return;
		}

		// SkAutoPathBoundsUpdate apbu(this, rect);

		if (Direction.kCW_Direction == dir) {
			add_corner_arc(this, rect, rad[0], rad[1], 180, dir, true);
			add_corner_arc(this, rect, rad[2], rad[3], 270, dir, false);
			add_corner_arc(this, rect, rad[4], rad[5], 0, dir, false);
			add_corner_arc(this, rect, rad[6], rad[7], 90, dir, false);
		} else {
			add_corner_arc(this, rect, rad[0], rad[1], 180, dir, true);
			add_corner_arc(this, rect, rad[6], rad[7], 90, dir, false);
			add_corner_arc(this, rect, rad[4], rad[5], 0, dir, false);
			add_corner_arc(this, rect, rad[2], rad[3], 270, dir, false);
		}
		close();
	}

	private void add_corner_arc(SkPath path, SkRect rect, float rx, float ry,
			int startAngle, Direction dir, boolean forceMoveTo) {
		rx = Math.min(rect.width() * 0.5f, rx);
		ry = Math.min(rect.height() * 0.5f, ry);
		SkRect r = new SkRect();
		r.set(-rx, -ry, rx, ry);
		switch (startAngle) {
		case 0:
			r.offset(rect.fRight - r.fRight, rect.fBottom - r.fBottom);
			break;
		case 90:
			r.offset(rect.fLeft - r.fLeft, rect.fBottom - r.fBottom);
			break;
		case 180:
			r.offset(rect.fLeft - r.fLeft, rect.fTop - r.fTop);
			break;
		case 270:
			r.offset(rect.fRight - r.fRight, rect.fTop - r.fTop);
			break;
		}
		float start = startAngle;
		float sweep = 90;
		if (Direction.kCCW_Direction == dir) {
			start += sweep;
			sweep = -sweep;
		}
		path.arcTo(r, start, sweep, forceMoveTo);
	}

	public void addPath(SkPath src) {
		SkMatrix m = new SkMatrix();
		m.reset();
		addPath(src, m);
	}

	public void offset(float dx, float dy, SkPath dst) {
		GeneralPath newPath = new GeneralPath();
		PathIterator iterator = fPath.getPathIterator(new AffineTransform(0, 0,
				dx, 0, 0, dy));
		newPath.append(iterator, false);

		if (dst != null) {
			dst.fPath = newPath;
		} else {
			fPath = newPath;
		}
	}

	public void offset(float dx, float dy) {
		offset(dx, dy, this);
	}

	public void setLastPt(float x, float y) {
		mLastX = x;
		mLastY = y;
	}

	public void transform(SkMatrix matrix) {
		transform(matrix, this);
	}

	public void lineTo(float x, float y) {
		fPath.lineTo(mLastX = x, mLastY = y);
	}

	public GeneralPath getAWTPath() {
		return fPath;
	}

	public void setPathIterator(PathIterator pathIterator) {
		fPath.reset();
		fPath.append(pathIterator, false);
	}

	public void swap(SkPath path) {
		// TODO Auto-generated method stub
		
	}

}
