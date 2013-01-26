/*
 * Copyright (C) 2013 ENTERTAILION LLC
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
package com.entertailion.android.overlay.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class Utils {
	private static final String LOG_TAG = "Utils";

	public static final void logDeviceInfo(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			Log.i(LOG_TAG, "Version=" + pi.versionName);
			Log.i(LOG_TAG, "IP Address=" + Utils.getLocalIpAddress());
			Log.i(LOG_TAG, "android.os.Build.VERSION.RELEASE=" + android.os.Build.VERSION.RELEASE);
			Log.i(LOG_TAG, "android.os.Build.VERSION.INCREMENTAL=" + android.os.Build.VERSION.INCREMENTAL);
			Log.i(LOG_TAG, "android.os.Build.DEVICE=" + android.os.Build.DEVICE);
			Log.i(LOG_TAG, "android.os.Build.MODEL=" + android.os.Build.MODEL);
			Log.i(LOG_TAG, "android.os.Build.PRODUCT=" + android.os.Build.PRODUCT);
			Log.i(LOG_TAG, "android.os.Build.MANUFACTURER=" + android.os.Build.MANUFACTURER);
			Log.i(LOG_TAG, "android.os.Build.BRAND=" + android.os.Build.BRAND);
		} catch (Exception e) {
			Log.e(LOG_TAG, "logDeviceInfo", e);
		}
	}

	public static final String getLocalIpAddress() {
		InetAddress inetAddress = Utils.getLocalInetAddress();
		if (inetAddress != null) {
			return inetAddress.getHostAddress().toString();
		}
		return null;
	}

	public static final InetAddress getLocalInetAddress() {
		InetAddress selectedInetAddress = null;
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				if (intf.isUp()) {
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							if (inetAddress instanceof Inet4Address) { // only
																		// want
																		// ipv4
																		// address
								if (inetAddress.getHostAddress().toString().charAt(0) != '0') {
									if (selectedInetAddress == null) {
										selectedInetAddress = inetAddress;
									} else if (intf.getName().startsWith("eth")) { // prefer
																					// wired
																					// interface
										selectedInetAddress = inetAddress;
									}
								}
							}
						}
					}
				}
			}
			return selectedInetAddress;
		} catch (Throwable e) {
			Log.e(LOG_TAG, "Failed to get the IP address", e);
		}
		return null;
	}

}
