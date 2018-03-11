/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

/**
 * A Class to represent internal processor memory of a PLATO V terminal
 * Created by thomc on 2/18/2018.
 */

class PLATORam {

    static final int M2ADDR = 0x2340;
    static final int M3ADDR = 0x2740;
    private static final int MODE = 0x22F6;
    private static final int WORK_RAM = 0x2000;
    static final int C2ORIGIN = WORK_RAM + 0x0306;
    static final int C3ORIGIN = WORK_RAM + 0x0308;
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

    void setMAR(int newMAR) {
        this.MAR = newMAR;
    }

    /**
     * Write a word to the following addr and addr+1
     *
     * @param addr The address in RAM to write
     * @param n    the byte to write.
     */
    void writeRAM(int addr, byte n) {
        ram[addr] = (n & 0xFF);
    }

    /**
     * Read byte from terminal RAM
     *
     * @param addr Address in PPT address space to read.
     * @return byte at that address.
     */
    public byte readRAM(int addr) {
        return (byte) (ram[addr] & 0xFF);
    }

    /**
     * Read word from terminal RAM
     *
     * @param addr Address in PPT address space to read.
     * @return the word at address and adddress + 1
     */
    short readRAMW(int addr) {
        return (short) ((ram[addr] & 0xFF) + (ram[addr + 1] << 8));
    }

}
