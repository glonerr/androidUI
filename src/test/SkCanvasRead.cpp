Canvas : SkCanvas.h 这个文件比较重要。

//SkCanvas.h
/*  This is the record we keep for each SkDevice that the user installs.
 The clip/matrix/proc are fields that reflect the top of the save/restore
 stack. Whenever the canvas changes, it marks a dirty flag, and then before
 these are used (assuming we're not on a layer) we rebuild these cache
 values: they reflect the top of the save stack, but translated and clipped
 by the device's XY offset and bitmap-bounds.
 */
//这个结构体是用来保存用户安装的SkDevice的。SkRegion，SkMatrix保存了当前SkDevice的状况。
//一旦SkDevice有变化，将会有个dirty标志位。这个时候将会重新构建每个被保存的SkDevice结构体--DeviceCM。
//这些被cache的值只反映顶点坐标，而坐标的转换和裁剪由SkDevice获得。
//注意这个结构体是一个链表结构DeviceCM* fNext.
struct DeviceCM {
	DeviceCM* fNext;
	SkDevice* fDevice;
	SkRegion fClip;
	const SkMatrix* fMatrix;
	SkPaint* fPaint;    // may be null (in the future)
	int16_t fX, fY;// relative to base matrix/clip

	DeviceCM(SkDevice* device, int x, int y, const SkPaint* paint)
	: fNext(NULL) {
		if (NULL != device) {
			device->ref();
			device->lockPixels();
		}
		fDevice = device;
		fX = SkToS16(x);
		fY = SkToS16(y);
		fPaint = paint ? SkNEW_ARGS(SkPaint, (*paint)) : NULL;
	}

	~DeviceCM() {
		if (NULL != fDevice) {
			fDevice->unlockPixels();
			fDevice->unref();
		}
		SkDELETE(fPaint);
	}

	void updateMC(const SkMatrix& totalMatrix, const SkRegion& totalClip,
			SkRegion* updateClip) {
		int x = fX;
		int y = fY;
		int width = fDevice->width();
		int height = fDevice->height();

		if ((x | y) == 0) { //x==0 && y==0
			fMatrix = &totalMatrix;
			fClip = totalClip;
		} else {
			fMatrixStorage = totalMatrix;
			fMatrixStorage.postTranslate(SkIntToScalar(-x),
					SkIntToScalar(-y));
			fMatrix = &fMatrixStorage;

			totalClip.translate(-x, -y, &fClip);
		}

		fClip.op(0, 0, width, height, SkRegion::kIntersect_Op);

		// intersect clip, but don't translate it (yet)

		if (updateClip) {
			updateClip->op(x, y, x + width, y + height,
					SkRegion::kDifference_Op);
		}

		fDevice->setMatrixClip(*fMatrix, fClip);

#ifdef SK_DEBUG
		if (!fClip.isEmpty()) {
			SkIRect deviceR;
			deviceR.set(0, 0, width, height);
			SkASSERT(deviceR.contains(fClip.getBounds()));
		}
#endif
	}

	void translateClip() {
		if (fX | fY) {
			fClip.translate(fX, fY);
		}
	}

private:
	SkMatrix fMatrixStorage;
};

/*  This is the record we keep for each save/restore level in the stack.
 Since a level optionally copies the matrix and/or stack, we have pointers
 for these fields. If the value is copied for this level, the copy is
 stored in the ...Storage field, and the pointer points to that. If the
 value is not copied for this level, we ignore ...Storage, and just point
 at the corresponding value in the previous level in the stack.
 */
//这个结构体是用来保存stack中的SkDevice的。SkCanvas可以由多个SkDevice构成，每个SkDevice都会有一个对应的在stack中的结构体保存。
//存取这些结构体使用SkDeque。
//注意这个结构体是一个链表结构MCRec* fNext.
class SkCanvas::MCRec {
public:
	MCRec* fNext;
	SkMatrix* fMatrix;    // points to either fMatrixStorage or prev MCRec
	SkRegion* fRegion;    // points to either fRegionStorage or prev MCRec
	SkDrawFilter* fFilter;    // the current filter (or null)

	DeviceCM* fLayer;
	/*  If there are any layers in the stack, this points to the top-most
	 one that is at or below this level in the stack (so we know what
	 bitmap/device to draw into from this level. This value is NOT
	 reference counted, since the real owner is either our fLayer field,
	 or a previous one in a lower level.)
	 */
	DeviceCM* fTopLayer;

	MCRec(const MCRec* prev, int flags) {
		if (NULL != prev) {
			if (flags & SkCanvas::kMatrix_SaveFlag) {
				fMatrixStorage = *prev->fMatrix;
				fMatrix = &fMatrixStorage;
			} else {
				fMatrix = prev->fMatrix;
			}

			if (flags & SkCanvas::kClip_SaveFlag) {
				fRegionStorage = *prev->fRegion;
				fRegion = &fRegionStorage;
			} else {
				fRegion = prev->fRegion;
			}

			fFilter = prev->fFilter;
			fFilter->safeRef();

			fTopLayer = prev->fTopLayer;
		} else {   // no prev
			fMatrixStorage.reset();

			fMatrix = &fMatrixStorage;
			fRegion = &fRegionStorage;
			fFilter = NULL;
			fTopLayer = NULL;
		}
		fLayer = NULL;

		// don't bother initializing fNext
		inc_rec();
	}
	~MCRec() {
		fFilter->safeUnref();
		SkDELETE(fLayer);
		dec_rec();
	}

private:
	SkMatrix fMatrixStorage;
	SkRegion fRegionStorage;
};

