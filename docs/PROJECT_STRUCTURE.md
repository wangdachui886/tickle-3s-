# 项目结构

Tickle 轻记是一个小型 Android App。公开仓库里只保留源码、必要文档、最终截图和当前体验包；过程稿、旧实验材料和本地构建产物默认不进仓库。

## 根目录

```text
app/          Android 源码、资源、Manifest 和 Gradle 配置
design/       公开设计说明；过程稿和源文件默认留在本地
docs/         数据格式、项目结构和发布检查文档
gradle/       Gradle Wrapper
releases/     当前体验版 APK 和校验说明
```

`.gradle/`、`build/`、`dist/`、`artifacts/`、`local.properties` 等本地文件已经在 `.gitignore` 里忽略。

## 代码分层

```text
com.lightledger.app
  data/
    model/
  domain/
  ui/
    theme/
  widget/
```

### `data`

负责持久化和导入导出。

- Room 数据库配置
- DAO 查询
- Repository 接口
- CSV 导出和恢复

这里不放 Compose 界面，也不放桌面小组件的布局逻辑。

### `data/model`

负责数据库实体和枚举。这里的变更会影响已安装用户和未来备份兼容性，需要谨慎处理。

### `domain`

负责不依赖 Android UI 的纯 Kotlin 逻辑，例如：

- 金额格式化
- 时间格式化
- 分类建议
- 文本解析辅助

能单元测试的规则优先放这里。

### `ui`

负责 Compose 页面、弹窗、引导页和 `LightLedgerViewModel`。UI 通过 ViewModel 和 Repository 访问数据，不直接操作 DAO。

### `widget`

负责 Android 桌面小组件和 RemoteViews 渲染。小组件动作尽量保持简单：更新临时状态、写入确认后的记账记录，然后刷新小组件和 App 数据。

## 设计和截图

App 内使用的教程图片在：

```text
app/src/main/res/drawable-nodpi/
```

README 使用的公开展示图在：

```text
docs/screenshots/
```

Logo 等最终品牌素材在：

```text
docs/brand/
```

Illustrator 源文件、提案板、历史重绘稿和评审截图默认留在本地。只有确认要公开展示的成品，才复制到 `docs/` 或 `design/`。

## 本地材料

`artifacts/` 用来放本地评审截图、UI dump、旧实验材料和样例数据，默认不提交。

如果某个本地文件后续要变成公开文档或演示素材，先整理、脱敏，再复制到 `docs/` 或 `design/`，不要直接引用本地路径。

## 后续拆分

现在先保持一个 Android app module。等代码真的变复杂，再考虑拆：

- `:core-model`：数据类和纯领域逻辑
- `:core-export`：CSV 导入导出契约
- `:widget`：如果小组件代码变得足够独立、足够大

目前更重要的是保持项目简单、可读、方便别人快速安装和理解。
