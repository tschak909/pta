/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;


import android.support.v4.util.CircularArray;
import android.util.Log;

/**
 * Created by thomc on 2/21/2018.
 * Process protocol input and output streams.
 */

class PLATOProtocol {
    private CircularArray<Byte> fromFIFO;
    private CircularArray<Byte> toFIFO;

    /**
     * Construct PLATOProtocol instance
     *
     * @param fromFIFO the from FIFO from the PLATONetworkService
     * @param toFIFO   the to FIFO from the PLATONetworkService
     */
    PLATOProtocol(CircularArray<Byte> fromFIFO, CircularArray<Byte> toFIFO) {
        this.fromFIFO = fromFIFO;
        this.toFIFO = toFIFO;
    }

    void processInput() {
        if (!fromFIFO.isEmpty()) {
            for (int i = 0; i < fromFIFO.size(); i++) {
                processNextInputByte(fromFIFO.popLast());
            }
        }
    }

    void processOutput() {
        if (!toFIFO.isEmpty()) {
            for (int i = 0; i < toFIFO.size(); i++) {
                processNextOutputByte(fromFIFO.popLast());
            }
        }
    }

    private void processNextOutputByte(Byte aByte) {
        Log.i("PLATOTerm", "Sent output byte: " + aByte);
    }

    private void processNextInputByte(Byte aByte) {
        Log.i("PLATOTerm", "Received input byte: " + aByte);
    }
}
