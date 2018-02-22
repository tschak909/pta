/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.Contract;

import java.nio.charset.Charset;

/**
 * PLATOProtocol - ASCII Protocol Decoding (for now)
 * Created by thomc on 2/21/2018.
 */

class PLATOProtocol {

    private static final byte ASCII_ESCAPE = 0x1B;
    private static final byte ASCII_STX = 0x02;
    private static final byte ASCII_ETX = 0x03;
    private static final byte ASCII_CR = 0x0D;
    private static final byte ASCII_LF = 0x0A;
    private String protocolError;
    private boolean dumbTerminal;
    private boolean decoded;
    private PLATOActivity platoActivity;
    private boolean escape;

    /**
     * ASCII to ROM character translation tables. Table M0
     */
    private int[] asciiM0 = {0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
            0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
            0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
            0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
            0x2d, 0x38, 0x37, 0x80, 0x2b, 0x33, 0xab, 0x36,
            0x29, 0x2a, 0x27, 0x25, 0x2e, 0x26, 0x2f, 0x28,
            0x1b, 0x1c, 0x1d, 0x1e, 0x1f, 0x20, 0x21, 0x22,
            0x23, 0x24, 0x00, 0x39, 0x3a, 0x2c, 0x3b, 0x3d,
            0xbd, 0x81, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87,
            0x88, 0x89, 0x8a, 0x8b, 0x8c, 0x8d, 0x8e, 0x8f,
            0x90, 0x91, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97,
            0x98, 0x99, 0x9a, 0x31, 0xbe, 0x32, 0x9d, 0x3c,
            0x9f, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
            0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17,
            0x18, 0x19, 0x1a, 0xa9, 0xae, 0xaa, 0xa4, 0xff
    };

    private int[] asciiM1 = {0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
            0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
            0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
            0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
            0x2d, 0x28, 0xb0, 0x9b, 0x35, 0xac, 0xa0, 0xa1,
            0xa2, 0xa3, 0x34, 0xa5, 0xa6, 0xa7, 0xa8, 0x30,
            0xb1, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8,
            0xb9, 0xba, 0xbb, 0xbc, 0xc0, 0xaf, 0xc1, 0x3e,
            0xc2, 0x9c, 0xc3, 0xc8, 0xc4, 0xc5, 0x9e, 0xc9,
            0xc6, 0xc7, 0xae, 0xff, 0xff, 0xff, 0xff, 0xff,
            0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
            0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
            0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
            0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
            0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
            0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff
    };

    /**
     * Constructor for PLATO protocol.
     *
     * @param platoActivity the main PLATO activity.
     */
    PLATOProtocol(PLATOActivity platoActivity) {
        this.platoActivity = platoActivity;
        this.dumbTerminal = true; // Initially bring up in Dumb terminal mode.
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
        if (isDumbTerminal()) {
            decodeDumbTerminal(b);
        } else {
            decodePLATOByte(b);
        }
        if (!getProtocolError().isEmpty()) {
            Log.i(this.getClass().getName(), "Protocol Error: " + getProtocolError());
        }
    }

    /**
     * Decode a byte in PLATO mode
     *
     * @param b Byte to be decoded.
     */
    private void decodePLATOByte(byte b) {
    }

    /**
     * Decode byte in dumb terminal mode.
     *
     * @param b Byte to decode
     */
    private void decodeDumbTerminal(byte b) {
        if (b == ASCII_ESCAPE) {
            setEscape(true);
            Log.i(this.getClass().getName(), "Escape detected in Dumb Terminal mode");
            setDecoded(true);
        } else if (b == ASCII_STX) {
            if (!isEscape()) {
                setProtocolError("STX detected without corresponding escape in dumb terminal mode.");
                setDecoded(true);
            } else {
                Log.i(this.getClass().getName(), "Proper STX sequence, setting PLATO mode.");
                setDumbTerminal(false);
                setEscape(false);
                Toast.makeText(getPlatoActivity().getApplicationContext(), "PLATO Connection Established", Toast.LENGTH_SHORT).show();
                setDecoded(true);
            }
        } else if (b == ASCII_ETX) {
            if (!isEscape()) {
                setProtocolError("ETX detected without corresponding escape in dumb terminal mode.");
                setDecoded(true);
            } else {
                setProtocolError("ETX detected while in dumb terminal mode. Ignoring.");
                setEscape(false);
                setDecoded(true);
            }
        } else if (b == ASCII_CR) {
            getPlatoActivity().setCurrentX(0); // Beginning of line.
            setDecoded(true);
        } else if (b == ASCII_LF) {
            if (getPlatoActivity().getCurrentY() != 0)
                getPlatoActivity().setCurrentY(getPlatoActivity().getCurrentY() - 16);
            else {
                getPlatoActivity().scrollUp();
            }
            setDecoded(true);
        } else if (b >= 32 && b < 127) // Printable character.
        {
            int charToPlot = asciiM0[b];
            if (charToPlot != 0xff) {
                int charsetToUse = (b & 0x80) >> 7;
                charToPlot &= 0x7F;
                setDecoded(true);
                Log.i(this.getClass().getName(), "!!! Printable : " + b + " : " + charsetToUse + " : " + charToPlot);
                getPlatoActivity().drawChar(charsetToUse, charToPlot);
            }
        }
    }

    /**
     * An escape sequence! Let's decode it.
     *
     * @param b Byte to decode
     */
    @SuppressLint("ShowToast")
    private void decodeEscape(byte b) {

    }

    @Contract(pure = true)
    private boolean isDumbTerminal() {
        return dumbTerminal;
    }

    private void setDumbTerminal(boolean dumbTerminal) {
        this.dumbTerminal = dumbTerminal;
    }

    private String displayByte(byte b) {
        byte[] bytes = new byte[1];
        bytes[0] = b;
        String s = new String(bytes, Charset.defaultCharset());

        if (b >= 32 && b < 127) {
            return s;
        } else {
            return String.valueOf(b);
        }
    }

}

