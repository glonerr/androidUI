/*
 * Copyright (C) 2008 The Android Open Source Project
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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

/**
 * Represents a 9-Patch bitmap.
 * 
 * DO NOT CHANGE THIS API OR OLDER VERSIONS OF LAYOUTLIB WILL CRASH.
 * 
 * This is a full representation of a NinePatch with both a
 * {@link BufferedImage} and a {@link NinePatchChunk}.
 * 
 * Newer versions of the Layoutlib will use only the {@link NinePatchChunk} as
 * the default nine patch drawable references a normal Android bitmap which
 * contains a BufferedImage through a Bitmap_Delegate.
 * 
 */
public class NinePatch {
	public static final String EXTENSION_9PATCH = ".9.png";

	private Image mImage;
	private NinePatchChunk mChunk;

	public Image getImage() {
		return mImage;
	}

	public NinePatchChunk getChunk() {
		return mChunk;
	}

	/**
	 * LEGACY METHOD to run older versions of Android Layoutlib. ==== DO NOT
	 * CHANGE ====
	 * 
	 * Loads a 9 patch or regular bitmap.
	 * 
	 * @param fileUrl
	 *            the URL of the file to load.
	 * @param convert
	 *            if <code>true</code>, non 9-patch bitmap will be converted
	 *            into a 9 patch. If <code>false</code> and the bitmap is not a
	 *            9 patch, the method will return <code>null</code>.
	 * @return a {@link NinePatch} or <code>null</code>.
	 * @throws IOException
	 */
	public static NinePatch load(URL fileUrl, boolean convert)
			throws IOException {
		Image image = null;
		image = new Image(null, fileUrl.getFile());

		boolean is9Patch = fileUrl.getPath().toLowerCase()
				.endsWith(EXTENSION_9PATCH);

		return load(image, is9Patch, convert);
	}

	/**
	 * LEGACY METHOD to run older versions of Android Layoutlib. ==== DO NOT
	 * CHANGE ====
	 * 
	 * Loads a 9 patch or regular bitmap.
	 * 
	 * @param stream
	 *            the {@link InputStream} of the file to load.
	 * @param is9Patch
	 *            whether the file represents a 9-patch
	 * @param convert
	 *            if <code>true</code>, non 9-patch bitmap will be converted
	 *            into a 9 patch. If <code>false</code> and the bitmap is not a
	 *            9 patch, the method will return <code>null</code>.
	 * @return a {@link NinePatch} or <code>null</code>.
	 * @throws IOException
	 */
	public static NinePatch load(InputStream stream, boolean is9Patch,
			boolean convert) throws IOException {
		Image image = null;
		image = new Image(null, stream);

		return load(image, is9Patch, convert);
	}

	/**
	 * LEGACY METHOD to run older versions of Android Layoutlib. ==== DO NOT
	 * CHANGE ====
	 * 
	 * Loads a 9 patch or regular bitmap.
	 * 
	 * @param image
	 *            the source {@link BufferedImage}.
	 * @param is9Patch
	 *            whether the file represents a 9-patch
	 * @param convert
	 *            if <code>true</code>, non 9-patch bitmap will be converted
	 *            into a 9 patch. If <code>false</code> and the bitmap is not a
	 *            9 patch, the method will return <code>null</code>.
	 * @return a {@link NinePatch} or <code>null</code>.
	 * @throws IOException
	 */
	public static NinePatch load(Image image, boolean is9Patch, boolean convert) {
		if (is9Patch == false) {
			if (convert) {
				image = convertTo9Patch(image);
			} else {
				return null;
			}
		} else {
			ensure9Patch(image);
		}

		return new NinePatch(image);
	}

	/**
	 * LEGACY METHOD to run older versions of Android Layoutlib. ==== DO NOT
	 * CHANGE ====
	 * 
	 * @return
	 */
	public int getWidth() {
		return mImage.getBounds().width;
	}

	/**
	 * LEGACY METHOD to run older versions of Android Layoutlib. ==== DO NOT
	 * CHANGE ====
	 * 
	 * @return
	 */
	public int getHeight() {
		return mImage.getBounds().height;
	}

	/**
	 * LEGACY METHOD to run older versions of Android Layoutlib. ==== DO NOT
	 * CHANGE ====
	 * 
	 * @param padding
	 *            array of left, top, right, bottom padding
	 * @return
	 */
	public boolean getPadding(int[] padding) {
		mChunk.getPadding(padding);
		return true;
	}

	/**
	 * LEGACY METHOD to run older versions of Android Layoutlib. ==== DO NOT
	 * CHANGE ====
	 * 
	 * @param graphics2D
	 * @param x
	 * @param y
	 * @param scaledWidth
	 * @param scaledHeight
	 */
	public void draw(GC gc, int x, int y, int scaledWidth,
			int scaledHeight) {
		mChunk.draw(mImage, gc, x, y, scaledWidth, scaledHeight, 0, 0);
	}

	private NinePatch(Image image) {
		mChunk = NinePatchChunk.create(image);
		mImage = extractBitmapContent(image);
	}

	private static void ensure9Patch(Image image) {
		int width = image.getBounds().width;
		int height = image.getBounds().height;
		ImageData data = image.getImageData();
		for (int i = 0; i < width; i++) {
			int pixel = data.getPixel(i, 0);
			if (pixel != 0 && pixel != 0xFF000000) {
				data.setPixel(i, 0, 0);
			}
			pixel = data.getPixel(i, height - 1);
			if (pixel != 0 && pixel != 0xFF000000) {
				data.setPixel(i, height - 1, 0);
			}
		}
		for (int i = 0; i < height; i++) {
			int pixel = data.getPixel(0, i);
			if (pixel != 0 && pixel != 0xFF000000) {
				data.setPixel(0, i, 0);
			}
			pixel = data.getPixel(width - 1, i);
			if (pixel != 0 && pixel != 0xFF000000) {
				data.setPixel(width - 1, i, 0);
			}
		}
		image = new Image(null, data);
	}

	private static Image convertTo9Patch(Image image) {
        Image buffer = new Image(null,
                image.getBounds().width + 2, image.getBounds().height + 2);
        GC gc = new GC(buffer);
        gc.drawImage(image, 1, 1);
        gc.dispose();
        return buffer;
    }

	private Image extractBitmapContent(Image image) {
		Image buffer = new Image(null, image.getBounds().width - 2, image.getBounds().height - 2);
		GC gc = new GC(buffer);
		gc.drawImage(image, -1, -1);
		gc.dispose();
		return buffer;
	}

}