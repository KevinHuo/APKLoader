package com.example.apkloader;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.pluginmanager.PluginInterface;
import com.example.pluginmanager.PluginManager;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class LoaderService extends Service implements PluginInterface {
    private PluginInterface pluginService;
    private static final String APK_URL = "https://sdk-release.qnsdk.com/pluginapk.apk";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        String className = "com.example.pluginapk.PluginService";
        try {
            File apkFile;
            String updatedPath = getDir("patch", Context.MODE_PRIVATE) + "/pluginapk.apk";
            File updatedFile = new File(updatedPath);
            if (updatedFile.exists()) {
                apkFile = updatedFile;
            } else {
                File originalDir = getDir("original", Context.MODE_PRIVATE);
                apkFile = new File(originalDir, "pluginapk.apk");
                readDataFromAssets(apkFile);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        update();
                    }
                }).start();
            }
            PluginManager.getInstance().setContext(this);
            PluginManager.getInstance().loadAPK(apkFile.getAbsolutePath());
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
        }
    }

    private boolean readDataFromAssets(File apkFile) {
        InputStream inputStream = null;
        BufferedOutputStream bos = null;

        if (!apkFile.getParentFile().exists()) {
            apkFile.getParentFile().mkdirs();
        }
        try {
            inputStream = getAssets().open(apkFile.getName());
            bos = new BufferedOutputStream(new FileOutputStream(apkFile));
            byte[] bytes = new byte[1024];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
            inputStream.close();
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void onDestroy() {
        pluginService.onDestroy();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return pluginService.onStartCommand(intent, flags, startId);
    }

    public void update() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(APK_URL).build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                String apkSavePath = getDir("patch", MODE_PRIVATE).getAbsolutePath() + "/pluginapk.apk";
                File file = new File(apkSavePath);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                }
                InputStream inputStream = response.body().byteStream();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] bytes = new byte[4 * 1024];
                int len;
                while ((len = inputStream.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, 0, len);
                }
                inputStream.close();
                fileOutputStream.flush();
                fileOutputStream.close();

                // 下载完成，通知 APP 重新启动，下次可以加载新版本的 APK
            }
        } catch (Exception e) {
        }
    }
}
