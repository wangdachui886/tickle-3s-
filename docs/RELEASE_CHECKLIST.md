# 发布检查清单

这份清单先服务于小范围体验版，后面如果要上架应用商店，再补正式签名、隐私政策和商店素材。

## 每次发 APK 前

- 构建目标版本：

```powershell
.\gradlew.bat :app:assembleMainlineDebug
```

- 尽量在干净设备或备用设备上安装。
- 首次打开 App，确认教程和主界面显示正常。
- 分别添加当前包含的桌面小组件：
  - 4x2 深色
  - 4x2 浅色
  - 4x3 深色
  - 4x3 浅色
  - 4x4 深色
  - 4x4 浅色
- 从小组件记一笔支出和一笔收入。
- 回到 App，确认流水和统计同步刷新。
- 编辑一笔流水，检查：
  - 金额
  - 日期
  - 时间
  - 支出 / 收入
  - 分类
  - 备注
- 导出 CSV，确认文件出现在 `Download/tickle`。
- 在测试安装里从最新 CSV 恢复一次。

## 发到 GitHub 前

- 确认 `.gradle/`、`build/`、`dist/`、`artifacts/` 没有被 Git 跟踪。
- 确认 `local.properties` 没有提交。
- 不把过程稿、旧 UI dump、个人截图直接放进公开仓库。
- 确认 `README.md`、`docs/PROJECT_STRUCTURE.md`、`docs/DATA_EXPORT.md` 和实际功能一致。
- 确认 APK 文件名、SHA-256 和下载链接一致。
- 确认 License 存在。

## 应用商店前

- 创建正式签名包。
- 更新 `versionCode` 和 `versionName`。
- 确认最终包名和应用名。
- 准备隐私说明：
  - 数据默认本地保存
  - CSV 导出 / 恢复方式
  - 无账号系统
  - 当前版本不联网、不读取短信、不扫描相册、不依赖通知识别账单
- 准备商店截图：
  - 桌面小组件快速记账
  - App 内手动记账
  - 流水
  - 统计
  - 导出 / 恢复
- 尽量测试 Samsung、Xiaomi、OPPO、vivo 和原生 Android 启动器的小组件可用性。

## 朋友测试版

`friends` flavor 带独立 application id 后缀，可以和普通版共存：

```text
com.lightledger.app.friends
```

给朋友发测试包时可以用这个版本，避免覆盖你自己手机里的主版本。
