# APKLoader

此工程用于演示宿主 APK 动态加载一个插件 APK 并升级的流程。

此工程包含三个模块 ：  

```
/APKLoader
├── app  // 模拟用户 app，会启动一个 Service，该 Service 会调用 pluginManager 进行动态加载 apk
├── pluginAPK // 模拟插件 apk，内部是一个简单的 Service
└── pluginManager // 通过 DexClassLoader 加载 pluginAPK 中的 Service 
```

### APK 加载
原理是通过 DexClassLoader 来加载一个存于应用内部空间下的 APK 中的一个类（PluginService），然后再通过反射把该类给实例化，此时你可以得到一个 pluginAPK 中的 Service，最后在宿主 APP 中调用该 Service 的方法即可。

### APK 更新
原理是启动时发请求检测是否有新的 APK，如果有则开启一个线程来下载该 APK，待下载完成之后，通知主 APP 来重新启动，从而加载新的 APK。如果检测到没有待更新的 APK，则加载原始的 APK（在 assert 文件夹下）。

### TODO
目前可以实现动态加载 APK 中的 java 文件，但 APK 中的 so 文件暂未跑通，后续会加上此部分逻辑。


