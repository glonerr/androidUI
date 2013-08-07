/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.content.res;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.util.TypedValue;

import com.lonerr.bridge.util.AssetManagerBridge;

/**
 * Provides access to an application's raw asset files; see {@link Resources}
 * for the way most applications will want to retrieve their resource data. This
 * class presents a lower-level API that allows you to open and read raw files
 * that have been bundled with the application as a simple stream of bytes.
 */
public class AssetManager {
	/* modes used when opening an asset */

	/**
	 * Mode for {@link #open(String, int)}: no specific information about how
	 * data will be accessed.
	 */
	public static final int ACCESS_UNKNOWN = 0;
	/**
	 * Mode for {@link #open(String, int)}: Read chunks, and seek forward and
	 * backward.
	 */
	public static final int ACCESS_RANDOM = 1;
	/**
	 * Mode for {@link #open(String, int)}: Read sequentially, with an
	 * occasional forward seek.
	 */
	public static final int ACCESS_STREAMING = 2;
	/**
	 * Mode for {@link #open(String, int)}: Attempt to load contents into
	 * memory, for fast small reads.
	 */
	public static final int ACCESS_BUFFER = 3;

	private static final String TAG = "AssetManager";
	private static final boolean localLOGV = false || false;

	private static final boolean DEBUG_REFS = false;

	private static final Object sSync = new Object();
	/* package */static AssetManager sSystem = null;

	private final TypedValue mValue = new TypedValue();
	private final long[] mOffsets = new long[2];

	// For communication with native code.
	private int mObject;
	private int mNObject; // used by the NDK

	private StringBlock mStringBlocks[] = null;

	private int mNumRefs = 1;
	private boolean mOpen = true;
	private HashMap<Integer, RuntimeException> mRefStacks;

	/**
	 * Create a new AssetManager containing only the basic system assets.
	 * Applications will not generally use this method, instead retrieving the
	 * appropriate asset manager with {@link Resources#getAssets}. Not for use
	 * by applications. {@hide}
	 */
	public AssetManager() {
		synchronized (this) {
			if (DEBUG_REFS) {
				mNumRefs = 0;
				incRefsLocked(this.hashCode());
			}
			init();
			if (localLOGV)
				Log.v(TAG, "New asset manager: " + this);
			ensureSystemAssets();
		}
	}

	private static void ensureSystemAssets() {
		synchronized (sSync) {
			if (sSystem == null) {
				AssetManager system = new AssetManager(true);
				system.makeStringBlocks(false);
				sSystem = system;
			}
		}
	}

	private AssetManager(boolean isSystem) {
		if (DEBUG_REFS) {
			synchronized (this) {
				mNumRefs = 0;
				incRefsLocked(this.hashCode());
			}
		}
		init();
		if (localLOGV)
			Log.v(TAG, "New asset manager: " + this);
	}

	/**
	 * Return a global shared asset manager that provides access to only system
	 * assets (no application assets). {@hide}
	 */
	public static AssetManager getSystem() {
		ensureSystemAssets();
		return sSystem;
	}

	/**
	 * Close this asset manager.
	 */
	public void close() {
		synchronized (this) {
			// System.out.println("Release: num=" + mNumRefs
			// + ", released=" + mReleased);
			if (mOpen) {
				mOpen = false;
				decRefsLocked(this.hashCode());
			}
		}
	}

	/**
	 * Retrieve the string value associated with a particular resource
	 * identifier for the current configuration / skin.
	 */
	/* package */final CharSequence getResourceText(int ident) {
		synchronized (this) {
			TypedValue tmpValue = mValue;
			int block = loadResourceValue(ident, (short) 0, tmpValue, true);
			if (block >= 0) {
				if (tmpValue.type == TypedValue.TYPE_STRING) {
					return mStringBlocks[block].get(tmpValue.data);
				}
				return tmpValue.coerceToString();
			}
		}
		return null;
	}

