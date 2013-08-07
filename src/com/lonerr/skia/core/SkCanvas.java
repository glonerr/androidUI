package com.lonerr.skia.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.lonerr.skia.core.SkBitmap.Config;
import com.lonerr.skia.core.SkRegion.Op;
import com.lonerr.skia.core.SkXfermode.Mode;

public class SkCanvas extends SkRefCnt {

	/** save the matrix state, restoring it on restore() */
	public static final int kMatrix_SaveFlag = 0x01;
	/** save the clip state, restoring it on restore() */
	public static final int kClip_SaveFlag = 0x02;
	/** the layer needs to support per-pixel alpha */
	public static final int kHasAlphaLayer_SaveFlag = 0x04;
	/** the layer needs to support 8-bits per color component */
	public static final int kFullColorLayer_SaveFlag = 0x08;
	/** the layer should clip against the bounds argument */
	public static final int kClipToLayer_SaveFlag = 0x10;

	// helper masks for common choices
	public static final int kMatrixClip_SaveFlag = 0x03;
	public static final int kARGB_NoClipLayer_SaveFlag = 0x0F;
	public static final int kARGB_ClipLayer_SaveFlag = 0x1F;

	public enum VertexMode {
		kTriangles_VertexMode, kTriangleStrip_VertexMode, kTriangleFan_VertexMode
	};

	public enum EdgeType {
		/**
		 * Treat the edges as B&W (not antialiased) for the purposes of testing
		 * against the current clip
		 */
		kBW_EdgeType,
		/**
		 * Treat the edges as antialiased for the purposes of testing against
		 * the current clip
		 */
		kAA_EdgeType
	};

	private static HashMap<Integer, SkCanvas> skCanvasPool = new HashMap<Integer, SkCanvas>();
	private SkClipStack fClipStack = new SkClipStack();
	private SkDeque<MCRec> fMCStack = new SkDeque<MCRec>(); // points to top of
	// stack
	private MCRec fMCRec;
	private SkBounder fBounder = new SkBounder();
	private SkDevice fLastDeviceToGainFocus;
	private int fLayerCount; // number of successful saveLayer calls
	private SkRect fLocalBoundsCompareType = new SkRect();
	private boolean fLocalBoundsCompareTypeDirty;
	private SkRect fLocalBoundsCompareTypeBW = new SkRect();
	private boolean fLocalBoundsCompareTypeDirtyBW;
	private boolean fDeviceCMDirty;
	private SkMatrix fExternalMatrix = new SkMatrix();
	private SkMatrix fExternalInverse = new SkMatrix();
	private boolean fUseExternalMatrix;
	private Graphics2D fG2d;
	private static int gCanvasCounter;

	private static SkCanvas getSkCanvas(int nCanvas) {
		return skCanvasPool.get(nCanvas);
	}

	private static void inc_canvas() {
		++gCanvasCounter;
	}

	private static Config resolve_config(SkCanvas skCanvas, SkIRect ir, int flags, boolean isOpaque) {
		return SkBitmap.Config.kARGB_8888_Config;
	}

	public SkCanvas() {
		inc_canvas();
		SkBitmap bitmap = new SkBitmap();
		SkDevice device = new SkDevice(bitmap);
		init(device).unref();
	}

	public SkCanvas(SkBitmap bitmap) {
		inc_canvas();
		SkDevice device = new SkDevice(bitmap);
		init(device).unref();
	}

	public SkCanvas(SkDevice device) {
		inc_canvas();
		init(device);
	}

	public void restore() {
		if (fMCStack.count() > 1) {
			internalRestore();
		}
	}

	public void drawLines(float startX, float startY, float stopX, float stopY, SkPaint skPaint) {
	}

	private boolean bounds_affects_clip(int flags) {
		return (flags & SkCanvas.kClipToLayer_SaveFlag) != 0;
	}

