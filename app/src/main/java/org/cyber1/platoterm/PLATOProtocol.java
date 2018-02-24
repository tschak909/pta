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
    private static final int ASCTYPE = 12; // Term type, move to protocol abstract class.
    private String protocolError;
    private boolean dumbTerminal;
    private boolean decoded;
    private PLATOActivity platoActivity;
    private boolean escape;
    private ascState currentAscState;
    private int ascBytes; // Byte counter for complex commands.
    private int modeWords; // Word counter for complex mode commands.
    private int lastCoordinateX;
    private int lastCoordinateY;
    private int assembler; // A temporary value used during byte assembly.


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

    private ascState getCurrentAscState() {
        return currentAscState;
    }

    private void setCurrentAscState(ascState currentAscState) {
        this.currentAscState = currentAscState;
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
                    setDecoded(true);
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
                    Log.d(this.getClass().getName(), "Start LDE_STATUS_REQUEST");
                    setCurrentAscState(ascState.LDE_STATUS_REQUEST);
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
            // auxiliary non-printable ASCII control characters not bolted to escape.
            switch (b) {
                case ASCII_BACKSPACE:
                    Log.d(this.getClass().getName(), "Backspace");
                    setDecoded(true);
                    getPlatoActivity().backspace();
                    break;
                case ASCII_TAB:
                    Log.d(this.getClass().getName(), "TAB interpreted as space.");
                    setDecoded(true);
                    getPlatoActivity().forwardspace();
                    break;
                case ASCII_LF:
                    Log.d(this.getClass().getName(), "Linefeed");
                    setDecoded(true);
                    getPlatoActivity().linefeed();
                    break;
                case ASCII_VT:
                    Log.d(this.getClass().getName(), "Vertical Tab");
                    setDecoded(true);
                    getPlatoActivity().verticalTab();
                    break;
                case ASCII_FF:
                    Log.d(this.getClass().getName(), "Form Feed");
                    setDecoded(true);
                    getPlatoActivity().formfeed();
                    break;
                case ASCII_CR:
                    Log.d(this.getClass().getName(), "Carriage Return");
                    setDecoded(true);
                    getPlatoActivity().carriageReturn();
                    break;
                case ASCII_EM:
                    int mode = ((getPlatoActivity().getRam().getMode() & 3) + (4 << 2));
                    Log.d(this.getClass().getName(), "EM - Load Mode " + mode);
                    getPlatoActivity().getRam().setMode(mode);
                    setModeWords(0);  // Number of words since entring mode
                    setDecoded(true);
                    break;
                case ASCII_FS:
                    mode = ((getPlatoActivity().getRam().getMode() & 3));
                    Log.d(this.getClass().getName(), "FS - Load Mode " + mode);
                    getPlatoActivity().getRam().setMode(mode);
                    setDecoded(true);
                    break;
                case ASCII_GS:
                    mode = ((getPlatoActivity().getRam().getMode() & 3) + (1 << 2));
                    Log.d(this.getClass().getName(), "GS - Load Mode " + mode);
                    getPlatoActivity().getRam().setMode(mode);
                    setDecoded(true);
                    break;
                case ASCII_RS:
                    Log.d(this.getClass().getName(), "RS - PNI_RS Start Download. Ignoring next 3 commands");
                    setCurrentAscState(ascState.PNI_RS);
                    setDecoded(true);
                    break;
                case ASCII_US:
                    mode = ((getPlatoActivity().getRam().getMode() & 3) + (3 << 2));
                    Log.d(this.getClass().getName(), "US - Load Mode " + mode);
                    getPlatoActivity().getRam().setMode(mode);
                    setDecoded(true);
                    break;
            }
        }
        if (b >= ASCII_SPACE) {
            switch (getCurrentAscState()) {
                case LOAD_COORDINATES:
                    if (assembleCoordinate(b)) {
                        getPlatoActivity().setCurrentX(getLastCoordinateX());
                        getPlatoActivity().setCurrentY(getLastCoordinateY());
                        Log.d(getClass().getName(), "load coordinates: " + getPlatoActivity().getCurrentX() + "," + getPlatoActivity().getCurrentY());
                    }
                    setDecoded(true);
                    break;
                case PAINT:
                    int n = assemblePaint(b);
                    if (n != -1) {
                        Log.d(getClass().getName(), "paint " + n);
                        getPlatoActivity().paint(n);
                    }
                case LDE_STATUS_REQUEST:
                    n = assembleData(b);
                    if (n != -1) {
                        n &= 0x7F;
                        n = processStatusRequest(n);
                    }
                    setDecoded(true);
                    break;
                case LDA:
                case EXT:
                case SSF:
                case FG:
                case BG:
                case GSFG:
                case PMD:
                case NONE:
                    processModes();
                case PNI_RS:
                    break;
            }
        }

    }

    private int processStatusRequest(int n) {
        switch (n) {
            case 0x70:
                n = 0x70 + ASCTYPE;
                Log.d(this.getClass().getName(), "load echo term type: " + n);
                break;
            case 0x71:
                n = 1; // Temporary.
                Log.d(this.getClass().getName(), "Get term subtype, returning 1");
                break;
            case 0x72:
                Log.d(this.getClass().getName(), "Load echo loadfile (unused)");
                n = 0;
                break;
            case 0x73:
                Log.d(this.getClass().getName(), "Load echo termdata");
                n = 0x40; // Terminal with touch panel, 16K
                break;
            case 0x7b:
                // Beep
                getPlatoActivity().beep();
                break;
            case 0x7d:
                // Report memory address register.
                n = getPlatoActivity().getRam().getMAR();
                Log.d(this.getClass().getName(), "Report MAR: " + n);
                break;
            case 0x52:
                // Enable flow control.
                

        }
        return n;
    }

    private int assembleData(byte b) {
        return 0;
    }

    /**
     * Assemble 9-bit paint word for ASCII protocol.
     *
     * @param b - current byte.
     * @return -1 if word not complete yet, otherwise, completed word.
     */
    private int assemblePaint(byte b) {
        if (getAscBytes() == 0) {
            setAssembler(0);
        }

        // Done this way because of the cumulative OR.
        assembler |= ((b & 0x3F) << (getAscBytes() * 6));
        if (++ascBytes == 2) {
            setAscBytes(0);
            setCurrentAscState(ascState.NONE);
            Log.d(this.getClass().getName(), "paint: " + assembler);
            return getAssembler();
        } else {
            Log.d(this.getClass().getName(), "Paint byte: " + getAscBytes() + " " + (b & 0x1F));
        }
        return -1;
    }

    /**
     * Process PLATO modes 0-7
     */
    private void processModes() {
        switch (getPlatoActivity().getRam().getMode() >> 2) {
            case 0:  // Dot mode
            case 1:  // Line mode
            case 2:  // Load Memory (Character sets)
            case 3:  // Text mode
            case 4:  // Block erase mode
            case 5:  // User program mode?
            case 6:
            case 7:
        }
    }

    /**
     * In coordinate mode, take next byte and assemble into the current set of screen coordinates.
     *
     * @param b the current byte.
     * @return true if coordinate is complete, false if coordinate needs another byte.
     */
    private boolean assembleCoordinate(byte b) {
        int coordinate = b & 0x1F; // Mask off top three bits

        switch (b >> 5) // Get control bits 6 and 7
        {
            case 1: // High X or High Y
                if (getAscBytes() == 0) {
                    // High Y coordinate
                    Log.d(this.getClass().getName(), "assembleCoordinate: High Y coordinate " + coordinate);
                    setLastCoordinateY((getLastCoordinateY() & 0x1F) | (coordinate << 5));
                    setAscBytes(2);
                } else {
                    // High X coordinate
                    Log.d(this.getClass().getName(), "assembleCoordinate: High X coordinate " + coordinate);
                    setLastCoordinateX((getLastCoordinateX() & 0x1F) | (coordinate << 5));
                }
                break;
            case 2: // Low X
                int nx = (getLastCoordinateX() & 0x480) | coordinate;
                setLastCoordinateX(nx);
                setAscBytes(0);
                setCurrentAscState(ascState.NONE);
                Log.d(this.getClass().getName(), "assembleCoordinate: Low X coordinate: " + coordinate + " - lastx: " + getLastCoordinateX() + " - lasty: " + getLastCoordinateY());
                return true;
            case 3: // Low Y
                Log.d(this.getClass().getName(), "assembleCoordinate: Low Y coordinate: " + coordinate);
                int ny = (getLastCoordinateX() & 0x480) | coordinate;
                setLastCoordinateY(ny);
                setAscBytes(2);
                break;
        }
        return false;
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
            // TODO: Properly decode upper case.
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

    public int getModeWords() {
        return modeWords;
    }

    private void setModeWords(int modeWords) {
        this.modeWords = modeWords;
    }

    private int getLastCoordinateX() {
        return lastCoordinateX;
    }

    private void setLastCoordinateX(int lastCoordinateX) {
        this.lastCoordinateX = lastCoordinateX;
    }

    private int getLastCoordinateY() {
        return lastCoordinateY;
    }

    private void setLastCoordinateY(int lastCoordinateY) {
        this.lastCoordinateY = lastCoordinateY;
    }

    private int getAscBytes() {
        return ascBytes;
    }

    private void setAscBytes(int ascBytes) {
        this.ascBytes = ascBytes;
    }

    public int getAssembler() {
        return assembler;
    }

    public void setAssembler(int assembler) {
        this.assembler = assembler;
    }

    private enum ascState {SSF, EXT, LDA, PMD, LDE_STATUS_REQUEST, FG, BG, PAINT, GSFG, NONE, PNI_RS, LOAD_COORDINATES}

}