	/**
	 * Retrieve the string value associated with a particular resource
	 * identifier for the current configuration / skin.
	 */
	/* package */final CharSequence getResourceBagText(int ident, int bagEntryId) {
		synchronized (this) {
			TypedValue tmpValue = mValue;
			int block = loadResourceBagValue(ident, bagEntryId, tmpValue, true);
			if (block >= 0) {
				if (tmpValue.type == TypedValue.TYPE_STRING) {
					return mStringBlocks[block].get(tmpValue.data);
				}
				return tmpValue.coerceToString();
			}
		}
		return null;
	}

	/**
	 * Retrieve the string array associated with a particular resource
	 * identifier.
	 * 
	 * @param id
	 *            Resource id of the string array
	 */
	/* package */final String[] getResourceStringArray(final int id) {
		String[] retArray = getArrayStringResource(id);
		return retArray;
	}

	/* package */final boolean getResourceValue(int ident, int density, TypedValue outValue, boolean resolveRefs) {
		int block = loadResourceValue(ident, (short) density, outValue, resolveRefs);
		if (block >= 0) {
			if (outValue.type != TypedValue.TYPE_STRING) {
				return true;
			}
			outValue.string = mStringBlocks[block].get(outValue.data);
			return true;
		}
		return false;
	}

	/**
	 * Retrieve the text array associated with a particular resource identifier.
	 * 
	 * @param id
	 *            Resource id of the string array
	 */
	/* package */final CharSequence[] getResourceTextArray(final int id) {
		int[] rawInfoArray = getArrayStringInfo(id);
		int rawInfoArrayLen = rawInfoArray.length;
		final int infoArrayLen = rawInfoArrayLen / 2;
		int block;
		int index;
		CharSequence[] retArray = new CharSequence[infoArrayLen];
		for (int i = 0, j = 0; i < rawInfoArrayLen; i = i + 2, j++) {
			block = rawInfoArray[i];
			index = rawInfoArray[i + 1];
			retArray[j] = index >= 0 ? mStringBlocks[block].get(index) : null;
		}
		return retArray;
	}

	/* package */final boolean getThemeValue(int theme, int ident, TypedValue outValue, boolean resolveRefs) {
		int block = loadThemeAttributeValue(theme, ident, outValue, resolveRefs);
		if (block >= 0) {
			if (outValue.type != TypedValue.TYPE_STRING) {
				return true;
			}
			StringBlock[] blocks = mStringBlocks;
			if (blocks == null) {
				ensureStringBlocks();
				blocks = mStringBlocks;
			}
			outValue.string = blocks[block].get(outValue.data);
			return true;
		}
		return false;
	}

	/* package */final void ensureStringBlocks() {
		if (mStringBlocks == null) {
			synchronized (this) {
				if (mStringBlocks == null) {
					makeStringBlocks(true);
				}
			}
		}
	}

	/* package */final void makeStringBlocks(boolean copyFromSystem) {
		final int sysNum = copyFromSystem ? sSystem.mStringBlocks.length : 0;
		final int num = getStringBlockCount();
		mStringBlocks = new StringBlock[num];
		if (localLOGV)
			Log.v(TAG, "Making string blocks for " + this + ": " + num);
		for (int i = 0; i < num; i++) {
			if (i < sysNum) {
				mStringBlocks[i] = sSystem.mStringBlocks[i];
			} else {
				mStringBlocks[i] = new StringBlock(getNativeStringBlock(i), true);
			}
		}
	}

	/* package */final CharSequence getPooledString(int block, int id) {
		// System.out.println("Get pooled: block=" + block
		// + ", id=#" + Integer.toHexString(id)
		// + ", blocks=" + mStringBlocks);
		return mStringBlocks[block - 1].get(id);
	}

