/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

/**
 * The PLATO Keyboard handler used by the activity.
 */
public class PLATOKeyboardHandler {

    /**
     * The default alphanumeric keyboard.
     */
    Keyboard mKeyboard;
    /**
     * Pointer to the PLATO activity
     */
    private PLATOActivity platoActivity;
    /**
     * Alphanumeric keyboard with shifted keys.
     */
    private Keyboard mKeyboardShifted;
    /**
     * Keyboard with Symbols
     */
    private Keyboard mKeyboardSym;
    /**
     * Is sticky shift enabled?
     */
    private boolean stickyShift;
    /**
     * Current keyboard state
     */
    private currentKeyboard currentKeyboardState;
    /**
     * Keyboard is shifted?
     */
    private boolean keyboardIsShifted;
    /**
     * The previous keycode.
     */
    private int lastKeycode;
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
                platoActivity.hideKeyboard();
            } else if (primaryCode == -6) { // SYM key

                switch (currentKeyboardState) {
                    case ALPHA:
                        platoActivity.mKeyboardView.setKeyboard(mKeyboardSym);
                        currentKeyboardState = currentKeyboard.SYMBOLS;
                        break;
                    case SYMBOLS:
                        platoActivity.mKeyboardView.setKeyboard(mKeyboard);
                        currentKeyboardState = currentKeyboard.ALPHA;
                        break;
                }

            } else if (primaryCode == -5) {
                if (!stickyShift && lastKeycode == -5) {
                    // Go sticky
                    Log.d(this.getClass().getName(), "STICKY!!!");
                    stickyShift = true;
                    keyboardIsShifted = true;
                    platoActivity.mKeyboardView.setShifted(true);
                    mKeyboard.setShifted(true);
                    mKeyboardSym.setShifted(true);
                    mKeyboardShifted.setShifted(true);
                    platoActivity.mKeyboardView.invalidateAllKeys();
                    lastKeycode = 0;
                } else if (stickyShift) {
                    Log.d(this.getClass().getName(), "Turn off sticky.");
                    stickyShift = false;
                    keyboardIsShifted = false;
                    platoActivity.mKeyboardView.setShifted(false);
                    platoActivity.mKeyboardView.setKeyboard(mKeyboard);
                    platoActivity.mKeyboardView.invalidateAllKeys();
                    lastKeycode = 0;
                }
                switch (currentKeyboardState) {
                    case ALPHA:
                        platoActivity.mKeyboardView.setVisibility(View.GONE);
                        platoActivity.mKeyboardView.setKeyboard(mKeyboardShifted);
                        platoActivity.mKeyboardView.setShifted(true);
                        platoActivity.mKeyboardView.setVisibility(View.VISIBLE);
                        currentKeyboardState = currentKeyboard.ALPHA_SHIFTED;
                        keyboardIsShifted = true;
                        break;
                    case ALPHA_SHIFTED:
                        if (!stickyShift) {
                            platoActivity.mKeyboardView.setKeyboard(mKeyboard);
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
                platoActivity.dispatchKeyEvent(event);

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
        }

        @Override
        public void swipeUp() {
        }
    };

    /**
     * Constructor
     */
    PLATOKeyboardHandler(PLATOActivity platoActivity) {
        this.platoActivity = platoActivity;
        mKeyboard = new Keyboard(platoActivity.getApplicationContext(), R.xml.keyboard, R.integer.keyboard_normal);
        mKeyboardShifted = new Keyboard(platoActivity.getApplicationContext(), R.xml.keyboard, R.integer.keyboard_shifted);
        mKeyboardSym = new Keyboard(platoActivity.getApplicationContext(), R.xml.keyboard_sym, 0);
        currentKeyboardState = currentKeyboard.ALPHA;
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
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x12);
            case PLATOKey.SOFTKEY_BACK:   // BACK
                if (keyboardIsShifted)
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x38);
                else
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x18);
                break;
            case PLATOKey.SOFTKEY_COPY:   // COPY
                if (keyboardIsShifted)
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x1B);
                else
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3B);
                break;
            case PLATOKey.SOFTKEY_DATA:   // DATA
                if (keyboardIsShifted)
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x39);
                else
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x19);
                break;
            case PLATOKey.SOFTKEY_EDIT:   // EDIT
                if (keyboardIsShifted)
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x37);
                else
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x17);
                break;
            case PLATOKey.SOFTKEY_FONT:   // FONT
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x34);
                break;
            case PLATOKey.SOFTKEY_HELP:   // HELP
                if (keyboardIsShifted)
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x35);
                else
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x15);
                break;
            case PLATOKey.SOFTKEY_LAB:   // LAB
                if (keyboardIsShifted)
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3D);
                else
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x1D);
                break;
            case PLATOKey.SOFTKEY_MICRO:   // MICRO
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x14);
                break;
            case -20:   // SQUARE
                if (keyboardIsShifted)
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                else
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x1C);
                break;
            case PLATOKey.SOFTKEY_TERM:   // TERM
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x32);
                break;
            case PLATOKey.SOFTKEY_SUPER:   // SUPER
                if (keyboardIsShifted)
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x30);
                else
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x10);
                break;
            case PLATOKey.SOFTKEY_SUB:   // SUB
                if (keyboardIsShifted)
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x31);
                else
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x11);
                break;
            case PLATOKey.SOFTKEY_STOP:   // STOP
                if (keyboardIsShifted) {
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3A);
                } else
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x1A);
                break;
            case PLATOKey.SOFTKEY_LT:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x20);
                break;
            case PLATOKey.SOFTKEY_GT:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x21);
                break;
            case PLATOKey.SOFTKEY_LEFT_BRACKET:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x22);
                break;
            case PLATOKey.SOFTKEY_RIGHT_BRACKET:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x23);
                break;
            case PLATOKey.SOFTKEY_DOLLAR:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x24);
                break;
            case PLATOKey.SOFTKEY_PERCENT:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x25);
                break;
            case PLATOKey.SOFTKEY_UNDERLINE:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x26);
                break;
            case PLATOKey.SOFTKEY_APOSTROPHE:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x27);
                break;
            case PLATOKey.SOFTKEY_ASTERISK:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x28);
                break;
            case PLATOKey.SOFTKEY_LEFT_PAREN:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x29);
                break;
            case PLATOKey.SOFTKEY_RIGHT_PAREN:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x7B);
                break;
            case PLATOKey.SOFTKEY_AT:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x05);
                break;
            case PLATOKey.SOFTKEY_POUND:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x24);
                break;
            case PLATOKey.SOFTKEY_CARET:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x40);
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0A);
                break;
            case PLATOKey.SOFTKEY_AMPERSAND:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0E);
                break;
            case PLATOKey.SOFTKEY_LEFT_BRACE:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x22);
                break;
            case PLATOKey.SOFTKEY_RIGHT_BRACE:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x23);
                break;
            case PLATOKey.SOFTKEY_BACKSLASH:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x5D);
                break;
            case PLATOKey.SOFTKEY_VERTICAL_BAR:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x69);
                break;
            case PLATOKey.SOFTKEY_GRAVE:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x40);
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x51);
                break;
            case PLATOKey.SOFTKEY_TILDE:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x40);
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x4E);
                break;
            case PLATOKey.SOFTKEY_DIVIDE:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0B);
                break;
            case PLATOKey.SOFTKEY_MINUS:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0F);
                break;
            case PLATOKey.SOFTKEY_PLUS:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0E);
                break;
            case PLATOKey.SOFTKEY_MULTIPLY:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0A);
                break;
            case PLATOKey.SOFTKEY_TAB:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0C);
                break;
            case PLATOKey.SOFTKEY_ASSIGN:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0d);
                break;
            case PLATOKey.SOFTKEY_SIGMA:
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x2E);
        }

        if (currentKeyboardState == currentKeyboard.SYMBOLS) {
            mKeyboardSym.setShifted(false);
            mKeyboard.setShifted(false);
            platoActivity.mKeyboardView.setShifted(false);
            platoActivity.mKeyboardView.setKeyboard(mKeyboard);
            keyboardIsShifted = false;
            currentKeyboardState = currentKeyboard.ALPHA;
        }

    }

    public void doPhysicalKeyDown(int keyCode, KeyEvent event) {
        Log.d(this.getClass().getName(), "Keydown - 0x" + String.format("%02X", keyCode));
        // Process SHIFT keys, they're the same for PLATO.

        if (platoActivity.getResources().getConfiguration().keyboard == 2) {
            // Physical keyboard
            doSpecialPhysicalKeys(keyCode, event);
            if (event.isCtrlPressed() && event.isShiftPressed()) {
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(PLATOKey.getShiftedPLATOcodeForCTRLKeycode(keyCode));
            } else if (event.isCtrlPressed()) {
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(PLATOKey.getPLATOcodeForCTRLKeycode(keyCode));
            } else if (event.isShiftPressed()) {
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(PLATOKey.getShiftedPLATOcodeForKeycode(keyCode));
            } else {
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(PLATOKey.getPLATOcodeForKeycode(keyCode));
            }
        } else {
            // Soft keyboard.
            if (keyboardIsShifted && !stickyShift) {
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(PLATOKey.getShiftedPLATOcodeForKeycode(keyCode));
            } else {
                platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(PLATOKey.getPLATOcodeForKeycode(keyCode));
            }
        }
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
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x05);
                }
                break;
            case 10: // #
                if (event.isShiftPressed()) {
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x24);
                }
                break;
            case 13: // ^
                if (event.isShiftPressed()) {
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x40);
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0A);
                }
                break;
            case 14: // &
                if (event.isShiftPressed()) {
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x0E);
                }
                break;
            case 71: // {
                if (event.isShiftPressed()) {
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x22);
                }
                break;
            case 72: // }
                if (event.isShiftPressed()) {
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x23);
                }
                break;
            case 73: // \
                if (event.isShiftPressed()) {
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x5D);
                }
                break;
            case 74: // |
                if (event.isShiftPressed()) {
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x69);
                }
                break;
            case 68: // `
                if (event.isShiftPressed()) {
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x40);
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x4E);
                } else {
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x40);
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x3C);
                    platoActivity.mService.getPlatoTerminal().getProtocol().sendProcessedKey(0x51);
                }
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
        ALPHA, ALPHA_SHIFTED, SYMBOLS
    }

}