class SkDrawIter: public SkDraw {
public:
	SkDrawIter(SkCanvas* canvas, bool skipEmptyClips = true) {
		fCanvas = canvas;
		canvas->updateDeviceCMCache();

		fBounder = canvas->getBounder();
		fCurrLayer = canvas->fMCRec->fTopLayer;
		fSkipEmptyClips = skipEmptyClips;
	}

	bool next() {
		// skip over recs with empty clips
		if (fSkipEmptyClips) {
			while (fCurrLayer && fCurrLayer->fClip.isEmpty()) {
				fCurrLayer = fCurrLayer->fNext;
			}
		}

		if (NULL != fCurrLayer) {
			const DeviceCM* rec = fCurrLayer;

			fMatrix = rec->fMatrix;
			fClip = &rec->fClip;
			fDevice = rec->fDevice;
			fBitmap = &fDevice->accessBitmap(true);
			fLayerX = rec->fX;
			fLayerY = rec->fY;
			fPaint = rec->fPaint;
			SkDEBUGCODE(this->validate();)

			fCurrLayer = rec->fNext;
			if (fBounder) {
				fBounder->setClip(fClip);
			}

			// fCurrLayer may be NULL now

			fCanvas->prepareForDeviceDraw(fDevice);
			return true;
		}
		return false;
	}

	int getX() const {
		return fLayerX;
	}
	int getY() const {
		return fLayerY;
	}
	SkDevice* getDevice() const {
		return fDevice;
	}
	const SkMatrix& getMatrix() const {
		return *fMatrix;
	}
	const SkRegion& getClip() const {
		return *fClip;
	}
	const SkPaint* getPaint() const {
		return fPaint;
	}
private:
	SkCanvas* fCanvas;
	const DeviceCM* fCurrLayer;
	const SkPaint* fPaint;     // May be null.
	int fLayerX;
	int fLayerY;
	SkBool8 fSkipEmptyClips;

	typedef SkDraw INHERITED;
};

/////////////////////////////////////////////////////////////////////////////

class AutoDrawLooper {
public:
	AutoDrawLooper(SkCanvas* canvas, const SkPaint& paint, SkDrawFilter::Type t) :
			fCanvas(canvas), fPaint((SkPaint*) &paint), fType(t) {
		if ((fLooper = paint.getLooper()) != NULL) {
			fLooper->init(canvas, (SkPaint*) &paint);
		} else {
			fOnce = true;
		}
		fFilter = canvas->getDrawFilter();
		fNeedFilterRestore = false;
	}

	~AutoDrawLooper() {
		if (fNeedFilterRestore) {
			SkASSERT(fFilter);
			fFilter->restore(fCanvas, fPaint, fType);
		}
		if (NULL != fLooper) {
			fLooper->restore();
		}
	}

	bool next() {
		SkDrawFilter* filter = fFilter;

		// if we drew earlier with a filter, then we need to restore first
		if (fNeedFilterRestore) {
			SkASSERT(filter);
			filter->restore(fCanvas, fPaint, fType);
			fNeedFilterRestore = false;
		}

		bool result;

		if (NULL != fLooper) {
			result = fLooper->next();
		} else {
			result = fOnce;
			fOnce = false;
		}

		// if we're gonna draw, give the filter a chance to do its work
		if (result && NULL != filter) {
			fNeedFilterRestore = result = filter->filter(fCanvas, fPaint,
					fType);
		}
		return result;
	}

private:
	SkDrawLooper* fLooper;
	SkDrawFilter* fFilter;
	SkCanvas* fCanvas;
	SkPaint* fPaint;
	SkDrawFilter::Type fType;
	bool fOnce;
	bool fNeedFilterRestore;

};

/*  Stack helper for managing a SkBounder. In the destructor, if we were
 given a bounder, we call its commit() method, signifying that we are
 done accumulating bounds for that draw.
 */
class SkAutoBounderCommit {
public:
	SkAutoBounderCommit(SkBounder* bounder) :
			fBounder(bounder) {
	}
	~SkAutoBounderCommit() {
		if (NULL != fBounder) {
			fBounder->commit();
		}
	}
private:
	SkBounder* fBounder;
};

class AutoValidator {
public:
	AutoValidator(SkDevice* device) :
			fDevice(device) {
	}
	~AutoValidator() {
#ifdef SK_DEBUG
		const SkBitmap& bm = fDevice->accessBitmap(false);
		if (bm.config() == SkBitmap::kARGB_4444_Config) {
			for (int y = 0; y < bm.height(); y++) {
				const SkPMColor16* p = bm.getAddr16(0, y);
				for (int x = 0; x < bm.width(); x++) {
					SkPMColor16 c = p[x];
					SkPMColor16Assert(c);
				}
			}
		}
#endif
	}
private:
	SkDevice* fDevice;
};

/** \class SkCanvas
 A Canvas encapsulates all of the state about drawing into a device (bitmap).
 This includes a reference to the device itself, and a stack of matrix/clip
 values. For any given draw call (e.g. drawRect), the geometry of the object
 being drawn is transformed by the concatenation of all the matrices in the
 stack. The transformed geometry is clipped by the intersection of all of
 the clips in the stack.

 While the Canvas holds the state of the drawing device, the state (style)
 of the object being drawn is held by the Paint, which is provided as a
 parameter to each of the draw() methods. The Paint holds attributes such as
 color, typeface, textSize, strokeWidth, shader (e.g. gradients, patterns),
 etc.
 */
