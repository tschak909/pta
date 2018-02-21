/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.CircularArray;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class PLATONetworkService extends Service {
    private static final int BUFFER_SIZE = 8192;
    private final IBinder mBinder = new PLATONetworkBinder();
    private InputStream is;
    private OutputStream os;
    private Socket mSocket;
    private CircularArray<Byte> fifo;
    private boolean mRunning = false;
    private Runnable serviceThread = new Runnable() {

        @Override
        public void run() {
            while (!isRunning()) {
                start();
            }
        }
    };

    public PLATONetworkService() {
    }

    private CircularArray<Byte> getFifo() {
        return fifo;
    }

    private void setFifo(CircularArray<Byte> fifo) {
        this.fifo = fifo;
    }

    public boolean isRunning() {
        return mRunning;
    }

    public void setRunning(boolean mRunning) {
        this.mRunning = mRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fifo = new CircularArray<Byte>(8192);
        new Thread(serviceThread).start();
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("PLATOTerm")
                .setContentText("Connected to CYBER1")
                .build();
        startForeground(8005, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void connectToPLATO(String host, int ProtocolMode) {
        try {
            mSocket = new Socket(host, ProtocolMode);
            is = mSocket.getInputStream();
            os = mSocket.getOutputStream();
        } catch (IOException e) {
            Log.e("PLATOTerm", "TCP Error: ", e);
        }

        doIO();

    }

    /**
     * Process data to and from socket, until we are told to disconnect.
     */
    private void doIO() {
        setRunning(true);
        while (isRunning()) {
            try {
                if (is.available() > 0) {
                    getFifo().addLast((byte) is.read());
                }
                // Silly, but looking to see if the service is behaving.
                if (!getFifo().isEmpty()) {
                    Log.i("PlatoTERM", "Received: " + String.valueOf(getFifo().popLast()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        connectToPLATO("cyberserv.org", 8005);
    }

    public void disconnectFromPLATO() {
        setRunning(false);
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class PLATONetworkBinder extends Binder {
        PLATONetworkService getService() {
            return PLATONetworkService.this;
        }
    }

}
