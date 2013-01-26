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
import android.os.SystemClock;

/**
 * A simple runnable that updates the position of each sprite on the screen
 * every frame by applying a very simple gravity simulation.
 */
public abstract class FallingMover extends Mover {
	private static final String LOG_CAT = "FallingMover";
	protected long lastTime;

	static final float SPEED_OF_GRAVITY = 400.0f;
	static final int COUNT = 10;

	public FallingMover(Context context, int width, int height, int duration, boolean config) {
		super(context, width, height, duration, config);
		// Allocate space for the robot sprites
		spriteArray = new Renderable[COUNT];

		bitmaps = getBitmaps();

		// This list of things to move. It points to the same content as
		// spriteArray except for the background.
		for (int x = 0; x < COUNT; x++) {
			Renderable robot;
			int pos = (int) Math.round(Math.random() * (bitmaps.length - 1));
			robot = new Renderable();
			robot.bitmap = bitmaps[pos];
			robot.width = 64;
			robot.height = 64;

			// Pick a random location for this sprite.
			robot.x = (float) (Math.random() * width);
			if (config) {
				robot.y = (float) (Math.random() * height);
			} else {
				robot.y = -(float) (Math.random() * height);
			}
			robot.startx = robot.x;
			robot.starty = robot.y;
			robot.startz = robot.z;
			if (pos == 3 || pos == 4 || pos == 5) { // smaller = slower
				robot.velocityY = (float) (Math.random() * SPEED_OF_GRAVITY / 4) + SPEED_OF_GRAVITY / 2;
			} else {
				robot.velocityY = (float) (Math.random() * SPEED_OF_GRAVITY / 3) + SPEED_OF_GRAVITY * 2 / 3;
			}
			// robot.rotation = (float) (Math.random() * 360);

			// Add this robot to the spriteArray so it gets drawn
			spriteArray[x] = robot;
		}
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
			if (object.y > height) {
				object.y = object.starty; // -(float) (Math.random() * height /
											// 2);
				object.alpha = 255;
			} else {
				// add a gravity effect
				object.y = object.y + (object.velocityY * timeDeltaSeconds);
				if (object.y > 0) {
					object.alpha = 255 - (int) ((object.y / height) * 255);
				}
				object.x = object.x + (object.velocityX * timeDeltaSeconds);
			}
			// object.rotation = object.rotation + 0.5f;
		}
	}

	public abstract Bitmap[] getBitmaps();

}
