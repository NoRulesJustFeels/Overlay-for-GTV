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
import android.util.Log;

/**
 * A simple runnable that updates the position of each sprite on the screen
 * every frame by applying a very simple gravity and bounce simulation. The
 * sprites are jumbled with random velocities every once and a while.
 * 
 * @see https://code.google.com/p/apps-for-android/
 */
public class AndroidMover extends Mover {
	private static final String LOG_CAT = "AndroidMover";
	private long lastTime;
	private long lastJumbleTime;
	private int jumbleCount;

	static final float COEFFICIENT_OF_RESTITUTION = 0.75f;
	static final float SPEED_OF_GRAVITY = 150.0f;
	static final long JUMBLE_EVERYTHING_DELAY = 15 * 1000;
	static final float MAX_VELOCITY = 500.0f;

	public AndroidMover(Context context, int width, int height, int count, boolean config) {
		super(context, width, height, count, config);
		// Allocate space for the robot sprites + one background sprite.
		spriteArray = new CanvasSprite[count];

		bitmaps = new Bitmap[3];
		bitmaps[0] = loadBitmap(R.drawable.skate1);
		bitmaps[1] = loadBitmap(R.drawable.skate2);
		bitmaps[2] = loadBitmap(R.drawable.skate3);

		// This list of things to move. It points to the same content as
		// spriteArray except for the background.
		final int robotBucketSize = count / 3;
		for (int x = 0; x < count; x++) {
			CanvasSprite robot;
			// Our robots come in three flavors. Split them up accordingly.
			if (x < robotBucketSize) {
				robot = new CanvasSprite(bitmaps[0]);
			} else if (x < robotBucketSize * 2) {
				robot = new CanvasSprite(bitmaps[1]);
			} else {
				robot = new CanvasSprite(bitmaps[2]);
			}

			robot.width = 64;
			robot.height = 64;

			// Pick a random location for this sprite.
			robot.x = (float) (Math.random() * width);
			robot.y = (float) (Math.random() * height);

			// Add this robot to the spriteArray so it gets drawn
			spriteArray[x] = robot;
		}
	}

	/**
	 * @see com.entertailion.android.overlay.Mover#run()
	 */
	public void run() {
		// Perform a single simulation step.
		final long time = SystemClock.uptimeMillis();
		final long timeDelta = time - lastTime;
		final float timeDeltaSeconds = lastTime > 0.0f ? timeDelta / 1000.0f
				: 0.0f;
		lastTime = time;

		// Check to see if it's time to jumble again.
		boolean jumble = (time - lastJumbleTime > JUMBLE_EVERYTHING_DELAY);
		if (jumble) {
			lastJumbleTime = time;
			jumbleCount++;
			// limit the number of jumbles
			if (jumbleCount==2) {
				jumble = false;
			}
		}

		boolean visible = false;
		for (int x = 0; x < spriteArray.length; x++) {
			Renderable object = spriteArray[x];

			// Jumble! Apply random velocities.
			if (jumble) {
				object.velocityX += (MAX_VELOCITY / 2.0f)
						- (float) (Math.random() * MAX_VELOCITY);
				object.velocityY += (MAX_VELOCITY / 2.0f)
						- (float) (Math.random() * MAX_VELOCITY);
			}

			// Move.
			object.x = object.x + (object.velocityX * timeDeltaSeconds);
			object.y = object.y + (object.velocityY * timeDeltaSeconds);
			object.z = object.z + (object.velocityZ * timeDeltaSeconds);

			// Apply Gravity.
			object.velocityY += SPEED_OF_GRAVITY * timeDeltaSeconds;

			// Bounce.
			if ((object.x < 0.0f && object.velocityX < 0.0f)
					|| (object.x > width - object.width && object.velocityX > 0.0f)) {
				object.velocityX = -object.velocityX
						* COEFFICIENT_OF_RESTITUTION;
				object.x = Math.max(0.0f,
						Math.min(object.x, width - object.width));
				if (Math.abs(object.velocityX) < 0.1f) {
					object.velocityX = 0.0f;
				}
			}

			if ((object.y < 0.0f && object.velocityY < 0.0f)
					|| (object.y > height - object.height && object.velocityY > 0.0f)) {
				object.count++;
				if (object.count>3) {
					object.alpha = object.alpha-100;
					if (object.alpha<0) {
						object.alpha = 0;
					}
				}
				object.velocityY = -object.velocityY
						* COEFFICIENT_OF_RESTITUTION;
				object.y = Math.max(0.0f,
						Math.min(object.y, height - object.height));
				if (Math.abs(object.velocityY) < 0.1f) {
					object.velocityY = 0.0f;
				}
			}

			if (object.alpha>0) {
				visible = true;
			}
		}
		// no more objects visible, so close activity
		if (!visible) {
			throw new RuntimeException();
		}
	}

	/**
	 * @see com.entertailion.android.overlay.Mover#sizeChanged(int, int)
	 */
	public void sizeChanged(int width, int height) {
		// nothing to be done
	}

}
