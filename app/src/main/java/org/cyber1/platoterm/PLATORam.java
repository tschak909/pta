/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

/**
 * A Class to represent internal processor memory of a PLATO V terminal
 * Created by thomc on 2/18/2018.
 */

class PLATORam {

    private static final int MODE = 0x22F6;
    private int[] ram;

    PLATORam() {
        ram = new int[0xD000];
    }

    int getMode() {
        return getRam()[MODE];
    }

    int getWeMode() {
        return getMode() & 3;
    }

    private int[] getRam() {
        return ram;
    }

}
