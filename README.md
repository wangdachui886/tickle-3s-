# tickle 3 秒记账

tickle 是一个面向中文用户的本地优先 Android 记账 App，主打「3 秒快速手动记账」和「桌面小组件直接记一笔」。

这个仓库是第一版公开源码快照。截图和最终版教程图还在整理中，后续会补到 README 和 `docs/` 里。

## 下载体验

当前可下载的是 mainline debug 体验包：

[下载 tickle-3s-mainline-debug.apk](https://github.com/wangdachui886/tickle-3s-/raw/main/releases/tickle-3s-mainline-debug.apk)

说明：

- 这是用于体验和小范围测试的 debug APK，不是应用商店签名 release。
- Android 安装时可能需要允许「安装未知来源应用」。
- 当前包名是 `com.lightledger.app`，应用名是 `tickle`。

## 产品方向

- 手动记账优先，不依赖截图识别、OCR、通知读取或后台自动抓取。
- 桌面小组件是核心入口：打开手机桌面就能快速记一笔。
- 数据默认只保存在本机 Room 数据库。
- 备份和恢复使用普通 CSV 文件，路径为 `Download/tickle`。
- 界面方向克制、黑白、轻量，优先保证触控效率。

## 当前功能

- App 内快速记账，支持支出/收入切换。
- 支持选择历史日期，方便补记。
- 支持常用分类、自定义分类和分类管理。
- 流水按月份和日期分组，支持筛选、编辑和删除。
- 统计页支持日/月/年视角，并明确区分支出和收入。
- 支持 CSV 导出和从最新导出文件恢复。
- 支持 6 个桌面小组件：
  - 4x2 深色
  - 4x2 浅色
  - 4x3 深色
  - 4x3 浅色
  - 4x4 深色
  - 4x4 浅色
- 小组件支持直接输入、撤销、支出/收入切换和跨天刷新。

## 隐私说明

当前版本没有账号系统、远程后端、统计 SDK、网络接口、截图读取、OCR 或通知读取。记账数据存储在本机，CSV 导出/恢复由用户主动触发。

## 数据导出

CSV 文件导出到：

```text
Download/tickle
```

每次导出会生成一份交易 CSV，字段包括 `date`、`direction`、`amount`、`unit`、`type` 和 `note`。具体字段见 `docs/DATA_EXPORT.md`。

## 本地开发

用 Android Studio 打开项目根目录：

```text
E:\AppDev\LightLedger
```

本机开发环境示例：

```powershell
$env:JAVA_HOME='D:\Andriod Studio\jbr'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
```

构建普通版本：

```powershell
.\gradlew.bat :app:assembleMainlineDebug
```

构建朋友测试版，不覆盖普通版本：

```powershell
.\gradlew.bat :app:assembleFriendsDebug
```

运行单元测试：

```powershell
.\gradlew.bat :app:testMainlineDebugUnitTest
.\gradlew.bat :app:testFriendsDebugUnitTest
```

本地 debug APK 会复制到：

```text
dist\tickle-mainline-debug.apk
dist\tickle-friends-debug.apk
```

## 版本说明

当前 debug flavors：

- `mainline`: `com.lightledger.app`，应用名 `tickle`
- `friends`: `com.lightledger.app.friends`，应用名 `tickle beta`

正式上架前还需要签名 release 包、更新 `versionCode` / `versionName`、准备商店截图和隐私文案，并按 `docs/RELEASE_CHECKLIST.md` 做完整回归。

## License

Apache License 2.0.

## 本地目录说明

- `dist/`：本地构建出的测试 APK，不直接作为源码目录维护。
- `artifacts/`：本地截图、UI dump、测试导出、旧实验材料等，不进入公开仓库。
- `releases/`：公开下载用 APK。后续有正式签名 release 后，会优先使用 GitHub Releases。

除明确放入 `releases/` 的公开 APK 外，其他构建产物、私有样本和过程材料都不进入仓库。
