package com.lonerr.skia.core;

public class SkRefCnt {
	void SkRefCnt_SafeAssign(SkRefCnt dst, SkRefCnt src) {
//		if (src != null)
//			src.ref();
//		if (dst != null)
//			dst.unref();
		dst = src;
	}

	void SkSafeUnref(SkRefCnt refCnt) {
		// TODO Auto-generated method stub

	}

	void unref() {
	}

	void ref() {
	}
}
