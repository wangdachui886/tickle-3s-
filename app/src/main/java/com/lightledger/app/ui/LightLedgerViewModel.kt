package com.lightledger.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lightledger.app.data.ExportResult
import com.lightledger.app.data.ImportResult
import com.lightledger.app.data.LedgerChangeBus
import com.lightledger.app.data.LightLedgerRepository
import com.lightledger.app.data.model.CategoryEntity
import com.lightledger.app.data.model.LedgerType
import com.lightledger.app.data.model.SettingEntity
import com.lightledger.app.data.model.TransactionEntity
import com.lightledger.app.domain.MoneyFormatter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LightLedgerViewModel(
    private val repository: LightLedgerRepository,
) : ViewModel() {
    private val _transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val transactions: StateFlow<List<TransactionEntity>> = _transactions.asStateFlow()

    val categories: StateFlow<List<CategoryEntity>> = repository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val settings: StateFlow<List<SettingEntity>> = repository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _lastExport = MutableStateFlow<ExportResult?>(null)
    val lastExport: StateFlow<ExportResult?> = _lastExport.asStateFlow()

    private val _lastImport = MutableStateFlow<ImportResult?>(null)
    val lastImport: StateFlow<ImportResult?> = _lastImport.asStateFlow()

    private val _messages = MutableSharedFlow<String>()
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.ensureSeedData()
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.transactions.collect { latest ->
                _transactions.value = latest
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            LedgerChangeBus.changes.collect {
                _transactions.value = repository.getTransactionsSnapshot()
            }
        }
    }

    fun addCategory(name: String) {
        addCategory(name, LedgerType.EXPENSE)
    }

    fun addCategory(name: String, ledgerType: String) {
        viewModelScope.launch {
            val added = repository.addCategory(name, ledgerType)
            _messages.emit(if (added) "已新增分类：${name.trim()}" else "分类已存在或名称为空")
        }
    }

    fun updateQuickCategories(ledgerType: String, categories: List<String>) {
        viewModelScope.launch {
            repository.updateQuickCategories(ledgerType, categories)
        }
    }

    fun updateCategoryOrder(ledgerType: String, categories: List<String>) {
        viewModelScope.launch {
            repository.updateCategoryOrder(ledgerType, categories)
        }
    }

    fun updateWidgetAmounts(ledgerType: String, amountValues: List<String>) {
        viewModelScope.launch {
            repository.updateWidgetAmounts(ledgerType, amountValues)
            _messages.emit("桌面卡片金额键已更新")
        }
    }

    fun updateWidgetCategories(ledgerType: String, categories: List<String>) {
        viewModelScope.launch {
            repository.updateWidgetCategories(ledgerType, categories)
            _messages.emit("桌面卡片类型已更新")
        }
    }

    fun addManualTransaction(
        amountText: String,
        merchant: String,
        category: String,
        note: String,
        ledgerType: String = LedgerType.EXPENSE,
        datetimeMillis: Long = System.currentTimeMillis(),
    ) {
        viewModelScope.launch {
            val added = repository.addManualTransaction(amountText, merchant, category, note, ledgerType, datetimeMillis)
            val amount = MoneyFormatter.yuanToCents(amountText)
            _messages.emit(
                if (added && amount != null) {
                    "已记入 ${MoneyFormatter.centsToDisplay(amount)} · ${category.ifBlank { "其他" }}"
                } else {
                    "先输入金额"
                },
            )
        }
    }

    fun updateTransaction(
        transactionId: String,
        merchant: String,
        amountText: String,
        category: String,
        note: String,
        ledgerType: String,
        datetimeMillis: Long,
    ) {
        viewModelScope.launch {
            val updated = repository.updateTransaction(
                transactionId = transactionId,
                merchant = merchant,
                amountText = amountText,
                category = category,
                note = note,
                ledgerType = ledgerType,
                datetimeMillis = datetimeMillis,
            )
            _messages.emit(if (updated) "流水已更新" else "金额格式不正确")
        }
    }

    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            repository.deleteTransaction(transactionId)
            _messages.emit("已删除流水")
        }
    }

    fun exportCsv() {
        viewModelScope.launch {
            _lastExport.value = repository.exportCsv()
            _messages.emit("备份已导出")
        }
    }

    fun importLatestBackup() {
        viewModelScope.launch {
            val result = runCatching { repository.importLatestBackup() }
            result.onSuccess { imported ->
                _lastImport.value = imported
                _messages.emit("已恢复 ${imported.transactionsImported} 笔流水")
            }.onFailure {
                _messages.emit("没有找到可恢复的备份")
            }
        }
    }
}

class LightLedgerViewModelFactory(
    private val repository: LightLedgerRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LightLedgerViewModel(repository) as T
    }
}
