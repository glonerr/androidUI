package com.lonerr.skia.core;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import android.graphics.Region;

public class SkRegion {
	public Area fArea = new Area();

	public enum Op {
		kDifference_Op, // !< subtract the op region from the first region
		kIntersect_Op, // !< intersect the two regions
		kUnion_Op, // !< union (inclusive-or) the two regions
		kXOR_Op, // !< exclusive-or the two regions
		/** subtract the first region from the op region */
		kReverseDifference_Op, kReplace_Op // !< replace the dst region with the
											// op region
	};

	public SkRegion(SkRegion src) {
		setRegion(src);
	}

	public SkRegion(SkIRect rect) {
		setRect(rect);
	}

	public SkRegion() {
	}

	/**
	 * Combines two {@link Shape} into another one (actually an {@link Area}),
	 * according to the given {@link Region.Op}.
	 * 
	 * If the Op is not one that combines two shapes, then this return null
	 * 
	 * @param rgnaOrig
	 *            the firt shape to combine which can be null if there's no
	 *            original clip.
	 * @param rgnbOrig
	 *            the 2nd shape to combine
	 * @param op
	 *            the operande for the combine
	 * @return a new area or null.
	 */
	private boolean op(Area rgnaOrig, Area rgnbOrig, Op op) {
		Area result = new Area(op==Op.kReverseDifference_Op||op==Op.kReplace_Op?rgnbOrig:rgnaOrig);
		switch (op) {
		case kDifference_Op:
			result.subtract(rgnbOrig);
			break;
		case kIntersect_Op:
			result.intersect(rgnbOrig);
			break;
		case kUnion_Op:
			result.add(rgnbOrig);
			break;
		case kXOR_Op:
			result.exclusiveOr(rgnbOrig);
			break;
		case kReverseDifference_Op:
			result.subtract(rgnaOrig);
			break;
		default:
			break;
		}
		fArea = result;
		return !fArea.isEmpty();
	}

	public boolean setRegion(SkRegion src) {
		if (this != src) {
			fArea.reset();
			fArea.add(src.fArea);
		}
		return !isEmpty();
	}

	public void translate(int dx, int dy, SkRegion dst) {
		if (dst == null) {
			return;
		}
		if (isEmpty()) {
			dst.setEmpty();
		} else {
			dst.fArea = new Area(fArea);
			AffineTransform mtx = new AffineTransform();
			mtx.translate(dx, dy);
			dst.fArea.transform(mtx);
		}
	}

	public void translate(int fX, int fY) {
		translate(fX, fY, this);
	}

	public boolean isEmpty() {
		return fArea.isEmpty();
	}

	public SkIRect getBounds() {
		Rectangle rectangle = fArea.getBounds();
		SkIRect bounds = new SkIRect();
		bounds.fLeft = rectangle.x;
		bounds.fTop = rectangle.y;
		bounds.fRight = rectangle.x + rectangle.width;
		bounds.fBottom = rectangle.y + rectangle.height;
		return bounds;
	}

	public boolean setRect(SkIRect r) {
		return setRect(r.fLeft, r.fTop, r.fRight, r.fBottom);
	}

	public boolean setEmpty() {
		fArea.reset();
		return false;
	}
	
	public boolean op(int left, int top, int right, int bottom, Op op) {
        SkIRect rect = new SkIRect();
        rect.set(left, top, right, bottom);
        return op(this, rect, op);
	}

	public boolean op(SkIRect rect, Op op) {
		return op(this,rect,op);
	}
	
	public boolean op(SkRegion rgn, Op op) {
		return op(this.fArea,rgn.fArea,op);
	}

	public boolean op(SkRegion rgn, SkIRect rect, Op op) {
		Area tmp = new Area(new Rectangle2D.Float(rect.fLeft, rect.fTop, rect.width(), rect.height()));
	    return op(rgn.fArea, tmp, op);
	}

	public boolean op(SkRegion rgnaOrig, SkRegion rgnbOrig, Op op) {
		return op(rgnaOrig.fArea, rgnbOrig.fArea, op);
	}

	public boolean op(SkIRect rect, SkRegion rgn, Op op) {
		Area tmp = new Area(new Rectangle2D.Float(rect.fLeft, rect.fTop, rect.width(), rect.height()));
	    return op(tmp, rgn.fArea, op);
	}
	
	public boolean setPath(SkPath path, SkRegion clip) {
		if (path == null)
			return true;
		Area tmp = clip.fArea;
		fArea = new Area(path.getAWTPath());
		fArea.intersect(tmp);
		return fArea.getBounds().isEmpty() == false;
	}

	public boolean setRect(int left, int top, int right, int bottom) {
		if (left >= right || top >= bottom) {
			return setEmpty();
		}
		fArea = new Area(new Rectangle2D.Float(left, top, right - left, bottom
				- top));
		return fArea.getBounds().isEmpty() == false;
	}

	public boolean isRect() {
		return fArea.isRectangular();
	}

	public boolean isComplex() {
		return !isEmpty() && !isRect(); 
	}

	public boolean contains(int x, int y) {
		return fArea.contains(x, y);
	}

	public boolean quickContains(int left, int top, int right, int bottom) {
		Rectangle fBounds = fArea.getBounds();
		return left < right && top < bottom && isRect()
				&& fBounds.x <= left && fBounds.y <= top &&
	               fBounds.x+fBounds.width >= right && fBounds.y+fBounds.height >= bottom;
	}

	public boolean quickReject(SkIRect rect) {
		return isEmpty() || rect.isEmpty()
				|| !SkIRect.Intersects(getBounds(), rect);
	}

	public boolean quickReject(SkRegion rgn) {
		return isEmpty() || rgn.isEmpty()
				|| !SkIRect.Intersects(getBounds(), rgn.getBounds());
	}

	public boolean getBoundaryPath(SkPath path) {
		if (fArea.isEmpty()) {
            path.reset();
            return false;
        }
        path.setPathIterator(fArea.getPathIterator(new AffineTransform()));
        return true;
	}

	public void copyTo(SkRegion fBW) {
		fBW.fArea = new Area(fArea);
	}
}
