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

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

/**
 * View used in the ConfigActivity to show the configured type of mover
 * 
 */
public class MoverView extends ImageView {
	private Mover mover;

	public MoverView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!isInEditMode()) { // support visual editor
			Activity activity = (Activity) context;
			DisplayMetrics dm = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			mover = new AndroidMover(getContext(), dm.widthPixels,
					dm.heightPixels, ConfigActivity.CONFIG_COUNT, true);
		}
	}

	/**
	 * @see android.widget.ImageView#onDraw(android.graphics.Canvas)
	 */
	public void onDraw(Canvas canvas) {
		if (mover != null) {
			mover.drawFrame(canvas);
		}
	}

	public void setMover(Mover mover) {
		this.mover = mover;
		invalidate();
	}

}