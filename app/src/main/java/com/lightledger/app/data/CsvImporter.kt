package com.lightledger.app.data

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.lightledger.app.data.model.CategoryEntity
import com.lightledger.app.data.model.LedgerType
import com.lightledger.app.data.model.TransactionEntity
import com.lightledger.app.domain.MoneyFormatter
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.abs

data class ImportResult(
    val transactionsImported: Int,
    val categoriesImported: Int,
    val sourceFile: String,
)

data class ImportedLedgerData(
    val transactions: List<TransactionEntity>,
    val categories: List<CategoryEntity>,
    val sourceFile: String,
)

object CsvImporter {
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    fun readLatestBackup(context: Context): ImportedLedgerData {
        val transactionFile = findLatestExportFile(context, "transactions_")
            ?: error("No transactions export found in Download/tickle")
        val stamp = transactionFile.name
            .removePrefix("transactions_")
            .removeSuffix(".csv")
        val categoryFile = findExportFile(context, "categories_$stamp.csv")
            ?: findLatestExportFile(context, "categories_")

        val transactions = parseTransactions(transactionFile.readText())
        val categories = categoryFile?.let { parseCategories(it.readText()) }.orEmpty()
        return ImportedLedgerData(
            transactions = transactions,
            categories = categories,
            sourceFile = transactionFile.displayPath,
        )
    }

    private fun findLatestExportFile(context: Context, prefix: String): ExportFile? {
        return listExportFiles(context)
            .filter { it.name.startsWith(prefix) && it.name.endsWith(".csv") }
            .maxByOrNull { it.modifiedAt }
    }

    private fun findExportFile(context: Context, name: String): ExportFile? {
        return listExportFiles(context).firstOrNull { it.name == name }
    }

