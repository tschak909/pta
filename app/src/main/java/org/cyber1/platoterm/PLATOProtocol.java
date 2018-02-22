/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.Contract;

/**
 * PLATOProtocol - ASCII Protocol Decoding (for now)
 * Created by thomc on 2/21/2018.
 */

class PLATOProtocol {

    private static final byte ASCII_ESCAPE = 0x1B;
    private static final byte ASCII_STX = 0x02;
    private static final byte ASCII_ETX = 0x03;

    private boolean isDumbTerminal;

    private PLATOActivity platoActivity;
    private boolean escape;

    PLATOProtocol(PLATOActivity platoActivity) {
        this.platoActivity = platoActivity;
    }

    private boolean isEscape() {
        return escape;
    }

    private void setEscape(boolean escape) {
        this.escape = escape;
    }

    @Contract(pure = true)
    private PLATOActivity getPlatoActivity() {
        return platoActivity;
    }

    /**
     * Given a byte, decode and send off to the appropriate logic.
     * @param b Byte to decode
     */
    void decodeByte(byte b) {
        if (b == ASCII_ESCAPE)
            setEscape(true);
        if (isEscape()) {
            decodeEscape(b);
        } else if (isDumbTerminal()) {
            decodeDumbTerminal(b);
        }
    }

    /**
     * Decode byte in dumb terminal mode.
     *
     * @param b Byte to decode
     */
    private void decodeDumbTerminal(byte b) {
        if ((b >= 32) && (b < 127)) {
            // Normal ASCII character in dumb terminal mode.
            Log.i("PLATOActivity", "Printing ASCII byte: " + Byte.toString(b));
        }
    }

    /**
     * An escape sequence! Let's decode it.
     *
     * @param b Byte to decode
     */
    @SuppressLint("ShowToast")
    private void decodeEscape(byte b) {
        if (b == ASCII_STX) {
            // Start PLATO mode
            setDumbTerminal(false);
            setEscape(false);
            Log.i("PLATOActivity", "STX Received");
            Toast.makeText(getPlatoActivity().getApplicationContext(), "PLATO Connection Established", Toast.LENGTH_LONG).show();
        } else if (b == ASCII_ETX) {
            // End PLATO mode
            setDumbTerminal(true);
            setEscape(false);
            Log.i("PLATOActivity", "ETX Received");
            Toast.makeText(getPlatoActivity().getApplicationContext(), "PLATO Connection Ended", Toast.LENGTH_LONG).show();

        }
    }

    @Contract(pure = true)
    private boolean isDumbTerminal() {
        return isDumbTerminal;
    }

    private void setDumbTerminal(boolean dumbTerminal) {
        isDumbTerminal = dumbTerminal;
    }
}
