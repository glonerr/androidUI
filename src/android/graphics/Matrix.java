/*
 * Copyright (C) 2006 The Android Open Source Project
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

package android.graphics;

import java.io.PrintWriter;
import java.util.Arrays;

/**
 * The Matrix class holds a 3x3 matrix for transforming coordinates. Matrix does
 * not have a constructor, so it must be explicitly initialized using either
 * reset() - to construct an identity matrix, or one of the set..() functions
 * (e.g. setTranslate, setRotate, etc.).
 */
public class Matrix {

	public static final int MSCALE_X = 0; // !< use with getValues/setValues
	public static final int MSKEW_X = 1; // !< use with getValues/setValues
	public static final int MTRANS_X = 2; // !< use with getValues/setValues
	public static final int MSKEW_Y = 3; // !< use with getValues/setValues
	public static final int MSCALE_Y = 4; // !< use with getValues/setValues
	public static final int MTRANS_Y = 5; // !< use with getValues/setValues
	public static final int MPERSP_0 = 6; // !< use with getValues/setValues
	public static final int MPERSP_1 = 7; // !< use with getValues/setValues
	public static final int MPERSP_2 = 8; // !< use with getValues/setValues

	/** @hide */
	public static Matrix IDENTITY_MATRIX = new Matrix() {
		void oops() {
			throw new IllegalStateException("Matrix can not be modified");
		}

		@Override
		public void set(Matrix src) {
			oops();
		}

		@Override
		public void reset() {
			oops();
		}

		@Override
		public void setTranslate(float dx, float dy) {
			oops();
		}

		@Override
		public void setScale(float sx, float sy, float px, float py) {
			oops();
		}

		@Override
		public void setScale(float sx, float sy) {
			oops();
		}

		@Override
		public void setRotate(float degrees, float px, float py) {
			oops();
		}

		@Override
		public void setRotate(float degrees) {
			oops();
		}

		@Override
		public void setSinCos(float sinValue, float cosValue, float px, float py) {
			oops();
		}

		@Override
		public void setSinCos(float sinValue, float cosValue) {
			oops();
		}

		@Override
		public void setSkew(float kx, float ky, float px, float py) {
			oops();
		}

		@Override
		public void setSkew(float kx, float ky) {
			oops();
		}

		@Override
		public boolean setConcat(Matrix a, Matrix b) {
			oops();
			return false;
		}

		@Override
		public boolean preTranslate(float dx, float dy) {
			oops();
			return false;
		}

		@Override
		public boolean preScale(float sx, float sy, float px, float py) {
			oops();
			return false;
		}

		@Override
		public boolean preScale(float sx, float sy) {
			oops();
			return false;
		}

		@Override
		public boolean preRotate(float degrees, float px, float py) {
			oops();
			return false;
		}

		@Override
		public boolean preRotate(float degrees) {
			oops();
			return false;
		}

		@Override
		public boolean preSkew(float kx, float ky, float px, float py) {
			oops();
			return false;
		}

		@Override
		public boolean preSkew(float kx, float ky) {
			oops();
			return false;
		}

		@Override
		public boolean preConcat(Matrix other) {
			oops();
			return false;
		}

		@Override
		public boolean postTranslate(float dx, float dy) {
			oops();
			return false;
		}

		@Override
		public boolean postScale(float sx, float sy, float px, float py) {
			oops();
			return false;
		}

		@Override
		public boolean postScale(float sx, float sy) {
			oops();
			return false;
		}

		@Override
		public boolean postRotate(float degrees, float px, float py) {
			oops();
			return false;
		}

		@Override
		public boolean postRotate(float degrees) {
			oops();
			return false;
		}

		@Override
		public boolean postSkew(float kx, float ky, float px, float py) {
			oops();
			return false;
		}

		@Override
		public boolean postSkew(float kx, float ky) {
			oops();
			return false;
		}

		@Override
		public boolean postConcat(Matrix other) {
			oops();
			return false;
		}

		@Override
		public boolean setRectToRect(RectF src, RectF dst, ScaleToFit stf) {
			oops();
			return false;
		}

		@Override
		public boolean setPolyToPoly(float[] src, int srcIndex, float[] dst,
				int dstIndex, int pointCount) {
			oops();
			return false;
		}

		@Override
		public void setValues(float[] values) {
			oops();
		}
	};

	/**
	 * @hide
	 */
	public int native_instance;

	/**
	 * Create an identity matrix
	 */
	public Matrix() {
		native_instance = sNativeInstance++;
		mValues[MSCALE_X] = MATRIX_SCALE;
		mValues[MSKEW_X] = 0;
		mValues[MTRANS_X] = 0;
		mValues[MSCALE_Y] = MATRIX_SCALE;
		mValues[MSKEW_Y] = 0;
		mValues[MTRANS_Y] = 0;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		isIdentity = true;
	}