    private fun listExportFiles(context: Context): List<ExportFile> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val projection = arrayOf(
                MediaStore.Downloads._ID,
                MediaStore.Downloads.DISPLAY_NAME,
                MediaStore.Downloads.DATE_MODIFIED,
                MediaStore.Downloads.RELATIVE_PATH,
            )
            val resolver = context.contentResolver
            val results = mutableListOf<ExportFile>()
            resolver.query(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                projection,
                "${MediaStore.Downloads.DISPLAY_NAME} LIKE ?",
                arrayOf("%.csv"),
                "${MediaStore.Downloads.DATE_MODIFIED} DESC",
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.DISPLAY_NAME)
                val modifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.DATE_MODIFIED)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.RELATIVE_PATH)
                while (cursor.moveToNext()) {
                    val relativePath = cursor.getString(pathColumn).orEmpty()
                    if (!relativePath.contains("Download/tickle")) continue
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn).orEmpty()
                    val uri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id)
                    results += ExportFile(
                        name = name,
                        displayPath = "Download/tickle/$name",
                        modifiedAt = cursor.getLong(modifiedColumn),
                        readText = {
                            resolver.openInputStream(uri)?.use { stream ->
                                stream.readBytes().toString(Charsets.UTF_8)
                            } ?: ""
                        },
                    )
                }
            }
            return results
        }

        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "tickle")
        return dir.listFiles()
            ?.filter { it.isFile && it.extension.equals("csv", ignoreCase = true) }
            ?.map { file ->
                ExportFile(
                    name = file.name,
                    displayPath = file.absolutePath,
                    modifiedAt = file.lastModified(),
                    readText = { file.readText(Charsets.UTF_8) },
                )
            }
            .orEmpty()
    }

    private fun parseTransactions(content: String): List<TransactionEntity> {
        val rows = parseCsv(content)
        if (rows.size <= 1) return emptyList()
        val header = rows.first().mapIndexed { index, name -> name.trim().removePrefix("\uFEFF") to index }.toMap()
        val now = System.currentTimeMillis()
        return rows.drop(1).mapNotNull { row ->
            val direction = row.value(header, "direction")
                .ifBlank { row.value(header, "进/出") }
                .trim()
                .lowercase(Locale.US)
            val signedAmount = row.value(header, "signed_amount")
                .takeIf { it.isNotBlank() }
                ?: row.value(header, "amount")
            val cents = MoneyFormatter.yuanToCents(signedAmount)?.let { amount ->
                if (signedAmount.trim().startsWith("-")) -abs(amount) else amount
            } ?: return@mapNotNull null
            val ledgerType = row.value(header, "ledger_type")
                .takeIf { it == LedgerType.INCOME || it == LedgerType.EXPENSE }
                ?: directionToLedgerType(direction)
                ?: if (cents >= 0) LedgerType.INCOME else LedgerType.EXPENSE
            val datetime = parseDateTime(row.value(header, "datetime"))
                ?: parseDate(row.value(header, "date"))
                ?: now
            TransactionEntity(
                transactionId = row.value(header, "transaction_id").ifBlank { "tx_${UUID.randomUUID()}" },
                datetime = datetime,
                amountCents = if (ledgerType == LedgerType.INCOME) abs(cents) else -abs(cents),
                currency = row.value(header, "currency").ifBlank { row.value(header, "unit").ifBlank { "CNY" } },
                merchant = row.value(header, "merchant").ifBlank { "恢复导入" },
                category = row.value(header, "category").ifBlank { row.value(header, "type").ifBlank { "其他" } },
                ledgerType = ledgerType,
                account = row.value(header, "account").ifBlank { "恢复" },
                sourceApp = row.value(header, "source").ifBlank { row.value(header, "source_app").ifBlank { "restore" } },
                note = row.value(header, "note"),
                isConsumption = ledgerType == LedgerType.EXPENSE,
                isRecurring = false,
                rawEventId = null,
                createdAt = parseDateTime(row.value(header, "created_at")) ?: datetime,
                updatedAt = parseDateTime(row.value(header, "updated_at")) ?: now,
            )
        }
    }

    private fun parseCategories(content: String): List<CategoryEntity> {
        val rows = parseCsv(content)
        if (rows.size <= 1) return emptyList()
        val header = rows.first().mapIndexed { index, name -> name.trim().removePrefix("\uFEFF") to index }.toMap()
        return rows.drop(1).mapNotNull { row ->
            val name = row.value(header, "name")
            if (name.isBlank()) return@mapNotNull null
            CategoryEntity(
                categoryId = row.value(header, "category_id").ifBlank { "custom_${UUID.randomUUID()}" },
                name = name.take(12),
                displayOrder = row.value(header, "display_order").toIntOrNull() ?: 999,
                isQuick = row.value(header, "is_quick").toBooleanStrictOrNull() ?: false,
                enabled = row.value(header, "enabled").toBooleanStrictOrNull() ?: true,
            )
        }
    }

    private fun directionToLedgerType(value: String): String? {
        return when (value) {
            "in", "income", "收入", "进", "入" -> LedgerType.INCOME
            "out", "expense", "支出", "出" -> LedgerType.EXPENSE
            else -> null
        }
    }

    private fun parseCsv(content: String): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        val row = mutableListOf<String>()
        val cell = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < content.length) {
            val ch = content[i]
            when {
                ch == '"' && inQuotes && content.getOrNull(i + 1) == '"' -> {
                    cell.append('"')
                    i++
                }
                ch == '"' -> inQuotes = !inQuotes
                ch == ',' && !inQuotes -> {
                    row += cell.toString()
                    cell.clear()
                }
                (ch == '\n' || ch == '\r') && !inQuotes -> {
                    if (ch == '\r' && content.getOrNull(i + 1) == '\n') i++
                    row += cell.toString()
                    cell.clear()
                    if (row.any { it.isNotEmpty() }) rows += row.toList()
                    row.clear()
                }
                else -> cell.append(ch)
            }
            i++
        }
        row += cell.toString()
        if (row.any { it.isNotEmpty() }) rows += row.toList()
        return rows
    }

    private fun List<String>.value(header: Map<String, Int>, key: String): String {
        val index = header[key] ?: return ""
        return getOrNull(index).orEmpty()
    }

    private fun parseDateTime(value: String): Long? {
        return runCatching { dateTimeFormat.parse(value)?.time }.getOrNull()
    }

    private fun parseDate(value: String): Long? {
        return runCatching {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(value)?.time
        }.getOrNull()
    }

    private data class ExportFile(
        val name: String,
        val displayPath: String,
        val modifiedAt: Long,
        val readText: () -> String,
    )
}