	private boolean clipRectBounds(SkRect bounds, int flags, SkIRect intersection) {
		SkIRect clipBounds = new SkIRect();
		if (!getClipDeviceBounds(clipBounds)) {// 设置当前光栅clip若当前的为empty
			return false;
		}
		SkIRect ir = new SkIRect();
		if (bounds != null) {
			SkRect r = new SkRect();
			getTotalMatrix().mapRect(r, bounds);
			r.roundOut(ir);
			if (!ir.intersect(clipBounds)) {
				if (bounds_affects_clip(flags)) {
					fMCRec.fRasterClip.setEmpty();
				}
				return false;
			}
		} else {
			ir = clipBounds;
		}

		fClipStack.clipDevRect(ir, SkRegion.Op.kIntersect_Op);
		if (bounds_affects_clip(flags) && !fMCRec.fRasterClip.op(ir, SkRegion.Op.kIntersect_Op)) {
			return false;
		}

		if (intersection != null) {
			intersection.set(ir.fLeft, ir.fTop, ir.fRight, ir.fBottom);
		}
		return true;
	}

	public boolean clipRect(final SkRect rect, SkRegion.Op op, boolean doAA) {
		fDeviceCMDirty = true; // 塞进来的是原始大小的
		fLocalBoundsCompareTypeDirty = true;
		fLocalBoundsCompareTypeDirtyBW = true;

		if (fMCRec.fMatrix.rectStaysRect()) {
			SkRect r = new SkRect();
			fMCRec.fMatrix.mapRect(r, rect);
			fClipStack.clipDevRect(r, op, doAA);
			return fMCRec.fRasterClip.op(rect, op, doAA);
		} else {
			SkPath path = new SkPath();
			path.addRect(rect, SkPath.Direction.kCW_Direction);
			return clipPath(path, op, doAA);
		}
	}

	public boolean clipPath(SkPath path, Op op, boolean doAA) {
		fDeviceCMDirty = true;
		fLocalBoundsCompareTypeDirty = true;
		fLocalBoundsCompareTypeDirtyBW = true;

		SkPath devPath = new SkPath();
		path.transform(fMCRec.fMatrix, devPath);

		// Check if the transfomation, or the original path itself
		// made us empty. Note this can also happen if we contained NaN
		// values. computing the bounds detects this, and will set our
		// bounds to empty if that is the case. (see SkRect::set(pts, count))
		if (devPath.getBounds().isEmpty()) {
			// // resetting the path will remove any NaN or other wanky values
			// // that might upset our scan converter.
			devPath.reset();
		}

		// if we called path.swap() we could avoid a deep copy of this path
		fClipStack.clipDevPath(path, op, doAA);

		return clipPathHelper(this, fMCRec.fRasterClip, devPath, op, doAA);
	}

	private boolean clipPathHelper(final SkCanvas canvas, SkRasterClip currClip, final SkPath devPath, Op op,
			boolean doAA) {
		SkRegion base = new SkRegion();
		if (SkRegion.Op.kIntersect_Op == op) {
			// since we are intersect, we can do better (tighter) with currRgn's
			// bounds, than just using the device. However, if currRgn is
			// complex,
			// our region blitter may hork, so we do that case in two steps.
			if (currClip.isRect()) {
				return currClip.setPath(devPath, currClip, doAA);
			} else {
				base.setRect(currClip.getBounds());
				SkRasterClip clip = new SkRasterClip();
				clip.setPath(devPath, base, doAA);
				return currClip.op(clip, op);
			}
		} else {
			final SkDevice device = canvas.getDevice();
			if (device == null) {
				return currClip.setEmpty();
			}
			base.setRect(0, 0, device.width(), device.height());
			if (SkRegion.Op.kReplace_Op == op) {
				return currClip.setPath(devPath, base, doAA);
			} else {
				SkRasterClip clip = new SkRasterClip();
				clip.setPath(devPath, base, doAA);
				return currClip.op(clip, op);
			}
		}
	}

	public boolean concat(SkMatrix matrix) {
		fDeviceCMDirty = true;
		fLocalBoundsCompareTypeDirty = true;
		fLocalBoundsCompareTypeDirtyBW = true;
		return fMCRec.fMatrix.preConcat(matrix);
	}

	private SkDevice createCompatibleDevice(Config config, int width, int height, boolean isOpaque) {
		SkDevice device = getDevice();
		if (device != null) {
			return device.createCompatibleDevice(config, width, height, isOpaque);
		} else {
			return null;
		}
	}

	private SkDevice createLayerDevice(Config config, int width, int height, boolean isOpaque) {
		SkDevice device = getTopDevice();
		if (device != null) {
			return device.createCompatibleDeviceForSaveLayer(config, width, height, isOpaque);
		} else {
			return null;
		}
	}

