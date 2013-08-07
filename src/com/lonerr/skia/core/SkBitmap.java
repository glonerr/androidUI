package com.lonerr.skia.core;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class SkBitmap {
	public static final int kImageIsOpaque_Flag = 0x01;
	public static final int kImageIsVolatile_Flag = 0x02;
	public static final int kImageIsImmutable_Flag = 0x04;

	public enum Config {
		kNo_Config, // !< bitmap has not been configured
		kA1_Config, // !< 1-bit per pixel, (0 is transparent, 1 is opaque)
		kA8_Config, // !< 8-bits per pixel, with only alpha specified (0 is
					// transparent, 0xFF is opaque)
		kIndex8_Config, // !< 8-bits per pixel, using SkColorTable to specify
						// the colors
		kRGB_565_Config, // !< 16-bits per pixel, (see SkColorPriv.h for
							// packing)
		kARGB_4444_Config, // !< 16-bits per pixel, (see SkColorPriv.h for
							// packing)
		kARGB_8888_Config, // !< 32-bits per pixel, (see SkColorPriv.h for
							// packing)
		kRLE_Index8_Config, kConfigCount
	};

	public BufferedImage fImage;
	public Config fConfig;
	public int fWidth;
	public int fHeight;
	public int fRowBytes;
	private byte fBytesPerPixel;
	private int fGenerationId = 0;
	private int fFlags;

	public SkBitmap() {
	}

	public SkBitmap(SkBitmap src) {

	}

	public void setConfig(Config c, int width, int height, int rowBytes) {
		if ((width | height | rowBytes) < 0) {
			return;
		}
		if (rowBytes == 0) {
			rowBytes = ComputeRowBytes(c, width);
			if (0 == rowBytes && Config.kNo_Config != c) {
				return;
			}
		}
		fConfig = c;
		fWidth = width;
		fHeight = height;
		fRowBytes = rowBytes;
		fBytesPerPixel = ComputeBytesPerPixel(c);
		fImage = new BufferedImage(width, height, getBufferedImageType(c));
		return;
	}

	private int getBufferedImageType(Config c) {
		switch (c) {
		case kA1_Config:
			return -1;
		case kA8_Config:
		case kARGB_4444_Config:
		case kARGB_8888_Config:
		case kConfigCount:
		case kIndex8_Config:
		case kNo_Config:
		case kRGB_565_Config:
		case kRLE_Index8_Config:
		default:
			return BufferedImage.TYPE_INT_ARGB;
		}
	}

	private byte ComputeBytesPerPixel(Config config) {
		byte bpp;
		switch (config) {
		case kNo_Config:
		case kA1_Config:
			bpp = 0; // not applicable
			break;
		case kRLE_Index8_Config:
		case kA8_Config:
		case kIndex8_Config:
			bpp = 1;
			break;
		case kRGB_565_Config:
		case kARGB_4444_Config:
			bpp = 2;
			break;
		case kARGB_8888_Config:
			bpp = 4;
			break;
		default:
			bpp = 0; // error
			break;
		}
		return bpp;
	}

	private int ComputeRowBytes(Config c, int width) {
		if (width < 0) {
			return 0;
		}

		int rowBytes = 0;
		switch (c) {
		case kNo_Config:
		case kRLE_Index8_Config:
			break;
		case kA1_Config:
			rowBytes = (width + 7) >> 3;
			break;
		case kA8_Config:
		case kIndex8_Config:
			rowBytes = width;
			break;
		case kRGB_565_Config:
		case kARGB_4444_Config:
			rowBytes = width << 1;
			break;
		case kARGB_8888_Config:
			rowBytes = width << 2;
			break;
		default:
			break;
		}
		return rowBytes;
	}

	public void setPixels(int[] colors, int offset, int stride, int x, int y,
			int width, int height) {
		fImage.setRGB(x, y, width, height, colors, offset, stride);
	}

	public boolean copyTo(SkBitmap dst, Config dstConfig) {
		if (!canCopyTo(dstConfig)) {
			return false;
		}
		// TODO Auto-generated method stub
		return true;
	}

	private boolean canCopyTo(Config dstConfig) {
		if (fConfig == Config.kNo_Config) {
			return false;
		}

		boolean sameConfigs = (fConfig == dstConfig);
		switch (dstConfig) {
		case kA8_Config:
		case kARGB_4444_Config:
		case kRGB_565_Config:
		case kARGB_8888_Config:
			break;
		case kA1_Config:
		case kIndex8_Config:
			if (!sameConfigs) {
				return false;
			}
			break;
		default:
			return false;
		}

		// do not copy src if srcConfig == kA1_Config while dstConfig !=
		// kA1_Config
		if (fConfig == Config.kA1_Config && !sameConfigs) {
			return false;
		}
		return true;
	}

	public void eraseColor(int c) {
		eraseARGB((c>>24)&0xff,(c>>16)&0xff, (c>>8)&0xff,
				c&0xff);
	}

	public void eraseARGB(int a, int r, int g, int b) {
		Graphics2D g2d = fImage.createGraphics();
		try {
			g2d.setColor(new java.awt.Color((a<<24)|(r<<16)|(g<<8)|b, true));
			g2d.fillRect(0, 0, fImage.getWidth(), fImage.getHeight());
		} finally {
			g2d.dispose();
		}
	}
	
	public void eraseRGB(int r, int g, int b) {
        eraseARGB(0xFF, r, g, b);
    }

	public int width() {
		return fWidth;
	}

	public int height() {
		return fHeight;
	}

	public int rowBytes() {
		return fRowBytes;
	}

	public Config config() {
		return fConfig;
	}

	public int getPixel(int x, int y) {
		return fImage.getRGB(x, y);
	}

	public void getPixels(int[] pixels, int offset, int stride, int x, int y,
			int width, int height) {
		fImage.getRGB(x, y, width, height, pixels, offset, stride);
	}

	public void setPixel(int x, int y, int color) {
		fImage.setRGB(x, y, color);
	}

	public byte[] getPixels() {
		// TODO Auto-generated method stub
		return new byte[0];
	}

	public void setPixels(byte[] pixel) {
		// TODO Auto-generated method stub
	}

	public int getGenerationID() {
		return fGenerationId;
	}

	public boolean isOpaque() {
		switch (fConfig) {
		case kNo_Config:
			return true;

		case kA1_Config:
		case kA8_Config:
		case kARGB_4444_Config:
		case kARGB_8888_Config:
			return (fFlags & kImageIsOpaque_Flag) != 0;

		case kIndex8_Config:
		case kRLE_Index8_Config:
			// TODO Auto-generated method stub
			return false;
		case kRGB_565_Config:
			return true;

		default:
			return false;
		}
	}

	public void setIsOpaque(boolean isOpaque) {
		if (isOpaque) {
			fFlags |= kImageIsOpaque_Flag;
		} else {
			fFlags &= ~kImageIsOpaque_Flag;
		}
	}

	public boolean hasHardwareMipMap() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setHasHardwareMipMap(boolean hasMipMap) {
		// TODO Auto-generated method stub

	}

	public void notifyPixelsChanged() {
		// TODO Auto-generated method stub
		
	}

	public Graphics2D getGraphics2D() {
		return fImage.createGraphics();
	}

	public Config getConfig() {
		return fConfig;
	}

	public void set() {
		if(fImage != null){
			fWidth = fImage.getWidth();
			fHeight = fImage.getHeight();
			fRowBytes = ComputeRowBytes(fConfig, fWidth);
		}
	}
}
