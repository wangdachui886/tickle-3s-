package com.lightledger.app.data

import android.content.Context
import com.lightledger.app.data.model.CategoryEntity
import com.lightledger.app.data.model.LedgerType
import com.lightledger.app.data.model.TransactionEntity
import com.lightledger.app.domain.MoneyFormatter
import com.lightledger.app.widget.LightLedgerWidgetProvider
import java.util.UUID

class LightLedgerRepository(
    private val dao: LightLedgerDao,
    private val appContext: Context,
) {
    val transactions = dao.observeTransactions()
    val categories = dao.observeCategories()
    val settings = dao.observeSettings()

    suspend fun getTransactionsSnapshot(): List<TransactionEntity> {
        return dao.getAllTransactions()
    }

    suspend fun ensureSeedData() {
        dao.insertCategories(SeedData.categories)
        if (dao.countSources() == 0) {
            dao.insertSources(SeedData.sources)
        }
    }

    suspend fun addCategory(name: String, ledgerType: String = LedgerType.EXPENSE): Boolean {
        val clean = name.trim()
        if (clean.isBlank()) return false
        val cleanLedgerType = ledgerType.takeIf { it == LedgerType.INCOME } ?: LedgerType.EXPENSE
        val idPrefix = "custom_${cleanLedgerType}_"
        val duplicate = dao.getAllCategories().any {
            it.name == clean &&
                when (cleanLedgerType) {
                    LedgerType.INCOME -> it.categoryId.startsWith("custom_income_")
                    else -> it.categoryId.startsWith("custom_expense_") ||
                        (it.categoryId.startsWith("custom_") &&
                            !it.categoryId.startsWith("custom_income_") &&
                            !it.categoryId.startsWith("custom_expense_"))
                }
        }
        if (duplicate) return false
        val nowOrder = dao.maxCategoryOrder().coerceAtLeast(999)
        dao.insertCategory(
            CategoryEntity(
                categoryId = "${idPrefix}${UUID.randomUUID()}",
                name = clean.take(12),
                displayOrder = nowOrder + 10,
                isQuick = false,
                enabled = true,
            ),
        )
        return true
    }

    suspend fun updateQuickCategories(ledgerType: String, categories: List<String>) {
        val cleanLedgerType = ledgerType.takeIf { it == LedgerType.INCOME } ?: LedgerType.EXPENSE
        val cleanValue = categories
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .take(11)
            .joinToString("|")
        dao.insertSetting(
            com.lightledger.app.data.model.SettingEntity(
                key = quickCategorySettingKey(cleanLedgerType),
                value = cleanValue,
            ),
        )
    }

    suspend fun updateCategoryOrder(ledgerType: String, categories: List<String>) {
        val cleanLedgerType = ledgerType.takeIf { it == LedgerType.INCOME } ?: LedgerType.EXPENSE
        val cleanValue = categories
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .joinToString("|")
        if (cleanValue.isBlank()) return
        dao.insertSetting(
            com.lightledger.app.data.model.SettingEntity(
                key = categoryOrderSettingKey(cleanLedgerType),
                value = cleanValue,
            ),
        )
    }

    suspend fun updateWidgetAmounts(ledgerType: String, amountValues: List<String>) {
        val cleanLedgerType = ledgerType.takeIf { it == LedgerType.INCOME } ?: LedgerType.EXPENSE
        val cleanValue = amountValues
            .mapNotNull { canonicalWidgetAmountInput(it) }
            .distinct()
            .take(6)
            .joinToString("|")
        if (cleanValue.isBlank()) return
        dao.insertSetting(
            com.lightledger.app.data.model.SettingEntity(
                key = widgetAmountSettingKey(cleanLedgerType),
                value = cleanValue,
            ),
        )
        refreshWidgets()
    }

    suspend fun updateWidgetCategories(ledgerType: String, categories: List<String>) {
        val cleanLedgerType = ledgerType.takeIf { it == LedgerType.INCOME } ?: LedgerType.EXPENSE
        val cleanValue = categories
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .take(4)
            .joinToString("|")
        if (cleanValue.isBlank()) return
        dao.insertSetting(
            com.lightledger.app.data.model.SettingEntity(
                key = widgetCategorySettingKey(cleanLedgerType),
                value = cleanValue,
            ),
        )
        refreshWidgets()
    }

    suspend fun addManualTransaction(
        amountText: String,
        merchant: String,
        category: String,
        note: String,
        ledgerType: String = LedgerType.EXPENSE,
        datetimeMillis: Long = System.currentTimeMillis(),
    ): Boolean {
        val amount = MoneyFormatter.yuanToCents(amountText) ?: return false
        if (amount <= 0L) return false
        val now = System.currentTimeMillis()
        val cleanLedgerType = ledgerType.takeIf { it == LedgerType.INCOME } ?: LedgerType.EXPENSE
        val signedAmount = if (cleanLedgerType == LedgerType.INCOME) {
            kotlin.math.abs(amount)
        } else {
            -kotlin.math.abs(amount)
        }
        dao.insertTransaction(
            TransactionEntity(
                transactionId = "tx_${UUID.randomUUID()}",
                datetime = datetimeMillis,
                amountCents = signedAmount,
                currency = "CNY",
                merchant = merchant.trim().ifBlank { "手动记账" },
                category = category.trim().ifBlank { "其他" },
                ledgerType = cleanLedgerType,
                account = "手动",
                sourceApp = "手动",
                note = note.trim(),
                isConsumption = cleanLedgerType == LedgerType.EXPENSE,
                isRecurring = false,
                rawEventId = null,
                createdAt = now,
                updatedAt = now,
            ),
        )
        refreshWidgets()
        return true
    }

    suspend fun updateTransaction(
        transactionId: String,
        merchant: String,
        amountText: String,
        category: String,
        note: String,
        ledgerType: String,
        datetimeMillis: Long,
    ): Boolean {
        val current = dao.getAllTransactions().firstOrNull { it.transactionId == transactionId } ?: return false
        val amount = MoneyFormatter.yuanToCents(amountText) ?: return false
        val cleanMerchant = merchant.trim().ifBlank { "未填写商户" }
        val cleanLedgerType = ledgerType.takeIf { it == LedgerType.INCOME } ?: LedgerType.EXPENSE
        val signedAmount = if (cleanLedgerType == LedgerType.INCOME) {
            kotlin.math.abs(amount)
        } else {
            -kotlin.math.abs(amount)
        }
        dao.updateTransaction(
            current.copy(
                datetime = datetimeMillis,
                amountCents = signedAmount,
                merchant = cleanMerchant,
                category = category.trim().ifBlank { current.category },
                ledgerType = cleanLedgerType,
                note = note.trim(),
                isConsumption = cleanLedgerType == LedgerType.EXPENSE,
                updatedAt = System.currentTimeMillis(),
            ),
        )
        refreshWidgets()
        return true
    }

    suspend fun deleteTransaction(transactionId: String) {
        dao.deleteTransaction(transactionId)
        refreshWidgets()
    }

    suspend fun exportCsv(): ExportResult {
        return CsvExporter.export(
            context = appContext,
            transactions = dao.getAllTransactions(),
            categories = dao.getAllCategories(),
        )
    }

    suspend fun importLatestBackup(): ImportResult {
        val imported = CsvImporter.readLatestBackup(appContext)
        if (imported.categories.isNotEmpty()) {
            dao.insertCategories(imported.categories)
        }
        imported.transactions.forEach { transaction ->
            dao.insertTransaction(transaction)
        }
        refreshWidgets()
        return ImportResult(
            transactionsImported = imported.transactions.size,
            categoriesImported = imported.categories.size,
            sourceFile = imported.sourceFile,
        )
    }

    private fun refreshWidgets() {
        runCatching { LightLedgerWidgetProvider.updateAll(appContext) }
    }

    private fun quickCategorySettingKey(ledgerType: String): String {
        return if (ledgerType == LedgerType.INCOME) {
            "quick_categories_income_v2"
        } else {
            "quick_categories_expense_v2"
        }
    }

    private fun categoryOrderSettingKey(ledgerType: String): String {
        return if (ledgerType == LedgerType.INCOME) {
            "category_order_income_v2"
        } else {
            "category_order_expense_v2"
        }
    }

    private fun widgetAmountSettingKey(ledgerType: String): String {
        return if (ledgerType == LedgerType.INCOME) {
            "widget_amounts_income_v2"
        } else {
            "widget_amounts_expense_v2"
        }
    }

    private fun widgetCategorySettingKey(ledgerType: String): String {
        return if (ledgerType == LedgerType.INCOME) {
            "widget_categories_income_v2"
        } else {
            "widget_categories_expense_v2"
        }
    }

    private fun canonicalWidgetAmountInput(raw: String): String? {
        val cents = parseWidgetAmountCents(raw) ?: return null
        val absCents = kotlin.math.abs(cents)
        val yuan = absCents / 100
        val tenths = (absCents % 100) / 10
        val amount = if (absCents % 100 == 0L) {
            yuan.toString()
        } else {
            "$yuan.$tenths"
        }
        return if (cents < 0L) "-$amount" else amount
    }

    private fun parseWidgetAmountCents(raw: String): Long? {
        val normalized = raw.trim()
            .replace(',', '.')
            .replace('，', '.')
            .replace('－', '-')
            .replace('−', '-')
            .replace('—', '-')
            .replace('–', '-')
        val negative = normalized.contains('-')
        val clean = normalized.replace("-", "")
        if (clean.isBlank() || clean == "-" || clean == "." || clean == "-.") return null
        val parts = clean.split('.')
        if (parts.size > 2) return null
        val integer = parts.getOrNull(0).orEmpty()
        val fraction = parts.getOrNull(1).orEmpty()
        if (integer.isBlank() || integer.length > 4 || !integer.all { it.isDigit() }) return null
        if (fraction.length > 1 || !fraction.all { it.isDigit() }) return null
        if (integer.length + fraction.length > 4) return null
        val yuan = integer.toLongOrNull() ?: return null
        val tenth = fraction.firstOrNull()?.digitToIntOrNull()?.toLong() ?: 0L
        val cents = yuan * 100L + tenth * 10L
        if (cents == 0L) return null
        return if (negative) -cents else cents
    }
}