	/**
	 * Create a matrix that is a (deep) copy of src
	 * 
	 * @param src
	 *            The matrix to copy into this matrix
	 */
	public Matrix(Matrix src) {
		native_instance = sNativeInstance++;
		if (src != null)
			System.arraycopy(src.mValues, 0, mValues, 0, MATRIX_SIZE);
		else {
			mValues[MSCALE_X] = MATRIX_SCALE;
			mValues[MSKEW_X] = 0;
			mValues[MTRANS_X] = 0;
			mValues[MSCALE_Y] = MATRIX_SCALE;
			mValues[MSKEW_Y] = 0;
			mValues[MTRANS_Y] = 0;
			mValues[MPERSP_0] = 0;
			mValues[MPERSP_1] = 0;
			mValues[MPERSP_2] = MATRIX22ELEM;
			isIdentity = true;
		}
		// native_instance = native_create(src != null ? src.native_instance :
		// 0);
	}

	/**
	 * Returns true if the matrix is identity. This maybe faster than testing if
	 * (getType() == 0)
	 */
	public boolean isIdentity() {
		return isIdentity;
		// return native_isIdentity(native_instance);
	}

	/**
	 * Returns true if will map a rectangle to another rectangle. This can be
	 * true if the matrix is identity, scale-only, or rotates a multiple of 90
	 * degrees.
	 */
	public boolean rectStaysRect() {
		return native_rectStaysRect(native_instance);
	}

	/**
	 * (deep) copy the src matrix into this matrix. If src is null, reset this
	 * matrix to the identity matrix.
	 */
	public void set(Matrix src) {
		if (src == null) {
			reset();
		} else {
			System.arraycopy(src.mValues, 0, mValues, 0, MATRIX_SIZE);
			if (src.equals(IDENTITY_MATRIX)) {
				isIdentity = true;
			}
			// native_set(native_instance, src.native_instance);
		}
	}

	/**
	 * Returns true iff obj is a Matrix and its values equal our values.
	 */
	public boolean equals(Object obj) {
		return obj != null && obj instanceof Matrix
				&& Arrays.equals(((Matrix) obj).mValues, mValues);
		// && native_equals(native_instance,
		// ((Matrix) obj).native_instance);
	}

	/** Set the matrix to identity */
	public void reset() {
		mValues[MSCALE_X] = MATRIX_SCALE;
		mValues[MSKEW_X] = 0;
		mValues[MTRANS_X] = 0;
		mValues[MSCALE_Y] = MATRIX_SCALE;
		mValues[MSKEW_Y] = 0;
		mValues[MTRANS_Y] = 0;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		isIdentity = true;
		// native_reset(native_instance);
	}

	/** Set the matrix to translate by (dx, dy). */
	public void setTranslate(float dx, float dy) {
		mValues[MSCALE_X] = MATRIX_SCALE;
		mValues[MSKEW_X] = 0;
		mValues[MTRANS_X] = dx;
		mValues[MSKEW_Y] = 0;
		mValues[MSCALE_Y] = MATRIX_SCALE;
		mValues[MTRANS_Y] = dy;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		if (dx == 0 && dy == 0) {
			isIdentity = true;
		} else {
			isIdentity = false;
		}
		// native_setTranslate(native_instance, dx, dy);
	}

	/**
	 * Set the matrix to scale by sx and sy, with a pivot point at (px, py). The
	 * pivot point is the coordinate that should remain unchanged by the
	 * specified transformation.
	 */
	public void setScale(float sx, float sy, float px, float py) {
		mValues[MSCALE_X] = sx;
		mValues[MSKEW_X] = 0;
		mValues[MTRANS_X] = px - sx * px;
		mValues[MSKEW_Y] = 0;
		mValues[MSCALE_Y] = sy;
		mValues[MTRANS_Y] = py - sy * py;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		if (sx == MATRIX_SCALE && sy == MATRIX_SCALE) {
			isIdentity = true;
		} else {
			isIdentity = false;
		}
		// native_setScale(native_instance, sx, sy, px, py);
	}

	/** Set the matrix to scale by sx and sy. */
	public void setScale(float sx, float sy) {
		mValues[MSCALE_X] = sx;
		mValues[MSKEW_X] = 0;
		mValues[MTRANS_X] = 0;
		mValues[MSCALE_Y] = sy;
		mValues[MSKEW_Y] = 0;
		mValues[MTRANS_Y] = 0;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		if (sx != MATRIX_SCALE || sy != MATRIX_SCALE) {
			isIdentity = false;
		} else {
			isIdentity = true;
		}
		// native_setScale(native_instance, sx, sy);
	}

	/**
	 * Set the matrix to rotate by the specified number of degrees, with a pivot
	 * point at (px, py). The pivot point is the coordinate that should remain
	 * unchanged by the specified transformation.
	 */
	public void setRotate(float degrees, float px, float py) {
		double radians = Math.toRadians(degrees);
		setSinCos((float) Math.sin(radians), (float) Math.cos(radians), px, py);
		// native_setRotate(native_instance, degrees, px, py);
	}

	/**
	 * Set the matrix to rotate about (0,0) by the specified number of degrees.
	 */
	public void setRotate(float degrees) {
		double radians = Math.toRadians(degrees);
		setSinCos((float) Math.sin(radians), (float) Math.cos(radians));
		// native_setRotate(native_instance, degrees);
	}

