# Data export

tickle exports a compact UTF-8 CSV ledger file. The goal is that a user can
open it directly, send it to a friend, restore it later, or give it to an LLM
without being buried in technical tables.

Exports are written to:

```text
Download/tickle
```

The file name includes a timestamp:

```text
transactions_YYYYMMDD_HHMMSS.csv
```

## `transactions_*.csv`

One row per ledger entry.

| Column | Meaning |
| --- | --- |
| `date` | Local transaction datetime, `yyyy-MM-dd HH:mm:ss`. |
| `direction` | `in` for income, `out` for expense. |
| `amount` | Absolute transaction amount. |
| `unit` | Currency unit, currently `CNY`. |
| `type` | User-facing category name. |
| `note` | User note, with merchant appended when useful. |

## Restore

The restore flow reads the latest `transactions_*.csv` in `Download/tickle`.
It supports this compact format and remains compatible with older detailed
exports when those files are present.
