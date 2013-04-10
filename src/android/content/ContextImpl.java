package android.content;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.DisplayMetrics;
import android.view.CompatibilityInfoHolder;
import android.view.ContextThemeWrapper;
import android.view.Display;

public class ContextImpl extends ContextThemeWrapper {

	private Resources mResources;

	public ContextImpl() {
		mResources = new Resources(new AssetManager(), new DisplayMetrics(),
				null);
	}

	@Override
	public void unregisterReceiver(BroadcastReceiver arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unbindService(ServiceConnection arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean stopServiceAsUser(Intent arg0, UserHandle arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stopService(Intent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ComponentName startServiceAsUser(Intent arg0, UserHandle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ComponentName startService(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startIntentSender(IntentSender arg0, Intent arg1, int arg2,
			int arg3, int arg4, Bundle arg5) throws SendIntentException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startIntentSender(IntentSender arg0, Intent arg1, int arg2,
			int arg3, int arg4) throws SendIntentException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean startInstrumentation(ComponentName arg0, String arg1,
			Bundle arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startActivity(Intent arg0, Bundle arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startActivity(Intent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startActivities(Intent[] arg0, Bundle arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startActivities(Intent[] arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	@Deprecated
	public void setWallpaper(InputStream arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	@Deprecated
	public void setWallpaper(Bitmap arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTheme(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendStickyOrderedBroadcastAsUser(Intent arg0, UserHandle arg1,
			BroadcastReceiver arg2, Handler arg3, int arg4, String arg5,
			Bundle arg6) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendStickyOrderedBroadcast(Intent arg0, BroadcastReceiver arg1,
			Handler arg2, int arg3, String arg4, Bundle arg5) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendStickyBroadcastAsUser(Intent arg0, UserHandle arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendStickyBroadcast(Intent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendOrderedBroadcastAsUser(Intent arg0, UserHandle arg1,
			String arg2, BroadcastReceiver arg3, Handler arg4, int arg5,
			String arg6, Bundle arg7) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendOrderedBroadcast(Intent arg0, String arg1,
			BroadcastReceiver arg2, Handler arg3, int arg4, String arg5,
			Bundle arg6) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendOrderedBroadcast(Intent arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadcastAsUser(Intent arg0, UserHandle arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadcastAsUser(Intent arg0, UserHandle arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadcast(Intent arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadcast(Intent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void revokeUriPermission(Uri arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeStickyBroadcastAsUser(Intent arg0, UserHandle arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeStickyBroadcast(Intent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Intent registerReceiverAsUser(BroadcastReceiver arg0,
			UserHandle arg1, IntentFilter arg2, String arg3, Handler arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList<BroadcastReceiver> receivers = new ArrayList<BroadcastReceiver>();
	private ArrayList<IntentFilter> filters = new ArrayList<IntentFilter>();

	@Override
	public Intent registerReceiver(BroadcastReceiver arg0, IntentFilter arg1,
			String arg2, Handler arg3) {
		receivers.add(arg0);
		filters.add(arg1);
		return null;
	}

	@Override
	public Intent registerReceiver(BroadcastReceiver arg0, IntentFilter arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Deprecated
	public Drawable peekWallpaper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String arg0, int arg1,
			CursorFactory arg2, DatabaseErrorHandler arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String arg0, int arg1,
			CursorFactory arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileOutputStream openFileOutput(String arg0, int arg1)
			throws FileNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileInputStream openFileInput(String arg0)
			throws FileNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void grantUriPermission(String arg0, Uri arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	@Deprecated
	public int getWallpaperDesiredMinimumWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Deprecated
	public int getWallpaperDesiredMinimumHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Deprecated
	public Drawable getWallpaper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getSystemService(String arg0) {
		// TODO Auto-generated method stub
		return super.getSystemService(arg0);
	}

	@Override
	public File getSharedPrefsFile(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SharedPreferences getSharedPreferences(String arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resources getResources() {
		return mResources;
	}

	@Override
	public String getPackageResourcePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPackageName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PackageManager getPackageManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPackageCodePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getObbDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Looper getMainLooper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getFilesDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getFileStreamPath(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getExternalFilesDir(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getExternalCacheDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getDir(String arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getDatabasePath(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentResolver getContentResolver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompatibilityInfoHolder getCompatibilityInfo(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClassLoader getClassLoader() {
		return ClassLoader.getSystemClassLoader();
	}

	@Override
	public File getCacheDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AssetManager getAssets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApplicationInfo getApplicationInfo() {
		// TODO Auto-generated method stub
		return new ApplicationInfo();
	}

	@Override
	public Context getApplicationContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] fileList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void enforceUriPermission(Uri arg0, String arg1, String arg2,
			int arg3, int arg4, int arg5, String arg6) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enforceUriPermission(Uri arg0, int arg1, int arg2, int arg3,
			String arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enforcePermission(String arg0, int arg1, int arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enforceCallingUriPermission(Uri arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enforceCallingPermission(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	private Theme mTheme;
	private int mThemeResource;

	@Override
	public Theme getTheme() {
		if (mTheme == null) {
			mThemeResource = Resources.selectDefaultTheme(mThemeResource,
					Build.VERSION_CODES.ICE_CREAM_SANDWICH);
			mTheme = mResources.newTheme();
			mAttrs = new ArrayList<String>();
			mTheme.applyStyle(mThemeResource, true);
		}
		return mTheme;
	}

	private ArrayList<String> mAttrs;

	private void loadAttr(int resId) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new InputStreamReader(new FileInputStream(
					"res/attrs.xml")));
			xpp.next();
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("attr".equals(xpp.getName())) {
						addAttrs(xpp);
					}
					break;
				case XmlPullParser.TEXT:
					System.out.println(xpp.getText());
					break;
				default:
					break;
				}
				eventType = xpp.next();
			}// eof-while
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addAttrs(XmlPullParser xpp) {
		Attr attr = new Attr();
		attr.name = xpp.getAttributeValue(0);
		attr.format = xpp.getAttributeValue(1);
		System.out.println("******" + xpp.getText() + "+"
				+ xpp.getAttributeCount());
	}

	@Override
	public void enforceCallingOrSelfUriPermission(Uri arg0, int arg1,
			String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enforceCallingOrSelfPermission(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean deleteFile(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteDatabase(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] databaseList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context createPackageContextAsUser(String arg0, int arg1,
			UserHandle arg2) throws NameNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context createPackageContext(String arg0, int arg1)
			throws NameNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context createDisplayContext(Display arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context createConfigurationContext(Configuration arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Deprecated
	public void clearWallpaper() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public int checkUriPermission(Uri arg0, String arg1, String arg2, int arg3,
			int arg4, int arg5) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int checkUriPermission(Uri arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int checkPermission(String arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int checkCallingUriPermission(Uri arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int checkCallingPermission(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int checkCallingOrSelfUriPermission(Uri arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int checkCallingOrSelfPermission(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean bindService(Intent arg0, ServiceConnection arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean running;

	public void stopLoop() {
		running = false;
	}

	public void startLoop() {
		running = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (running) {
					for (BroadcastReceiver receiver : receivers) {
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_ADVANCED_SETTINGS_CHANGED);
						receiver.onReceive(ContextImpl.this, intent);
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();

	}

	public static class Attr {
		private String name;
		private String format;
		private ArrayList<PairData> enums;
		private ArrayList<PairData> flags;

		public static class PairData {
			private String name;
			private String value;
		}
	}
}
