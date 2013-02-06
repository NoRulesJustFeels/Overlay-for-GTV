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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.SystemClock;

public class ValentinesMover extends Mover {
	private static final String LOG_CAT = "ValentinesMover";
	private long lastTime;
	static final float SPEED_OF_GRAVITY = 400.0f;

	public ValentinesMover(Context context, int width, int height, int duration, boolean config) {
		super(context, width, height, duration, config);
		// Allocate space for the robot sprites
		int count = 5;
		spriteArray = new Renderable[count];

		bitmaps = getBitmaps();

		for (int x = 0; x < count; x++) {
			Renderable robot;
			robot = new Renderable();
			if (x % 2 == 0) {
				robot.bitmap = bitmaps[4];
				robot.x = (int) ((width / 2.0) - Math.random() * (width / 4.0));
				robot.velocityY = (float) (Math.random() * SPEED_OF_GRAVITY / 4) + SPEED_OF_GRAVITY / 2;
			} else {
				robot.bitmap = bitmaps[5];
				robot.x = (int) ((width / 2.0) + Math.random() * (width / 4.0));
				robot.velocityY = (float) (Math.random() * SPEED_OF_GRAVITY / 3) + SPEED_OF_GRAVITY * 2 / 3;
			}
			robot.width = 64;
			robot.height = 64;
			robot.y = height;
			robot.startx = robot.x;
			robot.starty = robot.y;

			// Add this robot to the spriteArray so it gets drawn
			spriteArray[x] = robot;
		}
		lastTime = SystemClock.uptimeMillis();

	}

	public void doRun() {
		// Perform a single simulation step.
		final long time = SystemClock.uptimeMillis();
		final long timeDelta = time - lastTime;
		final float timeDeltaSeconds = lastTime > 0.0f ? timeDelta / 1000.0f : 0.0f;
		lastTime = time;

		for (int x = 0; x < spriteArray.length; x++) {
			Renderable object = spriteArray[x];

			// reset sprite position if goes over bottom edge
			if (object.y <= 0) {
				object.y = object.starty;
				object.alpha = 255;
			} else {
				// add a gravity effect
				object.y = object.y - (object.velocityY * timeDeltaSeconds);
				if (object.y < height && object.y > 0) {
					object.alpha = (int) ((object.y / height) * 255);
				}
				object.x = object.x + (object.velocityX * timeDeltaSeconds);
			}
			// object.rotation = object.rotation + 0.5f;
		}
	}

	public void drawFrame(Canvas canvas) {
		if (spriteArray != null) {
			canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

			if (!config) {
				for (int x = 0; x < spriteArray.length; x++) {
					if (!finished) {
						paint.setAlpha(spriteArray[x].alpha);
					}
					canvas.drawBitmap(spriteArray[x].bitmap, spriteArray[x].x, spriteArray[x].y, paint);
				}
			}
			
			if (!finished) {
				paint.setAlpha(255);
			}
			
			canvas.drawBitmap(bitmaps[2], 0, 0, paint); // top/left
			canvas.drawBitmap(bitmaps[3], width - bitmaps[3].getWidth(), 0, paint); // top/right
			canvas.drawBitmap(bitmaps[0], 0, height - bitmaps[0].getHeight(), paint); // bottom/left
			canvas.drawBitmap(bitmaps[1], width - bitmaps[1].getWidth(), height - bitmaps[1].getHeight(), paint); // bottom/right

			canvas.drawBitmap(bitmaps[7], (int) (width / 2.0 - bitmaps[7].getWidth() / 2.0), height - bitmaps[7].getHeight(), paint); // hearts bottom center
			
			canvas.drawBitmap(bitmaps[8], (int) (width / 2.0 - bitmaps[8].getWidth() / 2.0), 0, paint); // cupid top center

			if (!config && finished) {
				canvas.drawBitmap(bitmaps[6], (int) (width / 2.0 - bitmaps[6].getWidth() / 2.0), (int) (height / 2.0 - bitmaps[6].getHeight() / 2.0), paint); // large
																																								// heart
			}

		}
	}

	public Bitmap[] getBitmaps() {
		Bitmap[] bitmaps = new Bitmap[9];
		bitmaps[0] = loadBitmap(R.drawable.valentines_border_bottom_left);
		bitmaps[1] = loadBitmap(R.drawable.valentines_border_bottom_right);
		bitmaps[2] = loadBitmap(R.drawable.valentines_border_top_left);
		bitmaps[3] = loadBitmap(R.drawable.valentines_border_top_right);
		bitmaps[4] = loadBitmap(R.drawable.valentines_heart1);
		bitmaps[5] = loadBitmap(R.drawable.valentines_heart2);
		bitmaps[6] = loadBitmap(R.drawable.valentines_heart_large);
		bitmaps[7] = loadBitmap(R.drawable.valentines_hearts);
		bitmaps[8] = loadBitmap(R.drawable.valentines_cupid);
		return bitmaps;
	}

}
