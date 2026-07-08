# tickle

一个想把「记一笔」压到 3 秒以内的 Android 记账小工具。

我做它，是因为很多记账 App 太完整了。完整当然好，但每天真正要记账的时候，我常常只是想留下这一笔：刚刚吃饭花了多少钱、打车花了多少钱、今天有没有收入。打开 App、点好几层、填一堆字段，这件事很容易就断掉。

tickle 现在想先把一件事做好：**少打字，少切页面，能在桌面小组件里完成，就不要再进 App。**

> 目前还是早期版本，适合试用和小范围测试。截图和重新画过的教程图还在整理，后面会补上。

## 先下载试试

[下载 APK：tickle-3s-mainline-debug.apk](https://github.com/wangdachui886/tickle-3s-/raw/main/releases/tickle-3s-mainline-debug.apk)

这个包是 debug 体验版，不是应用商店的正式签名包。Android 安装时可能会提示「未知来源」或风险提醒，这是因为还没有上架商店。

当前包名：`com.lightledger.app`

## 现在能做什么

- 在 App 里快速记一笔支出或收入。
- 选择历史日期，补记前几天的账。
- 管理常用分类，也可以加自己的分类。
- 在流水页按日期看记录，支持编辑和删除。
- 在统计页看日、月、年的支出或收入。
- 导出 CSV，也可以从最近一次导出的 CSV 恢复。
- 添加桌面小组件，直接在桌面上记账。

小组件现在有 6 个版本：

- 4x2 深色 / 浅色
- 4x3 深色 / 浅色
- 4x4 深色 / 浅色

小组件支持金额按钮、分类按钮、支出/收入切换、保存、撤销，也会在跨天后刷新当天汇总。

## 暂时不做什么

目前没有账号、没有云同步、没有统计 SDK，也不会读通知、截屏或做 OCR。

之前试过更自动化的方向，但可靠性不够好。记账这种东西，一旦误记、漏记，用户很快就不会相信它。所以现在先把手动和小组件这条路做稳，再考虑后面的自动化。

数据默认留在手机本地。CSV 导出和恢复都由用户自己点，文件放在：

```text
Download/tickle
```

CSV 字段现在很少：`date`、`direction`、`amount`、`unit`、`type`、`note`。详细说明在 [docs/DATA_EXPORT.md](docs/DATA_EXPORT.md)。

## 开发

用 Android Studio 打开项目根目录即可。

这台机器上的本地开发环境示例：

```powershell
$env:JAVA_HOME='D:\Andriod Studio\jbr'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
```

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

更多结构说明见 [docs/PROJECT_STRUCTURE.md](docs/PROJECT_STRUCTURE.md)，发布前检查见 [docs/RELEASE_CHECKLIST.md](docs/RELEASE_CHECKLIST.md)。

## 版本

- `mainline`：`com.lightledger.app`，应用名 `tickle`
- `friends`：`com.lightledger.app.friends`，应用名 `tickle beta`

如果以后要上架商店，还需要重新做签名包、版本号、截图和隐私文案。

## 目录说明

- `app/`：Android 源码。
- `docs/`：数据格式、项目结构、发布检查清单。
- `releases/`：公开下载用 APK。
- `dist/`：本地构建出的测试包，不作为源码维护。
- `artifacts/`：本地截图、UI dump、旧实验材料，不进公开仓库。

## License

Apache License 2.0.
