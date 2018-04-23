/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.inputmethodservice.KeyboardView;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PLATOActivity extends AppCompatActivity {
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    /**
     * The handler for hiding the action bar.
     */
    private final Handler mHideHandler = new Handler();
    /**
     * The second stage handler for showing actionbar.
     */
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
    /**
     * The Network Service
     */
    PLATOService mService;
    /**
     * Activity is bound to the network service.
     */
    boolean mBound = false;

    /**
     * The Soft Keyboard View
     */
    KeyboardView mKeyboardView;
    /**
     * Keyboard Handler
     */
    private PLATOKeyboardHandler mKeyboardHandler;
    /**
     * The connection between the network service and this activity.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PLATOService.PLATONetworkBinder binder = (PLATOService.PLATONetworkBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };
    /**
     * Is view visible?
     */
    private boolean mVisible;

    /**
     * The activity's PLATOView
     */
    private PLATOView mContentView;
    /**
     * Runnable that hides the task bar after a short delay.
     */
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
    /**
     * The hide runnable, used to hide the action bar.
     */
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * The Show Keyboard button.
     */
    private FloatingActionButton mShowKeyboardButton;

    /**
     * Called when activity is started.
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("PLATOActivity", "ONSTART!!!");
        Intent intent = new Intent(this, PLATOService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Called when activity is stopped.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.i("PLATOActivity", "ONSTOP!!!");
        unbindService(mConnection);
        mBound = false;
    }

    /**
     * Called when activity is first created
     * @param savedInstanceState The saved instance state from onPause()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mKeyboardHandler = new PLATOKeyboardHandler(this);

        setContentView(R.layout.activity_platoterm);

        mVisible = true;
        mContentView = (PLATOView) findViewById(R.id.fullscreen_content);
        mKeyboardView = (KeyboardView) findViewById(R.id.keyboard_view);

        mKeyboardView.setKeyboard(mKeyboardHandler.mKeyboard);
        mKeyboardView.setPreviewEnabled(true);
        mKeyboardView.setOnKeyboardActionListener(mKeyboardHandler.keyboardActionListener);
        mShowKeyboardButton = (FloatingActionButton) findViewById(R.id.show_keyboard);

        if (getResources().getConfiguration().keyboard == 2) {
            mShowKeyboardButton.setVisibility(View.GONE);
        }

        mShowKeyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyboard();
            }
        });

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

    }

    /**
     * Called after onCreate to ensure that view bits are initialized before
     * kicking things off.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(UI_ANIMATION_DELAY);
    }

    /**
     * Toggle view hide/show.
     */
    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    /**
     * hide the action bar.
     */
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

    /**
     * Show the action bar.
     */
    @SuppressLint("InlinedApi")
    private void show() {
//        // Show the system bar
//        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
//        mVisible = true;
//
//        // Schedule a runnable to display UI elements after a delay
//        mHideHandler.removeCallbacks(mHidePart2Runnable);
//        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    /**
     * Play notification sound.
     */
    public void beep() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Show the soft keyboard.
     */
    private void showKeyboard() {
        mKeyboardView.setVisibility(View.VISIBLE);
        mShowKeyboardButton.setVisibility(View.GONE);
    }

    /**
     * Hide the soft keyboard.
     */
    void hideKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mShowKeyboardButton.setVisibility(View.VISIBLE);
    }

    /**
     * Activity onKeydown callback, used primarily for physical keyboard and
     * soft keyboard events that map to normal keys.
     * @param keyCode the key code emitted
     * @param event the event that was emitted
     * @return true if key was processed, false if not.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mKeyboardHandler.doPhysicalKeyDown(keyCode, event);
        return super.onKeyDown(keyCode, event);
    }

    private int updateCounter;
    /**
     * Receive update-bitmap messages from service
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateCounter++;
            mContentView.setBitmap(mService.getPlatoTerminal().getBitmap());
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("update-bitmap"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

}
