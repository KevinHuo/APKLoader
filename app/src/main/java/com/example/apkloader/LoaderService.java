package com.example.apkloader;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.pluginlibrary.PluginInterface;
import com.example.pluginlibrary.PluginManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class LoaderService extends Service {
    PluginInterface pluginService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("LoaderService", "hk ------ onCreate");

        String className = "PluginService";
        try {
            PluginManager.getInstance().setContext(this);
            File file = getDir("patch", Context.MODE_PRIVATE);
            File pluginFile = new File(file,"pluginapk.apk");
            FileWriter fileWriter = new FileWriter(pluginFile);

            PluginManager.getInstance().loadAPK("/storage/emulated/0/pluginapk.apk");
            Class<?> serviceClass = PluginManager.getInstance().getDexClassLoader().loadClass(className);
            Object newInstance = serviceClass.newInstance();
            if (newInstance instanceof PluginInterface) {
                pluginService = (PluginInterface) newInstance;
                pluginService.onCreate();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroy() {
        pluginService.onDestroy();
        super.onDestroy();
        Log.i("LoaderService", "onDestroy");
    }
}
