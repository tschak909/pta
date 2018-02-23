/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.Contract;

/**
 * PLATOProtocol - ASCII Protocol Decoding (for now)
 * Created by thomc on 2/21/2018.
 */

class PLATOProtocol {

    /**
     * Constants for ASCII codes
     */
    private static final byte ASCII_ESCAPE = 0x1B;
    private static final byte ASCII_STX = 0x02;
    private static final byte ASCII_ETX = 0x03;
    private static final byte ASCII_CR = 0x0D;
    private static final byte ASCII_LF = 0x0A;
    private static final byte ASCII_FF = 0x0C;
    private static final byte ASCII_SYN = 0x16;
    private static final byte ASCII_DC1 = 0x11;
    private static final byte ASCII_DC2 = 0x12;
    private static final byte ASCII_DC3 = 0x13;
    private static final byte ASCII_DC4 = 0x14;
    private static final byte ASCII_2 = 0x32;
    private static final byte ASCII_AT = 0x40;
    private static final byte ASCII_A = 0x41;
    private static final byte ASCII_B = 0x42;
    private static final byte ASCII_C = 0x43;
    private static final byte ASCII_D = 0x44;
    private static final byte ASCII_E = 0x45;
    private static final byte ASCII_F = 0x46;
    private static final byte ASCII_G = 0x47;
    private static final byte ASCII_H = 0x48;
    private static final byte ASCII_I = 0x49;
    private static final byte ASCII_J = 0x4A;
    private static final byte ASCII_K = 0x4B;
    private static final byte ASCII_L = 0x4C;
    private static final byte ASCII_M = 0x4D;
    private static final byte ASCII_N = 0x4E;
    private static final byte ASCII_O = 0x4F;
    private static final byte ASCII_P = 0x50;
    private static final byte ASCII_Q = 0x51;
    private static final byte ASCII_R = 0x52;
    private static final byte ASCII_S = 0x53;
    private static final byte ASCII_T = 0x54;
    private static final byte ASCII_U = 0x55;
    private static final byte ASCII_V = 0x56;
    private static final byte ASCII_W = 0x57;
    private static final byte ASCII_X = 0x58;
    private static final byte ASCII_Y = 0x59;
    private static final byte ASCII_Z = 0x5A;
    private static final byte ASCII_a = 0x61;
    private static final byte ASCII_b = 0x62;
    private static final byte ASCII_c = 0x63;
    private static final byte ASCII_g = 0x67;
    private static final byte ASCII_BACKSPACE = 0x08;
    private static final byte ASCII_TAB = 0x09;
    private static final byte ASCII_VT = 0x0B;
    private static final byte ASCII_EM = 0x19;
    private static final byte ASCII_FS = 0x1C;
    private static final byte ASCII_GS = 0x1D;
    private static final byte ASCII_RS = 0x1E;
    private static final byte ASCII_US = 0x1F;
    private static final byte ASCII_SPACE = 0x20;
    private static final int[] ascMode = {0, 3, 2, 1};
    private String protocolError;
    private boolean dumbTerminal;
    private boolean decoded;
    private PLATOActivity platoActivity;
    private boolean escape;
    private ascState currentAscState;
    private int ascBytes; // Byte counter for complex commands.

//    public int getAscBytes() {
//        return ascBytes;
//    }
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
        setCurrentAscState(ascState.NONE);
        setAscBytes(0); // Temporary until we get the secondary ascii states done.
    }

    public ascState getCurrentAscState() {
        return currentAscState;
    }

    private void setCurrentAscState(ascState currentAscState) {
        this.currentAscState = currentAscState;
    }

    private void setAscBytes(int ascBytes) {
        this.ascBytes = ascBytes;
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
        if (getProtocolError() != null && !getProtocolError().isEmpty()) {
            Log.d(this.getClass().getName(), "Protocol Error: " + getProtocolError());
            setProtocolError("");
        } else if (!isDecoded()) {
            Log.d(this.getClass().getName(), "Undecoded byte: " + b);
        }
    }

    /**
     * Decode a byte in PLATO mode
     *
     * @param b Byte to be decoded.
     */
    private void decodePLATOByte(byte b) {
        if (b == ASCII_ESCAPE)
            setEscape(true);

        if (isEscape()) {
            setEscape(false);
            switch (b) {
                case ASCII_STX:
                    setProtocolError("STX called when still in PLATO mode. Ignoring.");
                    setDumbTerminal(false);
                    setDecoded(true);
                    break;
                case ASCII_ETX:
                    Toast.makeText(getPlatoActivity().getApplicationContext(), "Exiting PLATO Mode. Dumb Terminal Mode Enabled.", Toast.LENGTH_SHORT).show();
                    setDumbTerminal(true);
                    setDumbTerminal(true);
                    break;
                case ASCII_FF:
                    Log.d(this.getClass().getName(), "Erasing screen.");
                    getPlatoActivity().eraseScreen();
                    setDecoded(true);
                    break;
                case ASCII_SYN:
                    Log.d(this.getClass().getName(), "Setting XOR mode.");
                    getPlatoActivity().getRam().setMode((getPlatoActivity().getRam().getMode() & ~3) + 2);
                    getPlatoActivity().setXORMode(true);
                    setDecoded(true);
                    break;
                case ASCII_DC1:
                case ASCII_DC2:
                case ASCII_DC3:
                case ASCII_DC4:
                    // inverse, write, erase, rewrite
                    Log.d(this.getClass().getName(), "Setting explicit mode " + ascMode[b - ASCII_DC1]);
                    getPlatoActivity().setXORMode(false);
                    getPlatoActivity().getRam().setMode((getPlatoActivity().getRam().getMode() & ~3) + ascMode[b - ASCII_DC1]);
                    setDecoded(true);
                    break;
                case ASCII_2:
                    // Load Coordinate
                    Log.d(this.getClass().getName(), "Start of load coordinate command.");
                    setCurrentAscState(ascState.LOAD_COORDINATES);
                    setAscBytes(0);
                    setDecoded(true);
                    break;
                case ASCII_AT:
                    // Superscript
                    Log.d(this.getClass().getName(), "Enable superscript");
                    // TODO: IMPLEMENT SUPERSCRIPT
                    setDecoded(true);
                    break;
                case ASCII_A:
                    // Subscript
                    Log.d(this.getClass().getName(), "Enable subscript");
                    // TODO: IMPLEMENT SUBSCRIPT
                    setDecoded(true);
                    break;
                case ASCII_B:
                case ASCII_C:
                case ASCII_D:
                case ASCII_E:
                case ASCII_F:
                case ASCII_G:
                case ASCII_H:
                case ASCII_I:
                    // Select current character set memory
                    int i = b - ASCII_B;
                    Log.d(this.getClass().getName(), "Select character memory #" + i);
                    getPlatoActivity().setCurrentCharacterSet(i);
                    setDecoded(true);
                    break;
                case ASCII_J:
                    Log.d(this.getClass().getName(), "Horizontal Writing Mode");
                    getPlatoActivity().setVerticalWritingMode(false);
                    setDecoded(true);
                    break;
                case ASCII_K:
                    Log.d(this.getClass().getName(), "Vertical writing mode");
                    getPlatoActivity().setVerticalWritingMode(true);
                    setDecoded(true);
                    break;
                case ASCII_L:
                    Log.d(this.getClass().getName(), "Forward writing mode");
                    getPlatoActivity().setReverseWritingMode(false);
                    setDecoded(true);
                    break;
                case ASCII_M:
                    Log.d(this.getClass().getName(), "Reverse writing mode");
                    getPlatoActivity().setReverseWritingMode(true);
                    setDecoded(true);
                    break;
                case ASCII_N:
                    Log.d(this.getClass().getName(), "Normal size writing mode");
                    getPlatoActivity().setBoldWritingMode(false);
                    setDecoded(true);
                    break;
                case ASCII_O:
                    Log.d(this.getClass().getName(), "Bold size writing mode");
                    getPlatoActivity().setBoldWritingMode(true);
                    setDecoded(true);
                    break;
                case ASCII_P:
                    // Implementing as is from pterm, we'll see if this works.
                    getPlatoActivity().setXORMode(false);
                    int mode = ((platoActivity.getRam().getMode() & 3) + (2 << 2));
                    Log.d(this.getClass().getName(), "Load mode " + mode);
                    getPlatoActivity().getRam().setMode(mode);
                    setDecoded(true);
                    break;
                case ASCII_Q:
                    Log.d(this.getClass().getName(), "Start SSF");
                    setCurrentAscState(ascState.SSF);
                    setAscBytes(0);
                    setDecoded(true);
                    break;
                case ASCII_R:
                    Log.d(this.getClass().getName(), "Start ext");
                    setCurrentAscState(ascState.EXT);
                    setAscBytes(0);
                    setDecoded(true);
                    break;
                case ASCII_S:
                    platoActivity.setXORMode(false);
                    mode = ((getPlatoActivity().getRam().getMode() & 3) + (2 << 2));
                    Log.d(this.getClass().getName(), "Load Mode " + mode);
                    getPlatoActivity().getRam().setMode(mode);
                    setDecoded(true);
                    break;
                case ASCII_T:
                    platoActivity.setXORMode(false);
                    mode = ((getPlatoActivity().getRam().getMode() & 3) + (5 << 2));
                    Log.d(this.getClass().getName(), "Load Mode " + mode);
                    getPlatoActivity().getRam().setMode(mode);
                    setDecoded(true);
                    break;
                case ASCII_U:
                    platoActivity.setXORMode(false);
                    mode = ((getPlatoActivity().getRam().getMode() & 3) + (6 << 2));
                    Log.d(this.getClass().getName(), "Load Mode " + mode);
                    getPlatoActivity().getRam().setMode(mode);
                    setDecoded(true);
                    break;
                case ASCII_V:
                    platoActivity.setXORMode(false);
                    mode = ((getPlatoActivity().getRam().getMode() & 3) + (7 << 2));
                    Log.d(this.getClass().getName(), "Load Mode " + mode);
                    getPlatoActivity().getRam().setMode(mode);
                    setDecoded(true);
                    break;
                case ASCII_W:
                    // Load memory address
                    Log.d(this.getClass().getName(), "Start LDA");
                    setCurrentAscState(ascState.LDA);
                    setAscBytes(0);
                    setDecoded(true);
                    break;
                case ASCII_X:
                    Log.d(this.getClass().getName(), "Start Load PLATO Metadata");
                    setCurrentAscState(ascState.PMD);
                    setAscBytes(0);
                    setDecoded(true);
                    break;
                case ASCII_Y:
                    Log.d(this.getClass().getName(), "Start LDE");
                    setCurrentAscState(ascState.LDE);
                    setAscBytes(0);
                    setDecoded(true);
                    break;
                case ASCII_Z:
                    Log.d(this.getClass().getName(), "Set X margin to " + getPlatoActivity().getCurrentX());
                    getPlatoActivity().setMargin(getPlatoActivity().getCurrentX());
                    setDecoded(true);
                    break;
                case ASCII_a:
                    Log.d(this.getClass().getName(), "Start Foreground color");
                    setCurrentAscState(ascState.FG);
                    setAscBytes(0);
                    setDecoded(true);
                    break;
                case ASCII_b:
                    Log.d(this.getClass().getName(), "Start Background color");
                    setCurrentAscState(ascState.BG);
                    setAscBytes(0);
                    setDecoded(true);
                    break;
                case ASCII_c:
                    Log.d(this.getClass().getName(), "Start paint");
                    setCurrentAscState(ascState.PAINT);
                    setAscBytes(0);
                    setDecoded(true);
                    break;
                case ASCII_g:
                    Log.d(this.getClass().getName(), "Start greyscale foreground color");
                    setCurrentAscState(ascState.GSFG);
                    setAscBytes(0);
                    setDecoded(true);
                    break;
                default:
                    setProtocolError("Unknown ESCAPE sequence: " + b);
                    setDecoded(false);
                    break;
            }
        } else {
            // auxiliary ASCII control characters not bolted to escape.
            switch (b) {
                case ASCII_BACKSPACE:
                case ASCII_TAB:
                case ASCII_LF:
                case ASCII_VT:
                case ASCII_FF:
                case ASCII_CR:
                case ASCII_EM:
                case ASCII_FS:
                case ASCII_GS:
                case ASCII_RS:
                case ASCII_US:
            }
        }
        if (b >= ASCII_SPACE) {
            switch (getCurrentAscState()) {
                case LOAD_COORDINATES:
                case PAINT:
                case LDE:
                case LDA:
                case EXT:
                case SSF:
                case FG:
                case BG:
                case GSFG:
                case PMD:
                case NONE:
                case PNI_RS:
                    break;
            }
        }

    }

    /**
     * Decode byte in dumb terminal mode.
     *
     * @param b Byte to decode
     */
    private void decodeDumbTerminal(byte b) {
        if (b == ASCII_ESCAPE) {
            setEscape(true);
            Log.d(this.getClass().getName(), "Escape detected in Dumb Terminal mode");
            setDecoded(true);
        } else if (b == ASCII_STX) {
            if (!isEscape()) {
                setProtocolError("STX detected without corresponding escape in dumb terminal mode.");
                setDecoded(true);
            } else {
                Log.d(this.getClass().getName(), "Proper STX sequence, setting PLATO mode.");
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
                getPlatoActivity().drawChar(charsetToUse, charToPlot);
            }
        }
    }

    @Contract(pure = true)
    private boolean isDumbTerminal() {
        return dumbTerminal;
    }

    private void setDumbTerminal(boolean dumbTerminal) {
        this.dumbTerminal = dumbTerminal;
    }

    private enum ascState {SSF, EXT, LDA, PMD, LDE, FG, BG, PAINT, GSFG, NONE, PNI_RS, LOAD_COORDINATES}

}

