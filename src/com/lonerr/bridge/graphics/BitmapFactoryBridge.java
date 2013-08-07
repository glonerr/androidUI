package com.lonerr.bridge.graphics;

import java.awt.Graphics2D;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.GraphicsJNI;
import android.graphics.NinePatchChunk;
import android.graphics.Rect;
import com.lonerr.skia.core.SkBitmap;

import com.sun.imageio.plugins.png.PNGMetadata;

public class BitmapFactoryBridge {
	private static Bitmap doDecode(InputStream stream, byte[] storage,
			Rect padding, Options options, boolean allowPurgeable,
			boolean forcePurgeable, boolean applyScale, float scale) {
		int sampleSize = 1;

		SkBitmap.Config prefConfig = SkBitmap.Config.kARGB_8888_Config;

		// boolean doDither = true;
		boolean isMutable = false;
		boolean willScale = applyScale && scale != 1.0f;
		// boolean isPurgeable = !willScale
		// && (forcePurgeable || (allowPurgeable && options.inPurgeable));
		// boolean preferQualityOverSpeed = false;

		Bitmap javaBitmap = null;
		if (options != null) {
			sampleSize = options.inSampleSize;
			// initialize these, in case we fail later on
			options.outWidth = -1;
			options.outHeight = -1;
			options.outMimeType = null;

			Bitmap.Config jconfig = options.inPreferredConfig;
			prefConfig = GraphicsJNI.getNativeBitmapConfig(jconfig);
			isMutable = options.inMutable;
			// doDither = options.inDither;
			// preferQualityOverSpeed = options.inPreferQualityOverSpeed;
			javaBitmap = options.inBitmap;
		}

		if (willScale && javaBitmap != null) {
			return null;
		}

		SkBitmap bitmap;
		if (javaBitmap == null) {
			bitmap = new SkBitmap();
		} else {
			if (sampleSize != 1) {
				return null;
			}
			bitmap = BitmapBridge.getBitmap(javaBitmap.mNativeBitmap);
			// config of supplied bitmap overrules config set in options
			prefConfig = bitmap.getConfig();
		}

		// To fix the race condition in case "requestCancelDecode"
		// happens earlier than AutoDecoderCancel object is added
		// to the gAutoDecoderCancelMutex linked list.
		if (options != null && options.mCancel) {
			return null;
		}

		SkBitmap decoded;
		if (willScale) {
			decoded = new SkBitmap();
		} else {
			decoded = bitmap;
		}

		decoded.fConfig = prefConfig;

		byte[] ninePatchChunk = null;
		int[] layoutBounds = null;
		NinePatchChunk chunk = null;

		try {
			ImageInputStream inputStream = ImageIO
					.createImageInputStream(stream);
			ImageReader reader = ImageIO.getImageReaders(inputStream).next();
			reader.setInput(inputStream);

			String formatType;
			try {
				ImageReadParam param = reader.getDefaultReadParam();
				decoded.fImage = reader.read(0, param);
				if (reader.getFormatName().contains("png")) {
					PNGMetadata metadata = (PNGMetadata) reader
							.getImageMetadata(0);
					int index;
					if ((index = metadata.unknownChunkType.indexOf("npTc")) != -1) {
						chunk = NinePatchChunk
								.deserialize(metadata.unknownChunkData
										.get(index));
						if (willScale) {
							scaleNinePatchChunk(chunk, scale);
						}
						ninePatchChunk = NinePatchChunk.serialize(chunk);
					}
					if ((index = metadata.unknownChunkType.indexOf("npLb")) != -1) {
						layoutBounds = new int[4];
						// if (layoutBounds == NULL) {
						// return nullObjectReturn("layoutBounds == null");
						// }
						DataInputStream inputstream = new DataInputStream(
								new ByteArrayInputStream(
										metadata.unknownChunkData.get(index)));
						for (int i = 0; i < layoutBounds.length; i++) {
							layoutBounds[i] = inputstream.readInt();
						}
						if (willScale) {
							for (int i = 0; i < 4; i++) {
								layoutBounds[i] = (int) (layoutBounds[i]
										* scale + 0.5f);
							}
						}
						if (javaBitmap != null) {
							javaBitmap.setLayoutBounds(layoutBounds);
						}
					}
				}
				decoded.set();
			} finally {
				formatType = reader.getFormatName().toLowerCase();
				reader.dispose();
			}

			String mimeType = null;
			if (formatType != null)
				for (String mime : ImageIO.getReaderMIMETypes()) {
					if (mime.contains(formatType)) {
						mimeType = mime;
						break;
					}
				}

			int scaledWidth = decoded.width();
			int scaledHeight = decoded.height();

			if (willScale) {
				scaledWidth = (int) (scaledWidth * scale + 0.5f);
				scaledHeight = (int) (scaledHeight * scale + 0.5f);
			}

			// update options (if any)
			if (options != null) {
				options.outWidth = scaledWidth;
				options.outHeight = scaledHeight;
				options.outMimeType = mimeType;
			}

			if (willScale) {
				// This is weird so let me explain: we could use the scale
				// parameter
				// directly, but for historical reasons this is how the
				// corresponding
				// Dalvik code has always behaved. We simply recreate the
				// behavior here.
				// The result is slightly different from simply using scale
				// because of
				// the 0.5f rounding bias applied when computing the target
				// image size

				SkBitmap.Config config = decoded.config();
				switch (config) {
				case kNo_Config:
				case kIndex8_Config:
				case kRLE_Index8_Config:
					config = SkBitmap.Config.kARGB_8888_Config;
					break;
				default:
					break;
				}

				bitmap.setConfig(config, scaledWidth, scaledHeight, 0);
				bitmap.setIsOpaque(decoded.isOpaque());
				// if (!bitmap->allocPixels(&javaAllocator, NULL)) {
				// return
				// nullObjectReturn("allocation failed for scaled bitmap");
				// }
				Graphics2D g2d = bitmap.fImage.createGraphics();
				g2d.drawImage(decoded.fImage, 0, 0, bitmap.fImage.getWidth(),
						bitmap.fImage.getHeight(), 0, 0,
						decoded.fImage.getWidth(), decoded.fImage.getHeight(),
						null);
			}
			if (padding != null) {

				if (chunk != null) {
					GraphicsJNI.set_jrect(padding, chunk.paddingLeft,
							chunk.paddingTop, chunk.paddingRight,
							chunk.paddingBottom);
				} else {
					GraphicsJNI.set_jrect(padding, -1, -1, -1, -1);
				}

			}

			if (javaBitmap != null) {
				// If a java bitmap was passed in for reuse, pass it back
				return javaBitmap;
			}
		} catch (Exception e) {
		}

		bitmap.set();
		return GraphicsJNI.createBitmap(bitmap, storage, isMutable,
				ninePatchChunk, layoutBounds, -1);
	}

