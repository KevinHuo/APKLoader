package com.example.pluginapk;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.pluginmanager.PluginInterface;

public class PluginService extends Service implements PluginInterface {
    static {
        System.loadLibrary("pluginapk");
    }

    @Override
    public void onCreate() {
        // 打印出需要更新的 apk 版本号
        Log.i("PluginService", stringFromJNI());
    }

    @Override
    public void onDestroy() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public native String stringFromJNI();
}