//SkCanvas包含了所有绘画到device上需要的状态。包括对device的参考，一堆矩阵和裁剪值。
class SkCanvas: public SkRefCnt {
public:
	/** Construct a canvas with the specified bitmap to draw into.
	 @param bitmap   Specifies a bitmap for the canvas to draw into. Its
	 structure are copied to the canvas.
	 */
	explicit SkCanvas(const SkBitmap& bitmap);
	//通过SkBitmap创建SkCanvas。SkCanvas将拷贝SkBitmap结构
	//虽然SkCanvas是通过SkBitmap创建，但SkCanvas内部使用的是SkDevice，由传入参数SkBitmap创建这个SkDevice.

	/** Construct a canvas with the specified device to draw into.
	 @param device   Specifies a device for the canvas to draw into. The
	 device may be null.
	 */
	explicit SkCanvas(SkDevice* device = NULL);
	//通过SkDevice创建SkCanvas。
	virtual ~SkCanvas();

	///////////////////////////////////////////////////////////////////////////

	/** If this subclass of SkCanvas supports GL viewports, return true and set
	 size (if not null) to the size of the viewport. If it is not supported,
	 ignore vp and return false.
	 */
	virtual bool getViewport(SkIPoint* size) const;
	//子类是否支持GL视口

	/** If this subclass of SkCanvas supports GL viewports, return true and set
	 the viewport to the specified x and y dimensions. If it is not
	 supported, ignore x and y and return false.
	 */
	virtual bool setViewport(int x, int y);
	//子类是否支持GL视口

	/** Return the canvas' device object, which may be null. The device holds
	 the bitmap of the pixels that the canvas draws into. The reference count
	 of the returned device is not changed by this call.
	 */
	SkDevice* getDevice() const;
	//返回SkCanvas的SkDevice，可能为空。

	/** Specify a device for this canvas to draw into. If it is not null, its
	 reference count is incremented. If the canvas was already holding a
	 device, its reference count is decremented. The new device is returned.
	 */
	SkDevice* setDevice(SkDevice* device);
	//设置SkCanvas的SkDevice。如果原来没有SkDevice,则SkDevice参考计数器加1。如果有，会将原来的SkDevice参考计数器减一，并用现在的SkDevice代替。

	/** Specify a bitmap for the canvas to draw into. This is a help method for
	 setDevice(), and it creates a device for the bitmap by calling
	 createDevice(). The structure of the bitmap is copied into the device.
	 */
	virtual SkDevice* setBitmapDevice(const SkBitmap& bitmap);
	//通过SkBitmap来设置SkCanvas的SkDevice。

	///////////////////////////////////////////////////////////////////////////

	enum SaveFlags {
		/** save the matrix state, restoring it on restore() */
		kMatrix_SaveFlag = 0x01,
		/** save the clip state, restoring it on restore() */
		kClip_SaveFlag = 0x02,
		/** the layer needs to support per-pixel alpha */
		kHasAlphaLayer_SaveFlag = 0x04,
		/** the layer needs to support 8-bits per color component */
		kFullColorLayer_SaveFlag = 0x08,
		/** the layer should clip against the bounds argument */
		kClipToLayer_SaveFlag = 0x10,

		// helper masks for common choices
		kMatrixClip_SaveFlag = 0x03,
		kARGB_NoClipLayer_SaveFlag = 0x0F,
		kARGB_ClipLayer_SaveFlag = 0x1F
	};

	/** This call saves the current matrix and clip information, and pushes a
	 copy onto a private stack. Subsequent calls to translate, scale,
	 rotate, skew, concat or clipRect, clipPath all operate on this copy.
	 When the balancing call to restore() is made, this copy is deleted and
	 the previous matrix/clip state is restored.
	 @return The value to pass to restoreToCount() to balance this save()
	 */
	//这个函数保存当前的matrix和clip状态，并把当前的状态做一个备份，压栈。
	//save()之后，所有的操作，translate, scale, rotate, skew, concat or clipRect, clipPath都会在调用restore()函数后去除。
	//所以在save()和restore()直接可以做一些临时操作。
	virtual int save(SaveFlags flags = kMatrixClip_SaveFlag);

	/** This behaves the same as save(), but in addition it allocates an
	 offscreen bitmap. All drawing calls are directed there, and only when
	 the balancing call to restore() is made is that offscreen transfered to
	 the canvas (or the previous layer). Subsequent calls to translate,
	 scale, rotate, skew, concat or clipRect, clipPath all operate on this
	 copy. When the balancing call to restore() is made, this copy is deleted
	 and the previous matrix/clip state is restored.
	 @param bounds (may be null) the maximum size the offscreen bitmap needs
	 to be (in local coordinates)
	 @param paint (may be null) This is copied, and is applied to the
	 offscreen when restore() is called
	 @param flags  LayerFlags
	 @return The value to pass to restoreToCount() to balance this save()
	 */
	//这个函数功能和save()一样。但是这个函数会保存到离屏图像上，所有的绘画函数都会直接画到那里，直到restore()调用。
	virtual int saveLayer(const SkRect* bounds, const SkPaint* paint,
			SaveFlags flags = kARGB_ClipLayer_SaveFlag);

	/** This behaves the same as save(), but in addition it allocates an
	 offscreen bitmap. All drawing calls are directed there, and only when
	 the balancing call to restore() is made is that offscreen transfered to
	 the canvas (or the previous layer). Subsequent calls to translate,
	 scale, rotate, skew, concat or clipRect, clipPath all operate on this
	 copy. When the balancing call to restore() is made, this copy is deleted
	 and the previous matrix/clip state is restored.
	 @param bounds (may be null) the maximum size the offscreen bitmap needs
	 to be (in local coordinates)
	 @param alpha  This is applied to the offscreen when restore() is called.
	 @param flags  LayerFlags
	 @return The value to pass to restoreToCount() to balance this save()
	 */
	int saveLayerAlpha(const SkRect* bounds, U8CPU alpha, SaveFlags flags =
			kARGB_ClipLayer_SaveFlag);