	/**
	 * Set the matrix to rotate by the specified sine and cosine values, with a
	 * pivot point at (px, py). The pivot point is the coordinate that should
	 * remain unchanged by the specified transformation.
	 */
	public void setSinCos(float sinValue, float cosValue, float px, float py) {
		float oneMinusCosV = MATRIX_SCALE - cosValue;
		mValues[MSCALE_X] = cosValue;
		mValues[MSKEW_X] = -sinValue;
		mValues[MTRANS_X] = sinValue * py + oneMinusCosV * px;
		mValues[MSKEW_Y] = sinValue;
		mValues[MSCALE_Y] = cosValue;
		mValues[MTRANS_Y] = -sinValue * px + oneMinusCosV * py;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		if (sinValue == 0 && cosValue == MATRIX_SCALE) {
			isIdentity = true;
		} else {
			isIdentity = false;
		}
		// native_setSinCos(native_instance, sinValue, cosValue, px, py);
	}

	/** Set the matrix to rotate by the specified sine and cosine values. */
	public void setSinCos(float sinValue, float cosValue) {
		mValues[MSCALE_X] = cosValue;
		mValues[MSKEW_X] = -sinValue;
		mValues[MTRANS_X] = 0;
		mValues[MSKEW_Y] = sinValue;
		mValues[MSCALE_Y] = cosValue;
		mValues[MTRANS_Y] = 0;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		// native_setSinCos(native_instance, sinValue, cosValue);
	}

	/**
	 * Set the matrix to skew by sx and sy, with a pivot point at (px, py). The
	 * pivot point is the coordinate that should remain unchanged by the
	 * specified transformation.
	 */
	public void setSkew(float kx, float ky, float px, float py) {
		mValues[MSCALE_X] = MATRIX_SCALE;
		mValues[MSKEW_X] = kx;
		mValues[MTRANS_X] = -kx * py;
		mValues[MSKEW_Y] = ky;
		mValues[MSCALE_Y] = MATRIX_SCALE;
		mValues[MTRANS_Y] = -ky * px;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		if (kx == 0 && ky == 0) {
			isIdentity = true;
		} else {
			isIdentity = false;
		}
		// native_setSkew(native_instance, kx, ky, px, py);
	}

	/** Set the matrix to skew by sx and sy. */
	public void setSkew(float kx, float ky) {
		mValues[MSCALE_X] = MATRIX_SCALE;
		mValues[MSKEW_X] = kx;
		mValues[MTRANS_X] = 0;
		mValues[MSKEW_Y] = ky;
		mValues[MSCALE_Y] = MATRIX_SCALE;
		mValues[MTRANS_Y] = 0;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		if (kx == 0 && ky == 0) {
			isIdentity = true;
		} else {
			isIdentity = false;
		}
		// native_setSkew(native_instance, kx, ky);
	}

	/**
	 * Set the matrix to the concatenation of the two specified matrices,
	 * returning true if the the result can be represented. Either of the two
	 * matrices may also be the target matrix. this = a * b
	 */
	public boolean setConcat(Matrix aMatrix, Matrix bMatrix) {
		if (aMatrix == null || bMatrix == null)
			return false;
		if (aMatrix.isIdentity()) {
			System.arraycopy(bMatrix.mValues, 0, mValues, 0, MATRIX_SIZE);
		} else if (bMatrix.isIdentity()) {
			System.arraycopy(aMatrix.mValues, 0, mValues, 0, MATRIX_SIZE);
		} else {
			multiply(aMatrix.mValues, bMatrix.mValues);
		}
		checkIdentity();
		return true;
		// return native_setConcat(native_instance,
		// a.native_instance,b.native_instance);
	}

	/**
	 * Preconcats the matrix with the specified translation. M' = M * T(dx, dy)
	 */
	public boolean preTranslate(float dx, float dy) {
		mValues[MTRANS_X] += mValues[MSCALE_X] * dx + mValues[MSKEW_X] * dy;
		mValues[MTRANS_Y] += mValues[MSKEW_Y] * dx + mValues[MSCALE_Y] * dy;
		checkIdentity();
		return true;
		// return native_preTranslate(native_instance, dx, dy);
	}

	/**
	 * Preconcats the matrix with the specified scale. M' = M * S(sx, sy, px,
	 * py)
	 */
	public boolean preScale(float sx, float sy, float px, float py) {
		float[] mValues = new float[MATRIX_SIZE];
		mValues[MSCALE_X] = sx;
		mValues[MSKEW_X] = 0;
		mValues[MTRANS_X] = px - sx * px;
		mValues[MSCALE_Y] = sy;
		mValues[MSKEW_Y] = py - sy * py;
		mValues[MTRANS_Y] = 0;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		multiply(this.mValues, mValues);
		checkIdentity();
		return true;
		// return native_preScale(native_instance, sx, sy, px, py);
	}

	/**
	 * Preconcats the matrix with the specified scale. M' = M * S(sx, sy)
	 */
	public boolean preScale(float sx, float sy) {
		float[] mValues = new float[MATRIX_SIZE];
		mValues[MSCALE_X] = sx;
		mValues[MSKEW_X] = 0;
		mValues[MTRANS_X] = 0;
		mValues[MSKEW_Y] = 0;
		mValues[MSCALE_Y] = sy;
		mValues[MTRANS_Y] = 0;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		multiply(this.mValues, mValues);
		checkIdentity();
		return true;
		// return native_preScale(native_instance, sx, sy);
	}

