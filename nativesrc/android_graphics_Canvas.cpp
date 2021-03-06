/*
 * android_graphics_Canvas.cpp
 *
 *  Created on: Apr 17, 2013
 *      Author: lonerr
 */
#include "jni.h"
#include "android_graphics_Canvas.h"
#include "SkCanvas.h"
#include "SkDevice.h"
#include "SkDrawFilter.h"
#include "SkGraphics.h"
#include "SkImageRef_GlobalPool.h"
#include "SkPorterDuff.h"
#include "SkShader.h"
#include "SkTemplates.h"
/*
 * Class:     android_graphics_Canvas
 * Method:    isOpaque
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_android_graphics_Canvas_isOpaque
  (JNIEnv *, jobject);

/*
 * Class:     android_graphics_Canvas
 * Method:    getWidth
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_android_graphics_Canvas_getWidth
  (JNIEnv *, jobject);

/*
 * Class:     android_graphics_Canvas
 * Method:    getHeight
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_android_graphics_Canvas_getHeight
  (JNIEnv *, jobject);

/*
 * Class:     android_graphics_Canvas
 * Method:    save
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_android_graphics_Canvas_save__
  (JNIEnv *, jobject);

/*
 * Class:     android_graphics_Canvas
 * Method:    save
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_android_graphics_Canvas_save__I
  (JNIEnv *, jobject, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    restore
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_restore
  (JNIEnv *, jobject);

/*
 * Class:     android_graphics_Canvas
 * Method:    getSaveCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_android_graphics_Canvas_getSaveCount
  (JNIEnv *, jobject);

/*
 * Class:     android_graphics_Canvas
 * Method:    restoreToCount
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_restoreToCount
  (JNIEnv *, jobject, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    translate
 * Signature: (FF)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_translate
  (JNIEnv *, jobject, jfloat, jfloat);

/*
 * Class:     android_graphics_Canvas
 * Method:    scale
 * Signature: (FF)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_scale
  (JNIEnv *, jobject, jfloat, jfloat);

/*
 * Class:     android_graphics_Canvas
 * Method:    rotate
 * Signature: (F)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_rotate
  (JNIEnv *, jobject, jfloat);

/*
 * Class:     android_graphics_Canvas
 * Method:    skew
 * Signature: (FF)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_skew
  (JNIEnv *, jobject, jfloat, jfloat);

/*
 * Class:     android_graphics_Canvas
 * Method:    clipRect
 * Signature: (Landroid/graphics/RectF;)Z
 */
JNIEXPORT jboolean JNICALL Java_android_graphics_Canvas_clipRect__Landroid_graphics_RectF_2
  (JNIEnv *, jobject, jobject);

/*
 * Class:     android_graphics_Canvas
 * Method:    clipRect
 * Signature: (Landroid/graphics/Rect;)Z
 */
JNIEXPORT jboolean JNICALL Java_android_graphics_Canvas_clipRect__Landroid_graphics_Rect_2
  (JNIEnv *, jobject, jobject);

/*
 * Class:     android_graphics_Canvas
 * Method:    clipRect
 * Signature: (FFFF)Z
 */
JNIEXPORT jboolean JNICALL Java_android_graphics_Canvas_clipRect__FFFF
  (JNIEnv *, jobject, jfloat, jfloat, jfloat, jfloat);

/*
 * Class:     android_graphics_Canvas
 * Method:    clipRect
 * Signature: (IIII)Z
 */
