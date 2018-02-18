/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PLATOTerm extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private PLATOView mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    private PlatoRAM ram;

    public PlatoRAM getRam() {
        return ram;
    }

    public void setRam(PlatoRAM ram) {
        this.ram = ram;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_platoterm);

        mVisible = true;
        mContentView = (PLATOView) findViewById(R.id.fullscreen_content);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mContentView.setDisplayMetrics(metrics);

        // Make view aware of terminal RAM
        ram = new PlatoRAM();
        mContentView.setRam(ram);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        mContentView.setDrawingColorBG(0xFFFF0000);
        mContentView.setDrawingColorFG(0);

        mContentView.drawChar(20, 20, 1, 8, false);
        mContentView.drawChar(20 + 8, 20, 0, 5, false);
        mContentView.drawChar(20 + 16, 20, 0, 12, false);
        mContentView.drawChar(20 + 24, 20, 0, 12, false);
        mContentView.drawChar(20 + 32, 20, 0, 15, false);
        mContentView.drawChar(20 + 40, 20, 0, 45, false);
        mContentView.drawChar(20 + 48, 20, 1, 23, false);
        mContentView.drawChar(20 + 56, 20, 0, 15, false);
        mContentView.drawChar(20 + 64, 20, 0, 18, false);
        mContentView.drawChar(20 + 72, 20, 0, 12, false);
        mContentView.drawChar(20 + 80, 20, 0, 4, false);

        mContentView.setDrawingColorBG(0);
        mContentView.setDrawingColorFG(0xFFFFFF00);

        mContentView.setPoint(256, 256, 0xFFFFFFFF, false);
        mContentView.plotLine(128, 128, 40, 40);


        mContentView.drawChar(20, 20 + 16, 1, 8, false);
        mContentView.drawChar(20 + 8, 20 + 16, 0, 5, false);
        mContentView.drawChar(20 + 16, 20 + 16, 0, 12, false);
        mContentView.drawChar(20 + 24, 20 + 16, 0, 12, false);
        mContentView.drawChar(20 + 32, 20 + 16, 0, 15, false);
        mContentView.drawChar(20 + 40, 20 + 16, 0, 45, false);
        mContentView.drawChar(20 + 48, 20 + 16, 1, 23, false);
        mContentView.drawChar(20 + 56, 20 + 16, 0, 15, false);
        mContentView.drawChar(20 + 64, 20 + 16, 0, 18, false);
        mContentView.drawChar(20 + 72, 20 + 16, 0, 12, false);
        mContentView.drawChar(20 + 80, 20 + 16, 0, 4, false);

        mContentView.setVerticalWritingMode(true);

        mContentView.drawChar(128, 128, 0, 22, false);
        mContentView.drawChar(128 + 8, 128, 0, 5, false);
        mContentView.drawChar(128 + 16, 128, 0, 3, false);


        mContentView.setVerticalWritingMode(true);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

}
