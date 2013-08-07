package com.lonerr.androidfw;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ResTable {

	private ReadWriteLock mLock = new ReentrantReadWriteLock();
	private ResTable_config mParams;
	// Array of all resource tables.
	private ArrayList<Header> mHeaders;

	private static class Header {

	}

	public static class resource_name {
		public String packageName;
		public int packageLen;
		public String type;
		public int typeLen;
		public String name;
		public int nameLen;
	}

	public ResTable(ResTable resources) {
		// TODO Auto-generated constructor stub
	}

	public int identifierForName(String name, int length, String defType, int length2, String defPackage,
			int length3) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getResourceName(int resid, resource_name name) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setParameters(ResTable_config params) {
		Lock lock = mLock.writeLock();
		lock.lock();
		mParams = params;
		lock.unlock();
	}

	public int getTableCount() {
		return mHeaders.size();
	}

	public int getResource(int ident, Res_value value, boolean b, short density, Integer typeSpecFlags,
			ResTable_config config) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int resolveReference(Res_value value, int block, Integer ref, Integer typeSpecFlags,
			ResTable_config config) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getTableCookie(int block) {
		// TODO Auto-generated method stub
		return 0;
	}

}
