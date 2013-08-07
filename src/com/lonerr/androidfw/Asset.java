package com.lonerr.androidfw;

import com.lonerr.androidfw.Asset.AccessMode;
import com.lonerr.utils.String8;

public class Asset {
	public enum AccessMode {
        ACCESS_UNKNOWN,

        /* read chunks, and seek forward and backward */
        ACCESS_RANDOM,

        /* read sequentially, with an occasional forward seek */
        ACCESS_STREAMING,

        /* caller plans to ask for a read-only buffer with all data */
        ACCESS_BUFFER,
    }

	public int openFileDescriptor(Long startOffset, Long length) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setAssetSource(String8 path) {
		// TODO Auto-generated method stub
		
	}

	public static Asset createFromCompressedFile(String string, AccessMode mode) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Asset createFromFile(String string, AccessMode mode) {
		// TODO Auto-generated method stub
		return null;
	}
}
