package com.example.pluginmanager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import dalvik.system.DexClassLoader;

public class PluginManager {
    private static PluginManager instance;
    private Context context;
    private PackageInfo packageInfo;
    private DexClassLoader dexClassLoader;
    private Resources resources;

    private PluginManager() {
    }

    public static PluginManager getInstance() {
        if (instance == null) {
            synchronized (PluginManager.class) {
                if (instance == null) {
                    instance = new PluginManager();
                }
            }
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public void loadAPK(String dexPath) {
        dexClassLoader = new DexClassLoader(dexPath, context.getDir("dex2opt", Context.MODE_PRIVATE).getAbsolutePath(), null, context.getClassLoader());
        packageInfo = context.getPackageManager().getPackageArchiveInfo(dexPath, PackageManager.GET_ACTIVITIES);
    }

    public DexClassLoader getDexClassLoader() {
        return dexClassLoader;
    }

    public Resources getResources() {
        return resources;
    }
}
