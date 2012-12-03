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

import android.content.Context;
import android.graphics.Bitmap;

/**
 * A simple runnable that updates the position of each sprite on the screen
 * every frame by applying a very simple gravity simulation.
 */
public class ChristmasMover extends FallingMover {
	private static final String LOG_CAT = "ChristmasMover";

	public ChristmasMover(Context context, int width, int height, int count,
			boolean config) {
		super(context, width, height, count, config);
	}

	@Override
	public Bitmap[] getBitmaps() {
		Bitmap[] bitmaps = new Bitmap[10];
		bitmaps[0] = loadBitmap(R.drawable.christmas_deer);
		bitmaps[1] = loadBitmap(R.drawable.christmas_drum);
		bitmaps[2] = loadBitmap(R.drawable.christmas_father);
		bitmaps[3] = loadBitmap(R.drawable.christmas_hat);
		bitmaps[4] = loadBitmap(R.drawable.christmas_light);
		bitmaps[5] = loadBitmap(R.drawable.christmas_mistletoe);
		bitmaps[6] = loadBitmap(R.drawable.christmas_present);
		bitmaps[7] = loadBitmap(R.drawable.christmas_snowman);
		bitmaps[8] = loadBitmap(R.drawable.christmas_tree);
		bitmaps[9] = loadBitmap(R.drawable.christmas_father);
		return bitmaps;
	}

}
