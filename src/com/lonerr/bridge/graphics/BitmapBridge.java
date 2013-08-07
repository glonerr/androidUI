package com.lonerr.bridge.graphics;

import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.GraphicsJNI;
import com.lonerr.skia.core.SkBitmap;
import com.lonerr.skia.core.SkBitmap.Config;
import android.os.Parcel;

public class BitmapBridge {
	private static HashMap<Integer, SkBitmap> skBitmapPool = new HashMap<Integer, SkBitmap>();
	public static final int kJPEG_JavaEncodeFormat = 0;
	public static final int kPNG_JavaEncodeFormat = 1;
	public static final int kWEBP_JavaEncodeFormat = 2;

	public static Bitmap Bitmap_creator(int[] colors, int offset, int stride,
			int width, int height, int nConfig, boolean isMutable) {
		if (colors != null) {
			int n = colors.length;
			if (n < stride * height) {
				return null;
			}
		}
		SkBitmap bitmap = new SkBitmap();
		bitmap.setConfig(getConfig(nConfig), width, height, 0);
		if(colors != null)
			bitmap.setPixels(colors, offset, stride, 0, 0, width, height);
		return GraphicsJNI.createBitmap(bitmap, null, isMutable, null, null,
				-1);
	}

	private static Config getConfig(int nConfig) {
		switch (nConfig) {
		case 2:
			return SkBitmap.Config.kA8_Config;
		case 4:
			return SkBitmap.Config.kRGB_565_Config;
		case 5:
			return SkBitmap.Config.kARGB_4444_Config;
		case 6:
			return SkBitmap.Config.kARGB_8888_Config;
		}
		return null;
	}

	public static SkBitmap getBitmap(int bitmap) {
		return skBitmapPool.get(bitmap);
	}

	public static Bitmap nativeCreate(int[] colors, int offset, int stride,
			int width, int height, int nConfig, boolean mutable) {
		return Bitmap_creator(colors, offset, stride, width, height, nConfig,
				mutable);
	}

	public static Bitmap nativeCopy(int srcBitmap, int nativeConfig,
			boolean isMutable) {
		SkBitmap result = new SkBitmap();
		SkBitmap src = skBitmapPool.get(srcBitmap);
		if (!src.copyTo(result, getConfig(nativeConfig))) {
			return null;
		}
		return GraphicsJNI.createBitmap(result, null, isMutable, null, null,
				-1);
	}

	public static void nativeDestructor(int nativeBitmap) {
		skBitmapPool.remove(nativeBitmap);
	}

	public static boolean nativeRecycle(int nativeBitmap) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean nativeCompress(int nativeBitmap, int format,
			int quality, OutputStream stream, byte[] tempStorage) {
		// SkImageEncoder.Type fm;
		// switch (format) {
		// case kJPEG_JavaEncodeFormat:
		// fm = SkImageEncoder.Type.kJPEG_Type;
		// break;
		// case kPNG_JavaEncodeFormat:
		// fm = SkImageEncoder.Type.kPNG_Type;
		// break;
		// case kWEBP_JavaEncodeFormat:
		// fm = SkImageEncoder.Type.kWEBP_Type;
		// break;
		// default:
		// return false;
		// }
		//
		boolean success = false;
		// TODO Auto-generated method stub
		return success;
	}

	public static void nativeErase(int nativeBitmap, int color) {
		skBitmapPool.get(nativeBitmap).eraseColor(color);
	}

	public static int nativeWidth(int nativeBitmap) {
		return skBitmapPool.get(nativeBitmap).width();
	}

	public static int nativeHeight(int nativeBitmap) {
		return skBitmapPool.get(nativeBitmap).height();
	}

	public static int nativeRowBytes(int nativeBitmap) {
		return skBitmapPool.get(nativeBitmap).rowBytes();
	}

	public static int nativeConfig(int nativeBitmap) {
		return getConfigInt(skBitmapPool.get(nativeBitmap).config());
	}