	/** This call balances a previous call to save(), and is used to remove all
	 modifications to the matrix/clip state since the last save call. It is
	 an error to call restore() more times than save() was called.
	 */
	//save()和restore()调用数量必须匹配。
	virtual void restore();

	/** Returns the number of matrix/clip states on the SkCanvas' private stack.
	 This will equal # save() calls - # restore() calls.
	 */
	int getSaveCount() const;

	/** Efficient way to pop any calls to save() that happened after the save
	 count reached saveCount. It is an error for saveCount to be less than
	 getSaveCount()
	 @param saveCount    The number of save() levels to restore from
	 */
	void restoreToCount(int saveCount);

	/** Preconcat the current matrix with the specified translation
	 @param dx   The distance to translate in X
	 @param dy   The distance to translate in Y
	 returns true if the operation succeeded (e.g. did not overflow)
	 */
	//坐标系统位移
	virtual bool translate(SkScalar dx, SkScalar dy);

	/** Preconcat the current matrix with the specified scale.
	 @param sx   The amount to scale in X
	 @param sy   The amount to scale in Y
	 returns true if the operation succeeded (e.g. did not overflow)
	 */
	//坐标系统缩放
	virtual bool scale(SkScalar sx, SkScalar sy);

	/** Preconcat the current matrix with the specified rotation.
	 @param degrees  The amount to rotate, in degrees
	 returns true if the operation succeeded (e.g. did not overflow)
	 */
	//坐标系统旋转
	virtual bool rotate(SkScalar degrees);

	/** Preconcat the current matrix with the specified skew.
	 @param sx   The amount to skew in X
	 @param sy   The amount to skew in Y
	 returns true if the operation succeeded (e.g. did not overflow)
	 */
	//坐标系统倾向
	virtual bool skew(SkScalar sx, SkScalar sy);

	/** Preconcat the current matrix with the specified matrix.
	 @param matrix   The matrix to preconcatenate with the current matrix
	 @return true if the operation succeeded (e.g. did not overflow)
	 */
	//设置坐标系统联系？？？
	virtual bool concat(const SkMatrix& matrix);

	/** Replace the current matrix with a copy of the specified matrix.
	 @param matrix The matrix that will be copied into the current matrix.
	 */
	//设置新坐标系统
	virtual void setMatrix(const SkMatrix& matrix);

	/** Helper for setMatrix(identity). Sets the current matrix to identity.
	 */
	//重置坐标系统
	void resetMatrix();

	/** Modify the current clip with the specified rectangle.
	 @param rect The rect to intersect with the current clip
	 @param op The region op to apply to the current clip
	 @return true if the canvas' clip is non-empty
	 */
	//通过给定的矩形来改变当前的clip。
	virtual bool clipRect(const SkRect& rect, SkRegion::Op op =
			SkRegion::kIntersect_Op);

	/** Modify the current clip with the specified path.
	 @param path The path to apply to the current clip
	 @param op The region op to apply to the current clip
	 @return true if the canvas' new clip is non-empty
	 */
	virtual bool clipPath(const SkPath& path, SkRegion::Op op =
			SkRegion::kIntersect_Op);

	/** Modify the current clip with the specified region. Note that unlike
	 clipRect() and clipPath() which transform their arguments by the current
	 matrix, clipRegion() assumes its argument is already in device
	 coordinates, and so no transformation is performed.
	 @param deviceRgn    The region to apply to the current clip
	 @param op The region op to apply to the current clip
	 @return true if the canvas' new clip is non-empty
	 */
	virtual bool clipRegion(const SkRegion& deviceRgn, SkRegion::Op op =
			SkRegion::kIntersect_Op);

	/** Helper for clipRegion(rgn, kReplace_Op). Sets the current clip to the
	 specified region. This does not intersect or in any other way account
	 for the existing clip region.
	 @param deviceRgn The region to copy into the current clip.
	 @return true if the new clip region is non-empty
	 */
	bool setClipRegion(const SkRegion& deviceRgn) {
		return this->clipRegion(deviceRgn, SkRegion::kReplace_Op);
	}

    /** Enum describing how to treat edges when performing quick-reject tests
        of a geometry against the current clip. Treating them as antialiased
        (kAA_EdgeType) will take into account the extra pixels that may be drawn
        if the edge does not lie exactly on a device pixel boundary (after being
        transformed by the current matrix).
    */
    //处理反锯齿问题
    //判断当前的clip和给定的矩形是否有交错，添加antialiased参数会使得判断更精确
    enum EdgeType {
        /** Treat the edges as B&W (not antialiased) for the purposes of testing
            against the current clip
        */
        kBW_EdgeType,
        /** Treat the edges as antialiased for the purposes of testing
            against the current clip
        */
        kAA_EdgeType
    };

    /** Return true if the specified rectangle, after being transformed by the
        current matrix, would lie completely outside of the current clip. Call
        this to check if an area you intend to draw into is clipped out (and
        therefore you can skip making the draw calls).
        @param rect the rect to compare with the current clip
        @param et  specifies how to treat the edges (see EdgeType)
        @return true if the rect (transformed by the canvas' matrix) does not
                     intersect with the canvas' clip
    */
    //判断当前的clip和给定的矩形是否有交错，如果没有交错，就返回true
    bool quickReject(const SkRect& rect, EdgeType et) const;

