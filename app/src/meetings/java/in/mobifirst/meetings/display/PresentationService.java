/*
 * Copyright (C) 2015 The Android Open Source Project
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

package in.mobifirst.meetings.display;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.cast.CastPresentation;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;

import java.util.ArrayList;

import in.mobifirst.meetings.R;
import in.mobifirst.meetings.model.Token;
import in.mobifirst.meetings.receiver.TTLocalBroadcastManager;
import in.mobifirst.meetings.tokens.MeetingsAdapter;

/**
 * Service to keep the remote display running even when the app goes into the background
 */
public class PresentationService extends CastRemoteDisplayLocalService implements Runnable {

    private static final String TAG = "PresentationService";

    public static final String SNAP_LIST_INTENT_KEY = "snap_list_intent_key";
    private Handler mHandler = null;
    private RecyclerView mRecyclerView;
    private MeetingsAdapter mMeetingsAdapter;
    private boolean flipMe = false;
    private LinearLayoutManager mLinearLayoutManager;
    private TextView mNoTokensTextView;

    private BroadcastReceiver mSnapBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Token> tokens = intent.getParcelableArrayListExtra(SNAP_LIST_INTENT_KEY);
            if (tokens != null) {
                if (mMeetingsAdapter != null) {
                    mMeetingsAdapter.replaceData(tokens);
                }
            }
            if (tokens.size() > 0) {
                if (mNoTokensTextView != null) {
                    mNoTokensTextView.setVisibility(View.GONE);
                }
            } else {
                if (mNoTokensTextView != null) {
                    mNoTokensTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    // Second screen
    private CastPresentation mPresentation;
    private MediaPlayer mMediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        // Audio
        mMediaPlayer = MediaPlayer.create(this, R.raw.sound);
        mMediaPlayer.setVolume((float) 0.1, (float) 0.1);
        mMediaPlayer.setLooping(true);

        mHandler = new Handler(Looper.getMainLooper());
        TTLocalBroadcastManager.registerReceiver(this, mSnapBroadcastReceiver, TTLocalBroadcastManager.TOKEN_CHANGE_INTENT_ACTION);
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(this);
        TTLocalBroadcastManager.unRegisterReceiver(this, mSnapBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onCreatePresentation(Display display) {
        createPresentation(display);
    }

    @Override
    public void onDismissPresentation() {
        dismissPresentation();
    }

    private void dismissPresentation() {
        if (mPresentation != null) {
            mMediaPlayer.stop();
            mPresentation.dismiss();
            mPresentation = null;
        }
    }

    private void createPresentation(Display display) {
        dismissPresentation();
        mPresentation = new SeconScreenPresentation(this, display, R.style.AppTheme);

        try {
            mPresentation.show();
            mMediaPlayer.start();
        } catch (WindowManager.InvalidDisplayException ex) {
            Log.e(TAG, "Unable to show presentation, display was removed.", ex);
            dismissPresentation();
        }
    }

    private class SeconScreenPresentation extends CastPresentation {

        public SeconScreenPresentation(Context context, Display display, int appTheme) {
            super(context, display, appTheme);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.extended_display);
            mNoTokensTextView = (TextView) findViewById(R.id.noTokensTextView);
            mNoTokensTextView.setVisibility(View.GONE);
            mRecyclerView = (RecyclerView) findViewById(R.id.display_recyclerview);
            mLinearLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLinearLayoutManager);

            mMeetingsAdapter = new MeetingsAdapter(getContext());
            mRecyclerView.setAdapter(mMeetingsAdapter);

            run();
        }
    }

    @Override
    public void run() {
        int itemCount = mMeetingsAdapter.getItemCount();
        if (itemCount > 0) {
            mNoTokensTextView.setVisibility(View.GONE);
            int firstVisiblePosition =
                    mLinearLayoutManager.findFirstVisibleItemPosition();
            int lastVisiblePosition =
                    mLinearLayoutManager.findLastVisibleItemPosition();
            int window = lastVisiblePosition - firstVisiblePosition;
            int scrollBy = lastVisiblePosition + window / 2;
            Log.e("TokenDisplayService", "scrollBy = " + scrollBy);
            if (scrollBy > 0 && scrollBy < itemCount) {
                mRecyclerView.scrollToPosition(scrollBy);
            } else {
                if (!flipMe) {
                    //Scroll to end to show the non-window multiples
                    mRecyclerView.scrollToPosition(itemCount - 1);
                    flipMe = !flipMe;
                } else {
                    //Scroll to start
                    mRecyclerView.scrollToPosition(0);
                    flipMe = !flipMe;
                }
            }
        } else {
            mNoTokensTextView.setVisibility(View.VISIBLE);
        }
        mHandler.postDelayed(this, 5000);
    }

    /**
     * The presentation to show on the first screen (the TV).
     * <p>
     * Note that this display may have different metrics from the display on
     * which the main activity is showing so we must be careful to use the
     * presentation's own {@link Context} whenever we load resources.
     * </p>
     */
//    private class FirstScreenPresentation extends CastPresentation {
//
//        private final String TAG = "FirstScreenPresentation";
//
//        public FirstScreenPresentation(Context context, Display display) {
//            super(context, display);
//        }
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//
//            setContentView(R.layout.first_screen_layout);
//
////            TextView titleTextView = (TextView) findViewById(R.id.title);
////            // Use TrueType font to get best looking text on remote display
////            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
////            titleTextView.setTypeface(typeface);
//
//            GLSurfaceView firstScreenSurfaceView = (GLSurfaceView) findViewById(R.id.surface_view);
//            // Create an OpenGL ES 2.0 context.
//            firstScreenSurfaceView.setEGLContextClientVersion(2);
//            // Allow UI elements above this surface; used for text overlay
//            firstScreenSurfaceView.setZOrderMediaOverlay(true);
//            // Enable anti-aliasing
//            firstScreenSurfaceView.setEGLConfigChooser(new CustomConfigChooser());
//            mCubeRenderer = new CubeRenderer();
//            firstScreenSurfaceView.setRenderer(mCubeRenderer);
//        }
//
//        /**
//         * OpenGL config to enable custom anti-aliasing
//         */
//        private final class CustomConfigChooser implements GLSurfaceView.EGLConfigChooser {
//
//            private int[] mValue = new int[1];
//            protected int mRedSize = 8;
//            protected int mGreenSize = 8;
//            protected int mBlueSize = 8;
//            protected int mAlphaSize = 8;
//            protected int mDepthSize = 16;
//            protected int mStencilSize = 0;
//
//            @Override
//            public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
//                int[] configSpec = {
//                        EGL10.EGL_RED_SIZE, mRedSize,
//                        EGL10.EGL_GREEN_SIZE, mGreenSize,
//                        EGL10.EGL_BLUE_SIZE, mBlueSize,
//                        EGL10.EGL_ALPHA_SIZE, mAlphaSize,
//                        EGL10.EGL_DEPTH_SIZE, mDepthSize,
//                        EGL10.EGL_STENCIL_SIZE, mStencilSize,
//                        EGL10.EGL_RENDERABLE_TYPE, 4,
//                        EGL10.EGL_SAMPLE_BUFFERS, 1,
//                        EGL10.EGL_SAMPLES, 4,
//                        EGL10.EGL_NONE
//                };
//                int[] num_config = new int[1];
//                if (!egl.eglChooseConfig(display, configSpec, null, 0, num_config)) {
//                    throw new IllegalArgumentException("eglChooseConfig1 failed");
//                }
//
//                int numConfigs = num_config[0];
//
//                if (numConfigs <= 0) {
//                    // Don't do anti-aliasing
//                    configSpec = new int[]{
//                            EGL10.EGL_RED_SIZE, mRedSize,
//                            EGL10.EGL_GREEN_SIZE, mGreenSize,
//                            EGL10.EGL_BLUE_SIZE, mBlueSize,
//                            EGL10.EGL_ALPHA_SIZE, mAlphaSize,
//                            EGL10.EGL_DEPTH_SIZE, mDepthSize,
//                            EGL10.EGL_STENCIL_SIZE, mStencilSize,
//                            EGL10.EGL_RENDERABLE_TYPE, 4,
//                            EGL10.EGL_NONE
//                    };
//
//                    if (!egl.eglChooseConfig(display, configSpec, null, 0, num_config)) {
//                        throw new IllegalArgumentException("eglChooseConfig2 failed");
//                    }
//                    numConfigs = num_config[0];
//
//                    if (numConfigs <= 0) {
//                        throw new IllegalArgumentException("No configs match configSpec");
//                    }
//                }
//
//                EGLConfig[] configs = new EGLConfig[numConfigs];
//                if (!egl.eglChooseConfig(display, configSpec, configs, numConfigs, num_config)) {
//                    throw new IllegalArgumentException("eglChooseConfig3 failed");
//                }
//                EGLConfig config = findConfig(egl, display, configs);
//                if (config == null) {
//                    throw new IllegalArgumentException("No config chosen");
//                }
//                return config;
//            }
//
//            private EGLConfig findConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
//                for (EGLConfig config : configs) {
//                    int d = findConfigAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE, 0);
//                    int s = findConfigAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE, 0);
//                    if ((d >= mDepthSize) && (s >= mStencilSize)) {
//                        int r = findConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE, 0);
//                        int g = findConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE, 0);
//                        int b = findConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE, 0);
//                        int a = findConfigAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE, 0);
//                        if ((r == mRedSize) && (g == mGreenSize) && (b == mBlueSize) && (a
//                                == mAlphaSize)) {
//                            return config;
//                        }
//                    }
//                }
//                return null;
//            }
//
//            private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config,
//                                         int attribute,
//                                         int defaultValue) {
//                if (egl.eglGetConfigAttrib(display, config, attribute, mValue)) {
//                    return mValue[0];
//                }
//                return defaultValue;
//            }
//        }
//    }

}
