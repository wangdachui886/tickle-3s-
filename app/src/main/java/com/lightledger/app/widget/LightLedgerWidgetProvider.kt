package com.lightledger.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import com.lightledger.app.R
import com.lightledger.app.WidgetActions
import com.lightledger.app.data.LedgerChangeBus
import com.lightledger.app.data.LightLedgerDatabase
import com.lightledger.app.data.LightLedgerRepository
import com.lightledger.app.data.model.LedgerType
import com.lightledger.app.data.model.TransactionEntity
import com.lightledger.app.domain.MoneyFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import kotlin.math.abs

class LightLedgerWidgetProvider : AppWidgetProvider() {
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action ?: return
        if (action !in WidgetDirectActions) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                handleDirectAction(context.applicationContext, action)
                updateAll(context.applicationContext)
            } finally {
                pendingResult.finish()
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        updateCompactWidgets(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        private const val PrefsName = "light_ledger_widget"
        private const val KeyDraftCents = "draft_cents"
        private const val KeyLedgerType = "ledger_type"
        private const val KeyCategoryIndex = "category_index"
        private const val KeyStatus = "status"
        private const val KeyLastTransactionId = "last_transaction_id"

        private val ExpensePrimaryCategories = listOf("餐饮", "交通", "购物", "娱乐")
        private val ExpenseOtherCategories = listOf("宠物", "房租", "服务", "书籍", "旅行", "其他")
        private val ExpenseCategories = ExpensePrimaryCategories + ExpenseOtherCategories
        private val IncomePrimaryCategories = listOf("工资", "报销", "转账", "红包")
        private val IncomeOtherCategories = listOf("退款", "奖金", "兼职", "理财", "生意", "其他")
        private val IncomeCategories = IncomePrimaryCategories + IncomeOtherCategories

        private val WidgetDirectActions = setOf(
            WidgetActions.WidgetPlusOne,
            WidgetActions.WidgetPlusFive,
            WidgetActions.WidgetPlusTen,
            WidgetActions.WidgetPlusFifty,
            WidgetActions.WidgetPlusHalf,
            WidgetActions.WidgetMinusOne,
            WidgetActions.WidgetMinusFive,
            WidgetActions.WidgetCategoryFirst,
            WidgetActions.WidgetCategorySecond,
            WidgetActions.WidgetCategoryThird,
            WidgetActions.WidgetCategoryFourth,
            WidgetActions.WidgetCategoryOther,
            WidgetActions.WidgetNextCategory,
            WidgetActions.WidgetToggleType,
            WidgetActions.WidgetSave,
            WidgetActions.WidgetClear,
            WidgetActions.WidgetUndo,
            WidgetActions.WidgetNoOp,
        )

        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            updateCompactWidgets(
                context = context,
                appWidgetManager = manager,
                appWidgetIds = manager.getAppWidgetIds(ComponentName(context, LightLedgerWidgetProvider::class.java)),
            )
            updateMediumWidgets(
                context = context,
                appWidgetManager = manager,
                appWidgetIds = manager.getAppWidgetIds(ComponentName(context, LightLedgerMediumWidgetProvider::class.java)),
            )
            updateCompactLightWidgets(
                context = context,
                appWidgetManager = manager,
                appWidgetIds = manager.getAppWidgetIds(ComponentName(context, LightLedgerLightWidgetProvider::class.java)),
            )
            updateMediumLightWidgets(
                context = context,
                appWidgetManager = manager,
                appWidgetIds = manager.getAppWidgetIds(ComponentName(context, LightLedgerMediumLightWidgetProvider::class.java)),
            )
            updateLargeWidgets(
                context = context,
                appWidgetManager = manager,
                appWidgetIds = manager.getAppWidgetIds(ComponentName(context, LightLedgerLargeWidgetProvider::class.java)),
            )
            updateLargeLightWidgets(
                context = context,
                appWidgetManager = manager,
                appWidgetIds = manager.getAppWidgetIds(ComponentName(context, LightLedgerLargeLightWidgetProvider::class.java)),
            )
        }

        internal fun updateCompactWidgets(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray,
        ) {
            appWidgetIds.forEach { id ->
                updateWidget(context, appWidgetManager, id, R.layout.light_ledger_widget, WidgetSize.Compact, WidgetTheme.Dark)
            }
        }

        internal fun updateMediumWidgets(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray,
        ) {
            appWidgetIds.forEach { id ->
                updateWidget(context, appWidgetManager, id, R.layout.light_ledger_widget_medium, WidgetSize.Medium, WidgetTheme.Dark)
            }
        }

        internal fun updateCompactLightWidgets(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray,
        ) {
            appWidgetIds.forEach { id ->
                updateWidget(context, appWidgetManager, id, R.layout.light_ledger_widget_light, WidgetSize.Compact, WidgetTheme.Light)
            }
        }

        internal fun updateMediumLightWidgets(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray,
        ) {
            appWidgetIds.forEach { id ->
                updateWidget(context, appWidgetManager, id, R.layout.light_ledger_widget_medium_light, WidgetSize.Medium, WidgetTheme.Light)
            }
        }

        internal fun updateLargeWidgets(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray,
        ) {
            appWidgetIds.forEach { id ->
                updateWidget(context, appWidgetManager, id, R.layout.light_ledger_widget_large, WidgetSize.Large, WidgetTheme.Dark)
            }
        }

        internal fun updateLargeLightWidgets(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray,
        ) {
            appWidgetIds.forEach { id ->
                updateWidget(context, appWidgetManager, id, R.layout.light_ledger_widget_large_light, WidgetSize.Large, WidgetTheme.Light)
            }
        }

        private fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            layoutResId: Int,
            size: WidgetSize,
            theme: WidgetTheme,
        ) {
            val draft = loadDraft(context)
            val summary = runBlocking(Dispatchers.IO) { loadTodaySummary(context.applicationContext) }
            val config = runBlocking(Dispatchers.IO) { loadWidgetConfig(context.applicationContext, draft.ledgerType) }
            val draftCategory = config.categoryAt(draft.categoryIndex)
            val modeLabel = typeLabel(draft.ledgerType)
            val switchTargetLabel = if (draft.ledgerType == LedgerType.INCOME) "支出" else "收入"
            val focusedTodayCents = if (draft.ledgerType == LedgerType.INCOME) summary.inCents else summary.outCents
            val primaryCategories = config.primaryCategories
            val otherSelected = draftCategory in config.otherCategories
            val amountLabels = amountButtonLabels(config.amountValues)

            val views = RemoteViews(context.packageName, layoutResId).apply {
                setTextViewText(R.id.widget_date, "今日$modeLabel")
                setTextViewText(R.id.widget_net_amount, MoneyFormatter.centsToDisplay(focusedTodayCents))
                setTextViewText(R.id.widget_flow_summary, "${summary.count} 笔")
                setTextViewText(R.id.widget_pending, draft.status.ifBlank { "点类型切换到$switchTargetLabel" })
                setTextViewText(R.id.widget_draft_amount, MoneyFormatter.centsToDisplay(draft.amountCents))
                setTextViewText(R.id.widget_draft_meta, "$modeLabel · $draftCategory")

                setOnClickPendingIntent(R.id.widget_root, broadcastIntent(context, WidgetActions.WidgetNoOp, 19))
                setOnClickPendingIntent(R.id.widget_summary_area, broadcastIntent(context, WidgetActions.WidgetToggleType, 20))
                setOnClickPendingIntent(R.id.widget_date, broadcastIntent(context, WidgetActions.WidgetToggleType, 35))
                setOnClickPendingIntent(R.id.widget_net_amount, broadcastIntent(context, WidgetActions.WidgetToggleType, 36))
                setOnClickPendingIntent(R.id.widget_flow_summary, broadcastIntent(context, WidgetActions.WidgetToggleType, 37))
                setOnClickPendingIntent(R.id.widget_pending, broadcastIntent(context, WidgetActions.WidgetToggleType, 38))

                configureCategoryButton(
                    viewId = R.id.widget_category_first,
                    label = primaryCategories[0],
                    selected = draftCategory == primaryCategories[0],
                    theme = theme,
                    size = size,
                )
                configureCategoryButton(
                    viewId = R.id.widget_category_second,
                    label = primaryCategories[1],
                    selected = draftCategory == primaryCategories[1],
                    theme = theme,
                    size = size,
                )
                configureCategoryButton(
                    viewId = R.id.widget_category_third,
                    label = primaryCategories[2],
                    selected = draftCategory == primaryCategories[2],
                    theme = theme,
                    size = size,
                )
                configureCategoryButton(
                    viewId = R.id.widget_category_fourth,
                    label = primaryCategories[3],
                    selected = draftCategory == primaryCategories[3],
                    theme = theme,
                    size = size,
                )
                configureCategoryButton(
                    viewId = R.id.widget_category_other,
                    label = if (otherSelected) draftCategory else "其他",
                    selected = otherSelected,
                    theme = theme,
                    size = size,
                )
                configureSaveButton(R.id.widget_save, draft.amountCents, theme, size)

                setTextViewText(R.id.widget_plus_one, amountLabels.plusOne)
                setTextViewText(R.id.widget_plus_five, amountLabels.plusFive)
                setTextViewText(R.id.widget_plus_ten, amountLabels.plusTen)
                setTextViewText(R.id.widget_plus_fifty, amountLabels.plusFifty)
                setTextViewText(R.id.widget_minus_one, amountLabels.minusOne)
                setTextViewText(R.id.widget_minus_five, amountLabels.minusFive)

                setOnClickPendingIntent(R.id.widget_amount_box, broadcastIntent(context, WidgetActions.WidgetClear, 21))
                setOnClickPendingIntent(R.id.widget_plus_one, broadcastIntent(context, WidgetActions.WidgetPlusOne, 22))
                setOnClickPendingIntent(R.id.widget_plus_five, broadcastIntent(context, WidgetActions.WidgetPlusFive, 23))
                setOnClickPendingIntent(R.id.widget_plus_ten, broadcastIntent(context, WidgetActions.WidgetPlusTen, 24))
                setOnClickPendingIntent(R.id.widget_minus_one, broadcastIntent(context, WidgetActions.WidgetMinusOne, 25))
                setOnClickPendingIntent(R.id.widget_plus_fifty, broadcastIntent(context, WidgetActions.WidgetPlusFifty, 26))
                setOnClickPendingIntent(R.id.widget_minus_five, broadcastIntent(context, WidgetActions.WidgetMinusFive, 27))
                setOnClickPendingIntent(R.id.widget_category_first, broadcastIntent(context, WidgetActions.WidgetCategoryFirst, 28))
                setOnClickPendingIntent(R.id.widget_category_second, broadcastIntent(context, WidgetActions.WidgetCategorySecond, 29))
                setOnClickPendingIntent(R.id.widget_category_third, broadcastIntent(context, WidgetActions.WidgetCategoryThird, 30))
                setOnClickPendingIntent(R.id.widget_category_fourth, broadcastIntent(context, WidgetActions.WidgetCategoryFourth, 31))
                setOnClickPendingIntent(R.id.widget_category_other, broadcastIntent(context, WidgetActions.WidgetCategoryOther, 32))
                setOnClickPendingIntent(R.id.widget_save, broadcastIntent(context, WidgetActions.WidgetSave, 33))
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private suspend fun loadTodaySummary(context: Context): WidgetSummary {
            val calendar = Calendar.getInstance()
            val label = SimpleDateFormat("M月d日", Locale.CHINA).format(calendar.time)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val start = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val end = calendar.timeInMillis
            val db = LightLedgerDatabase.create(context)
            return try {
                val dao = db.lightLedgerDao()
                val transactions = dao.getTransactionsBetween(start, end)
                val inCents = transactions.filter { it.amountCents > 0L }.sumOf { it.amountCents }
                val outCents = transactions.filter { it.amountCents < 0L }.sumOf { abs(it.amountCents) }
                WidgetSummary(
                    dateLabel = label,
                    inCents = inCents,
                    outCents = outCents,
                    netCents = inCents - outCents,
                    count = transactions.size,
                )
            } finally {
                db.close()
            }
        }

        private suspend fun handleDirectAction(context: Context, action: String) {
            val prefs = context.getSharedPreferences(PrefsName, Context.MODE_PRIVATE)
            val draft = loadDraft(context)
            val config = loadWidgetConfig(context, draft.ledgerType)
            when (action) {
                WidgetActions.WidgetPlusOne,
                WidgetActions.WidgetPlusFive,
                WidgetActions.WidgetPlusTen,
                WidgetActions.WidgetPlusFifty,
                WidgetActions.WidgetPlusHalf,
                WidgetActions.WidgetMinusOne,
                WidgetActions.WidgetMinusFive -> {
                    val delta = amountDeltaCents(action, config.amountValues)
                    saveDraft(context, draft.copy(amountCents = (draft.amountCents + delta).coerceAtLeast(0L), status = ""))
                }

                WidgetActions.WidgetNoOp -> Unit

                WidgetActions.WidgetClear -> saveDraft(
                    context,
                    WidgetDraft(
                        amountCents = 0L,
                        ledgerType = LedgerType.EXPENSE,
                        categoryIndex = 0,
                        status = "",
                    ),
                )

                WidgetActions.WidgetToggleType -> {
                    val nextType = if (draft.ledgerType == LedgerType.INCOME) LedgerType.EXPENSE else LedgerType.INCOME
                    saveDraft(
                        context,
                        WidgetDraft(
                            amountCents = draft.amountCents,
                            ledgerType = nextType,
                            categoryIndex = 0,
                            status = "",
                        ),
                    )
                }

                WidgetActions.WidgetUndo -> {
                    val lastId = prefs.getString(KeyLastTransactionId, "").orEmpty()
                    if (lastId.isNotBlank() && draft.status.isNotBlank()) {
                        val db = LightLedgerDatabase.create(context)
                        try {
                            db.lightLedgerDao().deleteTransaction(lastId)
                            LedgerChangeBus.notifyChanged()
                            prefs.edit()
                                .putString(KeyLastTransactionId, "")
                                .putString(KeyStatus, "已撤销")
                                .apply()
                        } finally {
                            db.close()
                        }
                    }
                }

                WidgetActions.WidgetCategoryFirst -> selectPrimaryCategory(context, draft, config, 0)
                WidgetActions.WidgetCategorySecond -> selectPrimaryCategory(context, draft, config, 1)
                WidgetActions.WidgetCategoryThird -> selectPrimaryCategory(context, draft, config, 2)
                WidgetActions.WidgetCategoryFourth -> selectPrimaryCategory(context, draft, config, 3)
                WidgetActions.WidgetCategoryOther,
                WidgetActions.WidgetNextCategory -> selectNextOtherCategory(context, draft, config)

                WidgetActions.WidgetSave -> {
                    if (draft.amountCents <= 0L) {
                        saveDraft(context, draft.copy(status = "先加金额"))
                        return
                    }
                    val db = LightLedgerDatabase.create(context)
                    try {
                        val dao = db.lightLedgerDao()
                        LightLedgerRepository(dao, context).ensureSeedData()
                        val now = System.currentTimeMillis()
                        val transactionId = "tx_${UUID.randomUUID()}"
                        val signedAmount = if (draft.ledgerType == LedgerType.INCOME) {
                            abs(draft.amountCents)
                        } else {
                            -abs(draft.amountCents)
                        }
                        dao.insertTransaction(
                            TransactionEntity(
                                transactionId = transactionId,
                                datetime = now,
                                amountCents = signedAmount,
                                currency = "CNY",
                                merchant = "小组件",
                                category = config.categoryAt(draft.categoryIndex),
                                ledgerType = draft.ledgerType,
                                account = "小组件",
                                sourceApp = "小组件",
                                note = "",
                                isConsumption = draft.ledgerType == LedgerType.EXPENSE,
                                isRecurring = false,
                                rawEventId = null,
                                createdAt = now,
                                updatedAt = now,
                            ),
                        )
                        LedgerChangeBus.notifyChanged()
                        prefs.edit()
                            .putLong(KeyDraftCents, 0L)
                            .putString(KeyLastTransactionId, transactionId)
                            .putString(KeyStatus, "已记入 · 撤销")
                            .apply()
                    } finally {
                        db.close()
                    }
                }
            }
        }

        private fun loadDraft(context: Context): WidgetDraft {
            val prefs = context.getSharedPreferences(PrefsName, Context.MODE_PRIVATE)
            val ledgerType = prefs.getString(KeyLedgerType, LedgerType.EXPENSE)
                ?.takeIf { it == LedgerType.INCOME }
                ?: LedgerType.EXPENSE
            val categoryIndex = prefs.getInt(KeyCategoryIndex, 0).coerceAtLeast(0)
            return WidgetDraft(
                amountCents = prefs.getLong(KeyDraftCents, 0L).coerceAtLeast(0L),
                ledgerType = ledgerType,
                categoryIndex = categoryIndex,
                status = prefs.getString(KeyStatus, "").orEmpty(),
            )
        }

        private fun saveDraft(context: Context, draft: WidgetDraft) {
            context.getSharedPreferences(PrefsName, Context.MODE_PRIVATE)
                .edit()
                .putLong(KeyDraftCents, draft.amountCents.coerceAtLeast(0L))
                .putString(KeyLedgerType, draft.ledgerType)
                .putInt(KeyCategoryIndex, draft.categoryIndex.coerceAtLeast(0))
                .putString(KeyStatus, draft.status)
                .apply()
        }

        private fun broadcastIntent(context: Context, action: String, requestCode: Int): PendingIntent {
            val intent = Intent(context, LightLedgerWidgetProvider::class.java).apply {
                this.action = action
            }
            return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        private suspend fun loadWidgetConfig(context: Context, ledgerType: String): WidgetConfig {
            val cleanLedgerType = ledgerType.takeIf { it == LedgerType.INCOME } ?: LedgerType.EXPENSE
            val defaultPrimary = if (cleanLedgerType == LedgerType.INCOME) IncomePrimaryCategories else ExpensePrimaryCategories
            val defaultAll = if (cleanLedgerType == LedgerType.INCOME) IncomeCategories else ExpenseCategories
            val defaultAmounts = defaultWidgetAmountValues(cleanLedgerType)
            val db = LightLedgerDatabase.create(context)
            return try {
                val dao = db.lightLedgerDao()
                val customCategories = dao.getAllCategories()
                    .filter { it.enabled }
                    .filterNot { isBlockedTestCategory(it.name) }
                    .filter {
                        if (cleanLedgerType == LedgerType.INCOME) {
                            it.categoryId.startsWith("custom_income_")
                        } else {
                            isExpenseCustomCategoryId(it.categoryId)
                        }
                    }
                    .map { categoryCustomDisplayName(it.name) }
                val allCategories = (defaultAll + customCategories).distinct()
                val savedPrimary = dao.getSettingValue(widgetCategorySettingKey(cleanLedgerType))
                    ?.split("|")
                    ?.map { it.trim() }
                    ?.filter { it.isNotBlank() && it in allCategories }
                    ?.distinct()
                    .orEmpty()
                val primary = (savedPrimary + defaultPrimary + allCategories)
                    .distinct()
                    .take(4)
                    .let { if (it.size >= 4) it else (it + defaultPrimary).distinct().take(4) }
                val other = allCategories.filterNot { it in primary }.ifEmpty { listOf("其他") }
                val amounts = dao.getSettingValue(widgetAmountSettingKey(cleanLedgerType))
                    ?.split("|")
                    ?.mapNotNull { parseWidgetAmountCents(it) }
                    ?.filter { it != 0L }
                    ?.take(6)
                    .orEmpty()
                    .let { values -> (values + defaultAmounts).take(6) }
                WidgetConfig(
                    primaryCategories = primary,
                    otherCategories = other,
                    amountValues = amounts,
                )
            } finally {
                db.close()
            }
        }

        private fun amountButtonLabels(amountValues: List<Long>): WidgetAmountLabels {
            val padded = (amountValues + defaultWidgetAmountValues(LedgerType.EXPENSE)).take(6)
            return WidgetAmountLabels(
                plusOne = formatAmountButton(padded[0]),
                plusFive = formatAmountButton(padded[1]),
                plusTen = formatAmountButton(padded[2]),
                plusFifty = formatAmountButton(padded[4]),
                minusOne = formatAmountButton(padded[3]),
                minusFive = formatAmountButton(padded[5]),
            )
        }

        private fun amountDeltaCents(action: String, amountValues: List<Long>): Long {
            val padded = (amountValues + defaultWidgetAmountValues(LedgerType.EXPENSE)).take(6)
            return when (action) {
                WidgetActions.WidgetPlusOne -> padded[0]
                WidgetActions.WidgetPlusFive -> padded[1]
                WidgetActions.WidgetPlusTen -> padded[2]
                WidgetActions.WidgetMinusOne -> padded[3]
                WidgetActions.WidgetPlusFifty -> padded[4]
                WidgetActions.WidgetMinusFive -> padded[5]
                WidgetActions.WidgetPlusHalf -> 50L
                else -> 0L
            }
        }

        private fun selectPrimaryCategory(context: Context, draft: WidgetDraft, config: WidgetConfig, primaryIndex: Int) {
            val category = config.primaryCategories.getOrNull(primaryIndex) ?: return
            saveDraft(context, draft.copy(categoryIndex = config.categories.indexOf(category).coerceAtLeast(0), status = ""))
        }

        private fun selectNextOtherCategory(context: Context, draft: WidgetDraft, config: WidgetConfig) {
            val otherCategories = config.otherCategories
            val categories = config.categories
            val currentCategory = config.categoryAt(draft.categoryIndex)
            val currentOtherIndex = otherCategories.indexOf(currentCategory)
            val nextCategory = if (currentOtherIndex >= 0) {
                otherCategories[(currentOtherIndex + 1) % otherCategories.size]
            } else {
                otherCategories.first()
            }
            saveDraft(context, draft.copy(categoryIndex = categories.indexOf(nextCategory).coerceAtLeast(0), status = ""))
        }

        private fun RemoteViews.configureCategoryButton(
            viewId: Int,
            label: String,
            selected: Boolean,
            theme: WidgetTheme,
            size: WidgetSize,
        ) {
            setTextViewText(viewId, widgetCategoryLabel(label))
            val background = when {
                selected && theme == WidgetTheme.Light -> primaryBackgroundFor(size, lightTheme = true)
                selected -> primaryBackgroundFor(size, lightTheme = false)
                theme == WidgetTheme.Light -> keyBackgroundFor(size, lightTheme = true)
                else -> keyBackgroundFor(size, lightTheme = false)
            }
            setInt(viewId, "setBackgroundResource", background)
            setTextColor(
                viewId,
                when {
                    selected && theme == WidgetTheme.Light -> Color.rgb(244, 242, 236)
                    selected -> Color.rgb(68, 70, 67)
                    theme == WidgetTheme.Light -> Color.rgb(58, 60, 56)
                    else -> Color.rgb(244, 242, 236)
                },
            )
        }

        private fun RemoteViews.configureSaveButton(
            viewId: Int,
            amountCents: Long,
            theme: WidgetTheme,
            size: WidgetSize,
        ) {
            val active = amountCents > 0L
            setTextViewText(
                viewId,
                "记入",
            )
            val background = when {
                active && theme == WidgetTheme.Light -> primaryBackgroundFor(size, lightTheme = true)
                active -> primaryBackgroundFor(size, lightTheme = false)
                theme == WidgetTheme.Light -> keyBackgroundFor(size, lightTheme = true)
                else -> keyBackgroundFor(size, lightTheme = false)
            }
            setInt(viewId, "setBackgroundResource", background)
            setTextColor(
                viewId,
                when {
                    active && theme == WidgetTheme.Light -> Color.rgb(244, 242, 236)
                    active -> Color.rgb(32, 34, 31)
                    theme == WidgetTheme.Light -> Color.rgb(92, 95, 89)
                    else -> Color.rgb(188, 191, 186)
                },
            )
        }

        private fun keyBackgroundFor(size: WidgetSize, lightTheme: Boolean): Int {
            return when (size) {
                WidgetSize.Compact -> if (lightTheme) {
                    R.drawable.widget_compact_light_key_bg
                } else {
                    R.drawable.widget_compact_dark_key_bg
                }
                WidgetSize.Medium -> if (lightTheme) {
                    R.drawable.widget_medium_light_key_bg
                } else {
                    R.drawable.widget_medium_dark_key_bg
                }
                WidgetSize.Large -> if (lightTheme) {
                    R.drawable.widget_large_light_key_bg
                } else {
                    R.drawable.widget_large_dark_key_bg
                }
            }
        }

        private fun primaryBackgroundFor(size: WidgetSize, lightTheme: Boolean): Int {
            return when (size) {
                WidgetSize.Compact -> if (lightTheme) {
                    R.drawable.widget_compact_selected_bg
                } else {
                    R.drawable.widget_compact_selected_dark_bg
                }
                WidgetSize.Medium -> if (lightTheme) {
                    R.drawable.widget_medium_selected_bg
                } else {
                    R.drawable.widget_medium_selected_dark_bg
                }
                WidgetSize.Large -> if (lightTheme) {
                    R.drawable.widget_large_selected_bg
                } else {
                    R.drawable.widget_large_selected_dark_bg
                }
            }
        }

        private fun typeLabel(ledgerType: String): String {
            return if (ledgerType == LedgerType.INCOME) "收入" else "支出"
        }

        private fun quickCategorySettingKey(ledgerType: String): String {
            return if (ledgerType == LedgerType.INCOME) {
                "quick_categories_income_v2"
            } else {
                "quick_categories_expense_v2"
            }
        }

        private fun widgetCategorySettingKey(ledgerType: String): String {
            return if (ledgerType == LedgerType.INCOME) {
                "widget_categories_income_v2"
            } else {
                "widget_categories_expense_v2"
            }
        }

        private fun widgetAmountSettingKey(ledgerType: String): String {
            return if (ledgerType == LedgerType.INCOME) {
                "widget_amounts_income_v2"
            } else {
                "widget_amounts_expense_v2"
            }
        }

        private fun defaultWidgetAmountValues(ledgerType: String): List<Long> {
            return if (ledgerType == LedgerType.INCOME) {
                listOf(10_000L, 50_000L, 100_000L, -10_000L, 500_000L, -50_000L)
            } else {
                listOf(100L, 500L, 1_000L, -100L, 5_000L, -500L)
            }
        }

        private fun formatAmountButton(valueCents: Long): String {
            val absCents = abs(valueCents)
            val yuan = absCents / 100
            val tenths = (absCents % 100) / 10
            val amount = if (absCents % 100 == 0L) {
                yuan.toString()
            } else {
                "$yuan.$tenths"
            }
            return if (valueCents > 0L) "+$amount" else "-$amount"
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

        private fun widgetCategoryLabel(label: String): String {
            return label.trim().take(2).ifBlank { "其他" }
        }

        private fun isBlockedTestCategory(name: String): Boolean {
            return name.trim().lowercase() in setOf("cafe", "café")
        }

        private fun isExpenseCustomCategoryId(categoryId: String): Boolean {
            return categoryId.startsWith("custom_expense_") ||
                (categoryId.startsWith("custom_") &&
                    !categoryId.startsWith("custom_income_") &&
                    !categoryId.startsWith("custom_expense_"))
        }

        private fun categoryCustomDisplayName(name: String): String {
            return when (name) {
                "日用品" -> "日用"
                "房租" -> "居住"
                else -> name
            }
        }
    }
}

class LightLedgerMediumWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        LightLedgerWidgetProvider.updateMediumWidgets(context, appWidgetManager, appWidgetIds)
    }
}

class LightLedgerLightWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        LightLedgerWidgetProvider.updateCompactLightWidgets(context, appWidgetManager, appWidgetIds)
    }
}

class LightLedgerMediumLightWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        LightLedgerWidgetProvider.updateMediumLightWidgets(context, appWidgetManager, appWidgetIds)
    }
}

class LightLedgerLargeWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        LightLedgerWidgetProvider.updateLargeWidgets(context, appWidgetManager, appWidgetIds)
    }
}

class LightLedgerLargeLightWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        LightLedgerWidgetProvider.updateLargeLightWidgets(context, appWidgetManager, appWidgetIds)
    }
}

private enum class WidgetSize {
    Compact,
    Medium,
    Large,
}

private enum class WidgetTheme {
    Dark,
    Light,
}

private data class WidgetSummary(
    val dateLabel: String,
    val inCents: Long,
    val outCents: Long,
    val netCents: Long,
    val count: Int,
)

private data class WidgetDraft(
    val amountCents: Long,
    val ledgerType: String,
    val categoryIndex: Int,
    val status: String,
)

private data class WidgetConfig(
    val primaryCategories: List<String>,
    val otherCategories: List<String>,
    val amountValues: List<Long>,
) {
    val categories: List<String> = (primaryCategories + otherCategories).distinct()

    fun categoryAt(index: Int): String {
        return categories.getOrElse(index.coerceAtLeast(0)) { categories.firstOrNull() ?: "其他" }
    }
}

private data class WidgetAmountLabels(
    val plusOne: String,
    val plusFive: String,
    val plusTen: String,
    val plusFifty: String,
    val minusOne: String,
    val minusFive: String,
)