	/**
	 * Preconcats the matrix with the specified rotation. M' = M * R(degrees,
	 * px, py)
	 */
	public boolean preRotate(float degrees, float px, float py) {
		double radians = Math.toRadians(degrees);
		float sinValue = (float) Math.sin(radians), cosValue = (float) Math
				.cos(radians);
		if (sinValue < Double.MIN_VALUE)
			sinValue = 0;
		if (cosValue < Double.MIN_VALUE)
			cosValue = 0;
		float oneMinusCosV = MATRIX_SCALE - cosValue;
		float[] mValues = new float[MATRIX_SIZE];
		mValues[MSCALE_X] = cosValue;
		mValues[MSKEW_X] = -sinValue;
		mValues[MTRANS_X] = sinValue * py + oneMinusCosV * px;
		mValues[MSKEW_Y] = sinValue;
		mValues[MSCALE_Y] = cosValue;
		mValues[MTRANS_Y] = -sinValue * px + oneMinusCosV * py;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		multiply(this.mValues, mValues);
		checkIdentity();
		return true;
		// return native_preRotate(native_instance, degrees, px, py);
	}

	/**
	 * Preconcats the matrix with the specified rotation. M' = M * R(degrees)
	 */
	public boolean preRotate(float degrees) {
		double radians = Math.toRadians(degrees);
		float sinValue = (float) Math.sin(radians), cosValue = (float) Math
				.cos(radians);
		float[] mValues = new float[MATRIX_SIZE];
		mValues[MSCALE_X] = cosValue;
		mValues[MSKEW_X] = -sinValue;
		mValues[MTRANS_X] = 0;
		mValues[MSKEW_Y] = sinValue;
		mValues[MSCALE_Y] = cosValue;
		mValues[MTRANS_Y] = 0;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		multiply(this.mValues, mValues);
		checkIdentity();
		return true;
		// return native_preRotate(native_instance, degrees);
	}

	/**
	 * Preconcats the matrix with the specified skew. M' = M * K(kx, ky, px, py)
	 */
	public boolean preSkew(float kx, float ky, float px, float py) {
		float[] mValues = new float[MATRIX_SIZE];
		mValues[MSCALE_X] = MATRIX_SCALE;
		mValues[MSKEW_X] = kx;
		mValues[MTRANS_X] = -kx * py;
		mValues[MSCALE_Y] = MATRIX_SCALE;
		mValues[MSKEW_Y] = ky;
		mValues[MTRANS_Y] = -ky * px;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		multiply(this.mValues, mValues);
		checkIdentity();
		return true;
		// return native_preSkew(native_instance, kx, ky, px, py);
	}

	/**
	 * Preconcats the matrix with the specified skew. M' = M * K(kx, ky)
	 */
	public boolean preSkew(float kx, float ky) {
		float[] mValues = new float[MATRIX_SIZE];
		mValues[MSCALE_X] = MATRIX_SCALE;
		mValues[MSKEW_X] = kx;
		mValues[MTRANS_X] = 0;
		mValues[MSCALE_Y] = MATRIX_SCALE;
		mValues[MSKEW_Y] = ky;
		mValues[MTRANS_Y] = 0;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		multiply(this.mValues, mValues);
		checkIdentity();
		return true;
		// return native_preSkew(native_instance, kx, ky);
	}

	/**
	 * Preconcats the matrix with the specified matrix. M' = M * other
	 */
	public boolean preConcat(Matrix other) {
		if (other == null)
			return false;
		return other.isIdentity() || setConcat(other, this);
		// return native_preConcat(native_instance, other.native_instance);
	}

	/**
	 * Postconcats the matrix with the specified translation. M' = T(dx, dy) * M
	 */
	public boolean postTranslate(float dx, float dy) {
		float[] mValues = new float[MATRIX_SIZE];
		mValues[MSCALE_X] = MATRIX_SCALE;
		mValues[MSKEW_X] = 0;
		mValues[MTRANS_X] = dx;
		mValues[MSCALE_Y] = 0;
		mValues[MSKEW_Y] = MATRIX_SCALE;
		mValues[MTRANS_Y] = dy;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		multiply(mValues, this.mValues);
		checkIdentity();
		return true;
		// return native_postTranslate(native_instance, dx, dy);
	}

	/**
	 * Postconcats the matrix with the specified scale. M' = S(sx, sy, px, py) *
	 * M
	 */
	public boolean postScale(float sx, float sy, float px, float py) {
		float[] mValues = new float[MATRIX_SIZE];
		mValues[MSCALE_X] = sx;
		mValues[MSKEW_X] = 0;
		mValues[MTRANS_X] = px - sx * px;
		mValues[MSCALE_Y] = sy;
		mValues[MSKEW_Y] = py - sy * py;
		mValues[MTRANS_Y] = 0;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		multiply(mValues, this.mValues);
		checkIdentity();
		return true;
		// return native_postScale(native_instance, sx, sy, px, py);
	}

	/**
	 * Postconcats the matrix with the specified scale. M' = S(sx, sy) * M
	 */
	public boolean postScale(float sx, float sy) {
		float[] mValues = new float[MATRIX_SIZE];
		mValues[MSCALE_X] = sx;
		mValues[MSKEW_X] = 0;
		mValues[MTRANS_X] = 0;
		mValues[MSCALE_Y] = sy;
		mValues[MSKEW_Y] = 0;
		mValues[MTRANS_Y] = 0;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		multiply(mValues, this.mValues);
		checkIdentity();
		return true;
		// return native_postScale(native_instance, sx, sy);
	}

