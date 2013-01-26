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

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * A simple runnable that updates the position of each sprite on the screen
 * every frame by applying a very simple gravity simulation.
 */
public class SmileyMover extends FallingMover {
	private static final String LOG_CAT = "SmileyMover";

	private Random random;

	public SmileyMover(Context context, int width, int height, int duration, boolean config) {
		super(context, width, height, duration, config);
		random = new Random();

		for (int x = 0; x < spriteArray.length; x++) {
			Renderable object = spriteArray[x];
			int randomValue = random.nextInt(2);
			if (randomValue == 0) {
				object.velocityX = -(50 + random.nextInt(50));
			} else if (randomValue == 1) {
				object.velocityX = (50 + random.nextInt(50));
			}
			object.count = 0;
		}
	}

	public void run() {
		super.run();

		for (int x = 0; x < spriteArray.length; x++) {
			int randomValue = random.nextInt(spriteArray.length);
			Renderable object = spriteArray[x];
			if (object.y > height/3 && randomValue >= 2*spriteArray.length / 3 && object.count == 0) {
				object.velocityX = -object.velocityX;
				object.count = 1;
			}
		}
	}

	@Override
	public Bitmap[] getBitmaps() {
		Bitmap[] bitmaps = new Bitmap[7];
		bitmaps[0] = loadBitmap(R.drawable.face_glasses);
		bitmaps[1] = loadBitmap(R.drawable.face_grin);
		bitmaps[2] = loadBitmap(R.drawable.face_kiss);
		bitmaps[3] = loadBitmap(R.drawable.face_plain);
		bitmaps[4] = loadBitmap(R.drawable.face_smile);
		bitmaps[5] = loadBitmap(R.drawable.face_smile_big);
		bitmaps[6] = loadBitmap(R.drawable.face_wink);
		return bitmaps;
	}

}
