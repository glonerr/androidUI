package com.lonerr.skia.core;


public class SkDrawFilter extends SkRefCnt {
	enum Type {
        kPaint_Type,
        kPoint_Type,
        kLine_Type,
        kBitmap_Type,
        kRect_Type,
        kPath_Type,
        kText_Type
    };
	public void safeRef() {
		// TODO Auto-generated method stub
		
	}

	public static SkDrawFilter getSkDrawFilter(int nativeFilter) {
		// TODO Auto-generated method stub
		return null;
	}

	public void restore(SkCanvas fCanvas, SkPaint fPaint, Type fType) {
		// TODO Auto-generated method stub
		
	}

	public boolean filter(SkCanvas fCanvas, SkPaint fPaint, Type fType) {
		// TODO Auto-generated method stub
		return false;
	}

}