	/**
	 * Postconcats the matrix with the specified rotation. M' = R(degrees, px,
	 * py) * M
	 */
	public boolean postRotate(float degrees, float px, float py) {
		double radians = Math.toRadians(degrees);
		float sinValue = (float) Math.sin(radians), cosValue = (float) Math
				.cos(radians);
		float oneMinusCosV = MATRIX_SCALE - cosValue;
		float[] mValues = new float[MATRIX_SIZE];
		mValues[MSCALE_X] = cosValue;
		mValues[MSKEW_X] = -sinValue;
		mValues[MTRANS_X] = sinValue * py + oneMinusCosV * px;
		mValues[MSCALE_Y] = sinValue;
		mValues[MSKEW_Y] = cosValue;
		mValues[MTRANS_Y] = -sinValue * px + oneMinusCosV * py;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		multiply(mValues, this.mValues);
		checkIdentity();
		return true;
		// return native_postRotate(native_instance, degrees, px, py);
	}

	/**
	 * Postconcats the matrix with the specified rotation. M' = R(degrees) * M
	 */
	public boolean postRotate(float degrees) {
		double radians = Math.toRadians(degrees);
		float sinValue = (float) Math.sin(radians), cosValue = (float) Math
				.cos(radians);
		float[] mValues = new float[MATRIX_SIZE];
		mValues[MSCALE_X] = cosValue;
		mValues[MSKEW_X] = -sinValue;
		mValues[MTRANS_X] = 0;
		mValues[MSCALE_Y] = sinValue;
		mValues[MSKEW_Y] = cosValue;
		mValues[MTRANS_Y] = 0;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		multiply(mValues, this.mValues);
		checkIdentity();
		return true;
		// return native_postRotate(native_instance, degrees);
	}

	/**
	 * Postconcats the matrix with the specified skew. M' = K(kx, ky, px, py) *
	 * M
	 */
	public boolean postSkew(float kx, float ky, float px, float py) {
		float[] mValues = new float[MATRIX_SIZE];
		mValues[MSCALE_X] = MATRIX_SCALE;
		mValues[MSKEW_X] = kx;
		mValues[MTRANS_X] = -kx * py;
		mValues[MSCALE_Y] = MATRIX_SCALE;
		mValues[MSKEW_Y] = ky;
		mValues[MTRANS_Y] = -ky * px;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		multiply(mValues, this.mValues);
		checkIdentity();
		return true;
		// return native_postSkew(native_instance, kx, ky, px, py);
	}

	/**
	 * Postconcats the matrix with the specified skew. M' = K(kx, ky) * M
	 */
	public boolean postSkew(float kx, float ky) {
		float[] mValues = new float[MATRIX_SIZE];
		mValues[MSCALE_X] = MATRIX_SCALE;
		mValues[MSKEW_X] = kx;
		mValues[MTRANS_X] = 0;
		mValues[MSCALE_Y] = MATRIX_SCALE;
		mValues[MSKEW_Y] = ky;
		mValues[MTRANS_Y] = 0;
		mValues[MPERSP_0] = 0;
		mValues[MPERSP_1] = 0;
		mValues[MPERSP_2] = MATRIX22ELEM;
		multiply(mValues, this.mValues);
		checkIdentity();
		return true;
		// return native_postSkew(native_instance, kx, ky);
	}

	/**
	 * Postconcats the matrix with the specified matrix. M' = other * M
	 */
	public boolean postConcat(Matrix other) {
		if (other == null)
			return false;
		return other.isIdentity() || setConcat(other, this);
		// return native_postConcat(native_instance, other.native_instance);
	}

	/**
	 * Controlls how the src rect should align into the dst rect for
	 * setRectToRect().
	 */
	public enum ScaleToFit {
		/**
		 * Scale in X and Y independently, so that src matches dst exactly. This
		 * may change the aspect ratio of the src.
		 */
		FILL(0),
		/**
		 * Compute a scale that will maintain the original src aspect ratio, but
		 * will also ensure that src fits entirely inside dst. At least one axis
		 * (X or Y) will fit exactly. START aligns the result to the left and
		 * top edges of dst.
		 */
		START(1),
		/**
		 * Compute a scale that will maintain the original src aspect ratio, but
		 * will also ensure that src fits entirely inside dst. At least one axis
		 * (X or Y) will fit exactly. The result is centered inside dst.
		 */
		CENTER(2),
		/**
		 * Compute a scale that will maintain the original src aspect ratio, but
		 * will also ensure that src fits entirely inside dst. At least one axis
		 * (X or Y) will fit exactly. END aligns the result to the right and
		 * bottom edges of dst.
		 */
		END(3);

		// the native values must match those in SkMatrix.h
		ScaleToFit(int nativeInt) {
			this.nativeInt = nativeInt;
		}

		final int nativeInt;
	}

