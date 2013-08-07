package com.lonerr.bridge.util;

import java.util.HashMap;

import com.lonerr.androidfw.ResStringPool;
import com.lonerr.androidfw.ResStringPool_span;
import com.lonerr.utils.Errors;

public class StringBlockBridge {
	private static HashMap<Integer, ResStringPool> resStringPoolPool = new HashMap<Integer, ResStringPool>();

	public static int nativeCreate(byte[] bArray, int off, int len) {
		int bLen = bArray.length;
		if (off < 0 || off >= bLen || len < 0 || len > bLen || (off + len) > bLen) {
			throw new IndexOutOfBoundsException();
		}
		ResStringPool osb = new ResStringPool(bArray, off, len, true);
		if (osb == null || osb.getError() != Errors.NO_ERROR) {
			throw new IllegalArgumentException();
		}

		int result = osb.hashCode();
		resStringPoolPool.put(result, osb);
		return result;
	}

	public static int nativeGetSize(int token) {
		ResStringPool osb = resStringPoolPool.get(token);
		if (osb == null) {
			throw new NullPointerException();
		}

		return osb.size();
	}

	public static String nativeGetString(int token, int idx) {
		ResStringPool osb = resStringPoolPool.get(token);
		if (osb == null) {
			throw new NullPointerException();
		}

		Integer len = new Integer(0);
		String str8 = osb.string8At(idx, len);
		if (str8 != null) {
			return str8;
		}

		String str = osb.stringAt(idx, len);
		if (str == null) {
			throw new IndexOutOfBoundsException();
		}

		return str;
	}

	public static int[] nativeGetStyle(int token, int idx) {
		ResStringPool osb = resStringPoolPool.get(token);
		if (osb == null) {
			throw new NullPointerException();
		}

	    ResStringPool_span spans = osb.styleAt(idx);
	    if (spans == null) {
	        return null;
	    }

//	    ResStringPool_span pos = spans;
//	    int num = 0;
//	    while (pos.name != ResStringPool_span.END) {
//	        num++;
//	        pos++;
//	    }
//
//	    if (num == 0) {
//	        return null;
//	    }
//
//	    num = 0;
//	    static const int numInts = sizeof(ResStringPool_span)/sizeof(jint);
//	    while (spans->name.index != ResStringPool_span::END) {
//	        env->SetIntArrayRegion(array,
//	                                  num*numInts, numInts,
//	                                  (jint*)spans);
//	        spans++;
//	        num++;
//	    }

	    return null;
	}

	public static void nativeDestroy(int token) {
		ResStringPool osb = resStringPoolPool.remove(token);
	    if (osb == null) {
	        throw new NullPointerException();
	    }
	}

}