	private static int getConfigInt(SkBitmap.Config config) {
		switch (config) {
		case kA8_Config:
			return 2;
		case kRGB_565_Config:
			return 4;
		case kARGB_4444_Config:
			return 5;
		case kARGB_8888_Config:
			return 6;
		default:
			return -1;
		}
	}

	public static int nativeGetPixel(int nativeBitmap, int x, int y) {
		return skBitmapPool.get(nativeBitmap).getPixel(x, y);
	}

	public static void nativeGetPixels(int nativeBitmap, int[] pixels,
			int offset, int stride, int x, int y, int width, int height) {
		skBitmapPool.get(nativeBitmap).getPixels(pixels, offset, stride, x, y,
				width, height);
	}

	public static void nativeSetPixel(int nativeBitmap, int x, int y, int color) {
		skBitmapPool.get(nativeBitmap).setPixel(x, y, color);
	}

	public static void nativeSetPixels(int nativeBitmap, int[] colors,
			int offset, int stride, int x, int y, int width, int height) {
		skBitmapPool.get(nativeBitmap).setPixels(colors, offset, stride, x, y,
				width, height);
	}

	public static void nativeCopyPixelsToBuffer(int nativeBitmap, Buffer dst) {
		SkBitmap bitmap = skBitmapPool.get(nativeBitmap);
		byte[] pixels = bitmap.getPixels();
		((ByteBuffer) dst).put(pixels);
	}

	public static void nativeCopyPixelsFromBuffer(int nb, Buffer src) {
		SkBitmap bitmap = skBitmapPool.get(nb);
		bitmap.setPixels(((ByteBuffer) src).array());
	}

	public static int nativeGenerationId(int nativeBitmap) {
		return skBitmapPool.get(nativeBitmap).getGenerationID();
	}

	public static Bitmap nativeCreateFromParcel(Parcel p) {
		if (p == null) {
			return null;
		}

		// final boolean isMutable = p.readInt() != 0;
		// final int nConfig = p.readInt();
		// final int width = p.readInt();
		// final int height = p.readInt();
		// final int rowBytes = p.readInt();
		// final int density = p.readInt();
		// SkBitmap.Config config = getConfig(nConfig);
		// if (SkBitmap.Config.kARGB_8888_Config != config
		// && SkBitmap.Config.kRGB_565_Config != config
		// && SkBitmap.Config.kARGB_4444_Config != config
		// && SkBitmap.Config.kIndex8_Config != config
		// && SkBitmap.Config.kA8_Config != config) {
		// return null;
		// }
		// TODO Auto-generated method stub
		return null;
	}

	public static boolean nativeWriteToParcel(int nativeBitmap,
			boolean isMutable, int density, Parcel p) {
		// TODO Auto-generated method stub
		return false;
	}

	public static Bitmap nativeExtractAlpha(int nativeBitmap, int nativePaint,
			int[] offsetXY) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void nativePrepareToDraw(int nativeBitmap) {
		// nothing to be done here.
	}

	public static boolean nativeHasAlpha(int nativeBitmap) {
		return !skBitmapPool.get(nativeBitmap).isOpaque();
	}

	public static void nativeSetHasAlpha(int nBitmap, boolean hasAlpha) {
		skBitmapPool.get(nBitmap).setIsOpaque(!hasAlpha);
	}

	public static boolean nativeHasMipMap(int nativeBitmap) {
		return skBitmapPool.get(nativeBitmap).hasHardwareMipMap();
	}

	public static void nativeSetHasMipMap(int nBitmap, boolean hasMipMap) {
		skBitmapPool.get(nBitmap).setHasHardwareMipMap(hasMipMap);
	}

	public static boolean nativeSameAs(int nb0, int nb1) {
		// TODO Auto-generated method stub
		return false;
	}

	public static void put(int key, SkBitmap value) {
		skBitmapPool.put(key, value);
	}
}
