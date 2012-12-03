/*
 * Copyright (C) 2009 The Android Open Source Project
 * Copyright (C) 2012 ENTERTAILION LLC
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

package com.entertailion.android.overlay;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;

import com.entertailion.android.overlay.CanvasSurfaceView.Renderer;

/**
 * Moving algorithm
 * 
 */
public abstract class Mover implements Runnable, Renderer {
	protected Context context;
	protected int width;
	protected int height;
	protected int count;
	protected boolean config;
	protected static BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
	protected Bitmap[] bitmaps;
	protected Renderable[] spriteArray;
	protected Paint paint = new Paint();

	public Mover(Context context, int width, int height, int count,
			boolean config) {
		this.context = context;
		this.width = width;
		this.height = height;
		this.count = count;
		this.config = config;
		//paint.setAntiAlias(true);
	}

	public void drawFrame(Canvas canvas) {
		if (spriteArray != null) {
			canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			for (int x = 0; x < spriteArray.length; x++) {
				paint.setAlpha(spriteArray[x].alpha);
				canvas.drawBitmap(spriteArray[x].bitmap, spriteArray[x].x, spriteArray[x].y, paint);
//				Matrix matrix = new Matrix();
//				matrix.setRotate(rotation, bitmap.getWidth()/2, bitmap.getHeight()/2);
//				matrix.postTranslate(x, y);
//				canvas.drawBitmap(bitmap, matrix, paint);
			}
		}
	}

	public void sizeChanged(int width, int height) {
		// nothing to be done
	}

	public abstract void run();

	/** Recycles all of the bitmaps loaded. */
	protected void onDestroy() {
		for (int x = 0; x < bitmaps.length; x++) {
			bitmaps[x].recycle();
			bitmaps[x] = null;
		}
	}

	/**
	 * Loads a bitmap from a resource and converts it to a bitmap.
	 * 
	 * @param context
	 *            The application context.
	 * @param resourceId
	 *            The id of the resource to load.
	 * @return A bitmap containing the image contents of the resource, or null
	 *         if there was an error.
	 */
	protected Bitmap loadBitmap(int resourceId) {
		bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
		Bitmap bitmap = null;
		if (context != null) {

			InputStream is = context.getResources().openRawResource(resourceId);
			try {
				bitmap = BitmapFactory.decodeStream(is, null, bitmapOptions);
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					// Ignore.
				}
			}
		}

		return bitmap;
	}
}
