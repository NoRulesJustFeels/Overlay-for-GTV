/*
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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Receive overlay notifications from other apps.
 * 
 * @author leon_nicholls
 * 
 */
public class OutgoingReceiver extends BroadcastReceiver {
	private static final String LOG_TAG = "OutgoingReceiver";

	public static final String OVERLAY_INTENT = "com.entertailion.overlay.action.OVERLAY";
	public static final String OVERLAY_QUERY_INTENT = "com.entertailion.overlay.action.OVERLAY_QUERY";
	public static final String OVERLAY_INTENT_STATE = "com.entertailion.intent.extra.overlay.STATE";
	public static final String OVERLAY_INTENT_STATE_STARTED = "started";
	public static final String OVERLAY_INTENT_STATE_STOPPED = "stopped";
	public static final String OVERLAY_INTENT_STATE_QUERY = "query";
	public static final String OVERLAY_INTENT_PACKAGE_NAME = "com.entertailion.intent.extra.overlay.PACKAGE_NAME";
	public static final String OVERLAY_INTENT_SCREEN_RECTANGLE = "com.entertailion.intent.extra.overlay.SCREEN_RECTANGLE";
	public static final String OVERLAY_INTENT_DURATION = "com.entertailion.intent.extra.overlay.DURATION"; // seconds
	public static final String OVERLAY_INTENT_TIME = "com.entertailion.intent.extra.overlay.TIME"; // ms
	public static final String PACKAGE_NAME = "com.entertailion.android.overlay";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "intent=" + intent);
		if (intent.getAction().equals(OutgoingReceiver.OVERLAY_INTENT)) {
			if (intent.getExtras() != null
					&& !intent.getExtras().getString(OutgoingReceiver.OVERLAY_INTENT_PACKAGE_NAME, "").equals(OutgoingReceiver.PACKAGE_NAME)) {
				Log.d(LOG_TAG, "state="+((OverlayApplication) context.getApplicationContext()).getOverlayState());
				if (intent.getExtras().getString(OutgoingReceiver.OVERLAY_INTENT_STATE, "").equals(OutgoingReceiver.OVERLAY_INTENT_STATE_STARTED)) {
					((OverlayApplication) context.getApplicationContext()).setOtherOverlayAppActive(true);
				}
			}
		}
	}

	public static void sendOverlayIntent(Context context) {
		// send broadcast to other overlay apps
		SharedPreferences preferences = context.getSharedPreferences(ConfigActivity.PREFS_NAME, Activity.MODE_PRIVATE);
		int duration = preferences.getInt(ConfigActivity.PREFERENCE_DURATION, ConfigActivity.PREFERENCE_DURATION_DEFAULT);
		Intent overlayIntent = new Intent();
		overlayIntent.setAction(OutgoingReceiver.OVERLAY_INTENT);
		overlayIntent.putExtra(OutgoingReceiver.OVERLAY_INTENT_PACKAGE_NAME, OutgoingReceiver.PACKAGE_NAME);
		overlayIntent.putExtra(OutgoingReceiver.OVERLAY_INTENT_STATE, ((OverlayApplication) context.getApplicationContext()).getOverlayState());
		overlayIntent.putExtra(OutgoingReceiver.OVERLAY_INTENT_SCREEN_RECTANGLE, "0,0,0,0");
		overlayIntent.putExtra(OutgoingReceiver.OVERLAY_INTENT_DURATION, duration);
		context.sendBroadcast(overlayIntent);
	}

	public static void sendOverlayQueryIntent(Context context) {
		// send query broadcast to other overlay apps
		long currentTime = System.currentTimeMillis();
		((OverlayApplication) context.getApplicationContext()).setOverlayTime(currentTime);
		Intent overlayIntent = new Intent();
		overlayIntent.setAction(OutgoingReceiver.OVERLAY_QUERY_INTENT);
		overlayIntent.putExtra(OutgoingReceiver.OVERLAY_INTENT_PACKAGE_NAME, OutgoingReceiver.PACKAGE_NAME);
		overlayIntent.putExtra(OutgoingReceiver.OVERLAY_INTENT_TIME, currentTime);
		context.sendBroadcast(overlayIntent);
	}

}