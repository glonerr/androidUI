package com.lonerr.utils;

public class String8 {
	private static final String OS_PATH_SEPARATOR = "/";
	private String mString = "";

	public String8() {
	}

	public String8(String8 o) {
		mString = new String(o.mString);
	}

	public String8(String o) {
		mString = o;
	}

	public void append(String other) {
		mString += other;
	}

	public void append(String other, int len) {
		mString += other.substring(0, len - 1);
	}

	public String8 appendPath(String name) {
		if (!name.startsWith(OS_PATH_SEPARATOR)) {
			mString += OS_PATH_SEPARATOR + name;
			return this;
		} else {
			mString += name;
			return this;
		}
	}

	public String string() {
		return mString;
	}

	@Override
	public boolean equals(Object o) {
		return mString.equals(((String8) o).mString);
	}

	public String8 getPathLeaf() {
		int cp = mString.lastIndexOf(OS_PATH_SEPARATOR);
		if (cp < 0)
			return new String8(this);
		else
			return new String8(mString.substring(cp + 1));
	}

	public void append(String8 other) {
		mString += other.mString;
	}

	public String8 getPathExtension() {
		String ext;
		ext = find_extension();
		if (ext != null)
			return new String8(ext);
		else
			return new String8("");
	}

	private String find_extension() {
		int lastSlash;
		int lastDot;
		// only look at the filename
		lastSlash = mString.lastIndexOf(OS_PATH_SEPARATOR);
		lastSlash++;
		// find the last dot
		lastDot = mString.lastIndexOf('.', lastSlash);
		if (lastDot == -1)
			return null;
		// looks good, ship it
		return mString.substring(lastDot);
	}

	public void appendPath(String8 fileName) {
		// TODO Auto-generated method stub
		
	}

	public String8 appendPathCopy(String8 fileName) {
		// TODO Auto-generated method stub
		return null;
	}
}
