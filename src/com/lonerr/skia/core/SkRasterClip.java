package com.lonerr.skia.core;

import com.lonerr.skia.core.SkRegion.Op;

public class SkRasterClip {

	private static final float SK_Scalar1 = 1.0f;
	SkRegion fBW = new SkRegion();
	private SkAAClip fAA = new SkAAClip();
	private boolean fIsBW;
	
	public SkRasterClip() {
	    fIsBW = true;
	}

	public SkRasterClip(SkRasterClip src) {
	    fIsBW = src.fIsBW;
	    if (fIsBW) {
	        src.fBW.copyTo(fBW);
	    } else {
	        src.fAA.copyTo(fAA);
	    }
	}

	public SkRasterClip(SkIRect bounds){
		fBW = new SkRegion(bounds);
	    fIsBW = true;
	}

	public boolean isEmpty() {
		return fIsBW ? fBW.isEmpty() : fAA.isEmpty();
	}

	public SkIRect getBounds() {
		return fIsBW ? fBW.getBounds() : fAA.getBounds();
	}

	public boolean setEmpty() {
		fIsBW = true;
		fBW.setEmpty();
		fAA.setEmpty();
		return false;
	}

	public boolean op(SkIRect rect, Op op) {
		return fIsBW ? fBW.op(rect, op) : fAA.op(rect, op);
	}

	public boolean setRect(SkIRect rect) {
		fIsBW = true;
		fAA.setEmpty();
		return fBW.setRect(rect);
	}

	public boolean op(SkRect r, Op op, boolean doAA) {
		if (doAA) {
			// check that the rect really needs aa
			if (is_integral(r.fLeft) && is_integral(r.fTop)
					&& is_integral(r.fRight) && is_integral(r.fBottom)) {
				doAA = false;
			}
		}

		if (fIsBW && !doAA) {
			SkIRect ir = new SkIRect();
			r.round(ir);
			return fBW.op(ir, op);
		} else {
			if (fIsBW) {
				convertToAA();
			}
			return fAA.op(r, op, doAA);
		}
	}

	private void convertToAA() {
		fAA.setRegion(fBW);
		fIsBW = false;
	}

	private boolean is_integral(float x) {
		int ix = SkMath.sk_float_round2int(x);
		return Math.abs(ix - x) < (SK_Scalar1 / 16);
	}

	public boolean isRect() {
		return fIsBW ? fBW.isRect() : false;
	}

	public boolean setPath(SkPath path, SkRasterClip clip, boolean doAA) {
		if (clip.isBW()) {
			return setPath(path, clip.bwRgn(), doAA);
		} else {
			SkRegion tmp = new SkRegion();
			tmp.setRect(clip.getBounds());
			if (!setPath(path, clip, doAA)) {
				return false;
			}
			return op(clip, SkRegion.Op.kIntersect_Op);
		}
	}

	private SkRegion bwRgn() {
		return fBW;
	}

	private boolean isBW() {
		return fIsBW;
	}

	public boolean setPath(SkPath path, SkRegion clip, boolean doAA) {
		if (isBW() && !doAA) {
			return fBW.setPath(path, clip);
		} else {
			// TODO: since we are going to over-write fAA completely (aren't
			// we?)
			// we should just clear our BW data (if any) and set fIsAA=true
			if (isBW()) {
				convertToAA();
			}
			return fAA.setPath(path, clip, doAA);
		}
	}

	public boolean op(SkRasterClip clip, Op op) {
		 if (isBW() && clip.isBW()) {
		        return fBW.op(clip.fBW, op);
		    } else {
		        SkAAClip tmp = new SkAAClip();
		        SkAAClip other;

		        if (isBW()) {
		            convertToAA();
		        }
		        if (clip.isBW()) {
		            tmp.setRegion(clip.bwRgn());
		            other = tmp;
		        } else {
		            other = clip.aaRgn();
		        }
		        return fAA.op(other, op);
		    }
	}

	private SkAAClip aaRgn() {
		return fAA;
	}

	public void copyTo(SkRasterClip fRasterClip) {
		fRasterClip.fIsBW = fIsBW;
		fBW.copyTo(fRasterClip.fBW);
		fAA.copyTo(fRasterClip.fAA);
	}

	public boolean op(SkRegion rgn, Op op) {
		if (fIsBW) {
			return fBW.op(rgn, op);
		} else {
			SkAAClip tmp = new SkAAClip();
			tmp.setRegion(rgn);
			return fAA.op(tmp, op);
		}
	}

}
