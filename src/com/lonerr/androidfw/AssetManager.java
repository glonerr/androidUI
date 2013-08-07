package com.lonerr.androidfw;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.lonerr.androidfw.Asset.AccessMode;
import com.lonerr.androidfw.AssetDir.FileInfo;
import com.lonerr.utils.String8;

public class AssetManager {
	public enum FileType {
		kFileTypeDirectory, kFileTypeRegular, kFileTypeUnknown, kFileTypeNonexistent;
	}

	public enum CacheMode {
		CACHE_UNKNOWN, CACHE_OFF, // don't try to cache file locations
		CACHE_DEFER, // construct cache as pieces are needed
		// CACHE_SCAN, // scan full(!) asset hierarchy at init() time
	}

	private static class asset_path {
		String8 path;
		FileType type;
		String8 idmap;
	};

	public static class ZipSet {
		public ZipSet() {

		}

		public ZipFile getZip(String8 path) {
			return null;
		}

		public Asset getZipResourceTableAsset(String8 path) {
			return null;
		}

		public Asset setZipResourceTableAsset(String8 path, Asset asset) {
			return null;
		}

		public ResTable getZipResourceTable(String8 path) {
			return null;
		}

		public ResTable setZipResourceTable(String8 path, ResTable res) {
			return null;
		}

		// generate path, e.g. "common/en-US-noogle.zip"
		public static String8 getPathName(String path) {
			return null;
		}

		public boolean isUpToDate() {
			return false;
		}

		public void closeZip(int idx) {

		}

		public int getIndex(String8 zip) {
			return -1;
		}

		private ArrayList<String8> mZipPath;
		private ArrayList<ZipFile> mZipFile;
	}

	private static final String kSystemAssets = "framework/framework-res.apk";
	private static final String kAppZipName = null;
	private static final String kIdmapCacheDir = "resource-cache";
	private static final String8 kAssetsRoot = null;
	private static final Asset kExcludedAsset = new Asset();
	private static final String kDefaultLocale = "default";
	private static final String kDefaultVendor = "default";
	private ArrayList<asset_path> mAssetPaths = new ArrayList<asset_path>();
	private CacheMode mCacheMode;
	private boolean mCacheValid;
	private ArrayList<AssetDir.FileInfo> mCache = new ArrayList<AssetDir.FileInfo>();
	private ZipSet mZipSet;
	private String mLocale;
	private String mVendor;
	private ResTable mResources;
	private ResTable_config mConfig;
	private ReentrantReadWriteLock mLock = new ReentrantReadWriteLock();