	/**
	 * Open an asset using ACCESS_STREAMING mode. This provides access to files
	 * that have been bundled with an application as assets -- that is, files
	 * placed in to the "assets" directory.
	 * 
	 * @param fileName
	 *            The name of the asset to open. This name can be hierarchical.
	 * 
	 * @see #open(String, int)
	 * @see #list
	 */
	public final InputStream open(String fileName) throws IOException {
		return open(fileName, ACCESS_STREAMING);
	}

	/**
	 * Open an asset using an explicit access mode, returning an InputStream to
	 * read its contents. This provides access to files that have been bundled
	 * with an application as assets -- that is, files placed in to the "assets"
	 * directory.
	 * 
	 * @param fileName
	 *            The name of the asset to open. This name can be hierarchical.
	 * @param accessMode
	 *            Desired access mode for retrieving the data.
	 * 
	 * @see #ACCESS_UNKNOWN
	 * @see #ACCESS_STREAMING
	 * @see #ACCESS_RANDOM
	 * @see #ACCESS_BUFFER
	 * @see #open(String)
	 * @see #list
	 */
	public final InputStream open(String fileName, int accessMode) throws IOException {
		synchronized (this) {
			if (!mOpen) {
				throw new RuntimeException("Assetmanager has been closed");
			}
			int asset = openAsset(fileName, accessMode);
			if (asset != 0) {
				AssetInputStream res = new AssetInputStream(asset);
				incRefsLocked(res.hashCode());
				return res;
			}
		}
		throw new FileNotFoundException("Asset file: " + fileName);
	}

	public final AssetFileDescriptor openFd(String fileName) throws IOException {
		synchronized (this) {
			if (!mOpen) {
				throw new RuntimeException("Assetmanager has been closed");
			}
			ParcelFileDescriptor pfd = openAssetFd(fileName, mOffsets);
			if (pfd != null) {
				return new AssetFileDescriptor(pfd, mOffsets[0], mOffsets[1]);
			}
		}
		throw new FileNotFoundException("Asset file: " + fileName);
	}

	/**
	 * Return a String array of all the assets at the given path.
	 * 
	 * @param path
	 *            A relative path within the assets, i.e., "docs/home.html".
	 * 
	 * @return String[] Array of strings, one for each asset. These file names
	 *         are relative to 'path'. You can open the file by concatenating
	 *         'path' and a name in the returned string (via File) and passing
	 *         that to open().
	 * 
	 * @see #open
	 */
	public final String[] list(String path) throws IOException {
		return AssetManagerBridge.list(mObject, path);
	}

	/**
	 * {@hide} Open a non-asset file as an asset using ACCESS_STREAMING mode.
	 * This provides direct access to all of the files included in an
	 * application package (not only its assets). Applications should not
	 * normally use this.
	 * 
	 * @see #open(String)
	 */
	public final InputStream openNonAsset(String fileName) throws IOException {
		return openNonAsset(0, fileName, ACCESS_STREAMING);
	}

	/**
	 * {@hide} Open a non-asset file as an asset using a specific access mode.
	 * This provides direct access to all of the files included in an
	 * application package (not only its assets). Applications should not
	 * normally use this.
	 * 
	 * @see #open(String, int)
	 */
	public final InputStream openNonAsset(String fileName, int accessMode) throws IOException {
		return openNonAsset(0, fileName, accessMode);
	}

	/**
	 * {@hide} Open a non-asset in a specified package. Not for use by
	 * applications.
	 * 
	 * @param cookie
	 *            Identifier of the package to be opened.
	 * @param fileName
	 *            Name of the asset to retrieve.
	 */
	public final InputStream openNonAsset(int cookie, String fileName) throws IOException {
		return openNonAsset(cookie, fileName, ACCESS_STREAMING);
	}

