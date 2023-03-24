package com.example.pluginapk;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class PluginService extends Service implements PluginInterface {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("PluginService", "hk ------ PluginService onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("PluginService", "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
