package android.graphics;

import com.lonerr.bridge.graphics.BitmapBridge;

import android.graphics.Rect;
import android.graphics.RectF;
import com.lonerr.skia.core.SkBitmap;
import com.lonerr.skia.core.SkBitmap.Config;
import com.lonerr.skia.core.SkIRect;
import com.lonerr.skia.core.SkRect;

public class GraphicsJNI {

	public static void jrectf_to_rect(RectF src, SkRect dst) {
		dst.fLeft = src.left;
		dst.fTop = src.top;
		dst.fRight = src.right;
		dst.fBottom = src.bottom;
	}

	public static void jrect_to_rect(Rect src, SkRect dst) {
		dst.fLeft = src.left;
		dst.fTop = src.top;
		dst.fRight = src.right;
		dst.fBottom = src.bottom;
	}

	public static void copyRect(Rect src, SkIRect dst) {
		dst.fLeft = src.left;
		dst.fTop = src.top;
		dst.fRight = src.right;
		dst.fBottom = src.bottom;
	}

	public static void copyRect(SkRect src, RectF dstf) {
		dstf.left = src.fLeft;
		dstf.top = src.fTop;
		dstf.right = src.fRight;
		dstf.bottom = src.fBottom;
	}

	public static void copyRect(SkIRect src, Rect dst) {
		dst.left = src.fLeft;
		dst.top = src.fTop;
		dst.right = src.fRight;
		dst.bottom = src.fBottom;
	}

	public static boolean setPixels(int[] colors, int offset, int stride,
			int i, int j, int width, int height, SkBitmap bitmap) {
		// TODO Auto-generated method stub
		return false;
	}

	public static Bitmap createBitmap(SkBitmap skBitmap, byte[] buffer,
			boolean isMutable, byte[] ninepatch, int[] layoutbounds, int density) {
		BitmapBridge.put(skBitmap.hashCode(), skBitmap);
		return new Bitmap(skBitmap.hashCode(), buffer, isMutable, ninepatch,
				layoutbounds, density);
	}

	public static Config getNativeBitmapConfig(
			android.graphics.Bitmap.Config jconfig) {
		return getSkBitmapConfig(jconfig.nativeInt);
	}

	private static Config getSkBitmapConfig(int nativeInt) {
		switch (nativeInt) {
		case 2:
			return SkBitmap.Config.kA8_Config;
		case 4:
			return SkBitmap.Config.kRGB_565_Config;
		case 5:
			return SkBitmap.Config.kARGB_4444_Config;
		case 6:
			return SkBitmap.Config.kARGB_8888_Config;
		default:
			return null;
		}
	}

	public static void set_jrect(Rect r, int L, int T,
			int R, int B) {
		r.set(L, T, R, B);
	}
}