	/**
	 * {@hide} Open a non-asset in a specified package. Not for use by
	 * applications.
	 * 
	 * @param cookie
	 *            Identifier of the package to be opened.
	 * @param fileName
	 *            Name of the asset to retrieve.
	 * @param accessMode
	 *            Desired access mode for retrieving the data.
	 */
	public final InputStream openNonAsset(int cookie, String fileName, int accessMode) throws IOException {
		synchronized (this) {
			if (!mOpen) {
				throw new RuntimeException("Assetmanager has been closed");
			}
			int asset = openNonAssetNative(cookie, fileName, accessMode);
			if (asset != 0) {
				AssetInputStream res = new AssetInputStream(asset);
				incRefsLocked(res.hashCode());
				return res;
			}
		}
		throw new FileNotFoundException("Asset absolute file: " + fileName);
	}

	public final AssetFileDescriptor openNonAssetFd(String fileName) throws IOException {
		return openNonAssetFd(0, fileName);
	}

	public final AssetFileDescriptor openNonAssetFd(int cookie, String fileName) throws IOException {
		synchronized (this) {
			if (!mOpen) {
				throw new RuntimeException("Assetmanager has been closed");
			}
			ParcelFileDescriptor pfd = openNonAssetFdNative(cookie, fileName, mOffsets);
			if (pfd != null) {
				return new AssetFileDescriptor(pfd, mOffsets[0], mOffsets[1]);
			}
		}
		throw new FileNotFoundException("Asset absolute file: " + fileName);
	}

	/**
	 * Retrieve a parser for a compiled XML file.
	 * 
	 * @param fileName
	 *            The name of the file to retrieve.
	 */
	public final XmlResourceParser openXmlResourceParser(String fileName) throws IOException {
		return openXmlResourceParser(0, fileName);
	}

	/**
	 * Retrieve a parser for a compiled XML file.
	 * 
	 * @param cookie
	 *            Identifier of the package to be opened.
	 * @param fileName
	 *            The name of the file to retrieve.
	 */
	public final XmlResourceParser openXmlResourceParser(int cookie, String fileName) throws IOException {
		XmlBlock block = openXmlBlockAsset(cookie, fileName);
		XmlResourceParser rp = block.newParser();
		block.close();
		return rp;
	}

	/**
	 * {@hide} Retrieve a non-asset as a compiled XML file. Not for use by
	 * applications.
	 * 
	 * @param fileName
	 *            The name of the file to retrieve.
	 */
	/* package */final XmlBlock openXmlBlockAsset(String fileName) throws IOException {
		return openXmlBlockAsset(0, fileName);
	}

	/**
	 * {@hide} Retrieve a non-asset as a compiled XML file. Not for use by
	 * applications.
	 * 
	 * @param cookie
	 *            Identifier of the package to be opened.
	 * @param fileName
	 *            Name of the asset to retrieve.
	 */
	/* package */final XmlBlock openXmlBlockAsset(int cookie, String fileName) throws IOException {
		synchronized (this) {
			if (!mOpen) {
				throw new RuntimeException("Assetmanager has been closed");
			}
			int xmlBlock = openXmlAssetNative(cookie, fileName);
			if (xmlBlock != 0) {
				XmlBlock res = new XmlBlock(this, xmlBlock);
				incRefsLocked(res.hashCode());
				return res;
			}
		}
		throw new FileNotFoundException("Asset XML file: " + fileName);
	}

	/* package */void xmlBlockGone(int id) {
		synchronized (this) {
			decRefsLocked(id);
		}
	}

	/* package */final int createTheme() {
		synchronized (this) {
			if (!mOpen) {
				throw new RuntimeException("Assetmanager has been closed");
			}
			int res = newTheme();
			incRefsLocked(res);
			return res;
		}
	}

	/* package */final void releaseTheme(int theme) {
		synchronized (this) {
			deleteTheme(theme);
			decRefsLocked(theme);
		}
	}

	protected void finalize() throws Throwable {
		try {
			if (DEBUG_REFS && mNumRefs != 0) {
				Log.w(TAG, "AssetManager " + this + " finalized with non-zero refs: " + mNumRefs);
				if (mRefStacks != null) {
					for (RuntimeException e : mRefStacks.values()) {
						Log.w(TAG, "Reference from here", e);
					}
				}
			}
			destroy();
		} finally {
			super.finalize();
		}
	}

