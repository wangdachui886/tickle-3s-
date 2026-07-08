# 数据导出

Tickle 轻记会把流水导出成一个简洁的 UTF-8 CSV 文件。这个文件可以直接打开、自己备份，也可以之后再导回 App。

导出目录：

```text
Download/tickle
```

文件名会带时间戳：

```text
transactions_YYYYMMDD_HHMMSS.csv
```

## `transactions_*.csv`

一行代表一笔记录。

| 字段 | 说明 |
| --- | --- |
| `date` | 本地交易时间，格式为 `yyyy-MM-dd HH:mm:ss` |
| `direction` | `in` 表示收入，`out` 表示支出 |
| `amount` | 金额，使用正数 |
| `unit` | 货币单位，目前为 `CNY` |
| `type` | 用户看到的分类名 |
| `note` | 备注；如果有商户信息，会合并到这里 |

## 恢复

恢复功能会读取 `Download/tickle` 里最新的 `transactions_*.csv`。

当前版本支持上面的简洁格式，也尽量兼容早期较详细的导出文件。为了稳妥，恢复前建议先保留一份原 CSV。
