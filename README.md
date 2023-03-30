# APKLoader

此工程用于演示宿主 APK 动态加载一个插件 APK 并升级的流程。

此工程包含三个模块 ：  

```
/APKLoader
├── app  // 模拟用户 app，会启动一个 Service，该 Service 会调用 pluginManager 进行动态加载 apk
├── pluginAPK // 模拟插件 apk，内部是一个简单的 Service
└── pluginManager // 通过 DexClassLoader 加载 pluginAPK 中的 Service 
```

### 加载插件 APK 中的 Service
原理是通过 DexClassLoader 来加载一个存于应用内部空间下的 APK 中的一个类（PluginService），然后再通过反射把该类给实例化，此时你可以得到一个 pluginAPK 中的 Service，最后在宿主 APP 中调用该 Service 的方法即可。

### 加载插件 APK 中的 so 文件
如果加载的插件 APK 中的类调用了 Native 的 c++ 方法，那么也需要加载相应的 so 文件。  
仍然是通过 DexClassLoader 把 so 文件加载进来。但是 so 文件是存在于 apk 中的，所以需要把 apk 解压缩到宿主应用下的一个内部文件夹中。然后通过获取设备 cpu 架构名称，从而判断其 so 的查找路径是什么（例如 ：/data/user/0/com.example.apkloader/app_unzip/lib/arm64-v8a/）。最后把 so 的查找路径传入 DexClassLoader 即可进行 so 文件的加载。

### APK 更新
原理是启动时发请求检测是否有新的 APK，如果有则开启一个线程来下载该 APK，待下载完成之后，通知主 APP 来重新启动，从而加载新的 APK。  
如果检测到没有待更新的 APK，则加载原始的 APK（在 assert 文件夹下）。



