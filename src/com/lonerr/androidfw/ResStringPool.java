package com.lonerr.androidfw;

import com.lonerr.utils.ByteArrayUtil;
import com.lonerr.utils.Errors;

public class ResStringPool {
	private int mError;
	private byte[] mOwnedData;
	private ResStringPool_header mHeader;
	private int mSize;
	private char[] mStrings;
	private int[] mEntries;
	private int mStringPoolSize;
	private int[] mEntryStyles;
	private int[] mStyles;
	private int mStylePoolSize;

	public ResStringPool(byte[] bArray, int off, int len, boolean b) {
		// TODO Auto-generated constructor stub
	}

	public ResStringPool() {
		// TODO Auto-generated constructor stub
	}

	public void uninit() {
		// TODO Auto-generated method stub

	}

	public int getError() {
		return mError;
	}

	public int setTo(byte[] data, int off, int size, boolean copyData) {
		if (data == null || size == 0) {
			return (mError = Errors.BAD_TYPE);
		}

		uninit();

		boolean notDeviceEndian = true;
		int dataStart = off;

		if (copyData || notDeviceEndian) {
			mOwnedData = new byte[size];
			System.arraycopy(data, off, mOwnedData, 0, size);
			data = mOwnedData;
			dataStart = 0;
		}

		mHeader = new ResStringPool_header(data, dataStart);

		// if (notDeviceEndian) {
		// ResStringPool_header* h = const_cast<ResStringPool_header*>(mHeader);
		// h->header.headerSize = dtohs(mHeader->header.headerSize);
		// h->header.type = dtohs(mHeader->header.type);
		// h->header.size = dtohl(mHeader->header.size);
		// h->stringCount = dtohl(mHeader->stringCount);
		// h->styleCount = dtohl(mHeader->styleCount);
		// h->flags = dtohl(mHeader->flags);
		// h->stringsStart = dtohl(mHeader->stringsStart);
		// h->stylesStart = dtohl(mHeader->stylesStart);
		// }
		if (mHeader.header.headerSize > mHeader.header.size || mHeader.header.size > size) {
			// ALOGW("Bad string block: header size %d or total size %d is larger than data size %d\n",
			// (int)mHeader->header.headerSize, (int)mHeader->header.size,
			// (int)size);
			return (mError = Errors.BAD_TYPE);
		}

		mSize = mHeader.header.size;
		int mEntriesStart = dataStart + mHeader.header.headerSize;
		if (mHeader.stringCount > 0) {
			if (mHeader.header.headerSize + mHeader.stringCount * 4 > size) {
				// ALOGW("Bad string block: entry of %d items extends past data size %d\n",
				// (int)(mHeader->header.headerSize+(mHeader->stringCount*sizeof(uint32_t))),
				// (int)size);
				return (mError = Errors.BAD_TYPE);
			}
			int charSize;
			if ((mHeader.flags & ResStringPool_header.UTF8_FLAG) != 0) {
				charSize = 1;
				// mCache = (char16_t**)calloc(mHeader->stringCount,
				// sizeof(char16_t**));
			} else {
				charSize = 2;
			}

			int mStringsStart = dataStart + mHeader.stringsStart;
			if (mHeader.stringsStart >= (mHeader.header.size - /*
																 * sizeof(uint16_t
																 * )
																 */2)) {
				// ALOGW("Bad string block: string pool starts at %d, after total size %d\n",
				// (int)mHeader->stringsStart, (int)mHeader->header.size);
				return (mError = Errors.BAD_TYPE);
			}
			if (mHeader.styleCount == 0) {
				mStringPoolSize = (mHeader.header.size - mHeader.stringsStart) / charSize;
			} else {
				// check invariant: styles starts before end of data
				if (mHeader.stylesStart >= (mHeader.header.size - /*
																 * sizeof(uint16_t
																 * )
																 */2)) {
					// ALOGW("Bad style block: style block starts at %d past data size of %d\n",
					// (int)mHeader->stylesStart, (int)mHeader->header.size);
					return (mError = Errors.BAD_TYPE);
				}
				// check invariant: styles follow the strings
				if (mHeader.stylesStart <= mHeader.stringsStart) {
					// ALOGW("Bad style block: style block starts at %d, before strings at %d\n",
					// (int)mHeader->stylesStart, (int)mHeader->stringsStart);
					return (mError = Errors.BAD_TYPE);
				}
				mStringPoolSize = (mHeader.stylesStart - mHeader.stringsStart) / charSize;
			}
			// check invariant: stringCount > 0 requires a string pool to exist
			if (mStringPoolSize == 0) {
				// ALOGW("Bad string block: stringCount is %d but pool size is 0\n",
				// (int)mHeader->stringCount);
				return (mError = Errors.BAD_TYPE);
			}

			if (notDeviceEndian) {
				mEntries = new int[mHeader.stringCount];
				for (int i = 0; i < mHeader.stringCount; i++) {
					mEntries[i] = ByteArrayUtil.getInt(data, mEntriesStart + (i << 2), false);// dtohl(mEntries[i]);
				}
				if ((mHeader.flags & ResStringPool_header.UTF8_FLAG) == 0) {
					mStrings = new char[mStringPoolSize];
					for (int i = 0; i < mStringPoolSize; i++) {
						mStrings[i] = (char) ByteArrayUtil.getShort(data, dataStart + mStringsStart + (i << 1), false);
					}
				}
			}

			if (((mHeader.flags & ResStringPool_header.UTF8_FLAG) != 0 && mStrings[mStringPoolSize - 1] != 0)
					|| ((mHeader.flags & ResStringPool_header.UTF8_FLAG) == 0 && mStrings[mStringPoolSize - 1] != 0)) {
				// ALOGW("Bad string block: last string is not 0-terminated\n");
				return (mError = Errors.BAD_TYPE);
			}
		} else {
			mStrings = null;
			mStringPoolSize = 0;
		}
		if (mHeader.styleCount > 0) {
			int mEntryStylesStart = mEntriesStart + mHeader.stringCount;
			// invariant: integer overflow in calculating mEntryStyles
			// if (mEntryStyles < mEntries) {
			// ALOGW("Bad string block: integer overflow finding styles\n");
			// return (mError=BAD_TYPE);
			// }
			// if (((const uint8_t*)mEntryStyles-(const uint8_t*)mHeader) >
			// (int)size) {
			// ALOGW("Bad string block: entry of %d styles extends past data size %d\n",
			// (int)((const uint8_t*)mEntryStyles-(const uint8_t*)mHeader),
			// (int)size);
			// return (mError=Errors.BAD_TYPE);
			// }
			int mStylesStart = dataStart + mHeader.stylesStart;
			if (mHeader.stylesStart >= mHeader.header.size) {
				// ALOGW("Bad string block: style pool starts %d, after total size %d\n",
				// (int)mHeader->stylesStart, (int)mHeader->header.size);
				return (mError = Errors.BAD_TYPE);
			}
			mStylePoolSize = (mHeader.header.size - mHeader.stylesStart) / 4;
			if (notDeviceEndian) {
				mEntryStyles = new int[mHeader.styleCount];
				for (int i = 0; i < mHeader.styleCount; i++) {
					mEntryStyles[i] = ByteArrayUtil.getInt(data, mEntryStylesStart, false);
				}
				mStyles = new int[mStylePoolSize];
				for (int i = 0; i < mStylePoolSize; i++) {
					mStyles[i] = ByteArrayUtil.getInt(data, mStylesStart, false);
				}
			}
		} else {
			mEntryStyles = null;
			mStyles = null;
			mStylePoolSize = 0;
		}
		return (mError = Errors.NO_ERROR);
	}

	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String string8At(int idx, Integer len) {
		// TODO Auto-generated method stub
		return null;
	}

	public String stringAt(int idx, Integer len) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResStringPool_span styleAt(int idx) {
		if (mError == Errors.NO_ERROR && idx < mHeader.styleCount) {
	        int off = mEntryStyles[idx]/4;
	        if (off < mStylePoolSize) {
//	            return (const ResStringPool_span*)(mStyles+off);
	        } else {
//	            ALOGW("Bad string block: style #%d entry is at %d, past end at %d\n",
//	                    (int)idx, (int)(off*sizeof(uint32_t)),
//	                    (int)(mStylePoolSize*sizeof(uint32_t)));
	        }
	    }
	    return null;
	}
}
