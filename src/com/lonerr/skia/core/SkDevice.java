package com.lonerr.skia.core;

import com.lonerr.skia.core.SkBitmap.Config;

public class SkDevice extends SkRefCnt {
	enum Usage {
		kGeneral_Usage, kSaveLayer_Usage, // <! internal use only
	};

	public SkDevice(SkBitmap bitmap) {
		fBitmap = bitmap;
		fOrigin.setZero();
	}

	public SkDevice(Config config, int width, int height, boolean isOpaque) {
		fOrigin.setZero();
		fBitmap = new SkBitmap();
		fBitmap.setConfig(config, width, height, 0);
		fBitmap.setIsOpaque(isOpaque);
		if (!isOpaque) {
			fBitmap.eraseColor(0);
		}else{
			fBitmap.eraseColor(0xffffffff);
		}
	}

	/**
	 * create graphics2d etc.
	 */
	public void lockPixels() {
	}

	/**
	 * discreate graphics2d etc.
	 */
	public void unlockPixels() {
	}

	public SkIPoint getOrigin() {
		return fOrigin;
	}

	public int width() {
		return fBitmap.width();
	}

	public int height() {
		return fBitmap.height();
	}

	public void setOrigin(int x, int y) {
		fOrigin.set(x, y);
	}

	public SkDevice createCompatibleDeviceForSaveLayer(SkBitmap.Config config,
			int width, int height, boolean isOpaque) {
		// TODO Auto-generated method stub
		return onCreateCompatibleDevice(config, width, height, isOpaque,
				Usage.kSaveLayer_Usage);
	}

	public SkDevice createCompatibleDevice(Config config, int width,
			int height, boolean isOpaque) {
		return onCreateCompatibleDevice(config, width, height, isOpaque,
				Usage.kGeneral_Usage);
	}

	public SkBitmap accessBitmap(boolean changePixels) {
		if (changePixels) {
			fBitmap.notifyPixelsChanged();
		}
		return fBitmap;
	}

	SkBitmap fBitmap;
	private SkIPoint fOrigin = new SkIPoint();

	private SkDevice onCreateCompatibleDevice(Config config, int width,
			int height, boolean isOpaque, Usage kgeneralUsage) {
		return new SkDevice(config, width, height, isOpaque);
	}

}