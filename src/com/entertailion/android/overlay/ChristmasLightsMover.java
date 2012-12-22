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
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.os.SystemClock;

/**
 * A simple runnable that updates the position of each sprite on the screen
 * every frame by applying a very simple gravity simulation.
 */
public class ChristmasLightsMover extends Mover {
	private static final String LOG_CAT = "ChristmasLightsMover";
	private long firstTime;
	private long lastTime;
	private boolean ending;

	public ChristmasLightsMover(Context context, int width, int height, int count,
			boolean config) {
		super(context, width, height, count, config);
		if (!config) {
			count = count*2;
			super.count = count;
		}
		// Allocate space for the robot sprites
		spriteArray = new Renderable[count];

		bitmaps = getBitmaps();
		
		// This list of things to move. It points to the same content as
		// spriteArray except for the background.
		int step = width/count;
		// above
		for (int x = 0; x < count; x++) {
			Renderable robot;
			int pos = (int) Math.round(Math.random() * (bitmaps.length - 1));
			robot = new Renderable();
			robot.bitmap = bitmaps[pos];
			robot.width = 64;
			robot.height = 64;

			// Pick a random location for this sprite.
			robot.x = step*x;
			robot.y = -10;
			robot.rotation = (float) (Math.random() * 360);
			robot.rotation = 180f;

			// Add this robot to the spriteArray so it gets drawn
			spriteArray[x] = robot;
		}
		firstTime = SystemClock.uptimeMillis();
		lastTime = firstTime;
	}

	public void run() {
		// Perform a single simulation step.
		final long time = SystemClock.uptimeMillis();
		final long timeDelta = time - firstTime;
		final long lastTimeDelta = time - lastTime;
		if (timeDelta>30*1000) {
			ending = true;
		}

		if (lastTimeDelta>100) {
			boolean visible = false;
			for (int x = 0; x < spriteArray.length; x++) {
				Renderable object = spriteArray[x];
				if (!ending) {
					object.alpha = 220 + (int)(Math.random()*35);
				} else if (object.alpha>0) {
					object.alpha = object.alpha/2;
				}
				if (object.alpha>0) {
					visible = true;
				}
			}
			if (!visible) {
				throw new RuntimeException();
			}
			lastTime = time;
		}
	}
	
	public void drawFrame(Canvas canvas) {
		if (spriteArray != null) {
			canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			for (int x = 0; x < spriteArray.length; x++) {
				paint.setAlpha(spriteArray[x].alpha);
				canvas.drawBitmap(spriteArray[x].bitmap, spriteArray[x].x, spriteArray[x].y, paint);
				Matrix matrix = new Matrix();
				matrix.setRotate(spriteArray[x].rotation, spriteArray[x].bitmap.getWidth()/2, spriteArray[x].bitmap.getHeight()/2);
				matrix.postTranslate(spriteArray[x].x, spriteArray[x].y+height-spriteArray[x].bitmap.getHeight()+20);
				canvas.drawBitmap(spriteArray[x].bitmap, matrix, paint);
			}
		}
	}
	
	public Bitmap[] getBitmaps() {
		Bitmap[] bitmaps = new Bitmap[3];
		bitmaps[0] = loadBitmap(R.drawable.christmas_light_blue);
		bitmaps[1] = loadBitmap(R.drawable.christmas_light_green);
		bitmaps[2] = loadBitmap(R.drawable.christmas_light_red);
		return bitmaps;
	}

}
