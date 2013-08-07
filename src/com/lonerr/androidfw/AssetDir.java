package com.lonerr.androidfw;

import com.lonerr.androidfw.AssetManager.FileType;
import com.lonerr.utils.String8;

public class AssetDir {

	public static class FileInfo {
		private String8    mFileName;      // filename only
        private FileType    mFileType;      // regular, directory, etc
        private String8    mSourceName;    // currently debug-only

		public FileType getFileType() {
			 return mFileType; 
		}

		public String8 getFileName() {
			 return mFileName; 
		}
		
		public String8 getSourceName() {
			 return mSourceName; 
		}

		public void set(String8 path, FileType type) {
			mFileName = path;
            mFileType = type;
		}

		public void setSourceName(String8 path) {
			mSourceName = path;
		}

		public void setFileName(String8 path) {
			mFileName = path; 
		}

	}

	public int getFileCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getFileName(int i) {
		// TODO Auto-generated method stub
		return null;
	}

}
