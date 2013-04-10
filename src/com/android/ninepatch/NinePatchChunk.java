/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.ninepatch;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Transform;

/**
 * The chunk information for a nine patch.
 *
 * This does not represent the bitmap, only the chunk info responsible for the padding and the
 * stretching areas.
 *
 * Since android.graphics.drawable.NinePatchDrawable and android.graphics.NinePatch both deal with
 * the nine patch chunk as a byte[], this class is converted to and from byte[] through
 * serialization.
 *
 * This is meant to be used with the NinePatch_Delegate in Layoutlib API 5+.
 */
public class NinePatchChunk implements Serializable {

    /** Generated Serial Version UID */
    private static final long serialVersionUID = -7353439224505296217L;

    private static final int[] sPaddingRect = new int[4];

    private boolean mVerticalStartWithPatch;
    private boolean mHorizontalStartWithPatch;

    private List<Rectangle> mFixed;
    private List<Rectangle> mPatches;
    private List<Rectangle> mHorizontalPatches;
    private List<Rectangle> mVerticalPatches;

    private Pair<Integer> mHorizontalPadding;
    private Pair<Integer> mVerticalPadding;


    /**
     * Data computed during drawing.
     */
    static final class DrawingData {
        private int mRemainderHorizontal;
        private int mRemainderVertical;
        private float mHorizontalPatchesSum;
        private float mVerticalPatchesSum;
    }

    /**
     * Computes and returns the 9-patch chunks.
     * @param image the image containing both the content and the control outer line.
     * @return the {@link NinePatchChunk}.
     */
    public static NinePatchChunk create(Image image) {
        NinePatchChunk chunk = new NinePatchChunk();
        chunk.findPatches(image);
        return chunk;
    }

    public void draw(Image image, GC gc, int x, int y, int scaledWidth,
            int scaledHeight, int destDensity, int srcDensity) {
//    	gc.drawImage(image, x, y, scaledWidth, scaledHeight, destX, destY, destWidth, destHeight)
    }

    /**
     * Fills the given array with the nine patch padding.
     *
     * @param padding array of left, top, right, bottom padding
     */
    public void getPadding(int[] padding) {
        padding[0] = mHorizontalPadding.mFirst; // left
        padding[2] = mHorizontalPadding.mSecond; // right
        padding[1] = mVerticalPadding.mFirst; // top
        padding[3] = mVerticalPadding.mSecond; // bottom
    }

    /**
     * Returns the padding as an int[] describing left, top, right, bottom.
     *
     * This method is not thread-safe and returns an array owned by the {@link NinePatchChunk}
     * class.
     * @return an internal array filled with the padding.
     */
    public int[] getPadding() {
        getPadding(sPaddingRect);
        return sPaddingRect;
    }

    private DrawingData computePatches(int scaledWidth, int scaledHeight) {
        DrawingData data = new DrawingData();
        boolean measuredWidth = false;
        boolean endRow = true;

        int remainderHorizontal = 0;
        int remainderVertical = 0;

        if (mFixed.size() > 0) {
            int start = mFixed.get(0).y;
            for (Rectangle rect : mFixed) {
                if (rect.y > start) {
                    endRow = true;
                    measuredWidth = true;
                }
                if (!measuredWidth) {
                    remainderHorizontal += rect.width;
                }
                if (endRow) {
                    remainderVertical += rect.height;
                    endRow = false;
                    start = rect.y;
                }
            }
        }

        data.mRemainderHorizontal = scaledWidth - remainderHorizontal;
        data.mRemainderVertical = scaledHeight - remainderVertical;

        data.mHorizontalPatchesSum = 0;
        if (mHorizontalPatches.size() > 0) {
            int start = -1;
            for (Rectangle rect : mHorizontalPatches) {
                if (rect.x > start) {
                    data.mHorizontalPatchesSum += rect.width;
                    start = rect.x;
                }
            }
        } else {
            int start = -1;
            for (Rectangle rect : mPatches) {
                if (rect.x > start) {
                    data.mHorizontalPatchesSum += rect.width;
                    start = rect.x;
                }
            }
        }

        data.mVerticalPatchesSum = 0;
        if (mVerticalPatches.size() > 0) {
            int start = -1;
            for (Rectangle rect : mVerticalPatches) {
                if (rect.y > start) {
                    data.mVerticalPatchesSum += rect.height;
                    start = rect.y;
                }
            }
        } else {
            int start = -1;
            for (Rectangle rect : mPatches) {
                if (rect.y > start) {
                    data.mVerticalPatchesSum += rect.height;
                    start = rect.y;
                }
            }
        }

        return data;
    }