JNIEXPORT jboolean JNICALL Java_android_graphics_Canvas_clipRect__IIII
  (JNIEnv *, jobject, jint, jint, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    drawPoints
 * Signature: ([FIILandroid/graphics/Paint;)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_drawPoints
  (JNIEnv *, jobject, jfloatArray, jint, jint, jobject);

/*
 * Class:     android_graphics_Canvas
 * Method:    drawPoint
 * Signature: (FFLandroid/graphics/Paint;)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_drawPoint
  (JNIEnv *, jobject, jfloat, jfloat, jobject);

/*
 * Class:     android_graphics_Canvas
 * Method:    drawLines
 * Signature: ([FIILandroid/graphics/Paint;)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_drawLines
  (JNIEnv *, jobject, jfloatArray, jint, jint, jobject);

/*
 * Class:     android_graphics_Canvas
 * Method:    freeCaches
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_freeCaches
  (JNIEnv *, jclass);

/*
 * Class:     android_graphics_Canvas
 * Method:    freeTextLayoutCaches
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_freeTextLayoutCaches
  (JNIEnv *, jclass);

/*
 * Class:     android_graphics_Canvas
 * Method:    initRaster
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_android_graphics_Canvas_initRaster
  (JNIEnv * env, jclass cls, jint nBitmap){
	int ptr = nBitmap;
	SkBitmap* bitmap = ptr;
	SkCanvas* canvas = new SkCanvas();
	return 0;
}

/*
 * Class:     android_graphics_Canvas
 * Method:    native_setBitmap
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1setBitmap
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_saveLayer
 * Signature: (ILandroid/graphics/RectF;II)I
 */
JNIEXPORT jint JNICALL Java_android_graphics_Canvas_native_1saveLayer__ILandroid_graphics_RectF_2II
  (JNIEnv *, jclass, jint, jobject, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_saveLayer
 * Signature: (IFFFFII)I
 */
JNIEXPORT jint JNICALL Java_android_graphics_Canvas_native_1saveLayer__IFFFFII
  (JNIEnv *, jclass, jint, jfloat, jfloat, jfloat, jfloat, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_saveLayerAlpha
 * Signature: (ILandroid/graphics/RectF;II)I
 */
JNIEXPORT jint JNICALL Java_android_graphics_Canvas_native_1saveLayerAlpha__ILandroid_graphics_RectF_2II
  (JNIEnv *, jclass, jint, jobject, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_saveLayerAlpha
 * Signature: (IFFFFII)I
 */
JNIEXPORT jint JNICALL Java_android_graphics_Canvas_native_1saveLayerAlpha__IFFFFII
  (JNIEnv *, jclass, jint, jfloat, jfloat, jfloat, jfloat, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_concat
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1concat
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_setMatrix
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1setMatrix
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_clipRect
 * Signature: (IFFFFI)Z
 */
JNIEXPORT jboolean JNICALL Java_android_graphics_Canvas_native_1clipRect
  (JNIEnv *, jclass, jint, jfloat, jfloat, jfloat, jfloat, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_clipPath
 * Signature: (III)Z
 */
JNIEXPORT jboolean JNICALL Java_android_graphics_Canvas_native_1clipPath
  (JNIEnv *, jclass, jint, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_clipRegion
 * Signature: (III)Z
 */
JNIEXPORT jboolean JNICALL Java_android_graphics_Canvas_native_1clipRegion
  (JNIEnv *, jclass, jint, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    nativeSetDrawFilter
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_nativeSetDrawFilter
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_getClipBounds
 * Signature: (ILandroid/graphics/Rect;)Z
 */
JNIEXPORT jboolean JNICALL Java_android_graphics_Canvas_native_1getClipBounds
  (JNIEnv *, jclass, jint, jobject);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_getCTM
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1getCTM
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_quickReject
 * Signature: (ILandroid/graphics/RectF;I)Z
 */
JNIEXPORT jboolean JNICALL Java_android_graphics_Canvas_native_1quickReject__ILandroid_graphics_RectF_2I
  (JNIEnv *, jclass, jint, jobject, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_quickReject
 * Signature: (III)Z
 */
JNIEXPORT jboolean JNICALL Java_android_graphics_Canvas_native_1quickReject__III
  (JNIEnv *, jclass, jint, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_quickReject
 * Signature: (IFFFFI)Z
 */
JNIEXPORT jboolean JNICALL Java_android_graphics_Canvas_native_1quickReject__IFFFFI
  (JNIEnv *, jclass, jint, jfloat, jfloat, jfloat, jfloat, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawRGB
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawRGB
  (JNIEnv *, jclass, jint, jint, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawARGB
 * Signature: (IIIII)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawARGB
  (JNIEnv *, jclass, jint, jint, jint, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawColor
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawColor__II
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawColor
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawColor__III
  (JNIEnv *, jclass, jint, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawPaint
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawPaint
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawLine
 * Signature: (IFFFFI)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawLine
  (JNIEnv *, jclass, jint, jfloat, jfloat, jfloat, jfloat, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawRect
 * Signature: (ILandroid/graphics/RectF;I)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawRect__ILandroid_graphics_RectF_2I
  (JNIEnv *, jclass, jint, jobject, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawRect
 * Signature: (IFFFFI)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawRect__IFFFFI
  (JNIEnv *, jclass, jint, jfloat, jfloat, jfloat, jfloat, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawOval
 * Signature: (ILandroid/graphics/RectF;I)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawOval
  (JNIEnv *, jclass, jint, jobject, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawCircle
 * Signature: (IFFFI)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawCircle
  (JNIEnv *, jclass, jint, jfloat, jfloat, jfloat, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawArc
 * Signature: (ILandroid/graphics/RectF;FFZI)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawArc
  (JNIEnv *, jclass, jint, jobject, jfloat, jfloat, jboolean, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawRoundRect
 * Signature: (ILandroid/graphics/RectF;FFI)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawRoundRect
  (JNIEnv *, jclass, jint, jobject, jfloat, jfloat, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawPath
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawPath
  (JNIEnv *, jclass, jint, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawBitmap
 * Signature: (IIFFIIII)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawBitmap__IIFFIIII
  (JNIEnv *, jobject, jint, jint, jfloat, jfloat, jint, jint, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawBitmap
 * Signature: (IILandroid/graphics/Rect;Landroid/graphics/RectF;III)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawBitmap__IILandroid_graphics_Rect_2Landroid_graphics_RectF_2III
  (JNIEnv *, jobject, jint, jint, jobject, jobject, jint, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawBitmap
 * Signature: (IILandroid/graphics/Rect;Landroid/graphics/Rect;III)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawBitmap__IILandroid_graphics_Rect_2Landroid_graphics_Rect_2III
  (JNIEnv *, jclass, jint, jint, jobject, jobject, jint, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawBitmap
 * Signature: (I[IIIFFIIZI)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawBitmap__I_3IIIFFIIZI
  (JNIEnv *, jclass, jint, jintArray, jint, jint, jfloat, jfloat, jint, jint, jboolean, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    nativeDrawBitmapMatrix
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_nativeDrawBitmapMatrix
  (JNIEnv *, jclass, jint, jint, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    nativeDrawBitmapMesh
 * Signature: (IIII[FI[III)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_nativeDrawBitmapMesh
  (JNIEnv *, jclass, jint, jint, jint, jint, jfloatArray, jint, jintArray, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    nativeDrawVertices
 * Signature: (III[FI[FI[II[SIII)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_nativeDrawVertices
  (JNIEnv *, jclass, jint, jint, jint, jfloatArray, jint, jfloatArray, jint, jintArray, jint, jshortArray, jint, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawText
 * Signature: (I[CIIFFII)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawText__I_3CIIFFII
  (JNIEnv *, jclass, jint, jcharArray, jint, jint, jfloat, jfloat, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawText
 * Signature: (ILjava/lang/String;IIFFII)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawText__ILjava_lang_String_2IIFFII
  (JNIEnv *, jclass, jint, jstring, jint, jint, jfloat, jfloat, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawTextRun
 * Signature: (ILjava/lang/String;IIIIFFII)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawTextRun__ILjava_lang_String_2IIIIFFII
  (JNIEnv *, jclass, jint, jstring, jint, jint, jint, jint, jfloat, jfloat, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawTextRun
 * Signature: (I[CIIIIFFII)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawTextRun__I_3CIIIIFFII
  (JNIEnv *, jclass, jint, jcharArray, jint, jint, jint, jint, jfloat, jfloat, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawPosText
 * Signature: (I[CII[FI)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawPosText__I_3CII_3FI
  (JNIEnv *, jclass, jint, jcharArray, jint, jint, jfloatArray, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawPosText
 * Signature: (ILjava/lang/String;[FI)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawPosText__ILjava_lang_String_2_3FI
  (JNIEnv *, jclass, jint, jstring, jfloatArray, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawTextOnPath
 * Signature: (I[CIIIFFII)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawTextOnPath__I_3CIIIFFII
  (JNIEnv *, jclass, jint, jcharArray, jint, jint, jint, jfloat, jfloat, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawTextOnPath
 * Signature: (ILjava/lang/String;IFFII)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawTextOnPath__ILjava_lang_String_2IFFII
  (JNIEnv *, jclass, jint, jstring, jint, jfloat, jfloat, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    native_drawPicture
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_native_1drawPicture
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     android_graphics_Canvas
 * Method:    finalizer
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_android_graphics_Canvas_finalizer
  (JNIEnv *, jclass, jint);