	public final class AssetInputStream extends InputStream {
		public final int getAssetInt() {
			return mAsset;
		}

		private AssetInputStream(int asset) {
			mAsset = asset;
			mLength = getAssetLength(asset);
		}

		public final int read() throws IOException {
			return readAssetChar(mAsset);
		}

		public final boolean markSupported() {
			return true;
		}

		public final int available() throws IOException {
			long len = getAssetRemainingLength(mAsset);
			return len > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) len;
		}

		public final void close() throws IOException {
			synchronized (AssetManager.this) {
				if (mAsset != 0) {
					destroyAsset(mAsset);
					mAsset = 0;
					decRefsLocked(hashCode());
				}
			}
		}

		public final void mark(int readlimit) {
			mMarkPos = seekAsset(mAsset, 0, 0);
		}

		public final void reset() throws IOException {
			seekAsset(mAsset, mMarkPos, -1);
		}

		public final int read(byte[] b) throws IOException {
			return readAsset(mAsset, b, 0, b.length);
		}

		public final int read(byte[] b, int off, int len) throws IOException {
			return readAsset(mAsset, b, off, len);
		}

		public final long skip(long n) throws IOException {
			long pos = seekAsset(mAsset, 0, 0);
			if ((pos + n) > mLength) {
				n = mLength - pos;
			}
			if (n > 0) {
				seekAsset(mAsset, n, 0);
			}
			return n;
		}

		protected void finalize() throws Throwable {
			close();
		}

