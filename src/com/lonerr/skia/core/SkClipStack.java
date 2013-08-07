package com.lonerr.skia.core;

import com.lonerr.skia.core.SkRegion.Op;

public class SkClipStack {
	private static class Rec {
		enum State {
			kEmpty_State, kRect_State, kPath_State
		};

		private SkPath fPath;
		private SkRect fRect;
		private int fSaveCount;
		private SkRegion.Op fOp;
		private State fState;
		private boolean fDoAA;

		public Rec(int saveCount, SkRect rect, Op op, boolean doAA) {
			fSaveCount = saveCount;
	        fOp = op;
	        fState = State.kRect_State;
	        fDoAA = doAA;
		}

		public boolean canBeIntersected(int saveCount, Op op) {
			if (State.kEmpty_State == fState && (
                    SkRegion.Op.kDifference_Op == op ||
                    SkRegion.Op.kIntersect_Op == op)) {
            return true;
        }
        return  fSaveCount == saveCount &&
 SkRegion.Op.kIntersect_Op == fOp &&
                SkRegion.Op.kIntersect_Op == op;
		}
	}

	private SkDeque<Rec> fDeque = new SkDeque<SkClipStack.Rec>();
	private int fSaveCount;

	public void clipDevRect(SkIRect r, Op kintersectOp) {
		// TODO Auto-generated method stub

	}

	public void reset() {
		fDeque.clear();
		fSaveCount = 0;
	}

	public void clipDevRect(SkRect rect, Op op, boolean doAA) {
		Rec rec = fDeque.back();
		if (rec != null && rec.canBeIntersected(fSaveCount, op)) {
			switch (rec.fState) {
			case kEmpty_State:
				return;
			case kRect_State:
				if (!rec.fRect.intersect(rect)) {
					rec.fState = Rec.State.kEmpty_State;
				}
				return;
			case kPath_State:
				if (!SkRect.Intersects(rec.fPath.getBounds(), rect)) {
					rec.fState = Rec.State.kEmpty_State;
					return;
				}
				break;
			default:
				break;
			}
		}
		fDeque.push_back(new Rec(fSaveCount, rect, op, doAA));
	}

	public void clipDevPath(SkPath devPath, Op op, boolean doAA) {
		// TODO Auto-generated method stub

	}

	public void restore() {
		fSaveCount -= 1;
	    while (!fDeque.empty()) {
	        Rec rec = fDeque.back();
	        if (rec.fSaveCount <= fSaveCount) {
	            break;
	        }
	        fDeque.pop_back();
	    }
	}

	public void save() {
		fSaveCount += 1;		
	}

	public void clipDevRect(SkIRect bounds) {
		// TODO Auto-generated method stub
		
	}

}
