package com.lonerr.androidfw;

import com.lonerr.utils.ByteArrayUtil;
import com.lonerr.utils.Errors;

public class ResXMLTree extends ResXMLParser{
	private byte[] mOwnedData;
	private int mEventCode;
	private ResXMLTree_header mHeader;
	private int mSize;
	private int[] mResIds;
	private int mNumResIds;
	private ResXMLTree_node mRootNode;
	private ResXMLTree_cdataExt mRootExt;
	private int mDataEnd;
	private int mRootCode;
	
	ResStringPool mStrings = new ResStringPool();
	int mError;

	public ResXMLTree(byte[] data, int off, int size, boolean copyData) {
		super();
		mError = Errors.NO_INIT;
		setTo(data, off, size, copyData);
	}

	/**
	 * @param data
	 * @param off
	 * @param size
	 * @param copyData
	 * @return
	 */
	private int setTo(byte[] data, int off, int size, boolean copyData) {
		uninit();
		mEventCode = ResXMLParser.START_DOCUMENT;


		if (data == null || size == 0) {
			return (mError = Errors.BAD_TYPE);
		}
		int dataStart = off;

		if (copyData) {
			mOwnedData = new byte[size];
			System.arraycopy(data, off, mOwnedData, 0, size);
			data = mOwnedData;
			dataStart = 0;
		}

		mHeader = new ResXMLTree_header(data, dataStart, size);
		mSize = mHeader.header.size;
		if (mHeader.header.headerSize > mSize || mSize > size) {
			// ALOGW("Bad XML block: header size %d or total size %d is larger than data size %d\n",
			// (int)dtohs(mHeader->header.headerSize),
			// (int)dtohl(mHeader->header.size), (int)size);
			mError = Errors.BAD_TYPE;
			restart();
			return mError;
		}
		mDataEnd = dataStart + mSize;
		dataStart += mHeader.header.headerSize;

		mStrings.uninit();
		mRootNode = null;
		mResIds = null;
		mNumResIds = 0;

		// // First look for a couple interesting chunks: the string block
		// // and first XML node.
		ResChunk_header chunk = new ResChunk_header(data, dataStart);
//		ResChunk_header lastChunk = chunk;
		while (dataStart < (mDataEnd - ResChunk_header.RESCHUNK_HEADER_SIZE) && dataStart < (mDataEnd - chunk.size)) {
			int err = validate_chunk(chunk, ResChunk_header.RESCHUNK_HEADER_SIZE, dataStart, mDataEnd, "XML");
			if (err != Errors.NO_ERROR) {
				mError = err;
				break;
			}

			short type = chunk.type;
			// XML_NOISY(printf("Scanning @ %p: type=0x%x, size=0x%x\n",
			// (void*)(((uint32_t)chunk)-((uint32_t)mHeader)), type, size));
			if (type == ResXMLParser.RES_STRING_POOL_TYPE) {
				mStrings.setTo(data, dataStart, chunk.size, false);
			} else if (type == ResXMLParser.RES_XML_RESOURCE_MAP_TYPE) {
				mNumResIds = (chunk.size - chunk.headerSize) / 4;
				mResIds = new int[mNumResIds];
				for (int i = 0; i < mNumResIds; i++) {
					mResIds[i] = ByteArrayUtil.getInt(data, (dataStart + chunk.headerSize) + (i << 2), false);
					System.out.println(Integer.toHexString(mResIds[i]));
				}
				
			} else if (type >= ResXMLParser.RES_XML_FIRST_CHUNK_TYPE && type <= ResXMLParser.RES_XML_LAST_CHUNK_TYPE) {
				 if (validateNode(chunk,data,dataStart) != Errors.NO_ERROR) {
		                mError = Errors.BAD_TYPE;
		                break;
		            }
		            mCurNode = new ResXMLTree_node(data,dataStart);
		            if (nextNode() == ResXMLParser.BAD_DOCUMENT) {
		                mError = Errors.BAD_TYPE;
		                break;
		            }
		            mRootNode = mCurNode;
		            mRootExt = mCurExt;
		            mRootCode = mEventCode;
		            break;
			}

//			lastChunk = chunk;
			chunk = new ResChunk_header(data, dataStart += chunk.size);
		}

		if (mRootNode == null) {
			// ALOGW("Bad XML block: no root element node found\n");
			mError = Errors.BAD_TYPE;
			restart();
			return mError;
		}

		mError = mStrings.getError();

		restart();
		return mError;
	}

	private int validateNode(ResChunk_header header, byte[] data, int dataStart) {
		short eventCode = header.type;
		int err = validate_chunk(header, 8, dataStart, mDataEnd, "ResXMLTree_node");
		if (err >= Errors.NO_ERROR) {
			// Only perform additional validation on START nodes
			if (eventCode != ResXMLParser.RES_XML_START_ELEMENT_TYPE) {
				return Errors.NO_ERROR;
			}

			int headerSize = header.headerSize;
			int size = header.size;
			ResXMLTree_attrExt attrExt = new ResXMLTree_attrExt(data, dataStart + headerSize);
			// check for sensical values pulled out of the stream so far...
			if ((size >= headerSize + ResXMLTree_attrExt.SIZE)
			/* && ((void*)attrExt > (void*)node) */) {
				int attrSize = attrExt.attributeSize * attrExt.attributeCount;
				if (attrExt.attributeStart + attrSize <= (size - headerSize)) {
					return Errors.NO_ERROR;
				}
				// ALOGW("Bad XML block: node attributes use 0x%x bytes, only have 0x%x bytes\n",
				// (unsigned int)(dtohs(attrExt->attributeStart)+attrSize),
				// (unsigned int)(size-headerSize));
			} else {
				// ALOGW("Bad XML start block: node header size 0x%x, size 0x%x\n",
				// (unsigned int)headerSize, (unsigned int)size);
			}
			return Errors.BAD_TYPE;
		}

		return err;
	}

	private static int validate_chunk(ResChunk_header chunk, int minSize, int dataStart, int dataEnd, String name) {
		short headerSize = chunk.headerSize;
		int size = chunk.size;
		if (headerSize >= minSize) {
			if (headerSize <= size) {
				if (((headerSize | size) & 0x3) == 0) {
					if (size <= dataEnd - dataStart) {
						return Errors.NO_ERROR;
					}
					// ALOGW("%s data size %p extends beyond resource end %p.",
					// name, (void*)size,
					// (void*)(dataEnd-((const uint8_t*)chunk)));
					return Errors.BAD_TYPE;
				}
				// ALOGW("%s size 0x%x or headerSize 0x%x is not on an integer boundary.",
				// name, (int)size, (int)headerSize);
				return Errors.BAD_TYPE;
			}
			// ALOGW("%s size %p is smaller than header size %p.",
			// name, (void*)size, (void*)(int)headerSize);
			return Errors.BAD_TYPE;
		}
		// ALOGW("%s header size %p is too small.",
		// name, (void*)(int)headerSize);
		return Errors.BAD_TYPE;
	}

	private void uninit() {
		mError = Errors.NO_INIT;
		mStrings.uninit();
		if (mOwnedData != null) {
			// free(mOwnedData);
			mOwnedData = null;
		}
		restart();
	}

	public int getError() {
		return mError;
	}

}
