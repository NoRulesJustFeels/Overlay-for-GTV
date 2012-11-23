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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * The Canvas version of a sprite. This class keeps a pointer to a bitmap and
 * draws it at the Sprite's current location.
 */
public class CanvasSprite extends Renderable {
	private static final String LOG_CAT = "CanvasSprite";
	private Bitmap bitmap;
	private Paint paint = new Paint();

	public CanvasSprite(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public void draw(Canvas canvas) {
		paint.setAlpha(alpha);
		canvas.drawBitmap(bitmap, x, y, paint);
	}

}
