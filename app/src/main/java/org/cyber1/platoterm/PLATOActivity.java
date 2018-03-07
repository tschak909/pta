/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
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
     * Set to trigger view test pattern instead of normal operation.
     */
    private static final boolean VIEW_TEST = false;
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    /**
     * PLATO Coordinate representing the leftmost screen coordinate.
     */
    private static final int SCREEN_LEFT = 0;

    /**
     * PLATO Coordinate representing the topmost screen coordinate.
     */
    private static final int SCREEN_TOP = 511;

    /**
     * This is a handler for pulling the latest data from the network service
     */
    private final Handler networkHandler = new Handler();
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
    private final Handler keytestHandler = new Handler();
    PLATONetworkService mService;
    boolean mBound = false;
    PLATOProtocol protocol;
    private final Runnable keytestRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("PLATOProtocol", "Smashing NEXT key.");
            protocol.sendProcessedKey(0x16);
        }
    };

    private KeyboardView mKeyboardView;
    private Keyboard mKeyboard;
    private Keyboard mKeyboardShifted;
    /**
     * The current keyboard state, see above.
     */
    private currentKeyboard currentKeyboardState;
    /**
     * Current PLATO Font instance
     */
    private PLATOFont platoFont;
    /**
     * Current X coordinate
     */
    private int currentX;
    /**
     * Current Y coordinate
     */
    private int currentY;
    /**
     * Render text vertically/horizontally
     */
    private boolean verticalWritingMode;
    /**
     * Render text in reverse/forward
     */
    private boolean reverseWritingMode;
    /**
     * Render text in bold/normal size
     */
    private boolean boldWritingMode;
    /**
     * Current X margin
     */
    private int margin;
    /**
     * Current Character set
     */
    private int currentCharacterSet;
    /**
     * Delta amount for X, typically to specify text cell size, doubled if bold is set
     */
    private int deltaX;
    /**
     * Delta amount for Y, typically to specify text cell size, doubled if bold is set
     */
    private int deltaY;
    /**
     * Font height (for custom font support), normally, 16 for standard PLATO fonts)
     */
    private int fontheight = 16;
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
     * Is the keyboard currently shifted?
     */
    private boolean keyboardIsShifted = false;
    private int[] termAreaBitmap;
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private PLATORam ram;
    /**
     * The runner for handling network data.
     */
    private final Runnable networkRunnable = new Runnable() {
        @Override
        public void run() {
            if (mService != null && mService.isRunning() && !mService.getFromFIFO().isEmpty()) {
                for (int i = 0; i < mService.getFromFIFO().size(); i++) {
                    processData(mService.getFromFIFO().poll());
                }
            }
            networkHandler.post(networkRunnable);
        }
    };
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PLATONetworkService.PLATONetworkBinder binder = (PLATONetworkService.PLATONetworkBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };
    private int fontSize;
    private int fontFlags;
    private FloatingActionButton mShowKeyboardButton;
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

            dispatchKeyEvent(event);

            if (primaryCode == -3) {
                hideKeyboard();
            }
            if (primaryCode == -5) {
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
                        mKeyboardView.setVisibility(View.VISIBLE);
                        mKeyboardView.setKeyboard(mKeyboard);
                        mKeyboardView.invalidateAllKeys();
                        mKeyboardView.setShifted(false);
                        currentKeyboardState = currentKeyboard.ALPHA;
                        keyboardIsShifted = false;
                        break;
                }
            }
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

    public PLATONetworkService getNetworkService() {
        return mService;
    }

    public int getCurrentCharacterSet() {
        return currentCharacterSet;
    }

    public void setCurrentCharacterSet(int currentCharacterSet) {
        this.currentCharacterSet = currentCharacterSet;
    }

    public int getCurrentX() {
        return currentX;
    }

    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    public PLATORam getRam() {
        return ram;
    }

    public void setRam(PLATORam ram) {
        this.ram = ram;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    private void processData(Byte aByte) {
        if (protocol == null) {
            protocol = new PLATOProtocol(this, this.ram, this.platoFont);
        } else {
            protocol.decodeByte(aByte);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("PLATOActivity", "ONSTART!!!");
        if (!VIEW_TEST) {
            Intent intent = new Intent(this, PLATONetworkService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("PLATOActivity", "ONSTOP!!!");
        mService.disconnectFromPLATO();
        unbindService(mConnection);
        mBound = false;
    }

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

/*        if (getResources().getConfiguration().keyboard == 2) {
            mShowKeyboardButton.setVisibility(View.GONE);
        }*/

        mShowKeyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyboard();
            }
        });

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mContentView.setDisplayMetrics(metrics);

        // Make view aware of terminal RAM
        setRam(new PLATORam());
        setFont(new PLATOFont());
        mContentView.setRam(ram);
        mContentView.setFont(getPlatoFont());
        getRam().setMode(0x0F); // char mode, rewrite
        setCurrentBG("#000000");
        setCurrentFG("#FFFFFF");
        mContentView.setModeXOR(false);
        mContentView.setBoldWritingMode(false);

        setDeltaX(8);
        setDeltaY(16);

//        // Set up the user interaction to manually show or hide the system UI.
//        mContentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggle();
//            }
//        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //
        // Throw up test pattern, if needed
        //
        if (VIEW_TEST) {
            mContentView.testPattern();
        } else {
            networkHandler.post(networkRunnable);
            keytestHandler.postDelayed(keytestRunnable, 6000);
        }

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(UI_ANIMATION_DELAY);
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
     * Scroll the terminal up one line.
     */
    public void scrollUp() {
        mContentView.scrollUp();
    }

    /**
     * Plot a character to the view
     *
     * @param charsetToUse Character set to use, 0, 1, 2, or 3
     * @param charToPlot   character to plot in character set
     */
    public void drawChar(int charsetToUse, int charToPlot) {
        mContentView.drawChar(getCurrentX(), getCurrentY(), charsetToUse, charToPlot, false);
        setCurrentX(getCurrentX() + getDeltaX());
        // todo handle vertical.
    }

    /**
     * Erase the entire PLATO view
     */
    public void eraseScreen() {
        mContentView.erase();
    }

    public void setXORMode(boolean b) {
        mContentView.setModeXOR(b);
    }

    public boolean isVerticalWritingMode() {
        return verticalWritingMode;
    }

    public void setVerticalWritingMode(boolean verticalWritingMode) {
        this.verticalWritingMode = verticalWritingMode;
    }

    public boolean isReverseWritingMode() {
        return reverseWritingMode;
    }

    public void setReverseWritingMode(boolean reverseWritingMode) {
        this.reverseWritingMode = reverseWritingMode;
    }

    public boolean isBoldWritingMode() {
        return boldWritingMode;
    }

    public void setBoldWritingMode(boolean boldWritingMode) {
        this.boldWritingMode = boldWritingMode;
        mContentView.setBoldWritingMode(boldWritingMode);
        if (boldWritingMode) {
            setDeltaX(getDeltaX() * 2);
            setDeltaY(getDeltaY() * 2);
        } else {
            setDeltaX(8);
            setDeltaY(16);
        }
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    /**
     * Back up cursor one character cell.
     */
    public void backspace() {
        setCurrentX(getCurrentX() - getDeltaX());
    }

    public int getDeltaX() {
        return deltaX;
    }

    public void setDeltaX(int deltaX) {
        this.deltaX = deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public void setDeltaY(int deltaY) {
        this.deltaY = deltaY;
    }

    public int getFontHeight() {
        return fontheight;
    }

    public void setFontHeight(int fontheight) {
        this.fontheight = fontheight;
    }

    /**
     * Move cursor forward one space.
     */
    public void forwardspace() {
        setCurrentX(getCurrentX() + getDeltaX());
    }

    /**
     * Move cursor down one space.
     */
    public void linefeed() {
        setCurrentY(getCurrentY() - getDeltaY());
    }

    /**
     * Move cursor up one space.
     */
    public void verticalTab() {
        setCurrentY(getCurrentY() + getDeltaY());
    }

    /**
     * Form feed - move cursor to the top of page (0,511)
     */
    public void formfeed() {
        setCurrentX(SCREEN_LEFT);
        setCurrentY(SCREEN_TOP);
    }

    /**
     * Carriage Return - move cursor to beginning of line + current margin.
     */
    public void carriageReturn() {
        setCurrentX(SCREEN_LEFT + getMargin());
        setCurrentY(getCurrentY() - getDeltaY() & 0777);
    }

    /**
     * End of Medium - Select block write/erase mode (mode 4)
     */
    public void endOfMedium() {
    }

    /**
     * Paint view at current cursor
     *
     * @param n value from assemblePaint()
     */
    public void paint(int n) {
        // TODO: Implement Paint
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
     * CWS extension: Save term area bitmap for restore later.
     */
    public void cwsTermSave() {
        Log.d(this.getClass().getName(), "CWS Save Term Area");
        setTermAreaBitmap(mContentView.getPixels(0, 463, 511, 511));
    }

    public int[] getTermAreaBitmap() {
        return termAreaBitmap;
    }

    public void setTermAreaBitmap(int[] termAreaBitmap) {
        this.termAreaBitmap = termAreaBitmap;
    }

    public void cwsTermRestore() {
        if (getTermAreaBitmap() == null)
            return;
        mContentView.setPixels(getTermAreaBitmap(), 0, 463, 511, 511);
    }

    /**
     * Set font face and family based on assembled ext data
     *
     * @param n
     */
    public void setFontFaceAndFamily(int n) {
        switch (n) {
            case 2:
                Log.d(this.getClass().getName(), "Font face set to Terminal");
                break;
            case 3:
                Log.d(this.getClass().getName(), "Font face set to UOL8X14");
                break;
            case 4:
                Log.d(this.getClass().getName(), "Font face set to UOL8X16");
                break;
            case 5:
                Log.d(this.getClass().getName(), "Font face set to Courier");
                break;
            case 6:
                Log.d(this.getClass().getName(), "Font face set to Courier New");
                break;
            case 16:
                Log.d(this.getClass().getName(), "Font face set to SSFONT");
                break;
            case 17:
                Log.d(this.getClass().getName(), "Font face set to Times New Roman");
                break;
            case 18:
                Log.d(this.getClass().getName(), "Font face set to Script");
                break;
            case 19:
                Log.d(this.getClass().getName(), "Font face set to MS Sans Serif");
                break;
            default:
                Log.d(this.getClass().getName(), "Font face set to default.");
        }
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int n) {
        this.fontSize = (n < 1 ? 1 : (n > 63 ? 63 : n));
    }

    public int getFontFlags() {
        return fontFlags;
    }

    public void setFontFlags(int n) {
        this.fontFlags = n; // temporary.
    }

    /**
     * Activate/Deactivate touch panel.
     *
     * @param b true = activate, false = deactivate
     */
    public void activateTouchPanel(boolean b) {
        mContentView.setTouchPanel(b);
    }

    public int getCurrentFG() {
        return mContentView.getDrawingColorFG();
    }

    public void setCurrentFG(String newFG) {
        mContentView.setDrawingColorFG(Color.parseColor(newFG));
    }

    public int getCurrentBG() {
        return mContentView.getDrawingColorBG();
    }

    public void setCurrentBG(String newBG) {
        this.mContentView.setDrawingColorBG(Color.parseColor(newBG));
    }

    /**
     * Draw point onto canvas in current FG color.
     *
     * @param x X coordinate (0-511)
     * @param y Y coordinate (0-511)
     */
    public void drawPoint(int x, int y) {
        mContentView.setPoint(x, y, getCurrentFG(), mContentView.isModeXOR());
    }

    /**
     * Draw line onto canvas in current FG color
     *
     * @param currentX current X coordinate (0-511)
     * @param currentY current Y coordinate (0-511)
     * @param x        destination X coordinate (0-511)
     * @param y        destination Y coordinate (0-511)
     */
    public void plotLine(int currentX, int currentY, int x, int y) {
        mContentView.plotLine(currentX, currentY, x, y);
    }

    /**
     * Refresh the content view. Called from protocol.
     */
    public void refreshView() {
        mContentView.invalidate();
    }

    public PLATOFont getPlatoFont() {
        return platoFont;
    }

    public void setFont(PLATOFont font) {
        this.platoFont = font;
    }

    /**
     * Erase a block of the screen
     *
     * @param x1 Beginning X coordinate (0-511)
     * @param y1 Beginning Y coordinate (0-511)
     * @param x2 Ending X coordinate (0-511)
     * @param y2 Ending Y coordinate (0-511)
     */
    public void eraseBlock(int x1, int y1, int x2, int y2) {
        mContentView.erase(x1, y1, x2, y2);
    }

    private void showKeyboard() {
        mKeyboardView.setVisibility(View.VISIBLE);
        mShowKeyboardButton.setVisibility(View.GONE);
    }

    private void hideKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mShowKeyboardButton.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.d(this.getClass().getName(), "Keydown - 0x" + String.format("%02X", keyCode));
        // Process SHIFT keys, they're the same for PLATO.

        if (getResources().getConfiguration().keyboard == 2) {
            // Physical keyboard
            if (event.isCtrlPressed() && event.isShiftPressed()) {
                protocol.sendProcessedKey(PLATOKey.getShiftedPLATOcodeForCTRLKeycode(keyCode));
            } else if (event.isCtrlPressed()) {
                protocol.sendProcessedKey(PLATOKey.getPLATOcodeForCTRLKeycode(keyCode));
            } else if (event.isShiftPressed()) {
                protocol.sendProcessedKey(PLATOKey.getShiftedPLATOcodeForKeycode(keyCode));
            } else {
                protocol.sendProcessedKey(PLATOKey.getPLATOcodeForKeycode(keyCode));
            }
        } else {
            if ((keyCode == 59) || (keyCode == 60)) {
                Log.d(this.getClass().getName(), "Keyboard shift toggle: " + keyboardIsShifted);
                keyboardIsShifted = !keyboardIsShifted;
            }
            if (keyCode < 0)
                translateSoftkeyDown(keyCode);

            if (keyboardIsShifted) {
                protocol.sendProcessedKey(PLATOKey.getShiftedPLATOcodeForKeycode(keyCode));
            } else {
                protocol.sendProcessedKey(PLATOKey.getPLATOcodeForKeycode(keyCode));
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Translate special PLATO softkey presses so they can be interpreted by normal
     * events.
     *
     * @param keyCode the intercepted keycode, less than 0.
     */
    private void translateSoftkeyDown(int keyCode) {
        switch (keyCode) {
            case -10:   // ANS
                protocol.sendProcessedKey(0x12);
            case -11:   // BACK
                if (keyboardIsShifted)
                    protocol.sendProcessedKey(0x38);
                else
                    protocol.sendProcessedKey(0x18);
                break;
            case -12:   // COPY
                if (keyboardIsShifted)
                    protocol.sendProcessedKey(0x1B);
                else
                    protocol.sendProcessedKey(0x3B);
                break;
            case -13:   // DATA
                if (keyboardIsShifted)
                    protocol.sendProcessedKey(0x39);
                else
                    protocol.sendProcessedKey(0x19);
                break;
            case -14:   // EDIT
                if (keyboardIsShifted)
                    protocol.sendProcessedKey(0x37);
                else
                    protocol.sendProcessedKey(0x17);
                break;

            case -15:   // FONT
                protocol.sendProcessedKey(0x34);
                break;
            case -16:   // HELP
                if (keyboardIsShifted)
                    protocol.sendProcessedKey(0x35);
                else
                    protocol.sendProcessedKey(0x15);
                break;
            case -17:   // LAB
                if (keyboardIsShifted)
                    protocol.sendProcessedKey(0x39);
                else
                    protocol.sendProcessedKey(0x19);
                break;
            case -18:   // MICRO
                protocol.sendProcessedKey(0x14);
                break;
            case -20:   // SQUARE
                if (keyboardIsShifted)
                    protocol.sendProcessedKey(0x3C);
                else
                    protocol.sendProcessedKey(0x1C);
            case -22:   // TERM
                protocol.sendProcessedKey(0x32);
                break;
            case -23:   // SUPER
                if (keyboardIsShifted)
                    protocol.sendProcessedKey(0x30);
                else
                    protocol.sendProcessedKey(0x10);
                break;
            case -24:   // SUB
                if (keyboardIsShifted)
                    protocol.sendProcessedKey(0x31);
                else
                    protocol.sendProcessedKey(0x11);
                break;
        }
    }

    /**
     * The current keyboard active:
     * ALPHA: alphanumweric keyboard
     * ALPHA_SHIFTED alphanumeric keyboard + SHIFT
     * PLATOKEYS Show the PLATO keys
     */
    private enum currentKeyboard {
        ALPHA, ALPHA_SHIFTED, PLATOKEYS
    }
}
