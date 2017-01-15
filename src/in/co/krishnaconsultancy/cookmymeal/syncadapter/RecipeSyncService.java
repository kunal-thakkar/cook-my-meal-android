package in.co.krishnaconsultancy.cookmymeal.syncadapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RecipeSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static RecipeSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) sSyncAdapter = new RecipeSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}