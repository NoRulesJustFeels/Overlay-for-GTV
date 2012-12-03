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

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Implements a surface view which writes updates to the surface's canvas using
 * a separate rendering thread. This class is based heavily on GLSurfaceView.
 * 
 * @see https://code.google.com/p/apps-for-android/
 */
public class CanvasSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {
	private SurfaceHolder holder;
	private CanvasThread canvasThread;
	private Handler handler = new Handler();

	public CanvasSurfaceView(Context context) {
		super(context);
		init();
	}

	public CanvasSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed
		setLayerType(View.LAYER_TYPE_HARDWARE, null);
		holder = getHolder();
		holder.addCallback(this);
		holder.setFormat(PixelFormat.TRANSPARENT | PixelFormat.RGB_565);
	}

	public SurfaceHolder getSurfaceHolder() {
		return holder;
	}

	/** Sets the user's renderer and kicks off the rendering thread. */
	public void setRenderer(Renderer renderer) {
		canvasThread = new CanvasThread(holder, renderer);
		canvasThread.setPriority(Thread.MAX_PRIORITY);
		handler.postDelayed(new Runnable() {
			public void run() {
				canvasThread.start();
			}
		}, 5000); // let the system settle to make the animation smoother
	}

	public void surfaceCreated(SurfaceHolder holder) {
		canvasThread.surfaceCreated();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return
		canvasThread.surfaceDestroyed();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// Surface size or format has changed. This should not happen in this
		// example.
		canvasThread.onWindowResize(w, h);
	}

	/** Inform the view that the activity is paused. */
	public void onPause() {
		canvasThread.onPause();
	}

	/** Inform the view that the activity is resumed. */
	public void onResume() {
		canvasThread.onResume();
	}

	/** Inform the view that the window focus has changed. */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		canvasThread.onWindowFocusChanged(hasFocus);
	}

	/**
	 * Set an "event" to be run on the rendering thread.
	 * 
	 * @param r
	 *            the runnable to be run on the rendering thread.
	 */
	public void setEvent(Runnable r) {
		canvasThread.setEvent(r);
	}

	/** Clears the runnable event, if any, from the rendering thread. */
	public void clearEvent() {
		canvasThread.clearEvent();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		canvasThread.requestExitAndWait();
	}

	protected void stopDrawing() {
		canvasThread.requestExitAndWait();
	}

	// ----------------------------------------------------------------------

	/** A generic renderer interface. */
	public interface Renderer {

		/**
		 * Surface changed size. Called after the surface is created and
		 * whenever the surface size changes. Set your viewport here.
		 * 
		 * @param width
		 * @param height
		 */
		void sizeChanged(int width, int height);

		/**
		 * Draw the current frame.
		 * 
		 * @param canvas
		 *            The target canvas to draw into.
		 */
		void drawFrame(Canvas canvas);

	}

	/**
	 * A generic Canvas rendering Thread. Delegates to a Renderer instance to do
	 * the actual drawing.
	 */
	class CanvasThread extends Thread {
		private boolean done;
		private boolean paused;
		private boolean hasFocus = true;
		private boolean hasSurface;
		private boolean contextLost;
		private Renderer renderer;
		private Runnable event;
		private SurfaceHolder surfaceHolder;

		CanvasThread(SurfaceHolder holder, Renderer renderer) {
			super();
			done = false;
			this.renderer = renderer;
			surfaceHolder = holder;
			setName("CanvasThread");
		}

		@Override
		public void run() {

			/*
			 * This is our main activity thread's loop, we go until asked to
			 * quit.
			 */
			final ProfileRecorder profiler = ProfileRecorder.sSingleton;
			while (!done) {
				profiler.start(ProfileRecorder.PROFILE_FRAME);
				/*
				 * Update the asynchronous state (window size)
				 */
				// If the user has set a runnable to run in this thread,
				// execute it
				if (event != null) {
					try {
						profiler.start(ProfileRecorder.PROFILE_SIM);
						event.run();
						profiler.stop(ProfileRecorder.PROFILE_SIM);
					} catch (Exception e) {
						profiler.stop(ProfileRecorder.PROFILE_SIM);

						// mover has ended, so close activity
						done = true;
						handler.postDelayed(new Runnable() {
							public void run() {
								if (getContext() instanceof Activity) {
									MainActivity activity = (MainActivity) getContext();
									activity.doFinish();
								}
							}
						}, 10);
						break;
					}
				}
				if (done) {
					break;
				}

				// Get ready to draw.
				profiler.start(ProfileRecorder.PROFILE_PAGE_FLIP);
				Canvas canvas = surfaceHolder.lockCanvas();
				profiler.start(ProfileRecorder.PROFILE_PAGE_FLIP);
				if (canvas != null) {
					// Draw a frame!
					profiler.start(ProfileRecorder.PROFILE_DRAW);
					renderer.drawFrame(canvas);
					profiler.stop(ProfileRecorder.PROFILE_DRAW);

					profiler.start(ProfileRecorder.PROFILE_PAGE_FLIP);
					surfaceHolder.unlockCanvasAndPost(canvas);
					profiler.stop(ProfileRecorder.PROFILE_PAGE_FLIP);
				}
				profiler.stop(ProfileRecorder.PROFILE_FRAME);
				profiler.endFrame();
			}
		}

		public void surfaceCreated() {
			synchronized (this) {
				hasSurface = true;
				contextLost = false;
				notify();
			}
		}

		public void surfaceDestroyed() {
			synchronized (this) {
				hasSurface = false;
				notify();
			}
		}

		public void onPause() {
			synchronized (this) {
				paused = true;
			}
		}

		public void onResume() {
			synchronized (this) {
				paused = false;
				notify();
			}
		}

		public void onWindowFocusChanged(boolean hasFocus) {
			synchronized (this) {
				this.hasFocus = hasFocus;
				if (hasFocus == true) {
					notify();
				}
			}
		}

		public void onWindowResize(int w, int h) {
		}

		public void requestExitAndWait() {
			// log performance data
			final ProfileRecorder profiler = ProfileRecorder.sSingleton;
			final long frameTime = profiler
					.getAverageTime(ProfileRecorder.PROFILE_FRAME);
			final long frameMin = profiler
					.getMinTime(ProfileRecorder.PROFILE_FRAME);
			final long frameMax = profiler
					.getMaxTime(ProfileRecorder.PROFILE_FRAME);

			final long drawTime = profiler
					.getAverageTime(ProfileRecorder.PROFILE_DRAW);
			final long drawMin = profiler
					.getMinTime(ProfileRecorder.PROFILE_DRAW);
			final long drawMax = profiler
					.getMaxTime(ProfileRecorder.PROFILE_DRAW);

			final long flipTime = profiler
					.getAverageTime(ProfileRecorder.PROFILE_PAGE_FLIP);
			final long flipMin = profiler
					.getMinTime(ProfileRecorder.PROFILE_PAGE_FLIP);
			final long flipMax = profiler
					.getMaxTime(ProfileRecorder.PROFILE_PAGE_FLIP);

			final long simTime = profiler
					.getAverageTime(ProfileRecorder.PROFILE_SIM);
			final long simMin = profiler
					.getMinTime(ProfileRecorder.PROFILE_SIM);
			final long simMax = profiler
					.getMaxTime(ProfileRecorder.PROFILE_SIM);

			final float fps = frameTime > 0 ? 1000.0f / frameTime : 0.0f;

			String result = "Frame: " + frameTime + "ms (" + fps + " fps)\n"
					+ "\t\tMin: " + frameMin + "ms\t\tMax: " + frameMax + "\n"
					+ "Draw: " + drawTime + "ms\n" + "\t\tMin: " + drawMin
					+ "ms\t\tMax: " + drawMax + "\n" + "Page Flip: " + flipTime
					+ "ms\n" + "\t\tMin: " + flipMin + "ms\t\tMax: " + flipMax
					+ "\n" + "Sim: " + simTime + "ms\n" + "\t\tMin: " + simMin
					+ "ms\t\tMax: " + simMax + "\n";
			Log.d(VIEW_LOG_TAG, result);
			// don't call this from CanvasThread thread or it is a guaranteed
			// deadlock!
			synchronized (this) {
				done = true;
				notify();
			}
			try {
				join();
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}

		/**
		 * Queue an "event" to be run on the rendering thread.
		 * 
		 * @param r
		 *            the runnable to be run on the rendering thread.
		 */
		public void setEvent(Runnable r) {
			synchronized (this) {
				event = r;
			}
		}

		public void clearEvent() {
			synchronized (this) {
				event = null;
			}
		}

	}

}
