/*
 * Copyright (C) 2009 The Android Open Source Project
 * Copyright (C) 2012 ENTERTAILION, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.entertailion.android.overlay;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.entertailion.android.overlay.utils.Analytics;
import com.entertailion.android.overlay.utils.Utils;

/**
 * This activity sets up sprites and passes them off to a CanvasSurfaceView for
 * rendering and movement. Note that Bitmap objects come out of a pool and must
 * be explicitly recycled on shutdown. See onDestroy().
 * 
 * @see https://code.google.com/p/apps-for-android/
 */
// TODO send broadcast while animation is active
public class MainActivity extends Activity {
	private static final String LOG_TAG = "MainActivity";
	private CanvasSurfaceView canvasSurfaceView;
	private Mover mover;
	private boolean finished = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		canvasSurfaceView = new CanvasSurfaceView(this);

		init();

		canvasSurfaceView.setRenderer(mover);
		canvasSurfaceView.setEvent(mover);
		setContentView(canvasSurfaceView);

		// Set the context for Google Analytics
		Analytics.createAnalytics(this);
		Utils.logDeviceInfo(this);
	}

	/**
	 * @see android.app.Activity#onResume()
	 */
	public void onResume() {
		super.onResume();

		Analytics.logEvent(Analytics.OVERLAY_MAIN);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Start Google Analytics for this activity
		Analytics.startAnalytics(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Stop Google Analytics for this activity
		Analytics.stopAnalytics(this);
	}

	private void init() {
		SharedPreferences preferences = getSharedPreferences(ConfigActivity.PREFS_NAME, Activity.MODE_PRIVATE);
		String type = preferences.getString(ConfigActivity.PREFERENCE_TYPE, ConfigActivity.PREFERENCE_TYPE_DEFAULT);
		int duration = preferences.getInt(ConfigActivity.PREFERENCE_DURATION, ConfigActivity.PREFERENCE_DURATION_DEFAULT);
		switch (duration) {
		case 1:
			duration = 15;
			break;
		case 2:
			duration = 30;
			break;
		case 3:
			duration = 45;
			break;
		case 4:
			duration = 60;
			break;
		default:
			duration = 30;
		}
		
		// Tell other overlay apps
		((OverlayApplication)getApplicationContext()).setOverlayDuration(duration);
		((OverlayApplication)getApplicationContext()).setOverlayState(OutgoingReceiver.OVERLAY_INTENT_STATE_STARTED);

		// We need to know the width and height of the display pretty soon,
		// so grab the information now.
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		// let the system settle to make the animation smooth
		Runtime r = Runtime.getRuntime();
		r.gc();

		if (type.equals(ConfigActivity.PREFERENCE_TYPE_ANDROID)) {
			mover = new AndroidMover(this, dm.widthPixels, dm.heightPixels, duration, false);
		} else if (type.equals(ConfigActivity.PREFERENCE_TYPE_SNOW)) {
			mover = new SnowMover(this, dm.widthPixels, dm.heightPixels, duration, false);
		} else if (type.equals(ConfigActivity.PREFERENCE_TYPE_CHRISTMAS)) {
			mover = new ChristmasMover(this, dm.widthPixels, dm.heightPixels, duration, false);
		} else if (type.equals(ConfigActivity.PREFERENCE_TYPE_CHRISTMAS_LIGHTS)) {
			mover = new ChristmasLightsMover(this, dm.widthPixels, dm.heightPixels, duration, false);
		} else if (type.equals(ConfigActivity.PREFERENCE_TYPE_NEW_YEARS)) {
			mover = new NewYearsMover(this, dm.widthPixels, dm.heightPixels, duration, false);
		} else if (type.equals(ConfigActivity.PREFERENCE_TYPE_SMILEYS)) {
			mover = new SmileyMover(this, dm.widthPixels, dm.heightPixels, duration, false);
		} else if (type.equals(ConfigActivity.PREFERENCE_TYPE_STARS)) {
			mover = new StarsMover(this, dm.widthPixels, dm.heightPixels, duration, false);
		}
	}

	/** Recycles all of the bitmaps loaded in onCreate(). */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		canvasSurfaceView.clearEvent();
		canvasSurfaceView.stopDrawing();
		mover.onDestroy();
	}

	/**
	 * Finish activity when the user interacts
	 */
	protected void doFinish() {
		synchronized (this) {
			if (!finished) {
				finished = true;
				new Thread(new Runnable() {

					@Override
					public void run() {
						((OverlayApplication)getApplicationContext()).setOverlayState(OutgoingReceiver.OVERLAY_INTENT_STATE_STOPPED);
						MainActivity.this.finish();
					}

				}).start();
			}
		}
	}

	/**
	 * @see android.app.Activity#dispatchKeyEvent(android.view.KeyEvent)
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		doFinish();
		return super.dispatchKeyEvent(e);
	};

	/**
	 * @see android.app.Activity#dispatchTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent e) {
		doFinish();
		return super.dispatchTouchEvent(e);
	};

	/**
	 * @see android.app.Activity#dispatchGenericMotionEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean dispatchGenericMotionEvent(MotionEvent e) {
		doFinish();
		return super.dispatchGenericMotionEvent(e);
	};

	/**
	 * @see android.app.Activity#onWindowFocusChanged(boolean)
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (!hasFocus) {
			doFinish();
		}
	}

}
