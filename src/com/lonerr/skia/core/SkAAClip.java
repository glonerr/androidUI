package com.lonerr.skia.core;

import com.lonerr.skia.core.SkRegion.Op;

public class SkAAClip {
	private SkIRect fBounds = new SkIRect();

	public SkIRect getBounds() {
		return fBounds;
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean setRegion(final SkRegion rgn) {
		if (rgn.isEmpty()) {
			return setEmpty();
		}
		if (rgn.isRect()) {
			return setRect(rgn.getBounds());
		}
		final SkIRect bounds = rgn.getBounds();
		final int offsetX = bounds.fLeft;
		final int offsetY = bounds.fTop;

		return true;
	}

	private boolean setRect(SkIRect bounds) {
		if (bounds.isEmpty()) {
			return setEmpty();
		}
		fBounds.set(bounds.fLeft, bounds.fTop, bounds.fRight, bounds.fBottom);
		return true;
	}

	public boolean setEmpty() {
		fBounds.setEmpty();
		return false;
	}

	public boolean op(SkRect rOrig, Op op, boolean doAA) {
		SkRect rStorage = new SkRect(), boundsStorage = new SkRect();
		SkRect r = rOrig;
		boundsStorage.set(fBounds);
		switch (op) {
		case kIntersect_Op:
		case kDifference_Op:
			if (!rStorage.intersect(rOrig, boundsStorage)) {
				return setEmpty();
			}
			r = rStorage; // use the intersected bounds
			break;
		case kUnion_Op:
			if (rOrig.contains(boundsStorage)) {
				return setRect(rOrig, true);
			}
			break;
		default:
			break;
		}

		SkAAClip clip = new SkAAClip();
		clip.setRect(r, doAA);
		return op(this, clip, op);
	}

	private boolean op(SkAAClip skAAClip, SkAAClip clip, Op op) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean setRect(SkRect r, boolean doAA) {
		if (r.isEmpty()) {
			return setEmpty();
		}

		SkPath path = new SkPath();
		path.addRect(r, SkPath.Direction.kCW_Direction);
		return setPath(path, null, doAA);
	}

	boolean setPath(SkPath path, SkRegion clip, boolean doAA) {
		return true;
	}

	public void copyTo(SkAAClip fAA) {
		// TODO Auto-generated method stub

	}

	public boolean op(SkAAClip tmp, Op op) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean op(SkIRect rOrig, Op op) {
		SkIRect rStorage = new SkIRect();
		SkIRect r = rOrig;

		switch (op) {
		case kIntersect_Op:
			if (!rStorage.intersect(rOrig, fBounds)) {
				// no overlap, so we're empty
				return setEmpty();
			}
			if (rStorage == fBounds) {
				// we were wholly inside the rect, no change
				return !isEmpty();
			}
			if (quickContains(rStorage)) {
				// the intersection is wholly inside us, we're a rect
				return setRect(rStorage);
			}
			r = rStorage; // use the intersected bounds
			break;
		case kDifference_Op:
			break;
		case kUnion_Op:
			if (rOrig.contains(fBounds)) {
				return setRect(rOrig);
			}
			break;
		default:
			break;
		}
		SkAAClip clip = new SkAAClip();
		clip.setRect(r);
		return op(this, clip, op);
	}

	public boolean quickContains(SkIRect r) {
		return quickContains(r.fLeft, r.fTop, r.fRight, r.fBottom);
	}

	private boolean quickContains(int left, int top, int right, int bottom) {
		return false;
	}

}