    /** Return true if the specified path, after being transformed by the
        current matrix, would lie completely outside of the current clip. Call
        this to check if an area you intend to draw into is clipped out (and
        therefore you can skip making the draw calls). Note, for speed it may
        return false even if the path itself might not intersect the clip
        (i.e. the bounds of the path intersects, but the path does not).
        @param path The path to compare with the current clip
        @param et  specifies how to treat the edges (see EdgeType)
        @return true if the path (transformed by the canvas' matrix) does not
                     intersect with the canvas' clip
    */
    bool quickReject(const SkPath& path, EdgeType et) const;

    /** Return true if the horizontal band specified by top and bottom is
        completely clipped out. This is a conservative calculation, meaning
        that it is possible that if the method returns false, the band may still
        in fact be clipped out, but the converse is not true. If this method
        returns true, then the band is guaranteed to be clipped out.
        @param top  The top of the horizontal band to compare with the clip
        @param bottom The bottom of the horizontal and to compare with the clip
        @return true if the horizontal band is completely clipped out (i.e. does
                     not intersect the current clip)
    */
    bool quickRejectY(SkScalar top, SkScalar bottom, EdgeType et) const;

    /** Return the bounds of the current clip (in local coordinates) in the
        bounds parameter, and return true if it is non-empty. This can be useful
        in a way similar to quickReject, in that it tells you that drawing
        outside of these bounds will be clipped out.
    */
    //返回当前clip的边界矩形
    bool getClipBounds(SkRect* bounds, EdgeType et = kAA_EdgeType) const;

    /** Fill the entire canvas' bitmap (restricted to the current clip) with the
        specified ARGB color, using the specified PorterDuff mode.
        @param a    the alpha component (0..255) of the color to fill the canvas
        @param r    the red component (0..255) of the color to fill the canvas
        @param g    the green component (0..255) of the color to fill the canvas
        @param b    the blue component (0..255) of the color to fill the canvas
        @param mode the mode to apply the color in (defaults to SrcOver)
    */
    //基于PorterDuff模式使用ARGB来填充整个canvas'的bitmap.
    void drawARGB(U8CPU a, U8CPU r, U8CPU g, U8CPU b,
                  SkPorterDuff::Mode mode = SkPorterDuff::kSrcOver_Mode);

    /** Fill the entire canvas' bitmap (restricted to the current clip) with the
        specified color and porter-duff xfermode.
        @param color    the color to draw with
        @param mode the mode to apply the color in (defaults to SrcOver)
    */
    //基于PorterDuff模式使用SkColor来填充整个canvas'的bitmap.
    void drawColor(SkColor color,
                   SkPorterDuff::Mode mode = SkPorterDuff::kSrcOver_Mode);

    /** Fill the entire canvas' bitmap (restricted to the current clip) with the
        specified paint.
        @param paint    The paint used to fill the canvas
    */
    //基于PorterDuff模式使用SkPaint来填充整个canvas'的bitmap。注意这里的paint是有自己的一套绘画标准的。
    virtual void drawPaint(const SkPaint& paint);

    enum PointMode {
        /** drawPoints draws each point separately */
        kPoints_PointMode,
        /** drawPoints draws each pair of points as a line segment */
        kLines_PointMode,
        /** drawPoints draws the array of points as a polygon */
        kPolygon_PointMode
    };

    /** Draw a series of points, interpreted based on the PointMode mode. For
        all modes, the count parameter is interpreted as the total number of
        points. For kLine mode, count/2 line segments are drawn.
        For kPoint mode, each point is drawn centered at its coordinate, and its
        size is specified by the paint's stroke-width. It draws as a square,
        unless the paint's cap-type is round, in which the points are drawn as
        circles.
        For kLine mode, each pair of points is drawn as a line segment,
        respecting the paint's settings for cap/join/width.
        For kPolygon mode, the entire array is drawn as a series of connected
        line segments.
        Note that, while similar, kLine and kPolygon modes draw slightly
        differently than the equivalent path built with a series of moveto,
        lineto calls, in that the path will draw all of its contours at once,
        with no interactions if contours intersect each other (think XOR
        xfermode). drawPoints always draws each element one at a time.
        @param mode     PointMode specifying how to draw the array of points.
        @param count    The number of points in the array
        @param pts      Array of points to draw
        @param paint    The paint used to draw the points
    */
    //根据PointMode画一系列的点
    //kPoints Mode: 在每个给定的坐标点画点。点的宽度由SkPaint的stroke-width决定。形状由SkPaint的cap-type决定，通常画正方形，如果指定圆形，就画圆形。
    //kLines Mode: 画count/2的线段。如果传两个点，则画一条线段，如果传4个点，画两条线段: (pts[0] <--> pts[1]), (pts[2] <--> pts[3])。 注意pts[]的个数就是count的值。
    //kPolygon Mode: 画多边形。如果传两个点，则画一条线段，如果传4个点，画三条线段: (pts[0] <--> pts[1]), (pts[1] <--> pts[2]), (pts[2] <--> pts[3])。 注意pts[]的个数就是count的值。

    virtual void drawPoints(PointMode mode, size_t count, const SkPoint pts[],
                            const SkPaint& paint);

    /** Helper method for drawing a single point. See drawPoints() for a more
        details.
    */
    void drawPoint(SkScalar x, SkScalar y, const SkPaint& paint);

    /** Draws a single pixel in the specified color.
        @param x        The X coordinate of which pixel to draw
        @param y        The Y coordiante of which pixel to draw
        @param color    The color to draw
    */
    void drawPoint(SkScalar x, SkScalar y, SkColor color);

    /** Draw a line segment with the specified start and stop x,y coordinates,
        using the specified paint. NOTE: since a line is always "framed", the
        paint's Style is ignored.
        @param x0    The x-coordinate of the start point of the line
        @param y0    The y-coordinate of the start point of the line
        @param x1    The x-coordinate of the end point of the line
        @param y1    The y-coordinate of the end point of the line
        @param paint The paint used to draw the line
    */
    void drawLine(SkScalar x0, SkScalar y0, SkScalar x1, SkScalar y1,
                  const SkPaint& paint);

