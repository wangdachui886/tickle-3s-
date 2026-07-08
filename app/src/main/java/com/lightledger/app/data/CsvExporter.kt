package com.lightledger.app.data

import android.content.ContentValues
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
import kotlin.math.abs

data class ExportResult(
    val transactionsPath: String,
) {
    val paths: List<String>
        get() = listOf(transactionsPath)
}

object CsvExporter {
    private val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    private val transactionsHeader = listOf(
        "date",
        "direction",
        "amount",
        "unit",
        "type",
        "note",
    )

    fun export(
        context: Context,
        transactions: List<TransactionEntity>,
        @Suppress("UNUSED_PARAMETER") categories: List<CategoryEntity>,
    ): ExportResult {
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val orderedTransactions = transactions.sortedWith(compareBy<TransactionEntity> { it.datetime }.thenBy { it.createdAt })
        val transactionsFile = "transactions_$stamp.csv"
        val transactionsPath = writeDownloadText(
            context = context,
            fileName = transactionsFile,
            content = buildTransactionsCsv(orderedTransactions),
            mimeType = "text/csv",
            addBom = true,
        )
        return ExportResult(transactionsPath = transactionsPath)
    }

    private fun buildTransactionsCsv(transactions: List<TransactionEntity>): String {
        val rows = transactions.map { tx ->
            listOf(
                dateTime.format(Date(tx.datetime)),
                if (normalizedLedgerType(tx) == LedgerType.INCOME) "in" else "out",
                MoneyFormatter.centsToCsvAmount(abs(tx.amountCents)),
                tx.currency.ifBlank { "CNY" },
                tx.category.ifBlank { "其他" },
                buildNote(tx),
            )
        }
        return toCsv(transactionsHeader, rows)
    }

    private fun buildNote(transaction: TransactionEntity): String {
        return listOf(
            transaction.note.trim(),
            transaction.merchant.trim().takeIf {
                it.isNotBlank() &&
                    it != transaction.category &&
                    it != "手动记账" &&
                    it != "小组件" &&
                    it != "恢复导入"
            }.orEmpty(),
        )
            .filter { it.isNotBlank() }
            .distinct()
            .joinToString(" · ")
    }

    private fun normalizedLedgerType(transaction: TransactionEntity): String {
        return when {
            transaction.ledgerType == LedgerType.INCOME -> LedgerType.INCOME
            transaction.ledgerType == LedgerType.EXPENSE -> LedgerType.EXPENSE
            transaction.amountCents > 0L -> LedgerType.INCOME
            else -> LedgerType.EXPENSE
        }
    }

    private fun writeDownloadText(
        context: Context,
        fileName: String,
        content: String,
        mimeType: String,
        addBom: Boolean,
    ): String {
        val relativeDisplayPath = "Download/tickle/$fileName"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, mimeType)
                put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/tickle")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: error("Cannot create $relativeDisplayPath")
            resolver.openOutputStream(uri)?.use { stream ->
                if (addBom) {
                    stream.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                }
                stream.write(content.toByteArray(Charsets.UTF_8))
            } ?: error("Cannot write $relativeDisplayPath")
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            return relativeDisplayPath
        }

        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "tickle")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        val bytes = content.toByteArray(Charsets.UTF_8)
        file.writeBytes(if (addBom) byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()) + bytes else bytes)
        return file.absolutePath
    }

    private fun toCsv(header: List<String>, rows: List<List<String>>): String {
        return buildString {
            appendLine(header.joinToString(",") { it.csvEscape() })
            rows.forEach { row ->
                appendLine(row.joinToString(",") { it.csvEscape() })
            }
        }
    }

    private fun String.csvEscape(): String {
        val escaped = replace("\"", "\"\"")
        return if (escaped.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
            "\"$escaped\""
        } else {
            escaped
        }
    }
}
