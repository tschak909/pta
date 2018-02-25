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
    private int MAR = 0;

    PLATORam() {
        ram = new int[0xD000];
    }

    int getMode() {
        return (getRam()[MODE] & 0xFF);
    }

    void setMode(int mode) {
        getRam()[MODE] = mode;
    }

    int getWeMode() {
        return getMode() & 3;
    }

    private int[] getRam() {
        return ram;
    }

    int getMAR() {
        return MAR;
    }

    public void setMAR(int newMAR) {
        this.MAR = newMAR;
    }
}
