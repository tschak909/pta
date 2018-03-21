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
    PLATONetworkService mService;
    /**
     * Activity is bound to the network service.
     */
    boolean mBound = false;
    /**
     * The PLATO Protocol decoder/encoder
     */
    PLATOProtocol protocol;
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
     * bitmap for the TERM area
     */
    private int[] termAreaBitmap;
    /**
     * Is this view visible?
     */
    private boolean mVisible;
    /**
     * The terminal RAM (mode, character sets, etc.)
     */
    private PLATORam ram;
    /**
     * The connection between the network service and this activity.
     */
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
     * Current PLATO Font instance
     */
    private PLATOFont platoFont;
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
     * Font size for custom font.
     */
    private int fontSize;
    /**
     * Custom font flags
     */
    private int fontFlags;
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
     * Return the Network Service
     *
     * @return the PLATONetworkService object
     */
    public PLATONetworkService getNetworkService() {
        return mService;
    }

    /**
     * Get the current character set.
     * @return The current character set.
     */
    public int getCurrentCharacterSet() {
        return currentCharacterSet;
    }

    /**
     * Set the current character set
     * @param currentCharacterSet the character set # to set.
     */
    public void setCurrentCharacterSet(int currentCharacterSet) {
        this.currentCharacterSet = currentCharacterSet;
    }

    /**
     * Get current X coordinate
     * @return Current X coordinate (0-511)
     */
    public int getCurrentX() {
        return currentX;
    }

    /**
     * Set current X coordinate
     * @param currentX New current X coordinate (0-511)
     */
    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    /**
     * Get the PLATORam for this activity
     * @return the instantiated PLATORam object.
     */
    public PLATORam getRam() {
        return ram;
    }

    /**
     * Set the current PLATORam object for this activity
     * @param ram the new instantiated PLATORam object.
     */
    public void setRam(PLATORam ram) {
        this.ram = ram;
    }

    /**
     * Get the current cursor Y position
     * @return The current cursor Y position (0-511)
     */
    public int getCurrentY() {
        return currentY;
    }

    /**
     * Set the current cursor Y position
     * @param currentY The new cursor Y position (0-511)
     */
    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    /**
     * Instantiate if needed, and ask PLATOProtocol to decode a byte
     * @param aByte The byte to decode via the PLATOProtocol
     */
    private void processData(Byte aByte) {
        if (protocol == null) {
            protocol = new PLATOProtocol(this, this.ram, this.platoFont);
        } else {
            protocol.decodeByte(aByte);
        }
    }

    /**
     * Called when activity is started.
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("PLATOActivity", "ONSTART!!!");
        if (!VIEW_TEST) {
            Intent intent = new Intent(this, PLATONetworkService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
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
    }

    /**
     * Called after onCreate to ensure that view bits are initialized before
     * kicking things off.
     * @param savedInstanceState The saved instance state.
     */
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
        }

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

    /**
     * Tell the view to enable or disable XOR mode
     * @param b 1 = XOR on, 0 = XOR off.
     */
    public void setXORMode(boolean b) {
        mContentView.setModeXOR(b);
    }

    /**
     * Is vertical writing mode enabled?
     * @return true = enabled, false = disabled.
     */
    public boolean isVerticalWritingMode() {
        return verticalWritingMode;
    }

    /**
     * Set the vertical writing mode
     * @param verticalWritingMode true = enabled, false = disabled.
     */
    public void setVerticalWritingMode(boolean verticalWritingMode) {
        this.verticalWritingMode = verticalWritingMode;
    }

    /**
     * are we in reverse writing mode (Right-to-left)
     * @return 1 = right to left, 0 = left-to-right
     */
    public boolean isReverseWritingMode() {
        return reverseWritingMode;
    }

    /**
     * Set a new reverse writing mode
     * @param reverseWritingMode 1 = right-to-left 0=left-to-right
     */
    public void setReverseWritingMode(boolean reverseWritingMode) {
        this.reverseWritingMode = reverseWritingMode;
    }

    /**
     * Is bold writing mode enabled?
     * @return 1 = bold writing enabled, 0 = bold writing disabled.
     */
    public boolean isBoldWritingMode() {
        return boldWritingMode;
    }

    /**
     * Set new bold writing mode
     * @param boldWritingMode 1 = enabled, 0 = disabled.
     */
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

    /**
     * Get the current X margin
     * @return The current X margin offset
     */
    public int getMargin() {
        return margin;
    }

    /**
     * Set a new X margin
     * @param margin the new X margin (0-511)
     */
    public void setMargin(int margin) {
        this.margin = margin;
    }

    /**
     * Back up cursor one character cell.
     */
    public void backspace() {
        setCurrentX(getCurrentX() - getDeltaX());
    }

    /**
     * Get the current X delta (change for next character cell)
     * @return The current X delta.
     */
    public int getDeltaX() {
        return deltaX;
    }

    /**
     * Set a new X delta (change for next character cell)
     * @param deltaX new X delta (0-511)
     */
    public void setDeltaX(int deltaX) {
        this.deltaX = deltaX;
    }

    /**
     * Get the current Y delta (change for next character cell)
     * @return the current Y delta.
     */
    public int getDeltaY() {
        return deltaY;
    }

    /**
     * Set the new Y delta (change for next character cell)
     * @param deltaY The new Y delta (0-511)
     */
    public void setDeltaY(int deltaY) {
        this.deltaY = deltaY;
    }

    /**
     * Get the font height for custom font.
     * @return The custom font height
     */
    public int getFontHeight() {
        return fontheight;
    }

    /**
     * Set a new custom font height
     * @param fontheight the new custom font height in pixels.
     */
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
        mContentView.doPaint(currentX, currentY, n);
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

    /**
     * Get the term area bitmap (for CWS_SAVE and CWS_RESTORE)
     * @return the Bitmap for the TERM area.
     */
    public int[] getTermAreaBitmap() {
        return termAreaBitmap;
    }

    /**
     * Set the term area bitmap (for CWS_SAVE and CWS_RESTORE
     * @param termAreaBitmap the new term area bitmap
     */
    public void setTermAreaBitmap(int[] termAreaBitmap) {
        this.termAreaBitmap = termAreaBitmap;
    }

    /**
     * Blit the term area from CWS_SAVE back to the main view bitmap
     */
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

    /**
     * Get the current custom font size.
     * @return The current font size.
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * Set a new custom font size in pixels
     * @param n the new font size in pixels.
     */
    public void setFontSize(int n) {
        this.fontSize = (n < 1 ? 1 : (n > 63 ? 63 : n));
    }

    /**
     * Get the current custom font flags.
     * @return The custom font flags.
     */
    public int getFontFlags() {
        return fontFlags;
    }

    /**
     * Set new custom font flags.
     * @param n The new custom font flags.
     */
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

    /**
     * Get the current background color
     * @return Current background color (as 32-bit integer)
     */
    public int getCurrentFG() {
        return mContentView.getDrawingColorFG();
    }

    /**
     * Set a new current background color (as a #112233 hex triplet)
     * @param newFG the new background color as #112233
     */
    public void setCurrentFG(String newFG) {
        mContentView.setDrawingColorFG(Color.parseColor(newFG));
    }

    /**
     * Get current background color as 32-bit integer
     * @return The current background color as 32-bit integer.
     */
    public int getCurrentBG() {
        return mContentView.getDrawingColorBG();
    }

    /**
     * Set a new background color as hex triplet #112233
     * @param newBG the new background color as hex triplet #112233
     */
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

    /**
     * Get the current PLATOFont object
     * @return The current PLATOFont object
     */
    public PLATOFont getPlatoFont() {
        return platoFont;
    }

    /**
     * Set a new PLATOFont object
     * @param font a PLATOFont object
     */
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
                protocol.sendProcessedKey(PLATOKey.getShiftedPLATOcodeForCTRLKeycode(keyCode));
            } else if (event.isCtrlPressed()) {
                protocol.sendProcessedKey(PLATOKey.getPLATOcodeForCTRLKeycode(keyCode));
            } else if (event.isShiftPressed()) {
                protocol.sendProcessedKey(PLATOKey.getShiftedPLATOcodeForKeycode(keyCode));
            } else {
                protocol.sendProcessedKey(PLATOKey.getPLATOcodeForKeycode(keyCode));
            }
        } else {
            // Soft keyboard.
            if (keyboardIsShifted && !stickyShift) {
                protocol.sendProcessedKey(PLATOKey.getShiftedPLATOcodeForKeycode(keyCode));
            } else {
                protocol.sendProcessedKey(PLATOKey.getPLATOcodeForKeycode(keyCode));
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
                    protocol.sendProcessedKey(0x3C);
                    protocol.sendProcessedKey(0x05);
                }
                break;
            case 10: // #
                if (event.isShiftPressed()) {
                    protocol.sendProcessedKey(0x3C);
                    protocol.sendProcessedKey(0x24);
                }
                break;
            case 13: // ^
                if (event.isShiftPressed()) {
                    protocol.sendProcessedKey(0x40);
                    protocol.sendProcessedKey(0x3C);
                    protocol.sendProcessedKey(0x0A);
                }
                break;
            case 14: // &
                if (event.isShiftPressed()) {
                    protocol.sendProcessedKey(0x3C);
                    protocol.sendProcessedKey(0x0E);
                }
                break;
            case 71: // {
                if (event.isShiftPressed()) {
                    protocol.sendProcessedKey(0x3C);
                    protocol.sendProcessedKey(0x22);
                }
                break;
            case 72: // }
                if (event.isShiftPressed()) {
                    protocol.sendProcessedKey(0x3C);
                    protocol.sendProcessedKey(0x23);
                }
                break;
            case 73: // \
                if (event.isShiftPressed()) {
                    protocol.sendProcessedKey(0x3C);
                    protocol.sendProcessedKey(0x5D);
                }
                break;
            case 74: // |
                if (event.isShiftPressed()) {
                    protocol.sendProcessedKey(0x3C);
                    protocol.sendProcessedKey(0x69);
                }
                break;
            case 68: // `
                if (event.isShiftPressed()) {
                    protocol.sendProcessedKey(0x40);
                    protocol.sendProcessedKey(0x3C);
                    protocol.sendProcessedKey(0x4E);
                } else {
                    protocol.sendProcessedKey(0x40);
                    protocol.sendProcessedKey(0x3C);
                    protocol.sendProcessedKey(0x51);
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
                protocol.sendProcessedKey(0x12);
            case PLATOKey.SOFTKEY_BACK:   // BACK
                if (keyboardIsShifted)
                    protocol.sendProcessedKey(0x38);
                else
                    protocol.sendProcessedKey(0x18);
                break;
            case PLATOKey.SOFTKEY_COPY:   // COPY
                if (keyboardIsShifted)
                    protocol.sendProcessedKey(0x1B);
                else
                    protocol.sendProcessedKey(0x3B);
                break;
            case PLATOKey.SOFTKEY_DATA:   // DATA
                if (keyboardIsShifted)
                    protocol.sendProcessedKey(0x39);
                else
                    protocol.sendProcessedKey(0x19);
                break;
            case PLATOKey.SOFTKEY_EDIT:   // EDIT
                if (keyboardIsShifted)
                    protocol.sendProcessedKey(0x37);
                else
                    protocol.sendProcessedKey(0x17);
                break;
            case PLATOKey.SOFTKEY_FONT:   // FONT
                protocol.sendProcessedKey(0x34);
                break;
            case PLATOKey.SOFTKEY_HELP:   // HELP
                if (keyboardIsShifted)
                    protocol.sendProcessedKey(0x35);
                else
                    protocol.sendProcessedKey(0x15);
                break;
            case PLATOKey.SOFTKEY_LAB:   // LAB
                if (keyboardIsShifted)
                    protocol.sendProcessedKey(0x3D);
                else
                    protocol.sendProcessedKey(0x1D);
                break;
            case PLATOKey.SOFTKEY_MICRO:   // MICRO
                protocol.sendProcessedKey(0x14);
                break;
            case -20:   // SQUARE
                if (keyboardIsShifted)
                    protocol.sendProcessedKey(0x3C);
                else
                    protocol.sendProcessedKey(0x1C);
                break;
            case PLATOKey.SOFTKEY_TERM:   // TERM
                protocol.sendProcessedKey(0x32);
                break;
            case PLATOKey.SOFTKEY_SUPER:   // SUPER
                if (keyboardIsShifted)
                    protocol.sendProcessedKey(0x30);
                else
                    protocol.sendProcessedKey(0x10);
                break;
            case PLATOKey.SOFTKEY_SUB:   // SUB
                if (keyboardIsShifted)
                    protocol.sendProcessedKey(0x31);
                else
                    protocol.sendProcessedKey(0x11);
                break;
            case PLATOKey.SOFTKEY_STOP:   // STOP
                if (keyboardIsShifted) {
                    protocol.sendProcessedKey(0x3A);
                } else
                    protocol.sendProcessedKey(0x1A);
                break;
            case PLATOKey.SOFTKEY_LT:
                protocol.sendProcessedKey(0x20);
                break;
            case PLATOKey.SOFTKEY_GT:
                protocol.sendProcessedKey(0x21);
                break;
            case PLATOKey.SOFTKEY_LEFT_BRACKET:
                protocol.sendProcessedKey(0x22);
                break;
            case PLATOKey.SOFTKEY_RIGHT_BRACKET:
                protocol.sendProcessedKey(0x23);
                break;
            case PLATOKey.SOFTKEY_DOLLAR:
                protocol.sendProcessedKey(0x24);
                break;
            case PLATOKey.SOFTKEY_PERCENT:
                protocol.sendProcessedKey(0x25);
                break;
            case PLATOKey.SOFTKEY_UNDERLINE:
                protocol.sendProcessedKey(0x26);
                break;
            case PLATOKey.SOFTKEY_APOSTROPHE:
                protocol.sendProcessedKey(0x27);
                break;
            case PLATOKey.SOFTKEY_ASTERISK:
                protocol.sendProcessedKey(0x28);
                break;
            case PLATOKey.SOFTKEY_LEFT_PAREN:
                protocol.sendProcessedKey(0x29);
                break;
            case PLATOKey.SOFTKEY_RIGHT_PAREN:
                protocol.sendProcessedKey(0x7B);
                break;
            case PLATOKey.SOFTKEY_AT:
                protocol.sendProcessedKey(0x3C);
                protocol.sendProcessedKey(0x05);
                break;
            case PLATOKey.SOFTKEY_POUND:
                protocol.sendProcessedKey(0x3C);
                protocol.sendProcessedKey(0x24);
                break;
            case PLATOKey.SOFTKEY_CARET:
                protocol.sendProcessedKey(0x40);
                protocol.sendProcessedKey(0x3C);
                protocol.sendProcessedKey(0x0A);
                break;
            case PLATOKey.SOFTKEY_AMPERSAND:
                protocol.sendProcessedKey(0x3C);
                protocol.sendProcessedKey(0x0E);
                break;
            case PLATOKey.SOFTKEY_LEFT_BRACE:
                protocol.sendProcessedKey(0x3C);
                protocol.sendProcessedKey(0x22);
                break;
            case PLATOKey.SOFTKEY_RIGHT_BRACE:
                protocol.sendProcessedKey(0x3C);
                protocol.sendProcessedKey(0x23);
                break;
            case PLATOKey.SOFTKEY_BACKSLASH:
                protocol.sendProcessedKey(0x3C);
                protocol.sendProcessedKey(0x5D);
                break;
            case PLATOKey.SOFTKEY_VERTICAL_BAR:
                protocol.sendProcessedKey(0x3C);
                protocol.sendProcessedKey(0x69);
                break;
            case PLATOKey.SOFTKEY_GRAVE:
                protocol.sendProcessedKey(0x40);
                protocol.sendProcessedKey(0x3C);
                protocol.sendProcessedKey(0x51);
                break;
            case PLATOKey.SOFTKEY_TILDE:
                protocol.sendProcessedKey(0x40);
                protocol.sendProcessedKey(0x3C);
                protocol.sendProcessedKey(0x4E);
                break;
            case PLATOKey.SOFTKEY_DIVIDE:
                protocol.sendProcessedKey(0x0B);
                break;
            case PLATOKey.SOFTKEY_MINUS:
                protocol.sendProcessedKey(0x0F);
                break;
            case PLATOKey.SOFTKEY_PLUS:
                protocol.sendProcessedKey(0x0E);
                break;
            case PLATOKey.SOFTKEY_MULTIPLY:
                protocol.sendProcessedKey(0x0A);
                break;
            case PLATOKey.SOFTKEY_TAB:
                protocol.sendProcessedKey(0x0C);
                break;
            case PLATOKey.SOFTKEY_ASSIGN:
                protocol.sendProcessedKey(0x0d);
                break;
            case PLATOKey.SOFTKEY_SIGMA:
                protocol.sendProcessedKey(0x2E);
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
     * Go to sleep for 8ms.
     */
    public void nullSleep() {
        try {
            mContentView.invalidate();
            Thread.sleep(8); // 8ms in ASCII.
        } catch (InterruptedException e) {
            e.printStackTrace();
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
