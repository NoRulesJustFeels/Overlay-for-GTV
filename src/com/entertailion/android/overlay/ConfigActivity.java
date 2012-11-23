/*
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.ToggleButton;

/**
 * Main UI to let the user configure the movers
 * 
 */
public class ConfigActivity extends Activity {
	public static final String LOG_CAT = "ConfigActivity";
	public static final String PREFS_NAME = "preferences";
	public static String PREFERENCE_TYPE = "preference.type";
	public static String PREFERENCE_TYPE_ANDROID = "preference.type.android";
	public static String PREFERENCE_TYPE_SNOW = "preference.type";
	public static String PREFERENCE_TYPE_DEFAULT = PREFERENCE_TYPE_ANDROID;
	public static String PREFERENCE_TIMING = "preference.timing";
	public static int PREFERENCE_TIMING_DEFAULT = 60;
	public static String PREFERENCE_AMOUNT = "preference.amount";
	public static int PREFERENCE_AMOUNT_DEFAULT = 10;
	public static String PREFERENCE_ON_OFF = "preference.onoff";
	public static final int CONFIG_COUNT = 20;
	public static String LAST_TIME_RUN = "last.time.run";
	private MoverView moverView;
	private Spinner typeSpinner;
	private Spinner timingSpinner;
	private Spinner amountSpinner;
	private ToggleButton toggleButton;
	private int width, height;
	private Handler handler = new Handler();
	private boolean changed; // track configuration changes

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.config);

		moverView = (MoverView) findViewById(R.id.moverView);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;

		typeSpinner = (Spinner) findViewById(R.id.spinnerType);
		List<String> typeList = new ArrayList<String>();
		typeList.add(getString(R.string.type_1));
		typeList.add(getString(R.string.type_2));
		ArrayAdapter<String> typeDataAdapter = new ArrayAdapter<String>(
				ConfigActivity.this, android.R.layout.simple_spinner_item,
				typeList);
		typeDataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeSpinner.setAdapter(typeDataAdapter);
		typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				changed = true;
				String type = PREFERENCE_TYPE_ANDROID;
				switch (pos) {
				case 0: // Android
					type = PREFERENCE_TYPE_ANDROID;
					Mover androidMover = new AndroidMover(ConfigActivity.this,
							width, height, CONFIG_COUNT, true);
					moverView.setMover(androidMover);
					break;
				case 1: // Snow
					type = PREFERENCE_TYPE_SNOW;
					Mover snowMover = new SnowMover(ConfigActivity.this, width,
							height, CONFIG_COUNT, true);
					moverView.setMover(snowMover);
					break;
				default:
					break;
				}

				SharedPreferences prefs = getSharedPreferences(PREFS_NAME,
						Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = prefs.edit();
				edit.putString(PREFERENCE_TYPE, type);
				edit.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
		final SharedPreferences preferences = getSharedPreferences(PREFS_NAME,
				Activity.MODE_PRIVATE);
		String type = preferences.getString(PREFERENCE_TYPE,
				PREFERENCE_TYPE_DEFAULT);
		if (type.equals(PREFERENCE_TYPE_ANDROID)) {
			typeSpinner.setSelection(0); // android
		} else {
			typeSpinner.setSelection(1); // snow
		}

		timingSpinner = (Spinner) findViewById(R.id.spinnerTiming);
		List<String> timingList = new ArrayList<String>();
		timingList.add(getString(R.string.timing_1));
		timingList.add(getString(R.string.timing_2));
		timingList.add(getString(R.string.timing_3));
		ArrayAdapter<String> timingDataAdapter = new ArrayAdapter<String>(
				ConfigActivity.this, android.R.layout.simple_spinner_item,
				timingList);
		timingDataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		timingSpinner.setAdapter(timingDataAdapter);
		timingSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				changed = true;
				int timing = 60;
				switch (pos) {
				case 0: // 30 mins
					timing = 30;
					break;
				case 1: // 1 hours
					timing = 60;
					break;
				case 2: // 2 hours
					timing = 120;
					break;
				default:
					break;
				}
				SharedPreferences prefs = getSharedPreferences(PREFS_NAME,
						Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = prefs.edit();
				edit.putInt(PREFERENCE_TIMING, timing);
				edit.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
		int timing = preferences.getInt(PREFERENCE_TIMING,
				PREFERENCE_TIMING_DEFAULT);
		switch (timing) {
		case 30: // 30 mins
			timingSpinner.setSelection(0);
			break;
		case 60: // 1 hours
			timingSpinner.setSelection(1);
			break;
		case 120: // 2 hours
			timingSpinner.setSelection(2);
			break;
		default:
			break;
		}

		amountSpinner = (Spinner) findViewById(R.id.spinnerAmount);
		List<String> amountList = new ArrayList<String>();
		amountList.add(getString(R.string.amount_1));
		amountList.add(getString(R.string.amount_2));
		amountList.add(getString(R.string.amount_3));
		amountList.add(getString(R.string.amount_4));
		amountList.add(getString(R.string.amount_5));
		amountList.add(getString(R.string.amount_6));
		amountList.add(getString(R.string.amount_7));
		amountList.add(getString(R.string.amount_8));
		amountList.add(getString(R.string.amount_9));
		amountList.add(getString(R.string.amount_10));
		ArrayAdapter<String> amountDataAdapter = new ArrayAdapter<String>(
				ConfigActivity.this, android.R.layout.simple_spinner_item,
				amountList);
		amountDataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		amountSpinner.setAdapter(amountDataAdapter);
		amountSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				changed = true;
				SharedPreferences prefs = getSharedPreferences(PREFS_NAME,
						Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = prefs.edit();
				edit.putInt(PREFERENCE_AMOUNT, pos + 1);
				edit.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
		int amount = preferences.getInt(PREFERENCE_AMOUNT,
				PREFERENCE_AMOUNT_DEFAULT);
		amountSpinner.setSelection(amount - 1);

		toggleButton = (ToggleButton) findViewById(R.id.onOffButton);
		toggleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean checked = toggleButton.isChecked();
				SharedPreferences prefs = getSharedPreferences(PREFS_NAME,
						Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = prefs.edit();
				edit.putBoolean(PREFERENCE_ON_OFF, checked);
				edit.commit();

				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
				Intent alarmIntent = new Intent(ConfigActivity.this,
						AlarmReceiver.class);
				alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						ConfigActivity.this, 0, alarmIntent, 0);
				if (checked) { // ON
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
							calendar.getTimeInMillis(), 1000 * 60 * 30,
							pendingIntent);

					int timing = preferences.getInt(PREFERENCE_TIMING,
							PREFERENCE_TIMING_DEFAULT);
					edit.putLong(ConfigActivity.LAST_TIME_RUN,
							System.currentTimeMillis() - (timing + 1) * 1000
									* 60); // set the default for first time run
					edit.commit();
				} else {
					alarmManager.cancel(pendingIntent);
					pendingIntent.cancel();
				}
			}

		});
		boolean onOff = preferences.getBoolean(PREFERENCE_ON_OFF, false);
		toggleButton.setChecked(onOff);
	}

	/**
	 * @see android.app.Activity#onPause()
	 */
	public void onPause() {
		if (changed && toggleButton.isChecked()) {
			// show the current mover if the user changed the config and goes to
			// live TV
			handler.post(new Runnable() {
				public void run() {
					AlarmReceiver.startMover(ConfigActivity.this);
				}
			});
		}
		super.onPause();
	}

	/**
	 * @see android.app.Activity#onResume()
	 */
	public void onResume() {
		super.onResume();
		changed = false;
	}
}
