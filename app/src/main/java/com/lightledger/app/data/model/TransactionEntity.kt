package com.lightledger.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [
        Index("raw_event_id"),
        Index("datetime"),
        Index("category"),
        Index("source_app"),
    ],
)
data class TransactionEntity(
    @PrimaryKey
    @ColumnInfo(name = "transaction_id")
    val transactionId: String,
    @ColumnInfo(name = "datetime")
    val datetime: Long,
    @ColumnInfo(name = "amount_cents")
    val amountCents: Long,
    @ColumnInfo(name = "currency")
    val currency: String = "CNY",
    @ColumnInfo(name = "merchant")
    val merchant: String,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "ledger_type")
    val ledgerType: String,
    @ColumnInfo(name = "account")
    val account: String,
    @ColumnInfo(name = "source_app")
    val sourceApp: String,
    @ColumnInfo(name = "note")
    val note: String = "",
    @ColumnInfo(name = "is_consumption")
    val isConsumption: Boolean,
    @ColumnInfo(name = "is_recurring")
    val isRecurring: Boolean = false,
    @ColumnInfo(name = "raw_event_id")
    val rawEventId: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
)
