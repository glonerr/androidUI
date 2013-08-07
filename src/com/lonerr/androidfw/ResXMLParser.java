package com.lonerr.androidfw;

import com.lonerr.utils.Errors;

public class ResXMLParser {
	public static final int RES_NULL_TYPE = 0x0000;
	public static final int RES_STRING_POOL_TYPE = 0x0001;
	public static final int RES_TABLE_TYPE = 0x0002;
	public static final int RES_XML_TYPE = 0x0003;

	// Chunk types in RES_XML_TYPE
	public static final int RES_XML_FIRST_CHUNK_TYPE = 0x0100;
	public static final int RES_XML_START_NAMESPACE_TYPE = 0x0100;
	public static final int RES_XML_END_NAMESPACE_TYPE = 0x0101;
	public static final int RES_XML_START_ELEMENT_TYPE = 0x0102;
	public static final int RES_XML_END_ELEMENT_TYPE = 0x0103;
	public static final int RES_XML_CDATA_TYPE = 0x0104;
	public static final int RES_XML_LAST_CHUNK_TYPE = 0x017f;
	// This contains a uint32_t array mapping strings in the string
	// pool back to resource identifiers. It is optional.
	public static final int RES_XML_RESOURCE_MAP_TYPE = 0x0180;

	// Chunk types in RES_TABLE_TYPE

	public static final int BAD_DOCUMENT = -1;
	public static final int START_DOCUMENT = 0;
	public static final int END_DOCUMENT = 1;
	public static final int FIRST_CHUNK_CODE = RES_XML_FIRST_CHUNK_TYPE;
	public static final int START_NAMESPACE = RES_XML_START_NAMESPACE_TYPE;
	public static final int END_NAMESPACE = RES_XML_END_NAMESPACE_TYPE;
	public static final int START_TAG = RES_XML_START_ELEMENT_TYPE;
	public static final int END_TAG = RES_XML_END_ELEMENT_TYPE;
	public static final int TEXT = RES_XML_CDATA_TYPE;
	private int mEventCode;
	private ResXMLTree mTree;
	protected ResXMLTree_node mCurNode;
	protected ResXMLTree_cdataExt mCurExt;

	public ResXMLParser(ResXMLTree osb) {
		mTree = osb;
	}

	public ResXMLParser() {
		mTree = (ResXMLTree) this;
	}

	public void restart() {
		mCurNode = null;
		mEventCode = mTree.mError == Errors.NO_ERROR ? START_DOCUMENT : BAD_DOCUMENT;
	}

	public int next() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getElementNamespaceID() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getElementNameID() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getTextID() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getLineNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getAttributeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getAttributeNamespaceID(int idx) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getAttributeNameID(int idx) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getAttributeNameResID(int idx) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getAttributeDataType(int idx) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getAttributeData(int idx) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getAttributeValueStringID(int idx) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int indexOfID() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int indexOfClass() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int indexOfStyle() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getAttributeValue(int idx, Res_value value) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int indexOfAttribute(String ns, String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int nextNode() {
		if (mEventCode < 0) {
			return mEventCode;
		}

		do {
			
		} while (true);
	}

	public ResStringPool getStrings() {
		return mTree.mStrings;
	}

}
