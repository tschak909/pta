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
    private static final String DEFAULT_HOST = "cyberserv.org";
    private static final int PROTOCOL_MODE_ASCII = 8005;
    private final IBinder mBinder = new PLATONetworkBinder();
    private InputStream is;
    private OutputStream os;
    private Socket mSocket;
    private CircularArray<Byte> fromFIFO;   // Data FROM PLATO
    private CircularArray<Byte> toFIFO;     // Data TO PLATO
    private boolean mRunning = false;

    private Runnable serviceThread = new Runnable() {

        @Override
        public void run() {
            start();
        }
    };

    public PLATONetworkService() {
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

    public CircularArray<Byte> getToFIFO() {
        return toFIFO;
    }

    public void setToFIFO(CircularArray<Byte> toFIFO) {
        this.toFIFO = toFIFO;
    }

    public CircularArray<Byte> getFromFIFO() {
        return fromFIFO;
    }

    public void setFromFIFO(CircularArray<Byte> fromFIFO) {
        this.fromFIFO = fromFIFO;
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
        setFromFIFO(new CircularArray<Byte>(BUFFER_SIZE));
        setToFIFO(new CircularArray<Byte>(BUFFER_SIZE));
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
            setSocket(new Socket(host, ProtocolMode));
            setIs(getSocket().getInputStream());
            setOs(getSocket().getOutputStream());
        } catch (IOException e) {
            Log.e("PLATOActivity", "TCP Error: ", e);
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
                // Fill up the input FIFO
                if (getIs().available() > 0) {
                    getFromFIFO().addFirst((byte) is.read());
                }

                // Drain the output FIFO
                if (!getToFIFO().isEmpty()) {
                    for (int i = 0; i < getToFIFO().size(); i++) {
                        getOs().write(getToFIFO().popLast());
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
        PLATONetworkService getService() {
            return PLATONetworkService.this;
        }
    }
}