    /** Draw the specified rectangle using the specified paint. The rectangle
        will be filled or stroked based on the Style in the paint.
        @param rect     The rect to be drawn
        @param paint    The paint used to draw the rect
    */
    virtual void drawRect(const SkRect& rect, const SkPaint& paint);

    /** Draw the specified rectangle using the specified paint. The rectangle
        will be filled or framed based on the Style in the paint.
        @param rect     The rect to be drawn
        @param paint    The paint used to draw the rect
    */
    //画矩形，SkIRect和SkRect的区别是两者的坐标系统表示方法不同，SkIRect使用SkScalar来表示，SkRect使用int32_t来表示。
    //SkScalar有两种表示方法：float和int32。如果使用float表示法，怎理论上将精度会提高
    void drawIRect(const SkIRect& rect, const SkPaint& paint)
    {
        SkRect r;
        r.set(rect);    // promotes the ints to scalars
        this->drawRect(r, paint);
    }

    /** Draw the specified rectangle using the specified paint. The rectangle
        will be filled or framed based on the Style in the paint.
        @param left     The left side of the rectangle to be drawn
        @param top      The top side of the rectangle to be drawn
        @param right    The right side of the rectangle to be drawn
        @param bottom   The bottom side of the rectangle to be drawn
        @param paint    The paint used to draw the rect
    */
    void drawRectCoords(SkScalar left, SkScalar top, SkScalar right,
                        SkScalar bottom, const SkPaint& paint);

    /** Draw the specified oval using the specified paint. The oval will be
        filled or framed based on the Style in the paint.
        @param oval     The rectangle bounds of the oval to be drawn
        @param paint    The paint used to draw the oval
    */
    //画椭圆
    void drawOval(const SkRect& oval, const SkPaint&);

    /** Draw the specified circle using the specified paint. If radius is <= 0,
        then nothing will be drawn. The circle will be filled
        or framed based on the Style in the paint.
        @param cx       The x-coordinate of the center of the cirle to be drawn
        @param cy       The y-coordinate of the center of the cirle to be drawn
        @param radius   The radius of the cirle to be drawn
        @param paint    The paint used to draw the circle
    */
    void drawCircle(SkScalar cx, SkScalar cy, SkScalar radius,
                    const SkPaint& paint);

    /** Draw the specified arc, which will be scaled to fit inside the
        specified oval. If the sweep angle is >= 360, then the oval is drawn
        completely. Note that this differs slightly from SkPath::arcTo, which
        treats the sweep angle mod 360.
        @param oval The bounds of oval used to define the shape of the arc
        @param startAngle Starting angle (in degrees) where the arc begins
        @param sweepAngle Sweep angle (in degrees) measured clockwise
        @param useCenter true means include the center of the oval. For filling
                         this will draw a wedge. False means just use the arc.
        @param paint    The paint used to draw the arc
    */
    void drawArc(const SkRect& oval, SkScalar startAngle, SkScalar sweepAngle,
                 bool useCenter, const SkPaint& paint);

    /** Draw the specified round-rect using the specified paint. The round-rect
        will be filled or framed based on the Style in the paint.
        @param rect     The rectangular bounds of the roundRect to be drawn
        @param rx       The x-radius of the oval used to round the corners
        @param ry       The y-radius of the oval used to round the corners
        @param paint    The paint used to draw the roundRect
    */
    void drawRoundRect(const SkRect& rect, SkScalar rx, SkScalar ry,
                       const SkPaint& paint);

    /** Draw the specified path using the specified paint. The path will be
        filled or framed based on the Style in the paint.
        @param path     The path to be drawn
        @param paint    The paint used to draw the path
    */
    //画路径，比较丰富
    virtual void drawPath(const SkPath& path, const SkPaint& paint);

    /** Draw the specified bitmap, with its top/left corner at (x,y), using the
        specified paint, transformed by the current matrix. Note: if the paint
        contains a maskfilter that generates a mask which extends beyond the
        bitmap's original width/height, then the bitmap will be drawn as if it
        were in a Shader with CLAMP mode. Thus the color outside of the original
        width/height will be the edge color replicated.
        @param bitmap   The bitmap to be drawn
        @param left     The position of the left side of the bitmap being drawn
        @param top      The position of the top side of the bitmap being drawn
        @param paint    The paint used to draw the bitmap, or NULL
    */
    //把指定的图像从left, top点起根据当前坐标系统画到当前canvas上。
    //内部实现中，限定了宽和高必须小于32767，否则不画。内部是由了blit函数。
    virtual void drawBitmap(const SkBitmap& bitmap, SkScalar left, SkScalar top,
                            const SkPaint* paint = NULL);

    /** Draw the specified bitmap, with the specified matrix applied (before the
        canvas' matrix is applied).
        @param bitmap   The bitmap to be drawn
        @param src      Optional: specify the subset of the bitmap to be drawn
        @param dst      The destination rectangle where the scaled/translated
                        image will be drawn
        @param paint    The paint used to draw the bitmap, or NULL
    */
    //把指定的图像中src矩形画到当前canvas的dst矩形中去。
    virtual void drawBitmapRect(const SkBitmap& bitmap, const SkIRect* src,
                                const SkRect& dst, const SkPaint* paint = NULL);

    virtual void drawBitmapMatrix(const SkBitmap& bitmap, const SkMatrix& m,
                                  const SkPaint* paint = NULL);

