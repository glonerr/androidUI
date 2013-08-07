package com.lonerr.bridge.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import android.os.ParcelFileDescriptor;
import android.util.TypedValue;

import com.lonerr.androidfw.Asset;
import com.lonerr.androidfw.AssetDir;
import com.lonerr.androidfw.AssetManager;
import com.lonerr.androidfw.ResTable;
import com.lonerr.androidfw.ResTable.resource_name;
import com.lonerr.androidfw.ResTable_config;
import com.lonerr.androidfw.Res_value;
import com.lonerr.utils.String8;

public class AssetManagerBridge {
	private static HashMap<Integer, AssetManager> assetManagerPool = new HashMap<Integer, AssetManager>();

	public static String[] list(int mObject, String fileName) throws IOException {
		if (fileName == null || fileName.length() <= 0) {
			return null;
		}
		AssetDir dir = assetManagerPool.get(mObject).openDir(fileName);
		int N = dir.getFileCount();
		String[] list = new String[N];
		for (int i = 0; i < N; i++) {
			list[i] = dir.getFileName(i);
		}
		return list;
	}

	public static int addAssetPath(int mObject, String path) {
		if (path == null || path.length() <= 0) {
			return 0;
		}
		AssetManager am = assetManagerPool.get(mObject);
		Integer cookie = 0;
		boolean res = am.addAssetPath(new String8(path), cookie);
		return res ? cookie : 0;
	}

	public static boolean isUpToDate(int mObject) {
		AssetManager am = assetManagerPool.get(mObject);
		return am.isUpToDate();
	}

	public static void setLocale(int mObject, String locale) {
		if (locale == null || locale.length() <= 0) {
			return;
		}
		AssetManager am = assetManagerPool.get(mObject);
		am.setLocale(locale);
	}

	public static String[] getLocales(int mObject) {
		Vector<String> locales = new Vector<String>();
		AssetManager am = assetManagerPool.get(mObject);
		am.getLocales(locales);
		return (String[]) locales.toArray();
	}

	public final static void setConfiguration(int mObject, int mcc, int mnc, String locale, int orientation,
			int touchscreen, int density, int keyboard, int keyboardHidden, int navigation, int screenWidth,
			int screenHeight, int smallestScreenWidthDp, int screenWidthDp, int screenHeightDp, int screenLayout,
			int uiMode, int sdkVersion) {
		 	AssetManager am = assetManagerPool.get(mObject);
		    if (am == null) {
		        return;
		    }

		    ResTable_config config = new ResTable_config();
		    
		    config.mcc = mcc;
		    config.mnc = mnc;
		    config.orientation = orientation;
		    config.touchscreen = touchscreen;
		    config.density = density;
		    config.keyboard = keyboard;
		    config.inputFlags = keyboardHidden;
		    config.navigation = navigation;
		    config.screenWidth = screenWidth;
		    config.screenHeight = screenHeight;
		    config.smallestScreenWidthDp = smallestScreenWidthDp;
		    config.screenWidthDp = screenWidthDp;
		    config.screenHeightDp = screenHeightDp;
		    config.screenLayout = screenLayout;
		    config.uiMode = uiMode;
		    config.sdkVersion = sdkVersion;
		    config.minorVersion = 0;
		    am.setConfiguration(config, locale);
	}

	public static int getResourceIdentifier(int mObject, String defType, String name, String defPackage) {
		AssetManager am = assetManagerPool.get(mObject);
		int ident = am.getResources().identifierForName(name, name.length(), defType, defType.length(), defPackage,
				defPackage.length());
		return ident;
	}

	public static String getResourceName(int mObject, int resid) {
		AssetManager am = assetManagerPool.get(mObject);
		if (am == null) {
			return null;
		}

		resource_name name = new resource_name();
		if (!am.getResources().getResourceName(resid, name)) {
			return null;
		}

		StringBuffer str = new StringBuffer();
		if (name.packageName != null) {
			str.append(name.packageName);
		}
		if (name.type != null) {
			if (str.length() > 0) {
				char div = ':';
				str.append(div);
			}
			str.append(name.type);
		}
		if (name.name != null) {
			if (str.length() > 0) {
				char div = '/';
				str.append(div);
			}
			str.append(name.name);
		}

		return str.toString();
	}

	public static String getResourcePackageName(int mObject, int resid) {
		AssetManager am = assetManagerPool.get(mObject);
		if (am == null) {
			return null;
		}

		ResTable.resource_name name = new resource_name();
		if (!am.getResources().getResourceName(resid, name)) {
			return null;
		}

		if (name.packageName != null) {
			return name.packageName;
		}

		return null;
	}

	public static String getResourceTypeName(int mObject, int resid) {
		AssetManager am = assetManagerPool.get(mObject);
		if (am == null) {
			return null;
		}

		ResTable.resource_name name = new resource_name();
		if (!am.getResources().getResourceName(resid, name)) {
			return null;
		}

		if (name.type != null) {
			return name.type;
		}

		return null;
	}

	public static String getResourceEntryName(int mObject, int resid) {
		AssetManager am = assetManagerPool.get(mObject);
		if (am == null) {
			return null;
		}

		ResTable.resource_name name = new resource_name();
		if (!am.getResources().getResourceName(resid, name)) {
			return null;
		}

		if (name.name != null) {
			return name.name;
		}

		return null;
	}

	public static int openAsset(int mObject, String fileName, int mode) {
		AssetManager am = assetManagerPool.get(mObject);
		if (am == null) {
			return 0;
		}

		if (fileName == null || fileName.length() <= 0) {
			return -1;
		}

		if (mode != android.content.res.AssetManager.ACCESS_UNKNOWN
				&& mode != android.content.res.AssetManager.ACCESS_RANDOM
				&& mode != android.content.res.AssetManager.ACCESS_STREAMING
				&& mode != android.content.res.AssetManager.ACCESS_BUFFER) {
			return -1;
		}

		Asset a = am.open(fileName, getAccessMode(mode));

		if (a == null) {
			return -1;
		}

		// printf("Created Asset Stream: %p\n", a);

		return a.hashCode();
	}