	private boolean getClipDeviceBounds(SkIRect bounds) {
		final SkRasterClip clip = fMCRec.fRasterClip;
		if (clip.isEmpty()) {
			if (bounds != null) {
				bounds.setEmpty();
			}
			return false;
		}
		if (bounds != null) {
			bounds.set(clip.getBounds());
		}
		return true;
	}

	public SkDevice getDevice() {
		SkIter<MCRec> iter = fMCStack.iter();
		MCRec rec = iter.next();
		return rec.fLayer.fDevice;
	}

	private final SkRect getLocalClipBoundsCompareType(EdgeType et) {
		if (et == EdgeType.kAA_EdgeType) {
			if (fLocalBoundsCompareTypeDirty) {
				computeLocalClipBoundsCompareType(et);
				fLocalBoundsCompareTypeDirty = false;
			}
			return fLocalBoundsCompareType;
		} else {
			if (fLocalBoundsCompareTypeDirtyBW) {
				computeLocalClipBoundsCompareType(et);
				fLocalBoundsCompareTypeDirtyBW = false;
			}
			return fLocalBoundsCompareTypeBW;
		}
	}

	private void computeLocalClipBoundsCompareType(EdgeType et) {
		SkRect r = new SkRect();
		SkRect rCompare = et == EdgeType.kAA_EdgeType ? fLocalBoundsCompareType : fLocalBoundsCompareTypeBW;
		if (!getClipBounds(r, et)) {
			rCompare.setEmpty();
		} else {
			rCompare.set(r.fLeft, r.fTop, r.fRight, r.fBottom);
		}
	}

	public boolean getClipBounds(SkRect bounds, EdgeType et) {
		SkIRect ibounds = new SkIRect();
		if (!getClipDeviceBounds(ibounds)) {
			return false;
		}
		SkMatrix inverse = new SkMatrix();
		// if we can't invert the CTM, we can't return local clip bounds
		if (!fMCRec.fMatrix.invert(inverse)) {
			if (bounds != null) {
				bounds.setEmpty();
			}
			return false;
		}
		if (bounds != null) {
			SkRect r = new SkRect();
			// adjust it outwards if we are antialiasing
			int inset = (et == EdgeType.kAA_EdgeType) ? 1 : 0;

			// SkRect::iset() will correctly assert if we pass a value out of
			// range
			// (when SkScalar==fixed), so we pin to legal values. This does not
			// really returnt the correct answer, but its the best we can do
			// given
			// that we've promised to return SkRect (even though we support
			// devices
			// that can be larger than 32K in width or height).
			r.iset(ibounds.fLeft - inset, ibounds.fTop - inset, ibounds.fRight + inset, ibounds.fBottom + inset);
			inverse.mapRect(bounds, r);
		}
		return true;
	}

	public int getSaveCount() {
		return fMCStack.count();
	}

	private SkDevice getTopDevice() {
		return fMCRec.fTopLayer.fDevice;
	}

	public SkMatrix getTotalMatrix() {
		return fMCRec.fMatrix;
	}

	private SkDevice init(SkDevice device) {
		fBounder = null;
		fLocalBoundsCompareType.setEmpty();
		fLocalBoundsCompareTypeDirty = true;
		fLocalBoundsCompareTypeBW.setEmpty();
		fLocalBoundsCompareTypeDirtyBW = true;
		fLastDeviceToGainFocus = null;
		fDeviceCMDirty = false;
		fLayerCount = 0;
		fMCRec = new MCRec(null, 0);
		fMCStack.push_back(fMCRec);
		fMCRec.fLayer = new DeviceCM(null, 0, 0, null);
		fMCRec.fTopLayer = fMCRec.fLayer;
		fMCRec.fNext = null;
		fExternalMatrix.reset();
		fExternalInverse.reset();
		fUseExternalMatrix = false;
		return setDevice(device);
	}

