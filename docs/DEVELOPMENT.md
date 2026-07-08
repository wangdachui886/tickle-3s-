# 开发说明

用 Android Studio 打开项目根目录即可。

如果命令行找不到 JDK 或 Android SDK，请在本机环境中配置 `JAVA_HOME`、`ANDROID_HOME` 或 Android Studio 自带的 Gradle/JDK 设置。不要把 `local.properties` 提交到仓库。

构建普通版：

```powershell
.\gradlew.bat :app:assembleMainlineDebug
```

构建朋友测试版，不覆盖普通版：

```powershell
.\gradlew.bat :app:assembleFriendsDebug
```

运行单元测试：

```powershell
.\gradlew.bat :app:testMainlineDebugUnitTest
.\gradlew.bat :app:testFriendsDebugUnitTest
```