	private static Asset.AccessMode getAccessMode(int mode) {
		switch (mode) {
		case 0:
			return Asset.AccessMode.ACCESS_BUFFER;
		case 1:
			return Asset.AccessMode.ACCESS_RANDOM;
		case 2:
			return Asset.AccessMode.ACCESS_STREAMING;
		case 3:
			return Asset.AccessMode.ACCESS_BUFFER;
		default:
			return null;
		}
	}

	public static ParcelFileDescriptor openAssetFd(int mObject, String fileName, long[] outOffsets) {
		AssetManager am = assetManagerPool.get(mObject);
		if (am == null) {
			return null;
		}

		if (fileName == null || fileName.length() <= 0) {
			return null;
		}
		
		Asset a = am.open(fileName, Asset.AccessMode.ACCESS_RANDOM);
		if (a == null) {
			return null;
		}

	    //printf("Created Asset Stream: %p\n", a);

	    return returnParcelFileDescriptor(a, outOffsets);
	}

	private static ParcelFileDescriptor returnParcelFileDescriptor(Asset a, long[] outOffsets) {
		return null;
	}

	public static int openNonAssetNative(int mObject, int cookie, String fileName, int accessMode) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static ParcelFileDescriptor openNonAssetFdNative(int mObject, int cookie, String fileName, long[] outOffsets) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void destroyAsset(int mObject, int asset) {
	}

	public static int readAssetChar(int mObject, int asset) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static int readAsset(int mObject, int asset, byte[] b, int off, int len) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static long seekAsset(int mObject, int asset, long offset, int whence) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static long getAssetLength(int mObject, int asset) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static long getAssetRemainingLength(int mObject, int asset) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static int getAssetRemainingLength(int mObject, int ident, short density, TypedValue outValue,
			boolean resolve) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static int loadResourceValue(int mObject, int ident, short density, TypedValue outValue, boolean resolve) {
		AssetManager am = assetManagerPool.get(mObject);
		if (am == null) {
	        return 0;
	    }
		ResTable res = new ResTable(am.getResources());
		Res_value value = new Res_value();
	    ResTable_config config = new ResTable_config();
	    Integer typeSpecFlags = new Integer(0);
	    int block = res.getResource(ident, value, false, density, typeSpecFlags, config);
	    Integer ref = ident;
	    if (resolve) {
	        block = res.resolveReference(value, block, ref, typeSpecFlags, config);
	    }
	    return block >= 0 ? copyValue(outValue, res, value, ref, block, typeSpecFlags, config) : block;
	}

	private static int copyValue(TypedValue outValue, ResTable table, Res_value value, Integer ref, int block,
			Integer typeSpecFlags, ResTable_config config) {
		outValue.type = value.dataType;
	    outValue.assetCookie = table.getTableCookie(block);
	    outValue.data = value.data;
	    outValue.string = null;
	    outValue.resourceId = ref;
	    outValue.changingConfigurations = typeSpecFlags;
	    if (config != null) {
	        outValue.density = config.density;
	    }
	    return block;
	}

	public static int loadResourceBagValue(int mObject, int ident, int bagEntryId, TypedValue outValue, boolean resolve) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static boolean applyStyle(int theme, int defStyleAttr, int defStyleRes, int xmlParser, int[] inAttrs,
			int[] outValues, int[] outIndices) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean retrieveAttributes(int mObject, int xmlParser, int[] inAttrs, int[] outValues,
			int[] outIndices) {
		// TODO Auto-generated method stub
		return false;
	}

	public static int getArraySize(int mObject, int resource) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static int retrieveArray(int mObject, int resource, int[] outValues) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static int getStringBlockCount(int mObject) {
		AssetManager am = assetManagerPool.get(mObject);
	    if (am == null) {
	        return 0;
	    }
	    return am.getResources().getTableCount();
	}

	public static int getNativeStringBlock(int mObject, int block) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static String getCookieName(int mObject, int cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	public static int getGlobalAssetCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static String getAssetAllocations() {
		// TODO Auto-generated method stub
		return null;
	}

	public static int getGlobalAssetManagerCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static int newTheme(int mObject) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static void deleteTheme(int mObject, int theme) {
		// TODO Auto-generated method stub
	}

	public static void applyThemeStyle(int theme, int styleRes, boolean force) {
		// TODO Auto-generated method stub

	}

	public static void copyTheme(int dest, int source) {
		// TODO Auto-generated method stub

	}

	public static int loadThemeAttributeValue(int theme, int ident, TypedValue outValue, boolean resolve) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static void dumpTheme(int theme, int priority, String tag, String prefix) {
		// TODO Auto-generated method stub

	}

	public static int openXmlAssetNative(int mObject, int cookie, String fileName) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static String[] getArrayStringResource(int mObject, int arrayRes) {
		// TODO Auto-generated method stub
		return null;
	}

	public static int[] getArrayStringInfo(int mObject, int arrayRes) {
		// TODO Auto-generated method stub
		return null;
	}

	public static int[] getArrayIntResource(int mObject, int arrayRes) {
		// TODO Auto-generated method stub
		return null;
	}

	public static int init() {
		AssetManager am = new AssetManager();
		am.addDefaultAssets();
		assetManagerPool.put(am.hashCode(), am);
		return am.hashCode();
	}

	public static void destroy(int mObject) {
		assetManagerPool.remove(mObject);
	}

}
