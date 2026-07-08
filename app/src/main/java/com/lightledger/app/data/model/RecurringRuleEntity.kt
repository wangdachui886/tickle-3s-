package com.lightledger.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurring_rules")
data class RecurringRuleEntity(
    @PrimaryKey
    @ColumnInfo(name = "rule_id")
    val ruleId: String,
    @ColumnInfo(name = "merchant")
    val merchant: String,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "amount_cents")
    val amountCents: Long?,
    @ColumnInfo(name = "currency")
    val currency: String = "CNY",
    @ColumnInfo(name = "day_of_month")
    val dayOfMonth: Int?,
    @ColumnInfo(name = "auto_post")
    val autoPost: Boolean,
    @ColumnInfo(name = "enabled")
    val enabled: Boolean,
)