	private void internalRestore() {
		fDeviceCMDirty = true;
		fLocalBoundsCompareTypeDirty = true;
		fLocalBoundsCompareTypeDirtyBW = true;
		fClipStack.restore();
		// reserve our layer (if any)
		DeviceCM layer = fMCRec.fLayer; // may be null
		// now detach it from fMCRec so we can pop(). Gets freed after its drawn
		fMCRec.fLayer = null;

		// now do the normal restore()
		fMCStack.pop_back();
		fMCRec = fMCStack.back();

		/*
		 * Time to draw the layer's offscreen. We can't call the public
		 * drawSprite, since if we're being recorded, we don't want to record
		 * this (the recorder will have already recorded the restore).
		 */
		if (layer != null) {
			if (layer.fNext != null) {
				final SkIPoint origin = layer.fDevice.getOrigin();
				if (fG2d != null) {
					fG2d.dispose();
				}
				if (fMCRec.fLayer.fDevice.fBitmap.fImage != null) {
					fG2d = fMCRec.fLayer.fDevice.fBitmap.fImage.createGraphics();
					SkIRect r = fMCRec.fRasterClip.getBounds();
					fG2d.setClip(r.fLeft, r.fTop, r.width(), r.height());
					drawDevice(layer.fDevice, origin.x(), origin.y(), layer.fPaint);
					fG2d.setTransform(fMCRec.fMatrix.getAffineTransform());
				}
				// // reset this, since drawDevice will have set it to true
				fDeviceCMDirty = true;
				//
				// SkASSERT(fLayerCount > 0);
				fLayerCount -= 1;
			}
			// SkDELETE(layer);
		} else {
			setG2d();
		}
	}

