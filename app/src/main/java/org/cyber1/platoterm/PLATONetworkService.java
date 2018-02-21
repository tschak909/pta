/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PLATONetworkService extends Service {
    public PLATONetworkService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
