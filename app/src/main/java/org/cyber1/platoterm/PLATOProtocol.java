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
    private String protocolError;
    private boolean dumbTerminal;
    private boolean decoded;
    private PLATOActivity platoActivity;
    private boolean escape;

    PLATOProtocol(PLATOActivity platoActivity) {
        this.platoActivity = platoActivity;
    }

    private String getProtocolError() {
        return protocolError;
    }

    private void setProtocolError(String protocolError) {
        this.protocolError = protocolError;
    }

    private boolean isDecoded() {
        return decoded;
    }

    private void setDecoded(boolean decoded) {
        this.decoded = decoded;
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
        setDecoded(false);

        if (b == ASCII_ESCAPE)
            Log.i(this.getClass().getName(), "New Escape Sequence");
            setEscape(true);
        if (isEscape()) {
            decodeEscape(b);
        } else if (isDumbTerminal()) {
            decodeDumbTerminal(b);
        }

        if (!isDecoded() && getProtocolError().isEmpty()) {
            setProtocolError("Undecoded top level character: " + b);
        }

        if (!getProtocolError().isEmpty()) {
            Log.i(this.getClass().getName(), "Protocol Error: " + getProtocolError());
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
            Log.i(this.getClass().getName(), "Dumb terminal Printing ASCII byte: " + Byte.toString(b));
            // TODO: Implement dumb terminal ascii output.
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
            if (!isDumbTerminal()) {
                // Should we act on this, or fall through?
                Log.i(this.getClass().getName(), "Received STX while in PLATO mode.");
            }
            // Start PLATO mode, if in dumb terminal mode
            setDumbTerminal(false);
            setEscape(false);
            setDecoded(true);
            Log.i(this.getClass().getName(), "STX Received");
            Toast.makeText(getPlatoActivity().getApplicationContext(), "PLATO Connection Established", Toast.LENGTH_LONG).show();
        } else if (b == ASCII_ETX) {
            // End PLATO mode
            if (isDumbTerminal()) {
                // Should we act on this? or fall through?
                Log.i(this.getClass().getName(), "Received ETX while in dumb terminal mode.");
            }
            setDumbTerminal(true);
            setEscape(false);
            setDecoded(true);
            Log.i(this.getClass().getName(), "ETX Received");
            Toast.makeText(getPlatoActivity().getApplicationContext(), "PLATO Connection Ended", Toast.LENGTH_LONG).show();

        }
        if (!isDecoded()) {
            setProtocolError("Undecoded ESCape: " + b);
        }
    }

    @Contract(pure = true)
    private boolean isDumbTerminal() {
        return dumbTerminal;
    }

    private void setDumbTerminal(boolean dumbTerminal) {
        this.dumbTerminal = dumbTerminal;
    }
}
