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

public class NewYearsMover extends Mover {
	private static final String LOG_CAT = "NewYearsMover";
	private static final int EXPLOSION_SIZE = 200;
	private long firstTime;
	private long lastTime;
	private boolean ending;
	private int counter = 0;
	private Explosion explosion;

	public NewYearsMover(Context context, int width, int height, int count, boolean config) {
		super(context, width, height, count, config);
		count = 1;
		// Allocate space for the robot sprites
		spriteArray = new Renderable[count];

		bitmaps = getBitmaps();

		// This list of things to move. It points to the same content as
		// spriteArray except for the background.
		int step = width / count;
		// above
		for (int x = 0; x < count; x++) {
			Renderable robot;
			int pos = (int) Math.round(Math.random() * (bitmaps.length - 1));
			robot = new Renderable();
			robot.bitmap = bitmaps[pos];
			robot.width = 64;
			robot.height = 64;

			// Pick a random location for this sprite.
			robot.x = width / 2 - robot.bitmap.getWidth() / 2;
			robot.y = height - robot.bitmap.getHeight();
			robot.rotation = (float) (Math.random() * 360);
			robot.rotation = 180f;

			// Add this robot to the spriteArray so it gets drawn
			spriteArray[x] = robot;
		}
		firstTime = SystemClock.uptimeMillis();
		lastTime = firstTime;

		if (!config) {
			explosion = new Explosion(EXPLOSION_SIZE, width / 2, 100);
		}
	}

	public void run() {
		// Perform a single simulation step.
		final long time = SystemClock.uptimeMillis();
		final long timeDelta = time - firstTime;
		final long lastTimeDelta = time - lastTime;
		if (timeDelta > 30 * 1000) {
			ending = true;
		}

		if (lastTimeDelta > 100) {
			boolean visible = false;
			for (int x = 0; x < spriteArray.length; x++) {
				Renderable object = spriteArray[x];
				if (!ending) {
					object.alpha = 220 + (int) (Math.random() * 35);
				} else if (object.alpha > 0) {
					object.alpha = object.alpha / 2;
				}
				if (object.alpha > 0) {
					visible = true;
				}
			}
			if (!visible) {
				throw new RuntimeException();
			}
			lastTime = time;
			counter++;
		}

		if (explosion != null) { 
			if (explosion.isAlive()) {
				explosion.update();
			} else if (!ending) {
				explosion = new Explosion(EXPLOSION_SIZE, (int)(Math.random()*width), 100);
			}
		}
	}

	public void drawFrame(Canvas canvas) {
		if (spriteArray != null) {
			canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

			// render explosions
			if (explosion != null) {
				explosion.draw(canvas);
			}

			if (!ending) {
				paint.setAlpha(255);
			}
			canvas.drawBitmap(bitmaps[4], 0, height - bitmaps[4].getHeight(), paint); // bottle
			canvas.drawBitmap(bitmaps[5], bitmaps[4].getWidth(), height - bitmaps[5].getHeight(), paint); // fireworks
			canvas.drawBitmap(bitmaps[6], width - bitmaps[6].getWidth(), height - bitmaps[6].getHeight(), paint); // plume
			canvas.drawBitmap(bitmaps[7], width - bitmaps[6].getWidth() - bitmaps[7].getWidth(), height - bitmaps[7].getHeight(), paint); // rockets

			// balloons on top
			int balloonsWidth = bitmaps[3].getWidth();
			canvas.drawBitmap(bitmaps[3], width / 2 - balloonsWidth / 2, 0, paint);
			canvas.drawBitmap(bitmaps[3], width / 2 - balloonsWidth * 3 / 2, 0, paint);
			canvas.drawBitmap(bitmaps[3], width / 2 - balloonsWidth * 5 / 2, 0, paint);
			canvas.drawBitmap(bitmaps[3], width / 2 - balloonsWidth * 7 / 2, 0, paint);
			canvas.drawBitmap(bitmaps[3], width / 2 - balloonsWidth * 9 / 2, 0, paint);
			canvas.drawBitmap(bitmaps[3], width / 2 - balloonsWidth * 11 / 2, 0, paint);
			canvas.drawBitmap(bitmaps[3], width / 2 - balloonsWidth * 13 / 2, 0, paint);
			canvas.drawBitmap(bitmaps[3], width / 2 - balloonsWidth * 15 / 2, 0, paint);
			canvas.drawBitmap(bitmaps[3], width / 2 + balloonsWidth * 1 / 2, 0, paint);
			canvas.drawBitmap(bitmaps[3], width / 2 + balloonsWidth * 3 / 2, 0, paint);
			canvas.drawBitmap(bitmaps[3], width / 2 + balloonsWidth * 5 / 2, 0, paint);
			canvas.drawBitmap(bitmaps[3], width / 2 + balloonsWidth * 7 / 2, 0, paint);
			canvas.drawBitmap(bitmaps[3], width / 2 + balloonsWidth * 9 / 2, 0, paint);
			canvas.drawBitmap(bitmaps[3], width / 2 + balloonsWidth * 11 / 2, 0, paint);
			canvas.drawBitmap(bitmaps[3], width / 2 + balloonsWidth * 13 / 2, 0, paint);

			for (int x = 0; x < spriteArray.length; x++) {
				paint.setAlpha(spriteArray[x].alpha);
				canvas.drawBitmap(bitmaps[counter % 3], width / 2 - bitmaps[counter % 3].getWidth() / 2, height - bitmaps[counter % 3].getHeight(), paint);
			}
		}
	}

	public Bitmap[] getBitmaps() {
		Bitmap[] bitmaps = new Bitmap[8];
		bitmaps[0] = loadBitmap(R.drawable.newyears1);
		bitmaps[1] = loadBitmap(R.drawable.newyears2);
		bitmaps[2] = loadBitmap(R.drawable.newyears3);
		bitmaps[3] = loadBitmap(R.drawable.newyears_balloons);
		bitmaps[4] = loadBitmap(R.drawable.newyears_champagne);
		bitmaps[5] = loadBitmap(R.drawable.newyears_fireworks);
		bitmaps[6] = loadBitmap(R.drawable.newyears_plume);
		bitmaps[7] = loadBitmap(R.drawable.newyears_rockets);
		return bitmaps;
	}

}
