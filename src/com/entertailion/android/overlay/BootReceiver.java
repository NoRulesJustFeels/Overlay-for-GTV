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

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Config alarm manager after device boots up
 * 
 */
public class BootReceiver extends BroadcastReceiver {
	private static final String LOG_CAT = "BootReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences prefs = context.getSharedPreferences(
				ConfigActivity.PREFS_NAME, Context.MODE_PRIVATE);
		boolean checked = prefs.getBoolean(ConfigActivity.PREFERENCE_ON_OFF,
				false);
		if (checked) { // ON
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent alarmIntent = new Intent(context, AlarmReceiver.class);
			alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
					0, alarmIntent, 0);

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			if (calendar.get(Calendar.MINUTE) < 30) {
				calendar.set(Calendar.MINUTE, 30);
			} else {
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.HOUR_OF_DAY,
						calendar.get(Calendar.HOUR_OF_DAY) + 1);
			}

			// configure the alarm manager to invoke the mover activity
			// every 30 mins
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), 1000 * 60 * 30, pendingIntent);
		}
	}
}