    /** Draw the specified bitmap, with its top/left corner at (x,y),
        NOT transformed by the current matrix. Note: if the paint
        contains a maskfilter that generates a mask which extends beyond the
        bitmap's original width/height, then the bitmap will be drawn as if it
        were in a Shader with CLAMP mode. Thus the color outside of the original
        width/height will be the edge color replicated.
        @param bitmap   The bitmap to be drawn
        @param left     The position of the left side of the bitmap being drawn
        @param top      The position of the top side of the bitmap being drawn
        @param paint    The paint used to draw the bitmap, or NULL
    */
    virtual void drawSprite(const SkBitmap& bitmap, int left, int top,
                            const SkPaint* paint = NULL);

    /** Draw the text, with origin at (x,y), using the specified paint.
        The origin is interpreted based on the Align setting in the paint.
        @param text The text to be drawn
        @param byteLength   The number of bytes to read from the text parameter
        @param x        The x-coordinate of the origin of the text being drawn
        @param y        The y-coordinate of the origin of the text being drawn
        @param paint    The paint used for the text (e.g. color, size, style)
    */
    //根据SkPaint中的Align设置在(x,y)画字符。
    virtual void drawText(const void* text, size_t byteLength, SkScalar x,
                          SkScalar y, const SkPaint& paint);

    /** Draw the text, with each character/glyph origin specified by the pos[]
        array. The origin is interpreted by the Align setting in the paint.
        @param text The text to be drawn
        @param byteLength   The number of bytes to read from the text parameter
        @param pos      Array of positions, used to position each character
        @param paint    The paint used for the text (e.g. color, size, style)
        */
    virtual void drawPosText(const void* text, size_t byteLength,
                             const SkPoint pos[], const SkPaint& paint);

    /** Draw the text, with each character/glyph origin specified by the x
        coordinate taken from the xpos[] array, and the y from the constY param.
        The origin is interpreted by the Align setting in the paint.
        @param text The text to be drawn
        @param byteLength   The number of bytes to read from the text parameter
        @param xpos     Array of x-positions, used to position each character
        @param constY   The shared Y coordinate for all of the positions
        @param paint    The paint used for the text (e.g. color, size, style)
        */
    virtual void drawPosTextH(const void* text, size_t byteLength,
                              const SkScalar xpos[], SkScalar constY,
                              const SkPaint& paint);

    /** Draw the text, with origin at (x,y), using the specified paint, along
        the specified path. The paint's Align setting determins where along the
        path to start the text.
        @param text The text to be drawn
        @param byteLength   The number of bytes to read from the text parameter
        @param path         The path the text should follow for its baseline
        @param hOffset      The distance along the path to add to the text's
                            starting position
        @param vOffset      The distance above(-) or below(+) the path to
                            position the text
        @param paint        The paint used for the text
    */
    void drawTextOnPathHV(const void* text, size_t byteLength,
                          const SkPath& path, SkScalar hOffset,
                          SkScalar vOffset, const SkPaint& paint);

    /** Draw the text, with origin at (x,y), using the specified paint, along
        the specified path. The paint's Align setting determins where along the
        path to start the text.
        @param text The text to be drawn
        @param byteLength   The number of bytes to read from the text parameter
        @param path         The path the text should follow for its baseline
        @param matrix       (may be null) Applied to the text before it is
                            mapped onto the path
        @param paint        The paint used for the text
        */
    virtual void drawTextOnPath(const void* text, size_t byteLength,
                                const SkPath& path, const SkMatrix* matrix,
                                const SkPaint& paint);

    /** Draw the picture into this canvas. This method effective brackets the
        playback of the picture's draw calls with save/restore, so the state
        of this canvas will be unchanged after this call. This contrasts with
        the more immediate method SkPicture::draw(), which does not bracket
        the canvas with save/restore, thus the canvas may be left in a changed
        state after the call.
        @param picture The recorded drawing commands to playback into this
                       canvas.
    */
    virtual void drawPicture(SkPicture& picture);

    enum VertexMode {
        kTriangles_VertexMode,
        kTriangleStrip_VertexMode,
        kTriangleFan_VertexMode
    };

    /** Draw the array of vertices, interpreted as triangles (based on mode).
        @param vmode How to interpret the array of vertices
        @param vertexCount The number of points in the vertices array (and
                    corresponding texs and colors arrays if non-null)
        @param vertices Array of vertices for the mesh
        @param texs May be null. If not null, specifies the coordinate
                             in texture space for each vertex.
        @param colors May be null. If not null, specifies a color for each
                      vertex, to be interpolated across the triangle.
        @param xmode Used if both texs and colors are present. In this
                    case the colors are combined with the texture using mode,
                    before being drawn using the paint. If mode is null, then
                    the porter-duff MULTIPLY mode is used.
        @param indices If not null, array of indices to reference into the
                    vertex (texs, colors) array.
        @param indexCount number of entries in the indices array (if not null)
        @param paint Specifies the shader/texture if present.
    */
    virtual void drawVertices(VertexMode vmode, int vertexCount,
                              const SkPoint vertices[], const SkPoint texs[],
                              const SkColor colors[], SkXfermode* xmode,
                              const uint16_t indices[], int indexCount,
                              const SkPaint& paint);

    //////////////////////////////////////////////////////////////////////////

    /** Get the current bounder object.
        The bounder's reference count is unchaged.
        @return the canva's bounder (or NULL).
    */
    SkBounder*  getBounder() const { return fBounder; }

    /** Set a new bounder (or NULL).
        Pass NULL to clear any previous bounder.
        As a convenience, the parameter passed is also returned.
        If a previous bounder exists, its reference count is decremented.
        If bounder is not NULL, its reference count is incremented.
        @param bounder the new bounder (or NULL) to be installed in the canvas
        @return the set bounder object
    */
    virtual SkBounder* setBounder(SkBounder* bounder);