	private void getPngFile(String filename) {
		try {
			ImageIO.write(fMCRec.fLayer.fDevice.fBitmap.fImage, "png", new File(filename + ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void drawDevice(SkDevice fDevice, int x, int y, SkPaint fPaint) {
		if (fDevice.fBitmap.fImage != null) {
			fG2d.drawImage(fDevice.fBitmap.fImage, x, y, fDevice.fBitmap.fImage.getWidth(),
					fDevice.fBitmap.fImage.getHeight(), null);
		}
	}

	private int internalSave(int flags) {
		int saveCount = getSaveCount();
		MCRec newTop = new MCRec(fMCRec, flags);
		fMCStack.push_back(newTop);
		fMCRec = newTop;
		fClipStack.save();
		return saveCount;
	}

	public boolean quickReject(SkRect rect, EdgeType et) {
		if (!rect.isFinite())
			return true;

		if (fMCRec.fRasterClip.isEmpty()) {
			return true;
		}

		if (fMCRec.fMatrix.hasPerspective()) {
			SkRect dst = new SkRect();
			fMCRec.fMatrix.mapRect(dst, rect);
			SkIRect idst = new SkIRect();
			dst.roundOut(idst);
			return !SkIRect.Intersects(idst, fMCRec.fRasterClip.getBounds());
		} else {
			final SkRect clipR = getLocalClipBoundsCompareType(et);

			// for speed, do the most likely reject compares first
			float userT = rect.fTop;
			float userB = rect.fBottom;
			if (userT >= clipR.fBottom || userB <= clipR.fTop) {
				return true;
			}
			float userL = rect.fLeft;
			float userR = rect.fRight;
			if (userL >= clipR.fRight || userR <= clipR.fLeft) {
				return true;
			}
			return false;
		}
	}

	public boolean quickReject(SkPath path, EdgeType et) {
		return path.isEmpty() || quickReject(path.getBounds(), et);
	}

	public void restoreToCount(int count) {
		if (count < 1) {
			count = 1;
		}
		int n = getSaveCount() - count;
		for (int i = 0; i < n; ++i) {
			restore();
		}
	}

	public boolean rotate(float degrees) {
		fDeviceCMDirty = true;
		fLocalBoundsCompareTypeDirty = true;
		fLocalBoundsCompareTypeDirtyBW = true;
		boolean result = fMCRec.fMatrix.preRotate(degrees);
		if (fG2d != null)
			fG2d.setTransform(fMCRec.fMatrix.getAffineTransform());
		return result;
	}

	public int save(int flags) {
		return internalSave(flags);
	}

	public boolean scale(float sx, float sy) {
		fDeviceCMDirty = true;
		fLocalBoundsCompareTypeDirty = true;
		fLocalBoundsCompareTypeDirtyBW = true;
		boolean result = fMCRec.fMatrix.preScale(sx, sy);
		if (fG2d != null)
			fG2d.setTransform(fMCRec.fMatrix.getAffineTransform());
		return result;
	}

	/**
	 * @param device
	 * @return
	 */
	public SkDevice setDevice(SkDevice device) {
		SkIter<MCRec> iter = fMCStack.iter();
		MCRec rec = iter.next();
		SkDevice rootDevice = rec.fLayer.fDevice;
		if (rootDevice == device) {
			return device;
		}
		if (device != null) {
			device.lockPixels();
		}
		if (rootDevice != null) {
			rootDevice.unlockPixels();
		}
		rec.fLayer.fDevice = device;
		rootDevice = device;
		fDeviceCMDirty = true;
		if (device == null) {
			rec.fRasterClip.setEmpty();
			while ((rec = iter.next()) != null) {
				rec.fRasterClip.setEmpty();
			}
			fClipStack.reset();
		} else {
			SkIRect bounds = new SkIRect();
			bounds.set(0, 0, device.width(), device.height());
			rec.fRasterClip.setRect(bounds);
			while ((rec = iter.next()) != null) {
				rec.fRasterClip.op(bounds, SkRegion.Op.kIntersect_Op);
			}
		}
		createG2d();
		return device;
	}

	private void createG2d() {
		if (fG2d != null) {
			fG2d.dispose();
		}
		if (fMCRec.fLayer.fDevice.fBitmap.fImage != null) {
			fG2d = fMCRec.fLayer.fDevice.fBitmap.fImage.createGraphics();
			SkIRect r = fMCRec.fRasterClip.getBounds();
			fG2d.setClip(r.fLeft, r.fTop, r.width(), r.height());
			fG2d.setTransform(fMCRec.fMatrix.getAffineTransform());
			fG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			fG2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		}
	}

	private void setG2d() {
		if (fG2d != null) {
			SkIRect r = fMCRec.fRasterClip.getBounds();
			fG2d.setTransform(fMCRec.fMatrix.getAffineTransform());
			fG2d.setClip(r.fLeft, r.fTop, r.width(), r.height());
			fG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			fG2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		}
	}

	public int saveLayer(SkRect bounds, SkPaint paint, int flags) {
		int count = internalSave(flags);
		fDeviceCMDirty = true;
		SkIRect ir = new SkIRect();
		if (!clipRectBounds(bounds, flags, ir)) {
			return count;
		}

		boolean isOpaque = (flags & SkCanvas.kHasAlphaLayer_SaveFlag) == 0;
		SkBitmap.Config config = resolve_config(this, ir, flags, isOpaque);

		SkDevice device;
		if (paint != null && paint.getImageFilter() != null) {
			device = createCompatibleDevice(config, ir.width(), ir.height(), isOpaque);
		} else {
			device = createLayerDevice(config, ir.width(), ir.height(), isOpaque);
		}
		if (device == null) {
			return count;
		}
		device.setOrigin(ir.fLeft, ir.fTop);
		DeviceCM layer = new DeviceCM(device, ir.fLeft, ir.fTop, paint);
		device.unref();
		layer.fNext = fMCRec.fTopLayer;
		fMCRec.fLayer = layer;
		createG2d();
		fMCRec.fTopLayer = layer; // this field is NOT an owner of layer
		fLayerCount += 1;
		return count;
	}

	public int saveLayerAlpha(final SkRect bounds, int alpha, int flags) {
		if (alpha == 0xFF) {
			return saveLayer(bounds, null, flags);
		} else {
			SkPaint tmpPaint = new SkPaint();
			tmpPaint.setAlpha(alpha);
			return saveLayer(bounds, tmpPaint, flags);
		}
	}

	public SkDevice setBitmapDevice(final SkBitmap bitmap) {
		SkDevice device = new SkDevice(bitmap);
		setDevice(device);
		device.unref();
		return device;
	}

	public void setMatrix(SkMatrix matrix) {
		fDeviceCMDirty = true;
		fLocalBoundsCompareTypeDirty = true;
		fLocalBoundsCompareTypeDirtyBW = true;
		matrix.copyTo(fMCRec.fMatrix);
	}

	public SkDrawFilter setDrawFilter(SkDrawFilter filter) {
		fMCRec.fFilter = filter;
		return filter;
	}

	public boolean skew(float sx, float sy) {
		fDeviceCMDirty = true;
		fLocalBoundsCompareTypeDirty = true;
		fLocalBoundsCompareTypeDirtyBW = true;
		boolean result = fMCRec.fMatrix.preSkew(sx, sy);
		if (fG2d != null)
			fG2d.setTransform(fMCRec.fMatrix.getAffineTransform());
		return result;
	}

	public boolean translate(float dx, float dy) {
		fDeviceCMDirty = true;
		fLocalBoundsCompareTypeDirty = true;
		fLocalBoundsCompareTypeDirtyBW = true;
		boolean result = fMCRec.fMatrix.preTranslate(dx, dy);
		if (fG2d != null)
			fG2d.setTransform(fMCRec.fMatrix.getAffineTransform());
		return result;
	}

	public static int getHeight(int nCanvas) {
		return getSkCanvas(nCanvas).getDevice().accessBitmap(false).width();
	}

	@SuppressWarnings("unused")
	private static class DeviceCM {
		private DeviceCM fNext;
		private SkDevice fDevice;
		private SkRasterClip fClip;
		private final SkMatrix fMatrix = new SkMatrix();
		private SkPaint fPaint; // may be null (in the future)
		// optional, related to canvas' external matrix
		private final SkMatrix fMVMatrix = new SkMatrix();
		private final SkMatrix fExtMatrix = new SkMatrix();

		private DeviceCM(SkDevice device, float fLeft, float fTop, final SkPaint paint) {
			if (device != null) {
				device.ref();
				device.lockPixels();
			}
			fDevice = device;
			fPaint = paint;
		}

		private void updateMC(final SkMatrix totalMatrix, final SkRasterClip totalClip, final SkClipStack clipStack,
				SkRasterClip updateClip) {
		}

		// can only be called after calling updateMC()
		private void updateExternalMatrix(final SkMatrix extM, final SkMatrix extI) {
		}

		private SkMatrix fMatrixStorage, fMVMatrixStorage;
	};

	static class MCRec {
		private MCRec fNext;
		private SkMatrix fMatrix = new SkMatrix();
		private SkRasterClip fRasterClip = new SkRasterClip();
		private SkDrawFilter fFilter;
		private DeviceCM fLayer;// 本层的画布
		private DeviceCM fTopLayer;

		public MCRec(MCRec prev, int flags) {
			if (prev != null) {
				if ((flags & SkCanvas.kMatrix_SaveFlag) != 0) {
					// prev.fMatrix.copyTo(fMatrixStorage);
					// fMatrixStorage.copyTo(fMatrix);
					prev.fMatrix.copyTo(fMatrix);
				} else {
					fMatrix = prev.fMatrix;
				}
				if ((flags & SkCanvas.kClip_SaveFlag) != 0) {
					prev.fRasterClip.copyTo(fRasterClip);
				} else {
					fRasterClip = prev.fRasterClip;
				}
				fFilter = prev.fFilter;
				fTopLayer = prev.fTopLayer;
			} else { // no prev
				fFilter = null;
				fTopLayer = null;
			}
		}
	}

	public void resetMatrix() {
		fMCRec.fMatrix.reset();
	}

	public void drawARGB(int i, int r, int g, int b) {
		// TODO Auto-generated method stub

	}

	public void drawColor(int color, Mode ksrcoverMode) {
		// TODO Auto-generated method stub

	}

	public void drawPaint(SkPaint paint) {
		internalDrawPaint(paint);
	}

	public void drawRect(SkRect rect_, SkPaint paint) {
		if (fG2d != null) {
			Color prev = fG2d.getColor();
			fG2d.setColor(new Color(paint.getColor(), true));
			if (paint.getStyle() != null)
				fG2d.fillRect((int) rect_.fLeft, (int) rect_.fTop, (int) rect_.width(), (int) rect_.height());
			fG2d.setColor(prev);
		}
	}

	private void internalDrawPaint(SkPaint paint) {
		if (fMCRec.fRasterClip.isEmpty())
			return;
	}

	public void drawRectCoords(float left, float top, float right, float bottom, SkPaint paint) {
		SkRect r = new SkRect();
		r.set(left, top, right, bottom);
		drawRect(r, paint);
	}

	public void drawOval(SkRect oval, SkPaint paint) {
		// TODO Auto-generated method stub

	}

	public void drawCircle(float cx, float cy, float radius, SkPaint paint) {
		// TODO Auto-generated method stub

	}

	public void drawArc(SkRect oval, float startAngle, float sweepAngle, boolean useCenter, SkPaint paint) {
		// TODO Auto-generated method stub

	}

	public void drawRoundRect(SkRect rect, float rx, float ry, SkPaint paint) {
		// TODO Auto-generated method stub

	}

	public void drawPath(SkPath path, SkPaint paint) {
		// TODO Auto-generated method stub

	}

	public void drawBitmap(SkBitmap bitmap, float x, float y, SkPaint paint) {
		if (paint == null || paint.canComputeFastBounds() != null) {
			SkRect bounds = new SkRect();
			bounds.set(x, y, x + bitmap.width(), y + bitmap.height());
			if (paint != null) {
				paint.computeFastBounds(bounds, bounds);
			}
			if (quickReject(bounds, paint2EdgeType(paint))) {
				return;
			}
		}

		SkMatrix matrix = new SkMatrix();
		matrix.setTranslate(x, y);
		internalDrawBitmap(bitmap, null, matrix, paint);
	}

	private EdgeType paint2EdgeType(SkPaint paint) {
		return paint != null && paint.isAntiAlias() ? SkCanvas.EdgeType.kAA_EdgeType : SkCanvas.EdgeType.kBW_EdgeType;
	}

	private void internalDrawBitmap(SkBitmap bitmap, SkIRect srcRect, SkMatrix matrix, SkPaint paint) {
		if (reject_bitmap(bitmap)) {
			return;
		}
		if (paint == null) {
			paint = new SkPaint();
		}
		commonDrawBitmap(bitmap, srcRect, matrix, paint);
	}

	private void commonDrawBitmap(SkBitmap bitmap, SkIRect srcRect, SkMatrix prematrix, SkPaint paint) {
		AffineTransform transform = fG2d.getTransform();
		if (prematrix != null) {
			SkMatrix matrix = new SkMatrix();
			matrix.setConcat(fMCRec.fMatrix, prematrix);
			fG2d.setTransform(matrix.getAffineTransform());
		}
		if (srcRect != null)
			fG2d.drawImage(bitmap.fImage, srcRect.fLeft, srcRect.fTop, srcRect.width(), srcRect.height(), null);
		else
			fG2d.drawImage(bitmap.fImage, 0, 0, null);
		if (prematrix != null) {
			fG2d.setTransform(transform);
		}
	}

	private boolean reject_bitmap(SkBitmap bitmap) {
		return bitmap.width() <= 0 || bitmap.height() <= 0;
	}

	public void drawBitmapRect(SkBitmap bitmap, SkIRect src, SkRect dst, SkPaint paint) {
		internalDrawBitmapRect(bitmap, src, dst, paint);
	}

	private void internalDrawBitmapRect(SkBitmap bitmap, SkIRect src, SkRect dst, SkPaint paint) {
		if (bitmap.width() == 0 || bitmap.height() == 0 || dst.isEmpty()) {
			return;
		}
		if (fG2d != null) {
			fG2d.drawImage(bitmap.fImage, (int) dst.fLeft, (int) dst.fTop, (int) dst.fRight, (int) dst.fBottom,
					src.fLeft, src.fTop, src.fRight, src.fBottom, null);
		}
	}

	public void drawBitmapMatrix(SkBitmap bitmap, SkMatrix matrix, SkPaint paint) {
		// TODO Auto-generated method stub

	}

	public void drawPicture(Object picture) {
		// TODO Auto-generated method stub

	}

	public boolean clipRegion(SkRegion rgn, Op op) {
		fDeviceCMDirty = true;
		fLocalBoundsCompareTypeDirty = true;
		fLocalBoundsCompareTypeDirtyBW = true;

		// todo: signal fClipStack that we have a region, and therefore (I
		// guess)
		// we have to ignore it, and use the region directly?
		fClipStack.clipDevRect(rgn.getBounds());

		return fMCRec.fRasterClip.op(rgn, op);
	}
}
