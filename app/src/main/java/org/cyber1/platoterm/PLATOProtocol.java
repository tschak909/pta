/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

import android.util.Log;

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
    private static final int ASCFEATURES = 0x09; // ASC_WINDOW | ASC_ZFGT
    private static final int ASCII_XOFF = ASCII_DC1;
    private static final int ASCII_XON = ASCII_DC3;
    private static final int EXT_CWS_TERMSAVE = 2000;
    private static final int EXT_CWS_TERMRESTORE = 2001;
    private static final byte ASCII_NUL = 0x00;
    private String protocolError;
    private boolean dumbTerminal;
    private boolean decoded;
    private PLATOTerminal platoTerminal;
    private boolean escape;
    private ascState currentAscState;
    private int ascBytes; // Byte counter for complex commands.
    private int modeWords; // Word counter for complex mode commands.
    private int lastCoordinateX;
    private int lastCoordinateY;
    private int assembler; // A temporary value used during byte assembly.
    private boolean flowControl; // Flow control enabled, re-map keys.
    private boolean sendFgt;
    private int pendingEcho;

    private int[] asciiKeycodes = {0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,
            0x38, 0x39, 0x26, 0x60, 0x0a, 0x5e, 0x2b, 0x2d,
            0x13, 0x04, 0x07, 0x08, 0x7b, 0x0b, 0x0d, 0x1a,
            0x02, 0x12, 0x01, 0x03, 0x7d, 0x0c, 0xff, 0xff,
            0x3c, 0x3e, 0x5b, 0x5d, 0x24, 0x25, 0x5f, 0x7c,
            0x2a, 0x28, 0x40, 0x27, 0x1c, 0x5c, 0x23, 0x7e,
            0x17, 0x05, 0x14, 0x19, 0x7f, 0x09, 0x1e, 0x18,
            0x0e, 0x1d, 0x11, 0x16, 0x00, 0x0f, 0xff, 0xff,
            0x20, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67,
            0x68, 0x69, 0x6a, 0x6b, 0x6c, 0x6d, 0x6e, 0x6f,
            0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77,
            0x78, 0x79, 0x7a, 0x3d, 0x3b, 0x2f, 0x2e, 0x2c,
            0x1f, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47,
            0x48, 0x49, 0x4a, 0x4b, 0x4c, 0x4d, 0x4e, 0x4f,
            0x50, 0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57,
            0x58, 0x59, 0x5a, 0x29, 0x3a, 0x3f, 0x21, 0x22};
    private int CWSMode;
    private int CWScnt;
    private int CWSfun;
    private boolean fontPMD;
    private boolean fontInfo;
    private boolean osInfo;
    private String PMD;
    private int fontWidth;
    private int fontHeight;
    private int mode4start;

    /**
     * Constructor for PLATO protocol.
     *
     * @param platoTerminal the main PLATO activity.
     */
    PLATOProtocol(PLATOTerminal platoTerminal) {
        this.platoTerminal = platoTerminal;
        this.dumbTerminal = true; // Initially bring up in Dumb terminal mode.
        setCurrentAscState(ascState.NONE);
        setAscBytes(0); // Temporary until we get the secondary ascii states done.
        getPLATOTerminal().getPLATORam().writeRAM(PLATORam.C2ORIGIN, (byte) (PLATORam.M2ADDR & 0xFF));
        getPLATOTerminal().getPLATORam().writeRAM(PLATORam.C2ORIGIN + 1, (byte) (PLATORam.M2ADDR >> 8));
        getPLATOTerminal().getPLATORam().writeRAM(PLATORam.C3ORIGIN, (byte) (PLATORam.M3ADDR & 0xFF));
        getPLATOTerminal().getPLATORam().writeRAM(PLATORam.C3ORIGIN + 1, (byte) (PLATORam.M3ADDR >> 8));
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

    private boolean isEscape() {
        return escape;
    }

    @Contract(pure = true)
    private PLATOTerminal getPLATOTerminal() {
        return platoTerminal;
    }

    /**
     * Given a byte, decode and send off to the appropriate logic.
     * @param b Byte to decode
     */
    void decodeByte(byte b) {
        decoded = false;
        if (isDumbTerminal()) {
            decodeDumbTerminal(b);
        } else {
            decodePLATOByte(b);
        }
        if (getProtocolError() != null && !getProtocolError().isEmpty()) {
            Log.d(this.getClass().getName(), "Protocol Error: " + getProtocolError());
            setProtocolError("");
        } else if (!isDecoded()) {
            Log.d(this.getClass().getName(), "Undecoded byte: " + String.format("%02X", (b & 0xFF)));
        }
    }

    /**
     * Decode a byte in PLATO mode
     *
     * @param b Byte to be decoded.
     */
    private void decodePLATOByte(byte b) {
        if (b == ASCII_ESCAPE) {
            escape = true;
            decoded = true;
        } else if (getCurrentAscState() == ascState.PNI_RS) {
            processPNI_RS();
        } else if (getCurrentAscState() == ascState.PMD) {
            processPMD(b);
        } else if (isEscape()) {
            escape = false;
            processEscapeSequence(b);
        } else {
            processControlCharacters(b);
            processOtherStates(b);
        }
    }

    private void processPMD(byte b) {
        int n = AssembleASCIIPLATOMetadata(b);
        if (n == 0) {
            if (getFontPMD()) {
                Log.d(this.getClass().getName(), "PLATO metadata font data...");
                setFontPMD(false);
            } else if (getFontInfo()) {
                Log.d(this.getClass().getName(), "PLATO metadata font data info...");
                setFontInfo(false);
            } else if (getosInfo()) {
                Log.d(this.getClass().getName(), "PLATO metadata get operating system info...");
                setOsInfo(false);
            } else {
                Log.d(this.getClass().getName(), "PLATO metadata complete...");
                processPLATOMetaData();
            }
        }
        decoded = true;
    }

    private void processPNI_RS() {
        // Ignore the next three bytes.
        if (++ascBytes == 3) {
            setAscBytes(0);
            setCurrentAscState(ascState.NONE);
        }
        decoded = true;
    }

    private void processOtherStates(byte b) {
        if (b >= ASCII_SPACE) {
            switch (getCurrentAscState()) {
                case LOAD_COORDINATES:
                    Log.d(this.getClass().getName(), "LOAD_COORDINATES pre assemblecoordinate");
                    if (assembleCoordinate(b)) {
                        getPLATOTerminal().setCurrentX(lastCoordinateX);
                        getPLATOTerminal().setCurrentY(lastCoordinateY);
                        Log.d(getClass().getName(), "load coordinates: " + getPLATOTerminal().getCurrentX() + "," + getPLATOTerminal().getCurrentY());
                    }
                    decoded = true;
                    break;
                case PAINT:
                    int n = assemblePaint(b);
                    if (n != -1) {
                        Log.d(getClass().getName(), "paint " + n);
                        getPLATOTerminal().paint(n);
                    }
                    decoded = true;
                    break;
                case LOAD_ECHO:
                    n = assembleData(b);
                    if (n != -1) {
                        n &= 0x7F;
                        processEchoRequest(n);
                    }
                    decoded = true;
                    break;
                case LOAD_ADDRESS:
                    n = assembleData(b);
                    if (n != -1) {
                        Log.d(this.getClass().getName(), "Load memory address 0x" + String.format("%04X", n));
                        getPLATOTerminal().getPLATORam().setMAR(n & 0x7FFF);
                    }
                    decoded = true;
                    break;
                case LOAD_EXTERNAL:
                    n = assembleData(b);
                    processExt(n);
                    decoded = true;
                case SSF:
                    n = assembleData(b);
                    if (n != -1) {
                        Log.d(this.getClass().getName(), "SSF " + n);
                        // getPLATOTerminal().activateTouchPanel((n & 0x20) != 0);
                    }
                    processSSF(n);
                    decoded = true;
                    break;
                case FG:
                case BG:
                    n = assembleColor(b);
                    processColor(n);
                    decoded = true;
                    break;
                case GSFG:
                    n = assembleGrayscale(b);
                    processGrayscale(n);
                    decoded = true;
                    break;
                case PMD:
                    decoded = true;
                    break; // handled above.
                case NONE:
                    processModes(b);
                    decoded = true;
                    break;
                case PNI_RS:
                    decoded = true;
                    break;
            }
        }
    }

    private void processControlCharacters(byte b) {
        // auxiliary non-printable ASCII control characters not bolted to escape.
        switch (b) {
            case ASCII_NUL:
                Log.d(this.getClass().getName(), "Got a NOP. Go sleepies for 8ms...");
                getPLATOTerminal().nullSleep();
                decoded = true;
                break;
            case ASCII_BACKSPACE:
                Log.d(this.getClass().getName(), "Backspace");
                decoded = true;
                getPLATOTerminal().backspace();
                break;
            case ASCII_TAB:
                Log.d(this.getClass().getName(), "TAB interpreted as space.");
                decoded = true;
                getPLATOTerminal().forwardspace();
                break;
            case ASCII_LF:
                Log.d(this.getClass().getName(), "Linefeed");
                decoded = true;
                getPLATOTerminal().linefeed();
                break;
            case ASCII_VT:
                Log.d(this.getClass().getName(), "Vertical Tab");
                decoded = true;
                getPLATOTerminal().verticalTab();
                break;
            case ASCII_FF:
                Log.d(this.getClass().getName(), "Form Feed");
                decoded = true;
                getPLATOTerminal().formfeed();
                break;
            case ASCII_CR:
                Log.d(this.getClass().getName(), "Carriage Return");
                decoded = true;
                getPLATOTerminal().carriageReturn();
                break;
            case ASCII_EM:
                int mode = ((getPLATOTerminal().getPLATORam().getMode() & 3) + (4 << 2));
                Log.d(this.getClass().getName(), "EM - Block write/erase - Load Mode " + mode);
                getPLATOTerminal().getPLATORam().setMode(mode);
                setModeWords(0);  // Number of words since entring mode
                decoded = true;
                break;
            case ASCII_FS:
                mode = ((getPLATOTerminal().getPLATORam().getMode() & 3));
                Log.d(this.getClass().getName(), "FS - Point Plot - Load Mode " + mode);
                getPLATOTerminal().getPLATORam().setMode(mode);
                decoded = true;
                break;
            case ASCII_GS:
                mode = ((getPLATOTerminal().getPLATORam().getMode() & 3) + (1 << 2));
                Log.d(this.getClass().getName(), "GS - Draw Line Load Mode " + mode);
                currentAscState = ascState.LOAD_COORDINATES;
                ascBytes = 0;
                getPLATOTerminal().getPLATORam().setMode(mode);
                decoded = true;
                break;
            case ASCII_RS:
                Log.d(this.getClass().getName(), "RS - PNI_RS Start Download. Ignoring next 3 commands");
                setCurrentAscState(ascState.PNI_RS);
                decoded = true;
                break;
            case ASCII_US:
                mode = ((getPLATOTerminal().getPLATORam().getMode() & 3) + (3 << 2));
                Log.d(this.getClass().getName(), "US - Alpha mode - Load Mode " + mode);
                getPLATOTerminal().getPLATORam().setMode(mode);
                decoded = true;
                break;
        }
    }

    private void processEscapeSequence(byte b) {
        switch (b) {
            case ASCII_STX:
                setProtocolError("STX called when still in PLATO mode. Ignoring.");
                dumbTerminal = false;
                decoded = true;
                break;
            case ASCII_ETX:
                dumbTerminal = true;
                decoded = true;
                break;
            case ASCII_FF:
                Log.d(this.getClass().getName(), "Erasing screen.");
                getPLATOTerminal().erase();
                decoded = true;
                break;
            case ASCII_SYN:
                Log.d(this.getClass().getName(), "Setting XOR mode.");
                getPLATOTerminal().getPLATORam().setMode((getPLATOTerminal().getPLATORam().getMode() & ~3) + 2);
                getPLATOTerminal().setModeXOR(true);
                decoded = true;
                break;
            case ASCII_DC1:
            case ASCII_DC2:
            case ASCII_DC3:
            case ASCII_DC4:
                // inverse, write, erase, rewrite
                Log.d(this.getClass().getName(), "Setting explicit mode " + ascMode[b - ASCII_DC1]);
                getPLATOTerminal().setModeXOR(false);
                getPLATOTerminal().getPLATORam().setMode((getPLATOTerminal().getPLATORam().getMode() & ~3) + ascMode[b - ASCII_DC1]);
                decoded = true;
                break;
            case ASCII_2:
                // Load Coordinate
                Log.d(this.getClass().getName(), "Start of load coordinate command.");
                setCurrentAscState(ascState.LOAD_COORDINATES);
                setAscBytes(0);
                decoded = true;
                break;
            case ASCII_AT:
                // Superscript
                Log.d(this.getClass().getName(), "Enable superscript");
                // TODO: IMPLEMENT SUPERSCRIPT
                decoded = true;
                break;
            case ASCII_A:
                // Subscript
                Log.d(this.getClass().getName(), "Enable subscript");
                // TODO: IMPLEMENT SUBSCRIPT
                decoded = true;
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
                getPLATOTerminal().setCurrentCharacterSet(i);
                decoded = true;
                break;
            case ASCII_J:
                Log.d(this.getClass().getName(), "Horizontal Writing Mode");
                getPLATOTerminal().setVerticalWritingMode(false);
                decoded = true;
                break;
            case ASCII_K:
                Log.d(this.getClass().getName(), "Vertical writing mode");
                getPLATOTerminal().setVerticalWritingMode(true);
                decoded = true;
                break;
            case ASCII_L:
                Log.d(this.getClass().getName(), "Forward writing mode");
                getPLATOTerminal().setReverseWritingMode(false);
                decoded = true;
                break;
            case ASCII_M:
                Log.d(this.getClass().getName(), "Reverse writing mode");
                getPLATOTerminal().setReverseWritingMode(true);
                decoded = true;
                break;
            case ASCII_N:
                Log.d(this.getClass().getName(), "Normal size writing mode");
                getPLATOTerminal().setBoldWritingMode(false);
                decoded = true;
                break;
            case ASCII_O:
                Log.d(this.getClass().getName(), "Bold size writing mode");
                getPLATOTerminal().setBoldWritingMode(true);
                decoded = true;
                break;
            case ASCII_P:
                // Implementing as is from pterm, we'll see if this works.
                getPLATOTerminal().setModeXOR(false);
                int mode = ((getPLATOTerminal().getPLATORam().getMode() & 3) + (2 << 2));
                Log.d(this.getClass().getName(), "Load mode " + mode);
                getPLATOTerminal().getPLATORam().setMode(mode);
                decoded = true;
                break;
            case ASCII_Q:
                Log.d(this.getClass().getName(), "Start SSF");
                setCurrentAscState(ascState.SSF);
                setAscBytes(0);
                decoded = true;
                break;
            case ASCII_R:
                Log.d(this.getClass().getName(), "Start ext");
                currentAscState = ascState.LOAD_EXTERNAL;
                setAscBytes(0);
                decoded = true;
                break;
            case ASCII_S:
                platoTerminal.setModeXOR(false);
                mode = ((getPLATOTerminal().getPLATORam().getMode() & 3) + (2 << 2));
                Log.d(this.getClass().getName(), "Load Mode " + mode);
                getPLATOTerminal().getPLATORam().setMode(mode);
                decoded = true;
                break;
            case ASCII_T:
                platoTerminal.setModeXOR(false);
                mode = ((getPLATOTerminal().getPLATORam().getMode() & 3) + (5 << 2));
                Log.d(this.getClass().getName(), "Load Mode " + mode);
                getPLATOTerminal().getPLATORam().setMode(mode);
                decoded = true;
                break;
            case ASCII_U:
                platoTerminal.setModeXOR(false);
                mode = ((getPLATOTerminal().getPLATORam().getMode() & 3) + (6 << 2));
                Log.d(this.getClass().getName(), "Load Mode " + mode);
                getPLATOTerminal().getPLATORam().setMode(mode);
                decoded = true;
                break;
            case ASCII_V:
                platoTerminal.setModeXOR(false);
                mode = ((getPLATOTerminal().getPLATORam().getMode() & 3) + (7 << 2));
                Log.d(this.getClass().getName(), "Load Mode " + mode);
                getPLATOTerminal().getPLATORam().setMode(mode);
                decoded = true;
                break;
            case ASCII_W:
                // Load memory address
                Log.d(this.getClass().getName(), "Start LOAD_ADDRESS");
                currentAscState = ascState.LOAD_ADDRESS;
                ascBytes = 0;
                decoded = true;
                break;
            case ASCII_X:
                Log.d(this.getClass().getName(), "Start Load PLATO Metadata");
                currentAscState = ascState.PMD;
                ascBytes = 0;
                decoded = true;
                break;
            case ASCII_Y:
                Log.d(this.getClass().getName(), "Start LOAD_ECHO");
                currentAscState = ascState.LOAD_ECHO;
                ascBytes = 0;
                decoded = true;
                break;
            case ASCII_Z:
                Log.d(this.getClass().getName(), "Set X margin to " + getPLATOTerminal().getCurrentX());
                getPLATOTerminal().setMargin(getPLATOTerminal().getCurrentX());
                decoded = true;
                break;
            case ASCII_a:
                Log.d(this.getClass().getName(), "Start Foreground color");
                currentAscState = ascState.FG;
                ascBytes = 0;
                decoded = true;
                break;
            case ASCII_b:
                Log.d(this.getClass().getName(), "Start Background color");
                currentAscState = ascState.BG;
                ascBytes = 0;
                decoded = true;
                break;
            case ASCII_c:
                Log.d(this.getClass().getName(), "Start paint");
                currentAscState = ascState.PAINT;
                ascBytes = 0;
                decoded = true;
                break;
            case ASCII_g:
                Log.d(this.getClass().getName(), "Start greyscale foreground color");
                currentAscState = ascState.GSFG;
                ascBytes = 0;
                decoded = true;
                break;
            default:
                setProtocolError("Unknown ESCAPE sequence: " + b);
                decoded = false;
                break;
        }
    }


    private void processPLATOMetaData() {

    }

    /**
     * Assemble a string for use as extended data from PLATO
     * sent in ASCII connection mode.
     */
    private int AssembleASCIIPLATOMetadata(byte b) {
        int ob = b;

        Log.d(this.getClass().getName(), "PLATO Metadata assemble: " + b + " (counter=" + getAscBytes() + 1);
        b &= 0x3F;
        if (getAscBytes() == 0) {
            setPMD("");
        }
        ascBytes++;
        if (ob == 'F' && getAscBytes() == 1)
            setFontPMD(true);
        else if (ob == 'f' && getAscBytes() == 1) {
            setFontInfo(true);
            setAscBytes(0);
            setCurrentAscState(ascState.NONE);
            sendEXT(getFontWidth());
            sendEXT(getFontHeight());
            return 0;
        } else if (ob == 'o' && getAscBytes() == 1) {
            // Sends 3 external keys, OS, major version, minor version
            sendEXT(0);
            sendEXT(0);
            sendEXT(0);
            setOsInfo(true);
            setAscBytes(0);
            setCurrentAscState(ascState.NONE);
        } else if (getFontPMD() && getAscBytes() == 2) {
            // TODO: add call to set font face and family
            if (b == 0) {
                setAscBytes(0);
                setCurrentAscState(ascState.NONE);
                return 0;
            }
        } else if (getFontPMD() && getAscBytes() == 3) {
            // TODO: CALL TO SET FONT SIZE
            Log.d(getClass().getName(), "set font size with getascbytes == 3");
        } else if (getFontPMD() && getAscBytes() == 4) {
            // TODO: CALLS TO SETFONTFLAGS, SETFONTACTIVE
            setAscBytes(0);
            setCurrentAscState(ascState.NONE);
            return 0;
        } else if (b == 0 || getAscBytes() == 1001) {
            if (getAscBytes() == 1001)
                Log.d(getClass().getName(), "PLATO meta data limit reached.");
            setAscBytes(0);
            setCurrentAscState(ascState.NONE);
            return 0;
        } else {
            if (b >= 1 && b <= 26) {
                PMD += Byte.toString((byte) (b + 97 - 1));
            } else if (b >= 27 && b <= 36) {
                PMD += Byte.toString((byte) (48 + b - 27));
            } else if (b == 38) {
                PMD += "-";
            } else if (b == 40) {
                PMD += "/";
            } else if (b == 44) {
                PMD += "=";
            } else if (b == 45) {
                PMD += " ";
            } else if (b == 63) {
                PMD += ";";
            }
        }
        return -1;
    }

    /**
     * Translate and Send extended metadata back to PLATO
     *
     * @param key The "response" to send back.
     */
    private void sendEXT(int key) {
        int[] data = new int[3];

        if (!getPLATOTerminal().getService().isRunning()) {
            Log.d(this.getClass().getName(), "sendEXT called when network service not running.");
            return;
        }
        // Send external key
        data[0] = 0x1B; // ESC
        data[1] = 0x40 | (key & 0x3F);
        data[2] = 0x68 | ((key >> 6) & 0x03);
        for (int i = 0; i < 3; i++) {
            Log.d(this.getClass().getName(), "sendEXT sent byte to PLATO." + data[i]);
            getPLATOTerminal().getService().getToFIFO().add((byte) data[i]);
        }
    }

    private void processGrayscale(int n) {
        if (n != 1) {
            int a = 0xFF;
            int r = (n & 0xFF);
            int g = (n & 0xFF);
            int b = (n & 0xFF);
            int c = (a & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
            if (getCurrentAscState() == ascState.GSFG) {
                Log.d(this.getClass().getName(), "set grayscale foreground color: " + c);
                getPLATOTerminal().setDrawingColorFG(c);
            }
        }
    }

    /**
     * Assemble a 7 bit grayscale word for the ASCII protocol
     *
     * @param b the current byte
     * @return -1 if word not complete yet, otherwise the color word
     */
    private int assembleGrayscale(byte b) {
        if (getAscBytes() == 0) {
            assembler = 0;
        }
        assembler = (b & 0x3F) << 2;
        if (++ascBytes == 1) {
            Log.d(this.getClass().getName(), "Grayscale color: " + assembler);
            return getAssembler();
        } else {
            Log.d(this.getClass().getName(), "Grayscale color byte " + getAscBytes() + " " + (b & 0x7F));
        }
        return -1;
    }

    /**
     * Process an assembled color word
     *
     * @param n a processed color word from assembleColor()
     */
    private void processColor(int n) {
        if (n != -1) {
            int r = (n >> 16) & 0xff;
            int g = (n >> 8) & 0xff;
            int b = (n) & 0xff;
            int c = (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
            Log.d(this.getClass().getName(), "color assembled to r: " + (r & 0xFF) + " g: " + (g & 0xFF) + " b: " + (b & 0xFF));
            if (currentAscState == ascState.FG) {
                Log.d(getClass().getName(), "color Setting foreground to: " + String.format("#%06X", (c)));
                getPLATOTerminal().setDrawingColorFG(c);
            } else {
                Log.d(getClass().getName(), "color Setting background to: " + String.format("#%06X", (c)));
                getPLATOTerminal().setDrawingColorBG(c);
            }
        }
        if (ascBytes == 4) {
            ascBytes = 0;
            currentAscState = ascState.NONE;
        }
    }

    /**
     * Assemble a 24-bit color word
     *
     * @param b current byte of input
     * @return -1 if word not complete yet, otherwise the color word.
     */
    private int assembleColor(byte b) {
        if (ascBytes == 0) {
            assembler = 0;
        }
        assembler |= ((b & 63) << (ascBytes * 6));
        if (++ascBytes == 4) {
            Log.d(this.getClass().getName(), "Assembled colorbyte #" + String.format("%06X", assembler));
            return assembler;
        } else {
            Log.d(this.getClass().getName(), "Assembling colorbyte, ascByte: " + ascBytes + "next byte: " + ascBytes + " " + String.format("%02X", (b & 0x3F)));
        }
        return -1;
    }

    /**
     * Process SSF from assembled data
     *
     * @param n value returned from assembleData()
     */
    private void processSSF(int n) {
        switch (n) {
            case 0x1f00:
                Log.d(this.getClass().getName(), "Start CWS mode " + n);
                setCWSMode(1);
                break;
            case 0x1d00:
                Log.d(this.getClass().getName(), "Stop CWS mode " + n);
                setCWSMode(2);
                break;
            case -1:
                break;
            default:
                Log.d(this.getClass().getName(), "SSF " + n);
//                getPLATOTerminal().activateTouchPanel((n & 0x20) != 0);
                break;
        }
    }

    /**
     * Process External.
     *
     * @param n The assembled external data from AssembleData()
     */
    private void processExt(int n) {
        switch (n) {
            case -1:
                break;
            case EXT_CWS_TERMSAVE:
//                getPLATOTerminal().cwsTermSave();
                break;
            case EXT_CWS_TERMRESTORE:
//                getPLATOTerminal().cwsTermRestore();
                break;
            default:
                processNonCWSExt(getCWSMode(), n);
                break;
        }
    }

    /**
     * Process non CWS ext word
     *
     * @param n The assembled external word from AssembleData()
     */
    private void processNonCWSExt(int cwsmode, int n) {
        Log.d(this.getClass().getName(), "processNonCWSExt - cwsmode: " + cwsmode + " n: " + n);
    }

    /**
     * Assemble an 18-bit data word for the ASCII protocol
     *
     * @param b Current byte of input
     * @return -1 if word not complete, yet, otherwise, the word.
     */
    private int assembleData(byte b) {
        if (getAscBytes() == 0) {
            assembler = 0;
        }
        assembler |= ((b & 63) << (ascBytes * 6));
        if (++ascBytes == 3) {
            ascBytes = 0;
            currentAscState = ascState.NONE;
            Log.d(this.getClass().getName(), "assembleData finished: Final word: " + String.format("%X", assembler));
            return assembler;
        } else {
            Log.d(this.getClass().getName(), "assembleData proceeding: Byte: 0x" + String.format("%02X", b & 63) + " " + String.format("%X", assembler));
        }
        return -1;
    }

    private void processEchoRequest(int n) {
        switch (n) {
            case 0x70:
                n = 0x70 + ASCTYPE;
                Log.d(this.getClass().getName(), "load echo term type: " + n);
                break;
            case 0x71:
                n = 1; // Temporary.
                Log.d(this.getClass().getName(), "load term subtype, returning 1");
                break;
            case 0x72:
                Log.d(this.getClass().getName(), "load echo loadfile (unused)");
                n = 0;
                break;
            case 0x73:
                Log.d(this.getClass().getName(), "load echo termdata");
                n = 0x40; // Terminal with touch panel, 16K
                break;
            case 0x7b:
                // Beep
                break;
            case 0x7d:
                // Report memory address register.
                n = getPLATOTerminal().getPLATORam().getMAR();
                Log.d(this.getClass().getName(), "load Report MAR: " + n);
                break;
            case 0x52:
                // Enable flow control.
                Log.d(this.getClass().getName(), "load Enable Flow Control");
                setFlowControl(true);
                n = 0x53;
                break;
            case 0x60:
                // Enquire features
                Log.d(this.getClass().getName(), "load Report features " + ASCFEATURES);
                n += ASCFEATURES;
                setSendFgt(true);
                break;
            default:
                Log.d(this.getClass().getName(), "load echo fallthrough" + String.format("%02X", n));
        }

        if (n == 0x7b) {
            // Beep does not send a reply.
            Log.d(this.getClass().getName(), "Beep. Not sending reply.");
        }
        n += 0x80;
        Log.d(this.getClass().getName(), "Sending echo key " + String.format("%02X", n));
        sendProcessedKey(n);
        setPendingEcho(-1);
    }

    /**
     * Send a key processed by sendKey() or processEchoRequest()
     *
     * @param n The processed key to send.
     */
    void sendProcessedKey(int n) {
        int[] data = new int[5];
        int len = 1;

        if (getPLATOTerminal().getService() == null || !getPLATOTerminal().getService().isRunning()) {
            // Connection isn't there anymore, just return.
            return;
        }

        Log.d(this.getClass().getName(), "Processed key before conversion: 0x" + String.format("%02X", n));

        if (n < 0x80) {
            // Regular key code.
            n = asciiKeycodes[n];
            if (n == 0xff)
                return;
            if (getFlowControl()) {
                switch (n) {
                    case 0x00:  // ACCESS
                        n = 0x1d;
                        len = 2;
                        break;
                    case 0x05:  // SHIFT-SUB
                        n = 0x04;
                        len = 2;
                        break;
                    case 0x0a:  // TAB
                        n = 0x09;
                        break;
                    case 0x11:  // SHIFT-STOP
                        n = 0x05;
                        break;
                    case 0x17:  // SHIFT-SUPER
                        len = 2;
                        // fall through
                    case 0x7C:
                        n = 0x27;
                        break;
                    case 0x27:
                        n = 0x7C;
                        break;
                }
                data[0] = 0x1B;   // store ESC for two byte codes.
            }
            data[len - 1] = n;
            // There was a parity function here, but since it just maps to x, I pulled it.
            if (len == 1) {
                Log.d(this.getClass().getName(), "Key to PLATO" + (data[0] & 0xff));
            } else {
                Log.d(this.getClass().getName(), "Double key to PLATO " + data[0] + " " + (data[1] & 0xff));
            }
            for (int i = 0; i < len; i++) {
                getPLATOTerminal().getService().getToFIFO().add((byte) data[i]);
            }
        } else if (!isDumbTerminal()) {
            // Ok, the constant referenced here resolves to o01607 (WTF?!)
            if (n == (ASCII_XOFF + 0x80)) {
                if (!getFlowControl()) {
                    return; // Ignore it.
                }
                data[0] = ASCII_XOFF;
                Log.d(this.getClass().getName(), "ASCII mode key to PLATO XOFF in Dumb Terminal Mode.");
            } else if (n == (ASCII_XON + 0x80)) {
                if (!getFlowControl()) {
                    return; // Ignore it.
                }
                data[0] = ASCII_XON + 0x80;
                Log.d(this.getClass().getName(), "ASCII mode key to PLATO XON in dumb terminal mode");
            } else {
                len = 3;
                data[0] = 0x1B;
                data[1] = (0x40 + (n & 0x3F));
                data[2] = (0x60 + (n >> 6));
                Log.d(this.getClass().getName(), "ASCII mode key to PLATO " + (data[0] & 0xFF) + " " + (data[1] & 0xFF) + " " + (data[2] & 0xFF));
            }
            for (int i = 0; i < len; i++) {
                getPLATOTerminal().getService().getToFIFO().add((byte) data[i]);
            }
        } else {
            data[0] = n >> 7;
            data[1] = 0x80 | n;
            len = 2;
            for (int i = 0; i < len; i++) {
                getPLATOTerminal().getService().getToFIFO().add((byte) data[i]);
            }

        }
    }

    /**
     * Assemble 9-bit paint word for ASCII protocol.
     *
     * @param b - current byte.
     * @return -1 if word not complete yet, otherwise, completed word.
     */
    private int assemblePaint(byte b) {
        if (getAscBytes() == 0) {
            assembler = 0;
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
     * @param b byte to pass to modes 0-7
     */
    private void processModes(byte b) {
        switch (getPLATOTerminal().getPLATORam().getMode() >> 2) {
            case 0:  // Dot mode
                Log.d(this.getClass().getName(), "mode0 pre assemblecoordinate");
                if (assembleCoordinate(b)) {
                    mode0((lastCoordinateX << 9) + lastCoordinateY);
                    decoded = true;
                }
                break;
            case 1:  // Line mode
                Log.d(this.getClass().getName(), "mode1 pre assemblecoordinate");
                if (assembleCoordinate(b)) {
                    mode1((lastCoordinateX << 9) + lastCoordinateY);
                    decoded = true;
                }
                break;
            case 2:  // Load Memory (Character sets)
                int n = assembleData(b);
                if (n != -1)
                    mode2(n);
                decoded = true;
                break;
            case 3:  // Text mode
                mode3(b);
                decoded = true;
                break;
            case 4:  // Block erase mode
                Log.d(this.getClass().getName(), "mode4 pre assemblecoordinate");
                if (assembleCoordinate(b)) {
                    modeWords++;
                    mode4((lastCoordinateX << 9) + lastCoordinateY);
                }
                decoded = true;
                break;
            case 5:  // User program mode?
                n = assembleData(b);
                if (n != 1)
                    mode5(n);
                decoded = true;
                break;
            case 6:  // User Program mode
                n = assembleData(b);
                if (n != 1)
                    mode6(n);
                decoded = true;
                break;
            case 7:  // User program mode
                n = assembleData(b);
                if (n != 1)
                    mode7(n);
                decoded = true;
                break;
        }
    }

    /**
     * Process a mode 3 (text output) data word.
     *
     * @param b the byte to process.
     */
    private void mode3(byte b) {
        Log.d(this.getClass().getName(), "out char: " + String.format("%c", b));
        currentAscState = ascState.NONE;
        ascBytes = 0;
        int i = getPLATOTerminal().getCurrentCharacterSet();
        if (i == 0) {
            b = (byte) PLATOFont.asciiM0[b];
            i = (b & 0x80) >> 7;
        } else if (i == 1) {
            b = (byte) PLATOFont.asciiM1[b];
            i = (b & 0x80) >> 7;
        } else {
            b = (byte) ((b - 0x20) & 0x3F);
        }
        if (b != (byte) 0xff) {
            b &= 0x7F;
            getPLATOTerminal().drawChar(getPLATOTerminal().getCurrentX(), getPLATOTerminal().getCurrentY(), i, b, false);
        }
    }

    /**
     * Process mode 7 data word
     *
     * @param n data word
     */
    private void mode7(int n) {
        Log.d(this.getClass().getName(), "mode7: Parameter: " + n);
    }

    /**
     * Process mode 6 data word
     *
     * @param n data word
     */
    private void mode6(int n) {
        Log.d(this.getClass().getName(), "mode6: Parameter: " + n);
    }

    /**
     * Process mode 5 Data word
     *
     * @param n data word
     */
    private void mode5(int n) {
        Log.d(this.getClass().getName(), "mode5: Parameter: " + n);
    }

    /**
     * Process mode 4 (block erase) data word.
     *
     * @param n Data word
     */
    private void mode4(int n) {
        if ((modeWords & 1) > 0) {
            Log.d(this.getClass().getName(), "mode4 block erase, first word.");
            setMode4start(n);
            return;
        }

        int x1 = (mode4start >> 9) & 0x1FF;
        int y1 = mode4start & 0x1FF;
        int x2 = (n >> 9) & 0x1FF;
        int y2 = n & 0x1FF;

        Log.d(this.getClass().getName(), "mode4 block erase X1: " + x1 + " Y1: " + x1 + " X2: " + x2 + " Y2: " + y2);
        // todo: add ignore delay.
        getPLATOTerminal().erase(x1, y1, x2, y2);
        getPLATOTerminal().setCurrentX(x1);
        getPLATOTerminal().setCurrentY(y1 - 15);
    }

    /**
     * Process Mode 2 (load memory) data word. for charsets.
     *
     * @param n Data word
     */
    private void mode2(int n) {
        int chaddr;
        int mar;
        int origin;

        getPLATOTerminal().getPLATORam().writeRAM(getPLATOTerminal().getPLATORam().getMAR(), (byte) (n & 0xFF));
        getPLATOTerminal().getPLATORam().writeRAM(getPLATOTerminal().getPLATORam().getMAR() + 1, (byte) ((n >> 8) & 0xFF));

        // translate PPT ram address to character memory address
        mar = getPLATOTerminal().getPLATORam().getMAR();
        origin = getPLATOTerminal().getPLATORam().readRAMW(PLATORam.C2ORIGIN);
        chaddr = mar - origin;
        if (chaddr < 0 || chaddr > 127 * 16) {
            Log.d(this.getClass().getName(), "mode2 - memdata 0x" + String.format("%04X", n & 0xffff) + " to addr 0x" + String.format("%04X", getPLATOTerminal().getPLATORam().getMAR()));
        } else {
            chaddr /= 2;
            if (((n >> 16) & 3) == 0) {
                // Load data
                if (chaddr < 0x200) {
                    Log.d(this.getClass().getName(), "mode2 - M2 load - character memdata " + String.format("%04X", n & 0xffff) + " to charword: " + String.format("%04X", chaddr));
                    getPLATOTerminal().getPlatoFont().getPlato_m2()[chaddr] = n & 0xFFFF;
                } else {
                    Log.d(this.getClass().getName(), "mode2 - M3 load - character memdata " + String.format("%04X", n & 0xffff) + " to charword: " + String.format("%04X", chaddr));
                    getPLATOTerminal().getPlatoFont().getPlato_m3()[chaddr - 0x0200] = n & 0xFFFF;
                }
                ++chaddr;
            }
        }
        getPLATOTerminal().getPLATORam().setMAR(getPLATOTerminal().getPLATORam().getMAR() + 2);
    }

    /**
     * Process mode 1 (plot line) data word.
     *
     * @param i Data word.
     */
    private void mode1(int i) {
        int x, y;

        x = (i >> 9) & 0x1FF;
        y = i & 0x1FF;
        Log.d(getClass().getName(), "lineto X:" + x + " Y:" + y);
        getPLATOTerminal().plotLine(getPLATOTerminal().getCurrentX(), getPLATOTerminal().getCurrentY(), x, y);
        getPLATOTerminal().setCurrentX(x);
        getPLATOTerminal().setCurrentY(y);
    }

    /**
     * Process mode 0 (plot dot) data word.
     *
     * @param i data word
     */
    private void mode0(int i) {
        int x, y;
        x = (i >> 9) & 0x1FF;
        y = i & 0x1FF;
        Log.d(this.getClass().getName(), "mode 0 plot x: " + x + " y: " + y);
        getPLATOTerminal().drawPoint(x, y);
        getPLATOTerminal().setCurrentX(x);
        getPLATOTerminal().setCurrentY(y);
    }

    /**
     * In coordinate mode, take next byte and assemble into the current set of screen coordinates.
     *
     * @param b the current byte.
     * @return true if coordinate is complete, false if coordinate needs another byte.
     */
    private boolean assembleCoordinate(byte b) {
        int coordinate = b & 31; // Mask off top three bits
        Log.d(this.getClass().getName(), "assembleCoordinate: byte: 0x" + String.format("%02X", b) + " coordinate: 0x" + String.format("%02X", coordinate));
        switch (b >> 5) // Get control bits 6 and 7
        {
            case 1: // High X or High Y
                if (ascBytes == 0) {
                    // High Y coordinate
                    Log.d(this.getClass().getName(), "assembleCoordinate: High Y coordinate " + coordinate);
                    lastCoordinateY = (lastCoordinateY & 31) | (coordinate << 5);
                    ascBytes = 0;
                } else {
                    // High X coordinate
                    Log.d(this.getClass().getName(), "assembleCoordinate: High X coordinate " + coordinate);
                    lastCoordinateX = (lastCoordinateX & 31) | (coordinate << 5);
                }
                break;
            case 2: // Low X
                lastCoordinateX = (lastCoordinateX & 480) | coordinate;
                ascBytes = 0;
                currentAscState = ascState.NONE;
                Log.d(this.getClass().getName(), "assembleCoordinate: Low X coordinate: " + coordinate + " - lastx: " + lastCoordinateX + " - lasty: " + lastCoordinateY);
                return true;
            case 3: // Low Y
                Log.d(this.getClass().getName(), "assembleCoordinate: Low Y coordinate: " + coordinate);
                lastCoordinateY = (lastCoordinateY & 0x1E0) | coordinate;
                ascBytes = 2;
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
            escape = true;
            Log.d(this.getClass().getName(), "Escape detected in Dumb Terminal mode");
            decoded = true;
        } else if (b == ASCII_STX) {
            if (!isEscape()) {
                setProtocolError("STX detected without corresponding escape in dumb terminal mode.");
                decoded = true;
            } else {
                Log.d(this.getClass().getName(), "Proper STX sequence, setting PLATO mode.");
                dumbTerminal = false;
                escape = false;
                setFlowControl(false);
                decoded = true;
            }
        } else if (b == ASCII_ETX) {
            if (!isEscape()) {
                setProtocolError("ETX detected without corresponding escape in dumb terminal mode.");
                decoded = true;
            } else {
                setProtocolError("ETX detected while in dumb terminal mode. Ignoring.");
                escape = false;
                decoded = true;
            }
        } else if (b == ASCII_CR) {
            getPLATOTerminal().setCurrentX(getPLATOTerminal().getMargin()); // Beginning of line.
            decoded = true;
        } else if (b == ASCII_LF) {
            if (getPLATOTerminal().getCurrentY() != 0)
                getPLATOTerminal().setCurrentY(getPLATOTerminal().getCurrentY() - 16);
            else {
                getPLATOTerminal().scrollUp();
            }
            decoded = true;
        } else if (b >= 32 && b < 127) // Printable character.
        {
            // TODO: Properly decode upper case.
            int charToPlot = PLATOFont.asciiM0[b];
            if (charToPlot != 0xff) {
                int charsetToUse = (b & 0x80) >> 7;
                charToPlot &= 0x7F;
                decoded = true;
                getPLATOTerminal().drawChar(getPLATOTerminal().getCurrentX(), getPLATOTerminal().getCurrentY(), charsetToUse, charToPlot, false);

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

    private int getAssembler() {
        return assembler;
    }

    private boolean getFlowControl() {
        return flowControl;
    }

    private void setFlowControl(boolean flowControl) {
        this.flowControl = flowControl;
    }

    public boolean isSendFgt() {
        return sendFgt;
    }

    private void setSendFgt(boolean sendFgt) {
        this.sendFgt = sendFgt;
    }

    private int getPendingEcho() {
        return pendingEcho;
    }

    private void setPendingEcho(int pendingEcho) {
        this.pendingEcho = pendingEcho;
    }

    private int getCWSMode() {
        return CWSMode;
    }

    private void setCWSMode(int CWSMode) {
        this.CWSMode = CWSMode;
    }

    private int getCWScnt() {
        return CWScnt;
    }

    private void setCWScnt(int CWScnt) {
        this.CWScnt = CWScnt;
    }

    public int getCWSfun() {
        return CWSfun;
    }

    private void setCWSfun(int CWSfun) {
        this.CWSfun = CWSfun;
    }

    private boolean getFontPMD() {
        return fontPMD;
    }

    public boolean isFontPMD() {
        return fontPMD;
    }

    private void setFontPMD(boolean fontPMD) {
        this.fontPMD = fontPMD;
    }

    private boolean getFontInfo() {
        return fontInfo;
    }

    public boolean isFontInfo() {
        return fontInfo;
    }

    private void setFontInfo(boolean fontInfo) {
        this.fontInfo = fontInfo;
    }

    private boolean getosInfo() {
        return osInfo;
    }

    public boolean isOsInfo() {
        return osInfo;
    }

    private void setOsInfo(boolean osInfo) {
        this.osInfo = osInfo;
    }

    public String getPMD() {
        return PMD;
    }

    private void setPMD(String PMD) {
        this.PMD = PMD;
    }

    private int getFontWidth() {
        return fontWidth;
    }

    public void setFontWidth(int fontWidth) {
        this.fontWidth = fontWidth;
    }

    private int getFontHeight() {
        return fontHeight;
    }

    public void setFontHeight(int fontHeight) {
        this.fontHeight = fontHeight;
    }

    public int getMode4start() {
        return mode4start;
    }

    private void setMode4start(int mode4start) {
        this.mode4start = mode4start;
    }

    private enum ascState {SSF, LOAD_EXTERNAL, LOAD_ADDRESS, PMD, LOAD_ECHO, FG, BG, PAINT, GSFG, NONE, PNI_RS, LOAD_COORDINATES}

}

