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
import android.os.SystemClock;

/**
 * A simple runnable that updates the position of each sprite on the screen
 * every frame by applying a very simple gravity simulation.
 */
public class SnowMover extends Mover {
	private static final String LOG_CAT = "SnowMover";
	private static final int ANIMATION_LOOPS = 2;
	private long lastTime;
	private int loopCounter;

	static final float SPEED_OF_GRAVITY = 400.0f;

	public SnowMover(Context context, int width, int height, int count,
			boolean config) {
		super(context, width, height, count, config);
		// Allocate space for the robot sprites
		spriteArray = new CanvasSprite[count];

		bitmaps = new Bitmap[10];
		bitmaps[0] = loadBitmap(R.drawable.snow1);
		bitmaps[1] = loadBitmap(R.drawable.snow2);
		bitmaps[2] = loadBitmap(R.drawable.snow3);
		bitmaps[3] = loadBitmap(R.drawable.snow4);
		bitmaps[4] = loadBitmap(R.drawable.snow5);
		bitmaps[5] = loadBitmap(R.drawable.snow6);
		bitmaps[6] = loadBitmap(R.drawable.snow7);
		bitmaps[7] = loadBitmap(R.drawable.snow8);
		bitmaps[8] = loadBitmap(R.drawable.snow9);
		bitmaps[9] = loadBitmap(R.drawable.snow10);

		// This list of things to move. It points to the same content as
		// spriteArray except for the background.
		for (int x = 0; x < count; x++) {
			CanvasSprite robot;
			int pos = (int) Math.round(Math.random() * (bitmaps.length - 1));
			robot = new CanvasSprite(bitmaps[pos]);

			robot.width = 64;
			robot.height = 64;

			// Pick a random location for this sprite.
			robot.x = (float) (Math.random() * width);
			if (config) {
				robot.y = (float) (Math.random() * height);
			} else {
				robot.y = -(float) (Math.random() * height);
			}
			if (pos == 3 || pos == 4 || pos == 5) { // smaller = slower
				robot.velocityY = (float) (Math.random() * SPEED_OF_GRAVITY / 4)
						+ SPEED_OF_GRAVITY / 4;
			} else {
				robot.velocityY = (float) (Math.random() * SPEED_OF_GRAVITY / 3)
						+ SPEED_OF_GRAVITY * 2 / 3;
			}

			// Add this robot to the spriteArray so it gets drawn
			spriteArray[x] = robot;
		}
	}

	public void run() {
		// Perform a single simulation step.
		final long time = SystemClock.uptimeMillis();
		final long timeDelta = time - lastTime;
		final float timeDeltaSeconds = lastTime > 0.0f ? timeDelta / 1000.0f
				: 0.0f;
		lastTime = time;

		boolean visible = false;
		for (int x = 0; x < spriteArray.length; x++) {
			Renderable object = spriteArray[x];

			// reset sprite position if goes over bottom edge
			if (object.y > height) {
				loopCounter++;
				// stop resetting if loop limit reached
				if (loopCounter < ANIMATION_LOOPS * spriteArray.length) {
					object.y = -(float) (Math.random() * height / 2);
					object.alpha = 255;
				}
			} else {
				visible = true; // at least one object is still visible on
								// screen
				// add a gravity effect
				object.y = object.y + (object.velocityY * timeDeltaSeconds);
				if (object.y > 0) {
					object.alpha = 255 - (int) ((object.y / height) * 255);
				}
			}
		}
		// no more visible objects, so exit activity
		if (!visible) {
			throw new RuntimeException();
		}
	}

	/**
	 * @see com.entertailion.android.overlay.Mover#drawFrame(android.graphics.Canvas)
	 */
	public void drawFrame(Canvas canvas) {
		if (spriteArray != null) {
			for (int x = 0; x < spriteArray.length; x++) {
				spriteArray[x].draw(canvas);
			}
		}
	}

	/**
	 * @see com.entertailion.android.overlay.Mover#sizeChanged(int, int)
	 */
	public void sizeChanged(int width, int height) {
		// nothing to be done
	}

}