	private static void scaleNinePatchChunk(NinePatchChunk chunk, float scale) {
		chunk.paddingLeft = (int) (chunk.paddingLeft * scale + 0.5f);
		chunk.paddingTop = (int) (chunk.paddingTop * scale + 0.5f);
		chunk.paddingRight = (int) (chunk.paddingRight * scale + 0.5f);
		chunk.paddingBottom = (int) (chunk.paddingBottom * scale + 0.5f);

		for (int i = 0; i < chunk.numXDivs; i++) {
			chunk.xDivs[i] = (int) (chunk.xDivs[i] * scale + 0.5f);
			if (i > 0 && chunk.xDivs[i] == chunk.xDivs[i - 1]) {
				chunk.xDivs[i]++;
			}
		}

		for (int i = 0; i < chunk.numYDivs; i++) {
			chunk.yDivs[i] = (int) (chunk.yDivs[i] * scale + 0.5f);
			if (i > 0 && chunk.yDivs[i] == chunk.yDivs[i - 1]) {
				chunk.yDivs[i]++;
			}
		}
	}

	private static Bitmap nativeDecodeAssetScaled(int native_asset,
			Rect padding, Options options, boolean applyScale, float scale) {
		return null;
	}

	public static Bitmap nativeDecodeStream(InputStream is, byte[] storage,
			Rect padding, Options options) {
		return nativeDecodeStreamScaled(is, storage, padding, options, false,
				1.0f);
	}

	public static Bitmap nativeDecodeStreamScaled(InputStream stream,
			byte[] storage, Rect padding, Options options, boolean applyScale,
			float scale) {
		Bitmap bitmap = null;
		if (stream != null) {
			// for now we don't allow purgeable with java inputstreams
			bitmap = doDecode(stream, storage, padding, options, false, false,
					applyScale, scale);
		}
		return bitmap;
	}

	public static Bitmap nativeDecodeStream(InputStream stream, byte[] storage,
			Rect padding, Options options, boolean applyScale, float scale) {
		Bitmap bitmap = null;
		if (stream != null) {
			// for now we don't allow purgeable with java inputstreams
			bitmap = doDecode(stream, storage, padding, options, false, false,
					applyScale, scale);
		}
		return bitmap;
	}

	public static Bitmap nativeDecodeFileDescriptor(FileDescriptor fd,
			Rect padding, Options bitmapFactoryOptions) {
		FileInputStream stream = new FileInputStream(fd);
		return doDecode(stream, null, padding, bitmapFactoryOptions, false,
				false, false, 1.0f);
	}

	public static Bitmap nativeDecodeAsset(int native_asset, Rect padding,
			Options options) {
		return nativeDecodeAssetScaled(native_asset, padding, options, false,
				1.0f);
	}

	public static Bitmap nativeDecodeAsset(int asset, Rect padding,
			Options opts, boolean applyScale, float scale) {
		return null;
	}

	public static Bitmap nativeDecodeByteArray(byte[] data, int offset,
			int length, Options options) {
		boolean purgeable = options.inPurgeable && !options.inJustDecodeBounds;
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		return doDecode(stream, null, null, options, purgeable, false, false,
				1.0f);
	}

	public static byte[] nativeScaleNinePatch(byte[] chunkObject, float scale,
			Rect padding) {
		if (chunkObject != null) {
			NinePatchChunk chunk = NinePatchChunk.deserialize(chunkObject);
			scaleNinePatchChunk(chunk, scale);
			if (padding != null) {
				GraphicsJNI.set_jrect(padding, chunk.paddingLeft,
						chunk.paddingTop, chunk.paddingRight,
						chunk.paddingBottom);
			}
			chunkObject = NinePatchChunk.serialize(chunk);
		}
		return chunkObject;
	}

	public static boolean nativeIsSeekable(FileDescriptor fd) {
		return false;
	}
}
