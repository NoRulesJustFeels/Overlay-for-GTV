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

/**
 * Base class defining the core set of information necessary to render (and move
 * an object on the screen. 
 */
public abstract class Renderable {
	// Position.
	public float x;
	public float y;
	public float z;

	// Velocity.
	public float velocityX;
	public float velocityY;
	public float velocityZ;

	// Size.
	public float width;
	public float height;

	// alpha
	public int alpha = 255;
	
	// counter
	public int count;
}
