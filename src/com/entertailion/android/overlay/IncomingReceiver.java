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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Receive overlay queries from other apps.
 * 
 * @author leon_nicholls
 *
 */
public class IncomingReceiver extends BroadcastReceiver {
	private static final String LOG_TAG = "OutgoingReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "intent="+intent);
		if (intent.getAction().equals(OutgoingReceiver.OVERLAY_QUERY_INTENT)) {
			if (intent.getExtras() != null
					&& !intent.getExtras().getString(OutgoingReceiver.OVERLAY_INTENT_PACKAGE_NAME, "").equals(OutgoingReceiver.PACKAGE_NAME)) {
				if (((OverlayApplication) context.getApplicationContext()).getOverlayState().equals(OutgoingReceiver.OVERLAY_INTENT_STATE_STARTED)) {
					Intent responseIntent = new Intent();
					responseIntent.setAction(OutgoingReceiver.OVERLAY_INTENT);
					responseIntent.putExtra(OutgoingReceiver.OVERLAY_INTENT_PACKAGE_NAME, OutgoingReceiver.PACKAGE_NAME);
					responseIntent.putExtra(OutgoingReceiver.OVERLAY_INTENT_STATE, OutgoingReceiver.OVERLAY_INTENT_STATE_STARTED);
					responseIntent.putExtra(OutgoingReceiver.OVERLAY_INTENT_SCREEN_RECTANGLE, "0,0,0,0");  // full screen
					responseIntent.putExtra(OutgoingReceiver.OVERLAY_INTENT_DURATION, ((OverlayApplication) context.getApplicationContext()).getOverlayDuration());
					context.sendBroadcast(responseIntent);
				}
			}
		}
	}
}