	/**
	 * Set the matrix to the scale and translate values that map the source
	 * rectangle to the destination rectangle, returning true if the the result
	 * can be represented.
	 * 
	 * @param src
	 *            the source rectangle to map from.
	 * @param dst
	 *            the destination rectangle to map to.
	 * @param stf
	 *            the ScaleToFit option
	 * @return true if the matrix can be represented by the rectangle mapping.
	 */
	public boolean setRectToRect(RectF src, RectF dst, ScaleToFit stf) {
		if (dst == null || src == null) {
			throw new NullPointerException();
		}
		return native_setRectToRect(native_instance, src, dst, stf.nativeInt);
	}

	// private helper to perform range checks on arrays of "points"
	private static void checkPointArrays(float[] src, int srcIndex,
			float[] dst, int dstIndex, int pointCount) {
		// check for too-small and too-big indices
		int srcStop = srcIndex + (pointCount << 1);
		int dstStop = dstIndex + (pointCount << 1);
		if ((pointCount | srcIndex | dstIndex | srcStop | dstStop) < 0
				|| srcStop > src.length || dstStop > dst.length) {
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	/**
	 * Set the matrix such that the specified src points would map to the
	 * specified dst points. The "points" are represented as an array of floats,
	 * order [x0, y0, x1, y1, ...], where each "point" is 2 float values.
	 * 
	 * @param src
	 *            The array of src [x,y] pairs (points)
	 * @param srcIndex
	 *            Index of the first pair of src values
	 * @param dst
	 *            The array of dst [x,y] pairs (points)
	 * @param dstIndex
	 *            Index of the first pair of dst values
	 * @param pointCount
	 *            The number of pairs/points to be used. Must be [0..4]
	 * @return true if the matrix was set to the specified transformation
	 */
	public boolean setPolyToPoly(float[] src, int srcIndex, float[] dst,
			int dstIndex, int pointCount) {
		if (pointCount > 4) {
			throw new IllegalArgumentException();
		}
		checkPointArrays(src, srcIndex, dst, dstIndex, pointCount);
		return native_setPolyToPoly(native_instance, src, srcIndex, dst,
				dstIndex, pointCount);
	}

	/**
	 * If this matrix can be inverted, return true and if inverse is not null,
	 * set inverse to be the inverse of this matrix. If this matrix cannot be
	 * inverted, ignore inverse and return false.
	 */
	public boolean invert(Matrix inverse) {
		return native_invert(native_instance, inverse.native_instance);
	}

	/**
	 * Apply this matrix to the array of 2D points specified by src, and write
	 * the transformed points into the array of points specified by dst. The two
	 * arrays represent their "points" as pairs of floats [x, y].
	 * 
	 * @param dst
	 *            The array of dst points (x,y pairs)
	 * @param dstIndex
	 *            The index of the first [x,y] pair of dst floats
	 * @param src
	 *            The array of src points (x,y pairs)
	 * @param srcIndex
	 *            The index of the first [x,y] pair of src floats
	 * @param pointCount
	 *            The number of points (x,y pairs) to transform
	 */
	public void mapPoints(float[] dst, int dstIndex, float[] src, int srcIndex,
			int pointCount) {
		checkPointArrays(src, srcIndex, dst, dstIndex, pointCount);
		native_mapPoints(native_instance, dst, dstIndex, src, srcIndex,
				pointCount, true);
	}

	/**
	 * Apply this matrix to the array of 2D vectors specified by src, and write
	 * the transformed vectors into the array of vectors specified by dst. The
	 * two arrays represent their "vectors" as pairs of floats [x, y].
	 * 
	 * Note: this method does not apply the translation associated with the
	 * matrix. Use {@link Matrix#mapPoints(float[], int, float[], int, int)} if
	 * you want the translation to be applied.
	 * 
	 * @param dst
	 *            The array of dst vectors (x,y pairs)
	 * @param dstIndex
	 *            The index of the first [x,y] pair of dst floats
	 * @param src
	 *            The array of src vectors (x,y pairs)
	 * @param srcIndex
	 *            The index of the first [x,y] pair of src floats
	 * @param vectorCount
	 *            The number of vectors (x,y pairs) to transform
	 */
	public void mapVectors(float[] dst, int dstIndex, float[] src,
			int srcIndex, int vectorCount) {
		checkPointArrays(src, srcIndex, dst, dstIndex, vectorCount);
		native_mapPoints(native_instance, dst, dstIndex, src, srcIndex,
				vectorCount, false);
	}

	/**
	 * Apply this matrix to the array of 2D points specified by src, and write
	 * the transformed points into the array of points specified by dst. The two
	 * arrays represent their "points" as pairs of floats [x, y].
	 * 
	 * @param dst
	 *            The array of dst points (x,y pairs)
	 * @param src
	 *            The array of src points (x,y pairs)
	 */
	public void mapPoints(float[] dst, float[] src) {
		if (dst.length != src.length) {
			throw new ArrayIndexOutOfBoundsException();
		}
		mapPoints(dst, 0, src, 0, dst.length >> 1);
	}

	/**
	 * Apply this matrix to the array of 2D vectors specified by src, and write
	 * the transformed vectors into the array of vectors specified by dst. The
	 * two arrays represent their "vectors" as pairs of floats [x, y].
	 * 
	 * Note: this method does not apply the translation associated with the
	 * matrix. Use {@link Matrix#mapPoints(float[], float[])} if you want the
	 * translation to be applied.
	 * 
	 * @param dst
	 *            The array of dst vectors (x,y pairs)
	 * @param src
	 *            The array of src vectors (x,y pairs)
	 */
	public void mapVectors(float[] dst, float[] src) {
		if (dst.length != src.length) {
			throw new ArrayIndexOutOfBoundsException();
		}
		mapVectors(dst, 0, src, 0, dst.length >> 1);
	}

	/**
	 * Apply this matrix to the array of 2D points, and write the transformed
	 * points back into the array
	 * 
	 * @param pts
	 *            The array [x0, y0, x1, y1, ...] of points to transform.
	 */
	public void mapPoints(float[] pts) {
		mapPoints(pts, 0, pts, 0, pts.length >> 1);
	}

	/**
	 * Apply this matrix to the array of 2D vectors, and write the transformed
	 * vectors back into the array.
	 * 
	 * Note: this method does not apply the translation associated with the
	 * matrix. Use {@link Matrix#mapPoints(float[])} if you want the translation
	 * to be applied.
	 * 
	 * @param vecs
	 *            The array [x0, y0, x1, y1, ...] of vectors to transform.
	 */
	public void mapVectors(float[] vecs) {
		mapVectors(vecs, 0, vecs, 0, vecs.length >> 1);
	}

	/**
	 * Apply this matrix to the src rectangle, and write the transformed
	 * rectangle into dst. This is accomplished by transforming the 4 corners of
	 * src, and then setting dst to the bounds of those points.
	 * 
	 * @param dst
	 *            Where the transformed rectangle is written.
	 * @param src
	 *            The original rectangle to be transformed.
	 * @return the result of calling rectStaysRect()
	 */
	public boolean mapRect(RectF dst, RectF src) {
		if (dst == null || src == null) {
			throw new NullPointerException();
		}
		return true;
		// return native_mapRect(native_instance, dst, src);
	}

	/**
	 * Apply this matrix to the rectangle, and write the transformed rectangle
	 * back into it. This is accomplished by transforming the 4 corners of rect,
	 * and then setting it to the bounds of those points
	 * 
	 * @param rect
	 *            The rectangle to transform.
	 * @return the result of calling rectStaysRect()
	 */
	public boolean mapRect(RectF rect) {
		return mapRect(rect, rect);
	}

	/**
	 * Return the mean radius of a circle after it has been mapped by this
	 * matrix. NOTE: in perspective this value assumes the circle has its center
	 * at the origin.
	 */
	public float mapRadius(float radius) {
		return native_mapRadius(native_instance, radius);
	}

	/**
	 * Copy 9 values from the matrix into the array.
	 */
	public void getValues(float[] values) {
		if (values.length < 9) {
			throw new ArrayIndexOutOfBoundsException();
		}
		System.arraycopy(mValues, 0, values, 0, MATRIX_SIZE);
		// native_getValues(native_instance, values);
	}

	/**
	 * Copy 9 values from the array into the matrix. Depending on the
	 * implementation of Matrix, these may be transformed into 16.16 integers in
	 * the Matrix, such that a subsequent call to getValues() will not yield
	 * exactly the same values.
	 */
	public void setValues(float[] values) {
		if (values.length < 9) {
			throw new ArrayIndexOutOfBoundsException();
		}
		System.arraycopy(values, 0, mValues, 0, MATRIX_SIZE);
		checkIdentity();
		// native_setValues(native_instance, values);
	}
	
	private void checkIdentity(){
		if (mValues[MSCALE_X] == MATRIX_SCALE
				&& mValues[MSCALE_Y] == MATRIX_SCALE && mValues[MSKEW_X] == 0
				&& mValues[MSKEW_Y] == 0 && mValues[MTRANS_X] == 0
				&& mValues[MTRANS_Y] == 0 && mValues[MPERSP_0] == 0
				&& mValues[MPERSP_1] == 0 && mValues[MPERSP_2] == MATRIX22ELEM) {
			isIdentity = true;
		} else {
			isIdentity = false;
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(64);
		sb.append("Matrix{");
		toShortString(sb);
		sb.append('}');
		return sb.toString();

	}

	public String toShortString() {
		StringBuilder sb = new StringBuilder(64);
		toShortString(sb);
		return sb.toString();
	}

	/**
	 * @hide
	 */
	public void toShortString(StringBuilder sb) {
		float[] values = new float[9];
		getValues(values);
		sb.append('[');
		sb.append(values[0]);
		sb.append(", ");
		sb.append(values[1]);
		sb.append(", ");
		sb.append(values[2]);
		sb.append("][");
		sb.append(values[3]);
		sb.append(", ");
		sb.append(values[4]);
		sb.append(", ");
		sb.append(values[5]);
		sb.append("][");
		sb.append(values[6]);
		sb.append(", ");
		sb.append(values[7]);
		sb.append(", ");
		sb.append(values[8]);
		sb.append(']');
	}

	/**
	 * Print short string, to optimize dumping.
	 * 
	 * @hide
	 */
	public void printShortString(PrintWriter pw) {
		float[] values = new float[9];
		getValues(values);
		pw.print('[');
		pw.print(values[0]);
		pw.print(", ");
		pw.print(values[1]);
		pw.print(", ");
		pw.print(values[2]);
		pw.print("][");
		pw.print(values[3]);
		pw.print(", ");
		pw.print(values[4]);
		pw.print(", ");
		pw.print(values[5]);
		pw.print("][");
		pw.print(values[6]);
		pw.print(", ");
		pw.print(values[7]);
		pw.print(", ");
		pw.print(values[8]);
		pw.print(']');

	}

	protected void finalize() throws Throwable {
		finalizer(native_instance);
	}

	/* package */final int ni() {
		return native_instance;
	}

	private static native int native_create(int native_src_or_zero);

	private static native boolean native_isIdentity(int native_object);

	private static native boolean native_rectStaysRect(int native_object);

	private static native void native_reset(int native_object);

	private static native void native_set(int native_object, int other);

	private static native void native_setTranslate(int native_object, float dx,
			float dy);

	private static native void native_setScale(int native_object, float sx,
			float sy, float px, float py);

	private static native void native_setScale(int native_object, float sx,
			float sy);

	private static native void native_setRotate(int native_object,
			float degrees, float px, float py);

	private static native void native_setRotate(int native_object, float degrees);

	private static native void native_setSinCos(int native_object,
			float sinValue, float cosValue, float px, float py);

	private static native void native_setSinCos(int native_object,
			float sinValue, float cosValue);

	private static native void native_setSkew(int native_object, float kx,
			float ky, float px, float py);

	private static native void native_setSkew(int native_object, float kx,
			float ky);

	private static native boolean native_setConcat(int native_object, int a,
			int b);

	private static native boolean native_preTranslate(int native_object,
			float dx, float dy);

	private static native boolean native_preScale(int native_object, float sx,
			float sy, float px, float py);

	private static native boolean native_preScale(int native_object, float sx,
			float sy);

	private static native boolean native_preRotate(int native_object,
			float degrees, float px, float py);

	private static native boolean native_preRotate(int native_object,
			float degrees);

	private static native boolean native_preSkew(int native_object, float kx,
			float ky, float px, float py);

	private static native boolean native_preSkew(int native_object, float kx,
			float ky);

	private static native boolean native_preConcat(int native_object,
			int other_matrix);

	private static native boolean native_postTranslate(int native_object,
			float dx, float dy);

	private static native boolean native_postScale(int native_object, float sx,
			float sy, float px, float py);

	private static native boolean native_postScale(int native_object, float sx,
			float sy);

	private static native boolean native_postRotate(int native_object,
			float degrees, float px, float py);

	private static native boolean native_postRotate(int native_object,
			float degrees);

	private static native boolean native_postSkew(int native_object, float kx,
			float ky, float px, float py);

	private static native boolean native_postSkew(int native_object, float kx,
			float ky);

	private static native boolean native_postConcat(int native_object,
			int other_matrix);

	private static native boolean native_setRectToRect(int native_object,
			RectF src, RectF dst, int stf);

	private static native boolean native_setPolyToPoly(int native_object,
			float[] src, int srcIndex, float[] dst, int dstIndex, int pointCount);

	private static native boolean native_invert(int native_object, int inverse);

	private static native void native_mapPoints(int native_object, float[] dst,
			int dstIndex, float[] src, int srcIndex, int ptCount, boolean isPts);

	private static native boolean native_mapRect(int native_object, RectF dst,
			RectF src);

	private static native float native_mapRadius(int native_object, float radius);

	private static native void native_getValues(int native_object,
			float[] values);

	private static native void native_setValues(int native_object,
			float[] values);

	private static native boolean native_equals(int native_a, int native_b);

	private static native void finalizer(int native_instance);

	private void multiply(float[] a, float[] b) {
		float[] tmp = new float[MATRIX_SIZE];
		// first row
		tmp[0] = b[0] * a[0] + b[1] * a[3] + b[2] * a[6];
		tmp[1] = b[0] * a[1] + b[1] * a[4] + b[2] * a[7];
		tmp[2] = b[0] * a[2] + b[1] * a[5] + b[2] * a[8];

		// 2nd row
		tmp[3] = b[3] * a[0] + b[4] * a[3] + b[5] * a[6];
		tmp[4] = b[3] * a[1] + b[4] * a[4] + b[5] * a[7];
		tmp[5] = b[3] * a[2] + b[4] * a[5] + b[5] * a[8];

		// 3rd row
		tmp[6] = b[6] * a[0] + b[7] * a[3] + b[8] * a[6];
		tmp[7] = b[6] * a[1] + b[7] * a[4] + b[8] * a[7];
		tmp[8] = b[6] * a[2] + b[7] * a[5] + b[8] * a[8];
		System.arraycopy(tmp, 0, mValues, 0, MATRIX_SIZE);
	}

	public boolean hasPerspective() {
		return (mValues[MPERSP_0] != 0 || mValues[MPERSP_1] != 0 || mValues[MPERSP_2] != 1);
	}

	private float[] mValues = new float[MATRIX_SIZE];
	private boolean isIdentity = true;
	private static final float MATRIX22ELEM = 1;
	private static final float MATRIX_SCALE = 1;
	private static final int MATRIX_SIZE = 9;
	private static int sNativeInstance = 1;
}