    /** Get the current filter object. The filter's reference count is not
        affected. The filter is part of the state this is affected by
        save/restore.
        @return the canvas' filter (or NULL).
    */
    //获得当前canvas对应device的filter。filter的作用不明朗？
    SkDrawFilter* getDrawFilter() const;

    /** Set the new filter (or NULL). Pass NULL to clear any existing filter.
        As a convenience, the parameter is returned. If an existing filter
        exists, its refcnt is decrement. If the new filter is not null, its
        refcnt is incremented. The filter is part of the state this is affected
        by save/restore.
        @param filter the new filter (or NULL)
        @return the new filter
    */
    virtual SkDrawFilter* setDrawFilter(SkDrawFilter* filter);

    //////////////////////////////////////////////////////////////////////////

    /** Return the current matrix on the canvas.
        This does not account for the translate in any of the devices.
        @return The current matrix on the canvas.
    */
    const SkMatrix& getTotalMatrix() const;

    /** Return the current device clip (concatenation of all clip calls).
        This does not account for the translate in any of the devices.
        @return the current device clip (concatenation of all clip calls).
    */
    const SkRegion& getTotalClip() const;

    /** May be overridden by subclasses. This returns a compatible device
        for this canvas, with the specified config/width/height. If isOpaque
        is true, then the underlying bitmap is optimized to assume that every
        pixel will be drawn to, and thus it does not need to clear the alpha
        channel ahead of time (assuming the specified config supports per-pixel
        alpha.) If isOpaque is false, then the bitmap should clear its alpha
        channel.
    */
    //创建device。注意，这个函数可以由子类覆盖。也就是说为2D加速提供了可能。
    virtual SkDevice* createDevice(SkBitmap::Config, int width, int height,
                                   bool isOpaque, bool isForLayer);

    ///////////////////////////////////////////////////////////////////////////

    /** After calling saveLayer(), there can be any number of devices that make
        up the top-most drawing area. LayerIter can be used to iterate through
        those devices. Note that the iterator is only valid until the next API
        call made on the canvas. Ownership of all pointers in the iterator stays
        with the canvas, so none of them should be modified or deleted.
    */
    class LayerIter /*: SkNoncopyable*/ {
    public:
        /** Initialize iterator with canvas, and set values for 1st device */
        LayerIter(SkCanvas*, bool skipEmptyClips);
        ~LayerIter();

        /** Return true if the iterator is done */
        bool done() const { return fDone; }
        /** Cycle to the next device */
        void next();

        // These reflect the current device in the iterator

        SkDevice*       device() const;
        const SkMatrix& matrix() const;
        const SkRegion& clip() const;
        const SkPaint&  paint() const;
        int             x() const;
        int             y() const;

    private:
        // used to embed the SkDrawIter object directly in our instance, w/o
        // having to expose that class def to the public. There is an assert
        // in our constructor to ensure that fStorage is large enough
        // (though needs to be a compile-time-assert!). We use intptr_t to work
        // safely with 32 and 64 bit machines (to ensure the storage is enough)
        intptr_t          fStorage[12];
        class SkDrawIter* fImpl;    // this points at fStorage
        SkPaint           fDefaultPaint;
        bool              fDone;
    };

protected:
    // all of the drawBitmap variants call this guy
    virtual void commonDrawBitmap(const SkBitmap&, const SkMatrix& m,
                                  const SkPaint& paint);

private:
    class MCRec;

    //这个变量是整个canvas的layer stack.贯穿整个canvas始终
    SkDeque     fMCStack;
    // points to top of stack
    MCRec*      fMCRec;
    // the first N recs that can fit here mean we won't call malloc
    uint32_t    fMCRecStorage[32];//这里是canvas的缓存机制。

    SkBounder*  fBounder;

    void prepareForDeviceDraw(SkDevice*);

    bool fDeviceCMDirty;            // cleared by updateDeviceCMCache()
    void updateDeviceCMCache();

    friend class SkDrawIter;    // needs setupDrawForLayerDevice()

    SkDevice* init(SkDevice*);
    void internalDrawBitmap(const SkBitmap&, const SkMatrix& m,
                                  const SkPaint* paint);
    void drawDevice(SkDevice*, int x, int y, const SkPaint*);
    // shared by save() and saveLayer()
    int internalSave(SaveFlags flags);
    void internalRestore();

    /*  These maintain a cache of the clip bounds in local coordinates,
        (converted to 2s-compliment if floats are slow).
     */
    mutable SkRectCompareType fLocalBoundsCompareType;
    mutable bool              fLocalBoundsCompareTypeDirty;

    const SkRectCompareType& getLocalClipBoundsCompareType() const {
        if (fLocalBoundsCompareTypeDirty) {
            this->computeLocalClipBoundsCompareType();
            fLocalBoundsCompareTypeDirty = false;
        }
        return fLocalBoundsCompareType;
    }
    void computeLocalClipBoundsCompareType() const;
};

/** Stack helper class to automatically call restoreToCount() on the canvas
    when this object goes out of scope. Use this to guarantee that the canvas
    is restored to a known state.
*/
class SkAutoCanvasRestore : SkNoncopyable {
public:
    SkAutoCanvasRestore(SkCanvas* canvas, bool doSave) : fCanvas(canvas) {
        SkASSERT(canvas);
        fSaveCount = canvas->getSaveCount();
        if (doSave) {
            canvas->save();
        }
    }
    ~SkAutoCanvasRestore() {
        fCanvas->restoreToCount(fSaveCount);
    }

private:
    SkCanvas*   fCanvas;
    int         fSaveCount;
};
