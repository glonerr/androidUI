package com.lonerr.bridge.util;

import java.util.HashMap;

import org.xmlpull.v1.XmlPullParserException;

import com.lonerr.androidfw.ResXMLParser;
import com.lonerr.androidfw.ResXMLTree;
import com.lonerr.androidfw.Res_value;
import com.lonerr.utils.Errors;

public class XmlBlockBridge {
	private static HashMap<Integer, ResXMLTree> resXMLTreePool = new HashMap<Integer, ResXMLTree>();
	private static HashMap<Integer, ResXMLParser> resXMLParserPool = new HashMap<Integer, ResXMLParser>();

	public static final int nativeCreate(byte[] bArray, int off, int len) {
		if (bArray == null) {
			throw new NullPointerException();
			// return 0;
		}
		int bLen = bArray.length;
		if (off < 0 || off >= bLen || len < 0 || len > bLen || (off + len) > bLen) {
			throw new IndexOutOfBoundsException();
			// return 0;
		}
		ResXMLTree osb = new ResXMLTree(bArray, off, len, true);
		// env->ReleaseByteArrayElements(bArray, b, 0);

		if (osb == null || osb.getError() != Errors.NO_ERROR) {
			throw new IllegalArgumentException();
			// return 0;
		}

		int result = osb.hashCode();
		resXMLTreePool.put(result, osb);
		return result;
	}

	public static final int nativeGetStringBlock(int token) {
		ResXMLTree osb = resXMLTreePool.get(token);
		if (osb == null) {
			throw new NullPointerException();
			// return 0;
		}

		return osb.getStrings().hashCode();
	}

	public static final int nativeCreateParseState(int token) {
		ResXMLTree osb = resXMLTreePool.get(token);
		if (osb == null) {
			throw new NullPointerException();
			// return 0;
		}

		ResXMLParser st = new ResXMLParser(osb);
		// if (st == null) {
		// jniThrowException(env, "java/lang/OutOfMemoryError", NULL);
		// return 0;
		// }

		st.restart();

		int result = st.hashCode();
		resXMLParserPool.put(result, st);
		return result;
	}

	/* package */public static final int nativeNext(int token) throws XmlPullParserException {
		ResXMLParser st = resXMLParserPool.get(token);
		if (st == null) {
			return ResXMLParser.END_DOCUMENT;
		}

		do {
			int code = st.next();
			switch (code) {
			case ResXMLParser.START_TAG:
				return 2;
			case ResXMLParser.END_TAG:
				return 3;
			case ResXMLParser.TEXT:
				return 4;
			case ResXMLParser.START_DOCUMENT:
				return 0;
			case ResXMLParser.END_DOCUMENT:
				return 1;
			case ResXMLParser.BAD_DOCUMENT:
				throw new XmlPullParserException("Corrupt XML binary file");
			}
		} while (true);
	}

	public static final int nativeGetNamespace(int token) {
		ResXMLParser st = resXMLParserPool.get(token);
		if (st == null) {
			return -1;
		}

		return st.getElementNamespaceID();
	}

	/* package */public static final int nativeGetName(int token) {
		ResXMLParser st = resXMLParserPool.get(token);
		if (st == null) {
			return -1;
		}

		return st.getElementNameID();
	}

	public static final int nativeGetText(int token) {
		ResXMLParser st = resXMLParserPool.get(token);
		if (st == null) {
			return -1;
		}

		return st.getTextID();
	}

	public static final int nativeGetLineNumber(int token) {
		ResXMLParser st = resXMLParserPool.get(token);
		if (st == null) {
			throw new NullPointerException();
			// return 0;
		}

		return st.getLineNumber();
	}

	public static final int nativeGetAttributeCount(int token) {
		ResXMLParser st = resXMLParserPool.get(token);
		if (st == null) {
			throw new NullPointerException();
			// return 0;
		}

		return st.getAttributeCount();
	}

	public static final int nativeGetAttributeNamespace(int token, int idx) {
		ResXMLParser st = resXMLParserPool.get(token);
		if (st == null) {
			throw new NullPointerException();
			// return 0;
		}

		return st.getAttributeNamespaceID(idx);
	}

	public static final int nativeGetAttributeName(int token, int idx) {
		ResXMLParser st = resXMLParserPool.get(token);
		if (st == null) {
			throw new NullPointerException();
			// return 0;
		}
		return st.getAttributeNameID(idx);
	}

	public static final int nativeGetAttributeResource(int token, int idx) {
		ResXMLParser st = resXMLParserPool.get(token);
		if (st == null) {
			throw new NullPointerException();
			// return 0;
		}
		return st.getAttributeNameResID(idx);
	}

	public static final int nativeGetAttributeDataType(int token, int idx) {
		ResXMLParser st = resXMLParserPool.get(token);
		if (st == null) {
			throw new NullPointerException();
			// return 0;
		}
		return st.getAttributeDataType(idx);
	}

	public static final int nativeGetAttributeData(int token, int idx) {
		ResXMLParser st = resXMLParserPool.get(token);
		if (st == null) {
			throw new NullPointerException();
			// return 0;
		}
		return st.getAttributeData(idx);
	}

	public static final int nativeGetAttributeStringValue(int token, int idx) {
		ResXMLParser st = resXMLParserPool.get(token);
		if (st == null) {
			throw new NullPointerException();
			// return 0;
		}
		return st.getAttributeValueStringID(idx);
	}

	public static final int nativeGetIdAttribute(int token) {
		ResXMLParser st = resXMLParserPool.get(token);
		if (st == null) {
			throw new NullPointerException();
			// return 0;
		}

		int idx = st.indexOfID();
		return idx >= 0 ? st.getAttributeValueStringID(idx) : -1;
	}

	public static final int nativeGetClassAttribute(int token) {
		ResXMLParser st = resXMLParserPool.get(token);
		if (st == null) {
			throw new NullPointerException();
			// return 0;
		}

		int idx = st.indexOfClass();
		return idx >= 0 ? st.getAttributeValueStringID(idx) : -1;
	}

	public static final int nativeGetStyleAttribute(int token) {
		ResXMLParser st = resXMLParserPool.get(token);
		if (st == null) {
			throw new NullPointerException();
			// return 0;
		}

		int idx = st.indexOfStyle();
		if (idx < 0) {
			return 0;
		}

		Res_value value = new Res_value();
		if (st.getAttributeValue(idx, value) < 0) {
			return 0;
		}

		return value.dataType == Res_value.TYPE_REFERENCE || value.dataType == Res_value.TYPE_ATTRIBUTE ? value.data
				: 0;
	}

	public static final int nativeGetAttributeIndex(int token, String ns, String name) {
		ResXMLParser st = resXMLParserPool.get(token);
		if (st == null || name == null) {
			throw new NullPointerException();
			// return 0;
		}

		int idx = st.indexOfAttribute(ns, name);

		return idx;
	}

	public static final void nativeDestroyParseState(int token) {
		ResXMLParser st = resXMLParserPool.remove(token);
		if (st == null) {
			throw new NullPointerException();
			// return;
		}
		// delete st;
	}

	public static final void nativeDestroy(int token) {
		ResXMLTree osb = resXMLTreePool.remove(token);
		if (osb == null) {
			throw new NullPointerException();
			// return;
		}

		// delete osb;
	}
}
