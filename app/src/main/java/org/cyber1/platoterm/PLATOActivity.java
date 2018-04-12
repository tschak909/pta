/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
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
     * Is the keyboard currently shifted?
     */
    private boolean keyboardIsShifted = false;
    /**
     * Is sticky shift enabled? (caps lock)
     */
    private boolean stickyShift = false;
    /**
     * The previous keycode pressed.
     */
    private int lastKeycode = 0;
    /**
     * Is this view visible?
     */
    private boolean mVisible;
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
     * The Soft Keyboard View
     */
    private KeyboardView mKeyboardView;
    /**
     * The default alphanumeric keyboard.
     */
    private Keyboard mKeyboard;
    /**
     * Alphanumeric keyboard with shifted keys.
     */
    private Keyboard mKeyboardShifted;
    /**
     * Keyboard with Symbols
     */
    private Keyboard mKeyboardSym;
    /**
     * The current keyboard state, see above.
     */
    private currentKeyboard currentKeyboardState;

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
     * The Keyboard Action listener for the keyboardview
     */
    public KeyboardView.OnKeyboardActionListener keyboardActionListener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            long eventTime = System.currentTimeMillis();
            KeyEvent event = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);

            if (primaryCode == -3) { // HIDE keyboard
                hideKeyboard();
            } else if (primaryCode == -6) { // SYM key

                switch (currentKeyboardState) {
                    case ALPHA:
                        mKeyboardView.setKeyboard(mKeyboardSym);
                        currentKeyboardState = currentKeyboard.SYMBOLS;
                        break;
                    case SYMBOLS:
                        mKeyboardView.setKeyboard(mKeyboard);
                        currentKeyboardState = currentKeyboard.ALPHA;
                        break;
                }

            } else if (primaryCode == -5) {
                if (!stickyShift && lastKeycode == -5) {
                    // Go sticky
                    Log.d(this.getClass().getName(), "STICKY!!!");
                    stickyShift = true;
                    keyboardIsShifted = true;
                    mKeyboardView.setShifted(true);
                    mKeyboard.setShifted(true);
                    mKeyboardSym.setShifted(true);
                    mKeyboardShifted.setShifted(true);
                    mKeyboardView.invalidateAllKeys();
                    lastKeycode = 0;
                } else if (stickyShift) {
                    Log.d(this.getClass().getName(), "Turn off sticky.");
                    stickyShift = false;
                    keyboardIsShifted = false;
                    mKeyboardView.setShifted(false);
                    mKeyboardView.setKeyboard(mKeyboard);
                    mKeyboardView.invalidateAllKeys();
                    lastKeycode = 0;
                }
                switch (currentKeyboardState) {
                    case ALPHA:
                        mKeyboardView.setVisibility(View.GONE);
                        mKeyboardView.setKeyboard(mKeyboardShifted);
                        mKeyboardView.setShifted(true);
                        mKeyboardView.setVisibility(View.VISIBLE);
                        currentKeyboardState = currentKeyboard.ALPHA_SHIFTED;
                        keyboardIsShifted = true;
                        break;
                    case ALPHA_SHIFTED:
                        if (!stickyShift) {
                            mKeyboardView.setKeyboard(mKeyboard);
                            currentKeyboardState = currentKeyboard.ALPHA;
                            keyboardIsShifted = false;
                        }
                        break;
                    case SYMBOLS:
                        keyboardIsShifted = !keyboardIsShifted;
                        break;
                }
            } else
                doSoftKeyDown(primaryCode);

            if (primaryCode > 0)
                dispatchKeyEvent(event);

            lastKeycode = primaryCode;
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeDown() {
            hideKeyboard();
        }

        @Override
        public void swipeUp() {
        }
    };

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
        mService.disconnectFromPLATO();
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

        setContentView(R.layout.activity_platoterm);

        mVisible = true;
        mContentView = (PLATOView) findViewById(R.id.fullscreen_content);
        mKeyboardView = (KeyboardView) findViewById(R.id.keyboard_view);
        mKeyboard = new Keyboard(getApplicationContext(), R.xml.keyboard, R.integer.keyboard_normal);
        mKeyboardShifted = new Keyboard(getApplicationContext(), R.xml.keyboard, R.integer.keyboard_shifted);
        mKeyboardView.setKeyboard(mKeyboard);
        currentKeyboardState = currentKeyboard.ALPHA;
        mKeyboardView.setPreviewEnabled(true);
        mKeyboardView.setOnKeyboardActionListener(keyboardActionListener);
        mShowKeyboardButton = (FloatingActionButton) findViewById(R.id.show_keyboard);
        mKeyboardSym = new Keyboard(getApplicationContext(), R.xml.keyboard_sym, 0);

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
    private void hideKeyboard() {
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
        Log.d(this.getClass().getName(), "Keydown - 0x" + String.format("%02X", keyCode));
        // Process SHIFT keys, they're the same for PLATO.

        if (getResources().getConfiguration().keyboard == 2) {
            // Physical keyboard
            doSpecialPhysicalKeys(keyCode,event);
            if (event.isCtrlPressed() && event.isShiftPressed()) {
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(PLATOKey.getShiftedPLATOcodeForCTRLKeycode(keyCode));
            } else if (event.isCtrlPressed()) {
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(PLATOKey.getPLATOcodeForCTRLKeycode(keyCode));
            } else if (event.isShiftPressed()) {
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(PLATOKey.getShiftedPLATOcodeForKeycode(keyCode));
            } else {
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(PLATOKey.getPLATOcodeForKeycode(keyCode));
            }
        } else {
            // Soft keyboard.
            if (keyboardIsShifted && !stickyShift) {
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(PLATOKey.getShiftedPLATOcodeForKeycode(keyCode));
            } else {
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(PLATOKey.getPLATOcodeForKeycode(keyCode));
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Translate physical keys into PLATO keypresses.
     *
     * @param keyCode Hardware keycode
     * @param event   event data (shift/ctrl/etc pressed?)
     */
    private void doSpecialPhysicalKeys(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 9: // @
                if (event.isShiftPressed()) {
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x05);
                }
                break;
            case 10: // #
                if (event.isShiftPressed()) {
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x24);
                }
                break;
            case 13: // ^
                if (event.isShiftPressed()) {
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x40);
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0A);
                }
                break;
            case 14: // &
                if (event.isShiftPressed()) {
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0E);
                }
                break;
            case 71: // {
                if (event.isShiftPressed()) {
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x22);
                }
                break;
            case 72: // }
                if (event.isShiftPressed()) {
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x23);
                }
                break;
            case 73: // \
                if (event.isShiftPressed()) {
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x5D);
                }
                break;
            case 74: // |
                if (event.isShiftPressed()) {
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x69);
                }
                break;
            case 68: // `
                if (event.isShiftPressed()) {
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x40);
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x4E);
                } else {
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x40);
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x51);
                }
                break;
        }
    }


    /**
     * Translate special PLATO softkey presses and send out appropriate keys.
     * events.
     *
     * @param keyCode the intercepted keycode, less than 0.
     */
    private void doSoftKeyDown(int keyCode) {
        switch (keyCode) {
            case PLATOKey.SOFTKEY_ANS:   // ANS
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x12);
            case PLATOKey.SOFTKEY_BACK:   // BACK
                if (keyboardIsShifted)
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x38);
                else
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x18);
                break;
            case PLATOKey.SOFTKEY_COPY:   // COPY
                if (keyboardIsShifted)
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x1B);
                else
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3B);
                break;
            case PLATOKey.SOFTKEY_DATA:   // DATA
                if (keyboardIsShifted)
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x39);
                else
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x19);
                break;
            case PLATOKey.SOFTKEY_EDIT:   // EDIT
                if (keyboardIsShifted)
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x37);
                else
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x17);
                break;
            case PLATOKey.SOFTKEY_FONT:   // FONT
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x34);
                break;
            case PLATOKey.SOFTKEY_HELP:   // HELP
                if (keyboardIsShifted)
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x35);
                else
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x15);
                break;
            case PLATOKey.SOFTKEY_LAB:   // LAB
                if (keyboardIsShifted)
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3D);
                else
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x1D);
                break;
            case PLATOKey.SOFTKEY_MICRO:   // MICRO
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x14);
                break;
            case -20:   // SQUARE
                if (keyboardIsShifted)
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                else
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x1C);
                break;
            case PLATOKey.SOFTKEY_TERM:   // TERM
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x32);
                break;
            case PLATOKey.SOFTKEY_SUPER:   // SUPER
                if (keyboardIsShifted)
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x30);
                else
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x10);
                break;
            case PLATOKey.SOFTKEY_SUB:   // SUB
                if (keyboardIsShifted)
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x31);
                else
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x11);
                break;
            case PLATOKey.SOFTKEY_STOP:   // STOP
                if (keyboardIsShifted) {
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3A);
                } else
                    mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x1A);
                break;
            case PLATOKey.SOFTKEY_LT:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x20);
                break;
            case PLATOKey.SOFTKEY_GT:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x21);
                break;
            case PLATOKey.SOFTKEY_LEFT_BRACKET:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x22);
                break;
            case PLATOKey.SOFTKEY_RIGHT_BRACKET:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x23);
                break;
            case PLATOKey.SOFTKEY_DOLLAR:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x24);
                break;
            case PLATOKey.SOFTKEY_PERCENT:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x25);
                break;
            case PLATOKey.SOFTKEY_UNDERLINE:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x26);
                break;
            case PLATOKey.SOFTKEY_APOSTROPHE:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x27);
                break;
            case PLATOKey.SOFTKEY_ASTERISK:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x28);
                break;
            case PLATOKey.SOFTKEY_LEFT_PAREN:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x29);
                break;
            case PLATOKey.SOFTKEY_RIGHT_PAREN:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x7B);
                break;
            case PLATOKey.SOFTKEY_AT:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x05);
                break;
            case PLATOKey.SOFTKEY_POUND:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x24);
                break;
            case PLATOKey.SOFTKEY_CARET:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x40);
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0A);
                break;
            case PLATOKey.SOFTKEY_AMPERSAND:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0E);
                break;
            case PLATOKey.SOFTKEY_LEFT_BRACE:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x22);
                break;
            case PLATOKey.SOFTKEY_RIGHT_BRACE:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x23);
                break;
            case PLATOKey.SOFTKEY_BACKSLASH:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x5D);
                break;
            case PLATOKey.SOFTKEY_VERTICAL_BAR:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x69);
                break;
            case PLATOKey.SOFTKEY_GRAVE:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x40);
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x51);
                break;
            case PLATOKey.SOFTKEY_TILDE:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x40);
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x4E);
                break;
            case PLATOKey.SOFTKEY_DIVIDE:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0B);
                break;
            case PLATOKey.SOFTKEY_MINUS:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0F);
                break;
            case PLATOKey.SOFTKEY_PLUS:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0E);
                break;
            case PLATOKey.SOFTKEY_MULTIPLY:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0A);
                break;
            case PLATOKey.SOFTKEY_TAB:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0C);
                break;
            case PLATOKey.SOFTKEY_ASSIGN:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0d);
                break;
            case PLATOKey.SOFTKEY_SIGMA:
                mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x2E);
        }

        if (currentKeyboardState == currentKeyboard.SYMBOLS) {
            mKeyboardSym.setShifted(false);
            mKeyboard.setShifted(false);
            mKeyboardView.setShifted(false);
            mKeyboardView.setKeyboard(mKeyboard);
            keyboardIsShifted = false;
            currentKeyboardState = currentKeyboard.ALPHA;
        }

    }

    /**
     * The current keyboard active:
     * ALPHA: alphanumweric keyboard
     * ALPHA_SHIFTED alphanumeric keyboard + SHIFT
     * PLATOKEYS Show the PLATO keys
     */
    private enum currentKeyboard {
        ALPHA, ALPHA_SHIFTED, SYMBOLS
    }
}