	public AssetDir openDir(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean addAssetPath(String8 path, Integer cookie) {
		asset_path ap = new asset_path();

		String8 realPath = new String8(path);
		if (kAppZipName != null) {
			realPath.appendPath(kAppZipName);
		}
		ap.type = getFileType(realPath.string());
		if (ap.type == FileType.kFileTypeRegular) {
			ap.path = realPath;
		} else {
			ap.path = path;
			ap.type = getFileType(path.string());
			if (ap.type != FileType.kFileTypeDirectory && ap.type != FileType.kFileTypeRegular) {
				// ALOGW("Asset path %s is neither a directory nor file (type=%d).",
				// path.string(), (int)ap.type);
				return false;
			}
		}

		for (int i = 0; i < mAssetPaths.size(); i++) {
			if (mAssetPaths.get(i).path.equals(ap.path)) {
				if (cookie != null) {
					cookie = (i + 1);
				}
				return true;
			}
		}

		mAssetPaths.add(ap);

		// new paths are always added at the end
		if (cookie != null) {
			cookie = mAssetPaths.size();
		}

		if (path.equals("/system/framework/")) {
			// When there is an environment variable for /vendor, this
			// should be changed to something similar to how ANDROID_ROOT
			// and ANDROID_DATA are used in this file.
			String8 overlayPath = new String8("/vendor/overlay/framework/");
			overlayPath.append(path.getPathLeaf());
			if (/* TEMP_FAILURE_RETRY(access(overlayPath.string(), R_OK)) == 0 */true) {
				asset_path oap = new asset_path();
				oap.path = overlayPath;
				oap.type = getFileType(overlayPath.string());
				boolean addOverlay = (oap.type == FileType.kFileTypeRegular); // only
																				// .apks
																				// supported
																				// as
																				// overlay
				if (addOverlay) {
					oap.idmap = idmapPathForPackagePath(overlayPath);

					if (isIdmapStaleLocked(ap.path, oap.path, oap.idmap)) {
						addOverlay = createIdmapFileLocked(ap.path, oap.path, oap.idmap);
					}
				}
				if (addOverlay) {
					mAssetPaths.add(oap);
				} else {
					// ALOGW("failed to add overlay package %s\n",
					// overlayPath.string());
				}
			}
		}

		return true;
	}

	private boolean createIdmapFileLocked(String8 path, String8 path2, String8 idmap) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isIdmapStaleLocked(String8 originalPath, String8 overlayPath, String8 idmapPath) {
		long st;
		return false;
	}

	// Transform string /a/b/c.apk to /data/resource-cache/a@b@c.apk@idmap
	public String8 idmapPathForPackagePath(String8 pkgPath) {
		String root = getenv("ANDROID_DATA");
		String8 path = new String8(root);
		path.appendPath(kIdmapCacheDir);

		int index = 0;
		String tmp = pkgPath.string();
		while (tmp.charAt(index) == '/') {
			index++;
		}
		tmp = tmp.substring(index);
		tmp = tmp.replace('/', '@');
		path.appendPath(tmp);
		path.append("@idmap");
		return path;
	}

	private FileType getFileType(String fileName) {
		Asset pAsset = null;

		/*
		 * Open the asset. This is less efficient than simply finding the file,
		 * but it's not too bad (we don't uncompress or mmap data until the
		 * first read() call).
		 */
		pAsset = open(fileName, Asset.AccessMode.ACCESS_STREAMING);
		if (pAsset == null)
			return FileType.kFileTypeNonexistent;
		else
			return FileType.kFileTypeRegular;
	}

	public boolean isUpToDate() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setLocale(String locale) {
		setLocaleLocked(locale);
	}

	private void setLocaleLocked(String locale) {
		if (mLocale != null) {
			/* previously set, purge cached data */
			purgeFileNameCacheLocked();
			// mZipSet.purgeLocale();
		}
		mLocale = locale;

		updateResourceParamsLocked();
	}

	private void updateResourceParamsLocked() {
		ResTable res = mResources;
		if (res == null) {
			return;
		}

		int llen = mLocale != null ? mLocale.length() : 0;
		mConfig.language[0] = 0;
		mConfig.language[1] = 0;
		mConfig.country[0] = 0;
		mConfig.country[1] = 0;
		if (llen >= 2) {
			mConfig.language[0] = mLocale.charAt(0);
			mConfig.language[1] = mLocale.charAt(1);
		}
		if (llen >= 5) {
			mConfig.country[0] = mLocale.charAt(3);
			mConfig.country[1] = mLocale.charAt(4);
		}

		res.setParameters(mConfig);
	}

	private void purgeFileNameCacheLocked() {
		mCacheValid = false;
		mCache.clear();
	}

	public void getLocales(Vector<String> locales) {
		// TODO Auto-generated method stub

	}

	public boolean addDefaultAssets() {
		String root = getenv("ANDROID_ROOT");
		String8 path = new String8(root);
		path.appendPath(kSystemAssets);
		return addAssetPath(path, null);
	}

	private String getenv(String string) {
		return "";
	}

	public ResTable getResources() {
		// TODO Auto-generated method stub
		return null;
	}

	public Asset open(String fileName, AccessMode mode) {
		Lock lock = mLock.writeLock();
		try {
			lock.lock();
			if (mCacheMode != CacheMode.CACHE_OFF && !mCacheValid)
				loadFileNameCacheLocked();

			String8 assetName = new String8(kAssetsRoot);
			assetName.appendPath(fileName);

			/*
			 * For each top-level asset path, search for the asset.
			 */

			int i = mAssetPaths.size();
			while (i > 0) {
				i--;
				Asset pAsset = openNonAssetInPathLocked(assetName.string(), mode, mAssetPaths.get(i));
				if (pAsset != null) {
					return pAsset != kExcludedAsset ? pAsset : null;
				}
			}
			return null;
		} finally {
			lock.unlock();
		}
	}

	private Asset openNonAssetInPathLocked(String fileName, AccessMode mode, asset_path ap) {
		Asset pAsset = null;

		/* look at the filesystem on disk */
		if (ap.type == FileType.kFileTypeDirectory) {
			String8 path = new String8(ap.path);
			path.appendPath(fileName);

			pAsset = openAssetFromFileLocked(path, mode);

			if (pAsset == null) {
				/* try again, this time with ".gz" */
				path.append(".gz");
				pAsset = openAssetFromFileLocked(path, mode);
			}

			if (pAsset != null) {
				// printf("FOUND NA '%s' on disk\n", fileName);
				pAsset.setAssetSource(path);
			}

			/* look inside the zip file */
		} else {
			String8 path = new String8(fileName);

			/* check the appropriate Zip file */
			ZipFile pZip;
			ZipEntry entry;

			pZip = getZipFileLocked(ap);
			if (pZip != null) {
				// printf("GOT zip, checking NA '%s'\n", (const char*) path);
				entry = pZip.getEntry(path.string());
				if (entry != null) {
					// printf("FOUND NA in Zip file for %s\n", appName ? appName
					// : kAppCommon);
					pAsset = openAssetFromZipLocked(pZip, entry, mode, path);
				}
			}

			if (pAsset != null) {
				/* create a "source" name, for debug/display */
				pAsset.setAssetSource(createZipSourceNameLocked(ZipSet.getPathName(ap.path.string()), new String8(""),
						new String8(fileName)));
			}
		}

		return pAsset;
	}

	private String8 createZipSourceNameLocked(String8 pathName, String8 string8, String8 string82) {
		// TODO Auto-generated method stub
		return null;
	}

	private Asset openAssetFromZipLocked(ZipFile pZip, ZipEntry entry, AccessMode mode, String8 path) {
		// TODO Auto-generated method stub
		return null;
	}

	private ZipFile getZipFileLocked(asset_path ap) {
		// TODO Auto-generated method stub
		return null;
	}

	private Asset openAssetFromFileLocked(String8 pathName, AccessMode mode) {
		Asset pAsset = null;

		if (pathName.getPathExtension().string().equals(".gz")) {
			// printf("TRYING '%s'\n", (const char*) pathName);
			pAsset = Asset.createFromCompressedFile(pathName.string(), mode);
		} else {
			// printf("TRYING '%s'\n", (const char*) pathName);
			pAsset = Asset.createFromFile(pathName.string(), mode);
		}

		return pAsset;
	}

	private void loadFileNameCacheLocked() {
		fncScanLocked(mCache, "");
	}

	private void fncScanLocked(ArrayList<FileInfo> pMergedInfo, String dirName) {
		int i = mAssetPaths.size();
	    while (i > 0) {
	        i--;
	        asset_path ap = mAssetPaths.get(i);
	        fncScanAndMergeDirLocked(pMergedInfo, ap, null, null, dirName);
	        if (mLocale != null)
	            fncScanAndMergeDirLocked(pMergedInfo, ap, mLocale, null, dirName);
	        if (mVendor != null)
	            fncScanAndMergeDirLocked(pMergedInfo, ap, null, mVendor, dirName);
	        if (mLocale != null && mVendor != null)
	            fncScanAndMergeDirLocked(pMergedInfo, ap, mLocale, mVendor, dirName);
	    }
	}

	private boolean fncScanAndMergeDirLocked(ArrayList<FileInfo> pMergedInfo, asset_path ap, String locale,
			String vendor, String dirName) {
		ArrayList <AssetDir.FileInfo> pContents = new ArrayList<AssetDir.FileInfo>();
	    String8 partialPath;
	    String8 fullPath;

	    // XXX This is broken -- the filename cache needs to hold the base
	    // asset path separately from its filename.
	    
	    partialPath = createPathNameLocked(ap, locale, vendor);
	    if (dirName.length() != 0) {
	        partialPath.appendPath(dirName);
	    }

	    fullPath = partialPath;
	    pContents = scanDirLocked(fullPath);
	    if (pContents == null) {
	        return false;       // directory did not exist
	    }

	    /*
	     * Scan all subdirectories of the current dir, merging what we find
	     * into "pMergedInfo".
	     */
	    for (int i = 0; i < (int) pContents.size(); i++) {
	        if (pContents.get(i).getFileType() == FileType.kFileTypeDirectory) {
	            String8 subdir = new String8(dirName);
	            subdir.appendPath(pContents.get(i).getFileName());

	            fncScanAndMergeDirLocked(pMergedInfo, ap, locale, vendor, subdir.string());
	        }
	    }

	    /*
	     * To be consistent, we want entries for the root directory.  If
	     * we're the root, add one now.
	     */
	    if (dirName.length() != 0) {
	        FileInfo tmpInfo = new FileInfo();

	        tmpInfo.set(new String8(""), FileType.kFileTypeDirectory);
	        tmpInfo.setSourceName(createPathNameLocked(ap, locale, vendor));
	        pContents.add(tmpInfo);
	    }
	    

	    /*
	     * We want to prepend the extended partial path to every entry in
	     * "pContents".  It's the same value for each entry, so this will
	     * not change the sorting order of the vector contents.
	     */
	    for (int i = 0; i < (int) pContents.size(); i++) {
	        AssetDir.FileInfo info = pContents.get(i);
	        info.setFileName(partialPath.appendPathCopy(info.getFileName()));
	    }

	    mergeInfoLocked(pMergedInfo, pContents);
	    return true;
	}

	private void mergeInfoLocked(ArrayList<FileInfo> pMergedInfo, ArrayList<FileInfo> pContents) {
		// TODO Auto-generated method stub
		
	}

	private ArrayList<FileInfo> scanDirLocked(String8 fullPath) {
		// TODO Auto-generated method stub
		return null;
	}

	private String8 createPathNameLocked(asset_path ap, String locale, String vendor) {
		String8 path = new String8(ap.path);
	    path.appendPath((locale != null) ? locale : kDefaultLocale);
	    path.appendPath((vendor != null) ? vendor : kDefaultVendor);
	    return path;
	}

	public void setConfiguration(ResTable_config config, String locale) {
		Lock lock = mLock.writeLock();
		try {
			lock.lock();
		    mConfig = config;
		    if (locale != null) {
		        setLocaleLocked(locale);
		    } else if (config.language[0] != 0) {
		        char spec[] = new char[9];
		        spec[0] = config.language[0];
		        spec[1] = config.language[1];
		        if (config.country[0] != 0) {
		            spec[2] = '_';
		            spec[3] = config.country[0];
		            spec[4] = config.country[1];
		            spec[5] = 0;
		        } else {
		            spec[3] = 0;
		        }
		        setLocaleLocked(new String(spec));
		    } else {
		        updateResourceParamsLocked();
		    }
		} finally {
			lock.unlock();
		}
	}
}
