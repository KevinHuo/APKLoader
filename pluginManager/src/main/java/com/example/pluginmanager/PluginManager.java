package com.example.pluginmanager;

import android.content.Context;
import android.os.Build;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dalvik.system.DexClassLoader;

public class PluginManager {
    private static PluginManager instance;
    private Context context;
    private DexClassLoader dexClassLoader;

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
        String unzipDir = context.getDir("unzip", Context.MODE_PRIVATE) + "/";
        try {
            // 需要把 apk 解压到一个文件夹，目的是加载 apk 中的 so 文件
            unzip(new File(dexPath), new File(unzipDir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 获取 device 的架构名称
        String archName = getArch();
        // 根据架构名称得到应该加载的 so 的路径 例如：/data/user/0/com.example.apkloader/app_unzip/lib/arm64-v8a/
        String soPath = unzipDir + "lib/" + archName + "/";
        dexClassLoader = new DexClassLoader(dexPath, context.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath(), soPath, context.getClassLoader());
    }

    public DexClassLoader getDexClassLoader() {
        return dexClassLoader;
    }

    public static void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            }
        } finally {
            zis.close();
        }
    }

    public static String getArch() {
        String arch = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String[] supportedAbis = Build.SUPPORTED_ABIS;
            if (supportedAbis != null && supportedAbis.length > 0) {
                arch = supportedAbis[0];
            }
        } else {
            arch = Build.CPU_ABI;
        }
        return arch;
    }
}
