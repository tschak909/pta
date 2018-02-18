/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

/**
 * A Class to represent internal processor memory of a PLATO V terminal
 * Created by thomc on 2/18/2018.
 */

public class PlatoRAM {

    public static final int MODE = 0x22F6;
    private int[] ram;

    public PlatoRAM() {
        ram = new int[0xD000];
    }

    public int getMode() {
        return getRam()[MODE];
    }

    public int getWeMode() {
        return getMode() & 3;
    }

    public int[] getRam() {
        return ram;
    }

    public void setRam(int[] ram) {
        this.ram = ram;
    }

}
