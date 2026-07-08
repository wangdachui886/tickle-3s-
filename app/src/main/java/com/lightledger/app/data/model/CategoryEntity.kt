package com.lightledger.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "category_id")
    val categoryId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "display_order")
    val displayOrder: Int,
    @ColumnInfo(name = "is_quick")
    val isQuick: Boolean,
    @ColumnInfo(name = "enabled")
    val enabled: Boolean = true,
)
