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
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PLATOService extends Service {
    public static final int BUFFER_SIZE = 3000;
    public static final int BUFFER_SIZE_ON = BUFFER_SIZE / 8;
    private static final String DEFAULT_HOST = "irata.online";
    private static final int PROTOCOL_MODE_ASCII = 8005;
    private final IBinder mBinder = new PLATONetworkBinder();
    private boolean flowStopped = false;
    private InputStream is;
    private OutputStream os;
    private Socket mSocket;
    private ConcurrentLinkedQueue<Byte> fromFIFO;   // Data FROM PLATO
    private ConcurrentLinkedQueue<Byte> toFIFO;     // Data TO PLATO
    private boolean mRunning = false;

    private Runnable serviceThread = new Runnable() {

        @Override
        public void run() {
            start();
        }
    };

    private PLATOTerminal platoTerminal;

    public PLATOService() {
        platoTerminal = new PLATOTerminal(this);
    }

    public InputStream getIs() {
        return is;
    }

    public void setIs(InputStream is) {
        this.is = is;
    }

    public OutputStream getOs() {
        return os;
    }

    public void setOs(OutputStream os) {
        this.os = os;
    }

    public Socket getSocket() {
        return mSocket;
    }

    public void setSocket(Socket mSocket) {
        this.mSocket = mSocket;
    }

    public ConcurrentLinkedQueue<Byte> getToFIFO() {
        return toFIFO;
    }

    public void setToFIFO(ConcurrentLinkedQueue<Byte> toFIFO) {
        this.toFIFO = toFIFO;
    }

    public ConcurrentLinkedQueue<Byte> getFromFIFO() {
        return fromFIFO;
    }

    public void setFromFIFO(ConcurrentLinkedQueue<Byte> fromFIFO) {
        this.fromFIFO = fromFIFO;
    }

    public boolean isRunning() {
        return mRunning;
    }

    public void setRunning(boolean mRunning) {
        this.mRunning = mRunning;
    }

    /**
     * Return PLATOTerminal attached to this service
     *
     * @return the PLATOTerminal attached to this service
     */
    public PLATOTerminal getPlatoTerminal() {
        return platoTerminal;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setFromFIFO(new ConcurrentLinkedQueue<Byte>());
        setToFIFO(new ConcurrentLinkedQueue<Byte>());
        new Thread(serviceThread).start();
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("PLATOActivity")
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
            // Somehow, the very first item in the fifo doesn't make it there
            // So I put nulls in.
            getFromFIFO().clear();
            getToFIFO().clear();
            getFromFIFO().add((byte) 0x00);
            getToFIFO().add((byte) 0x00);

            // Wait for 5 seconds, before connecting.
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            setSocket(new Socket(host, ProtocolMode));
            setIs(getSocket().getInputStream());
            setOs(getSocket().getOutputStream());
        } catch (IOException e) {
            Log.e("PLATOActivity", "TCP Error: ", e);
        }

        platoTerminal.start();
        doIO();

    }

    /**
     * Process data to and from socket, until we are told to disconnect.
     */
    private void doIO() {
        byte b = 0;
        byte lastb = 0;
        setRunning(true);
        while (isRunning()) {
            try {
                // Fill up the input FIFO
                if (getIs().available() > 0) {
                    b = (byte) (is.read());
                    if (b == (byte) -0x01 && lastb == (byte) -0x01) {
                        Log.d(this.getClass().getName(), "Dropping extra 0xff.");
                        lastb = 0x00;
                    } else
                    {
                        getFromFIFO().add((byte) (b & 0x7F));
                        lastb = b;
                    }
                }

                // Deal with Flow Control
                if (!flowStopped && (getFromFIFO().size() > BUFFER_SIZE)) {
                    Log.d(this.getClass().getName(), "Flow Control OFF");
                    getToFIFO().add((byte) 0x13);
                    flowStopped = true;
                } else if (flowStopped && (getFromFIFO().size() < BUFFER_SIZE_ON)) {
                    Log.d(this.getClass().getName(), "Flow Control ON");
                    getToFIFO().add((byte) 0x11);
                    flowStopped = false;
                }

                // Drain the output FIFO
                if (!getToFIFO().isEmpty()) {
                    for (int i = 0; i < getToFIFO().size(); i++) {
                        b = getToFIFO().poll();
                        getOs().write(b);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        connectToPLATO(DEFAULT_HOST, PROTOCOL_MODE_ASCII);
    }

    public void disconnectFromPLATO() {
        setRunning(false);
        fromFIFO.clear();
        toFIFO.clear();
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Try to make this service stick around
     *
     * @param intent  The intent that started it
     * @param flags   Special flags passed in when starting service
     * @param startId The given ID for the service
     * @return START_STICKY to make it stick.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    class PLATONetworkBinder extends Binder {
        PLATOService getService() {
            return PLATOService.this;
        }
    }
}
