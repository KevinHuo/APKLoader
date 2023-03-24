package com.example.pluginmanager;


import android.content.Intent;

public interface PluginInterface {
    void onCreate();

    void onDestroy();

    int onStartCommand(Intent intent, int flags, int startId);
}
