/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

/**
 * A class to handle key mappings between PLATO and ASCII
 */

public class PLATOKey {

    private static int[] asciiKeyToPLATOTable = {
            0xFF,       // ASCII NUL


    };

    /**
     * Private constructor as everything here is static.
     */
    private PLATOKey() {

    }

    public static int getASCIIKeyToPLATO(int key) {
        return asciiKeyToPLATOTable[key];
    }
}
