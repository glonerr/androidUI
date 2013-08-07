package com.lonerr.androidfw;

import com.lonerr.utils.ByteArrayUtil;

public class ResXMLTree_attrExt {
	public static final int SIZE = 20;

	public ResXMLTree_attrExt(byte[] data, int offset) {
		ns = ByteArrayUtil.getInt(data, offset, false);
		name = ByteArrayUtil.getInt(data, offset+=4, false);
		attributeStart = ByteArrayUtil.getShort(data, offset+=4, false);
		attributeSize = ByteArrayUtil.getShort(data, offset+=2, false);
		attributeCount = ByteArrayUtil.getShort(data, offset+=2, false);
		idIndex = ByteArrayUtil.getShort(data, offset+=2, false);
		classIndex = ByteArrayUtil.getShort(data, offset+=2, false);
		styleIndex = ByteArrayUtil.getShort(data, offset+=2, false);
	}

	// String of the full namespace of this element.
    int ns;
    
    // String name of this node if it is an ELEMENT; the raw
    // character data if this is a CDATA node.
    int name;
    
    // Byte offset from the start of this structure where the attributes start.
    short attributeStart;
    
    // Size of the ResXMLTree_attribute structures that follow.
    short attributeSize;
    
    // Number of attributes associated with an ELEMENT.  These are
    // available as an array of ResXMLTree_attribute structures
    // immediately following this node.
    short attributeCount;
    
    // Index (1-based) of the "id" attribute. 0 if none.
    short idIndex;
    
    // Index (1-based) of the "class" attribute. 0 if none.
    short classIndex;
    
    // Index (1-based) of the "style" attribute. 0 if none.
    short styleIndex;
}