		private int mAsset;
		private long mLength;
		private long mMarkPos;
	}

	/**
	 * Add an additional set of assets to the asset manager. This can be either
	 * a directory or ZIP file. Not for use by applications. Returns the cookie
	 * of the added asset, or 0 on failure. {@hide}
	 */
	public final int addAssetPath(String path) {
		return AssetManagerBridge.addAssetPath(mObject, path);
	}

	/**
	 * Add multiple sets of assets to the asset manager at once. See
	 * {@link #addAssetPath(String)} for more information. Returns array of
	 * cookies for each added asset with 0 indicating failure, or null if the
	 * input array of paths is null. {@hide}
	 */
	public final int[] addAssetPaths(String[] paths) {
		if (paths == null) {
			return null;
		}

		int[] cookies = new int[paths.length];
		for (int i = 0; i < paths.length; i++) {
			cookies[i] = addAssetPath(paths[i]);
		}

		return cookies;
	}

	/**
	 * Determine whether the state in this asset manager is up-to-date with the
	 * files on the filesystem. If false is returned, you need to instantiate a
	 * new AssetManager class to see the new data. {@hide}
	 */
	public final boolean isUpToDate() {
		return AssetManagerBridge.isUpToDate(mObject);
	}

	/**
	 * Change the locale being used by this asset manager. Not for use by
	 * applications. {@hide}
	 */
	public final void setLocale(String locale) {
		AssetManagerBridge.setLocale(mObject, locale);
	}

	/**
	 * Get the locales that this asset manager contains data for.
	 */
	public final String[] getLocales() {
		return AssetManagerBridge.getLocales(mObject);
	}

	/**
	 * Change the configuation used when retrieving resources. Not for use by
	 * applications. {@hide}
	 */
	public final void setConfiguration(int mcc, int mnc, String locale, int orientation, int touchscreen, int density,
			int keyboard, int keyboardHidden, int navigation, int screenWidth, int screenHeight,
			int smallestScreenWidthDp, int screenWidthDp, int screenHeightDp, int screenLayout, int uiMode,
			int majorVersion) {
		AssetManagerBridge.setConfiguration(mObject, mcc, mnc, locale, orientation, touchscreen, density, keyboard,
				keyboardHidden, navigation, screenWidth, screenHeight, smallestScreenWidthDp, screenWidthDp,
				screenHeightDp, screenLayout, uiMode, majorVersion);
	}

	/**
	 * Retrieve the resource identifier for the given resource name.
	 */
	/* package */final int getResourceIdentifier(String type, String name, String defPackage) {
		return AssetManagerBridge.getResourceIdentifier(mObject, type, name, defPackage);
	}

	/* package */final String getResourceName(int resid) {
		return AssetManagerBridge.getResourceName(mObject, resid);
	}

	/* package */final String getResourcePackageName(int resid) {
		return AssetManagerBridge.getResourcePackageName(mObject, resid);
	}

	/* package */final String getResourceTypeName(int resid) {
		return AssetManagerBridge.getResourceTypeName(mObject, resid);
	}

	/* package */final String getResourceEntryName(int resid) {
		return AssetManagerBridge.getResourceEntryName(mObject, resid);
	}

	private final int openAsset(String fileName, int accessMode) {
		return AssetManagerBridge.openAsset(mObject, fileName, accessMode);
	}

	private final ParcelFileDescriptor openAssetFd(String fileName, long[] outOffsets) throws IOException {
		return AssetManagerBridge.openAssetFd(mObject, fileName, outOffsets);
	}

	private final int openNonAssetNative(int cookie, String fileName, int accessMode) {
		return AssetManagerBridge.openNonAssetNative(mObject, cookie, fileName, accessMode);
	}

	private ParcelFileDescriptor openNonAssetFdNative(int cookie, String fileName, long[] outOffsets)
			throws IOException {
		return AssetManagerBridge.openNonAssetFdNative(mObject, cookie, fileName, outOffsets);
	}

	private final void destroyAsset(int asset) {
		AssetManagerBridge.destroyAsset(mObject, asset);
	}

	private final int readAssetChar(int asset) {
		return AssetManagerBridge.readAssetChar(mObject, asset);
	}

	private final int readAsset(int asset, byte[] b, int off, int len) {
		return AssetManagerBridge.readAsset(mObject, asset, b, off, len);
	}

	private final long seekAsset(int asset, long offset, int whence) {
		return AssetManagerBridge.seekAsset(mObject, asset, offset, whence);
	}

	private final long getAssetLength(int asset) {
		return AssetManagerBridge.getAssetLength(mObject, asset);
	}

	private final long getAssetRemainingLength(int asset) {
		return AssetManagerBridge.getAssetRemainingLength(mObject, asset);
	}

	/**
	 * Returns true if the resource was found, filling in mRetStringBlock and
	 * mRetData.
	 */
	private final int loadResourceValue(int ident, short density, TypedValue outValue, boolean resolve) {
		return AssetManagerBridge.loadResourceValue(mObject, ident, density, outValue, resolve);
	}

	/**
	 * Returns true if the resource was found, filling in mRetStringBlock and
	 * mRetData.
	 */
	private final int loadResourceBagValue(int ident, int bagEntryId, TypedValue outValue, boolean resolve) {
		return AssetManagerBridge.loadResourceBagValue(mObject, ident, bagEntryId, outValue, resolve);
	}

	/* package */static final int STYLE_NUM_ENTRIES = 6;
	/* package */static final int STYLE_TYPE = 0;
	/* package */static final int STYLE_DATA = 1;
	/* package */static final int STYLE_ASSET_COOKIE = 2;
	/* package */static final int STYLE_RESOURCE_ID = 3;
	/* package */static final int STYLE_CHANGING_CONFIGURATIONS = 4;
	/* package */static final int STYLE_DENSITY = 5;

	/* package */static final boolean applyStyle(int theme, int defStyleAttr, int defStyleRes, int xmlParser,
			int[] inAttrs, int[] outValues, int[] outIndices) {
		return AssetManagerBridge.applyStyle(theme, defStyleAttr, defStyleRes, xmlParser, inAttrs, outValues,
				outIndices);
	}

	/* package */final boolean retrieveAttributes(int xmlParser, int[] inAttrs, int[] outValues, int[] outIndices) {
		return AssetManagerBridge.retrieveAttributes(mObject, xmlParser, inAttrs, outValues, outIndices);
	}

	/* package */final int getArraySize(int resource) {
		return AssetManagerBridge.getArraySize(mObject, resource);
	}

	/* package */final int retrieveArray(int resource, int[] outValues) {
		return AssetManagerBridge.retrieveArray(mObject, resource, outValues);
	}

	private final int getStringBlockCount() {
		return AssetManagerBridge.getStringBlockCount(mObject);
	}

	private final int getNativeStringBlock(int block) {
		return AssetManagerBridge.getNativeStringBlock(mObject, block);
	}

	/**
	 * {@hide}
	 */
	public final String getCookieName(int cookie) {
		return AssetManagerBridge.getCookieName(mObject, cookie);
	}

	/**
	 * {@hide}
	 */
	public static final int getGlobalAssetCount() {
		return AssetManagerBridge.getGlobalAssetCount();
	}

	/**
	 * {@hide}
	 */
	public static final String getAssetAllocations() {
		return AssetManagerBridge.getAssetAllocations();
	}

	/**
	 * {@hide}
	 */
	public static final int getGlobalAssetManagerCount() {
		return AssetManagerBridge.getGlobalAssetManagerCount();
	}

	private final int newTheme() {
		return AssetManagerBridge.newTheme(mObject);
	}

	private final void deleteTheme(int theme) {
		AssetManagerBridge.deleteTheme(mObject, theme);
	}

	/* package */static final void applyThemeStyle(int theme, int styleRes, boolean force) {
		AssetManagerBridge.applyThemeStyle(theme, styleRes, force);
	}

	/* package */static final void copyTheme(int dest, int source) {
		AssetManagerBridge.copyTheme(dest, source);
	}

	/* package */static final int loadThemeAttributeValue(int theme, int ident, TypedValue outValue, boolean resolve) {
		return AssetManagerBridge.loadThemeAttributeValue(theme, ident, outValue, resolve);
	}

	/* package */static final void dumpTheme(int theme, int priority, String tag, String prefix){
		AssetManagerBridge.dumpTheme(theme, priority, tag, prefix);
	}

	private final int openXmlAssetNative(int cookie, String fileName){
		return AssetManagerBridge.openXmlAssetNative(mObject,cookie, fileName);
	}

	private final String[] getArrayStringResource(int arrayRes){
		return AssetManagerBridge.getArrayStringResource(mObject,arrayRes);
	}

	private final int[] getArrayStringInfo(int arrayRes){
		return AssetManagerBridge.getArrayStringInfo(mObject,arrayRes);
	}

	/* package */final int[] getArrayIntResource(int arrayRes){
		return AssetManagerBridge.getArrayIntResource(mObject,arrayRes);
	}

	private final void init(){
		mObject = AssetManagerBridge.init();
	}

	private final void destroy(){
		AssetManagerBridge.destroy(mObject);
	}

	private final void incRefsLocked(int id) {
		if (DEBUG_REFS) {
			if (mRefStacks == null) {
				mRefStacks = new HashMap<Integer, RuntimeException>();
				RuntimeException ex = new RuntimeException();
				ex.fillInStackTrace();
				mRefStacks.put(this.hashCode(), ex);
			}
		}
		mNumRefs++;
	}

	private final void decRefsLocked(int id) {
		if (DEBUG_REFS && mRefStacks != null) {
			mRefStacks.remove(id);
		}
		mNumRefs--;
		// System.out.println("Dec streams: mNumRefs=" + mNumRefs
		// + " mReleased=" + mReleased);
		if (mNumRefs == 0) {
			destroy();
		}
	}
}