    /**
     * Finds the 9-patch patches and padding from a {@link BufferedImage} image that contains
     * both the image content and the control outer lines.
     */
    private void findPatches(Image image) {
        // the size of the actual image content
        int width = image.getBounds().width - 2;
        int height = image.getBounds().height - 2;

        int[] row = new int[width];
        int[] column = new int[height];
        ImageData data = image.getImageData();
        // extract the patch line. Make sure to start at 1 and be only as long as the image content,
        // to not include the outer control line.
        data.getPixels(1, 0, width, row, 0);
        for(int a=0;a<column.length;a++){
        	column[a] = data.getPixel(0, 1+a);
        }

        boolean[] result = new boolean[1];
        Pair<List<Pair<Integer>>> left = getPatches(column, result);
        mVerticalStartWithPatch = result[0];

        result = new boolean[1];
        Pair<List<Pair<Integer>>> top = getPatches(row, result);
        mHorizontalStartWithPatch = result[0];

        mFixed = getRectangles(left.mFirst, top.mFirst);
        mPatches = getRectangles(left.mSecond, top.mSecond);

        if (mFixed.size() > 0) {
            mHorizontalPatches = getRectangles(left.mFirst, top.mSecond);
            mVerticalPatches = getRectangles(left.mSecond, top.mFirst);
        } else {
            if (top.mFirst.size() > 0) {
                mHorizontalPatches = new ArrayList<Rectangle>(0);
                mVerticalPatches = getVerticalRectangles(height, top.mFirst);
            } else if (left.mFirst.size() > 0) {
                mHorizontalPatches = getHorizontalRectangles(width, left.mFirst);
                mVerticalPatches = new ArrayList<Rectangle>(0);
            } else {
                mHorizontalPatches = mVerticalPatches = new ArrayList<Rectangle>(0);
            }
        }

        // extract the padding line. Make sure to start at 1 and be only as long as the image
        // content, to not include the outer control line.
        data.getPixels(1, height + 1, width, row, 0);
        for(int a=0;a<column.length;a++){
        	column[a] = data.getPixel(width + 1, 1+a);
        }

        top = getPatches(row, result);
        mHorizontalPadding = getPadding(top.mFirst);

        left = getPatches(column, result);
        mVerticalPadding = getPadding(left.mFirst);
    }

    private List<Rectangle> getVerticalRectangles(int imageHeight,
            List<Pair<Integer>> topPairs) {
        List<Rectangle> rectangles = new ArrayList<Rectangle>();
        for (Pair<Integer> top : topPairs) {
            int x = top.mFirst;
            int width = top.mSecond - top.mFirst;

            rectangles.add(new Rectangle(x, 0, width, imageHeight));
        }
        return rectangles;
    }

    private List<Rectangle> getHorizontalRectangles(int imageWidth,
            List<Pair<Integer>> leftPairs) {
        List<Rectangle> rectangles = new ArrayList<Rectangle>();
        for (Pair<Integer> left : leftPairs) {
            int y = left.mFirst;
            int height = left.mSecond - left.mFirst;

            rectangles.add(new Rectangle(0, y, imageWidth, height));
        }
        return rectangles;
    }

    private Pair<Integer> getPadding(List<Pair<Integer>> pairs) {
        if (pairs.size() == 0) {
            return new Pair<Integer>(0, 0);
        } else if (pairs.size() == 1) {
            if (pairs.get(0).mFirst == 0) {
                return new Pair<Integer>(pairs.get(0).mSecond - pairs.get(0).mFirst, 0);
            } else {
                return new Pair<Integer>(0, pairs.get(0).mSecond - pairs.get(0).mFirst);
            }
        } else {
            int index = pairs.size() - 1;
            return new Pair<Integer>(pairs.get(0).mSecond - pairs.get(0).mFirst,
                    pairs.get(index).mSecond - pairs.get(index).mFirst);
        }
    }

    private List<Rectangle> getRectangles(List<Pair<Integer>> leftPairs,
            List<Pair<Integer>> topPairs) {
        List<Rectangle> rectangles = new ArrayList<Rectangle>();
        for (Pair<Integer> left : leftPairs) {
            int y = left.mFirst;
            int height = left.mSecond - left.mFirst;
            for (Pair<Integer> top : topPairs) {
                int x = top.mFirst;
                int width = top.mSecond - top.mFirst;

                rectangles.add(new Rectangle(x, y, width, height));
            }
        }
        return rectangles;
    }

    /**
     * Computes a list of Patch based on a pixel line.
     *
     * This returns both the fixed areas, and the patches (stretchable) areas.
     *
     * The return value is a pair of list. The first list ({@link Pair#mFirst}) is the list
     * of fixed area. The second list ({@link Pair#mSecond}) is the list of stretchable areas.
     *
     * Each area is defined as a Pair of (start, end) coordinate in the given line.
     *
     * @param pixels the pixels of the control line. The line should have the same length as the
     *           content (i.e. it should be stripped of the first/last control pixel which are not
     *           used)
     * @param startWithPatch a boolean array of size 1 used to return the boolean value of whether
     *           a patch (stretchable area) is first or not.
     * @return
     */
    private Pair<List<Pair<Integer>>> getPatches(int[] pixels, boolean[] startWithPatch) {
        int lastIndex = 0;
        int lastPixel = pixels[0];
        boolean first = true;

        List<Pair<Integer>> fixed = new ArrayList<Pair<Integer>>();
        List<Pair<Integer>> patches = new ArrayList<Pair<Integer>>();

        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            if (pixel != lastPixel) {
                if (lastPixel == 0xFF000000) {
                    if (first) startWithPatch[0] = true;
                    patches.add(new Pair<Integer>(lastIndex, i));
                } else {
                    fixed.add(new Pair<Integer>(lastIndex, i));
                }
                first = false;

                lastIndex = i;
                lastPixel = pixel;
            }
        }
        if (lastPixel == 0xFF000000) {
            if (first) startWithPatch[0] = true;
            patches.add(new Pair<Integer>(lastIndex, pixels.length));
        } else {
            fixed.add(new Pair<Integer>(lastIndex, pixels.length));
        }

        if (patches.size() == 0) {
            patches.add(new Pair<Integer>(1, pixels.length));
            startWithPatch[0] = true;
            fixed.clear();
        }

        return new Pair<List<Pair<Integer>>>(fixed, patches);
    }

    /**
     * A pair of values.
     *
     * @param <E>
     */
    /*package*/ static class Pair<E> implements Serializable {
        /** Generated Serial Version UID */
        private static final long serialVersionUID = -2204108979541762418L;
        E mFirst;
        E mSecond;

        Pair(E first, E second) {
            mFirst = first;
            mSecond = second;
        }

        @Override
        public String toString() {
            return "Pair[" + mFirst + ", " + mSecond + "]";
        }
    }

}