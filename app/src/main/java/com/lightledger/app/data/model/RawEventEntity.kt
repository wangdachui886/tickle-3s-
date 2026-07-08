package com.lightledger.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "raw_events")
data class RawEventEntity(
    @PrimaryKey
    @ColumnInfo(name = "raw_event_id")
    val rawEventId: String,
    @ColumnInfo(name = "source_type")
    val sourceType: String,
    @ColumnInfo(name = "source_app")
    val sourceApp: String,
    @ColumnInfo(name = "raw_text")
    val rawText: String,
    @ColumnInfo(name = "image_path")
    val imagePath: String? = null,
    @ColumnInfo(name = "detected_amount_cents")
    val detectedAmountCents: Long? = null,
    @ColumnInfo(name = "detected_merchant")
    val detectedMerchant: String? = null,
    @ColumnInfo(name = "detected_time")
    val detectedTime: Long? = null,
    @ColumnInfo(name = "confidence")
    val confidence: Float = 0f,
    @ColumnInfo(name = "suggested_category")
    val suggestedCategory: String? = null,
    @ColumnInfo(name = "direction")
    val direction: String = MoneyDirection.OUT,
    @ColumnInfo(name = "ledger_type")
    val ledgerType: String = LedgerType.EXPENSE,
    @ColumnInfo(name = "is_consumption")
    val isConsumption: Boolean = true,
    @ColumnInfo(name = "status")
    val status: String = RawEventStatus.PENDING,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
)
