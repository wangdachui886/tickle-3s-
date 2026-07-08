package com.lightledger.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sources")
data class SourceProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "source_id")
    val sourceId: String,
    @ColumnInfo(name = "display_name")
    val displayName: String,
    @ColumnInfo(name = "package_name")
    val packageName: String?,
    @ColumnInfo(name = "source_type")
    val sourceType: String,
    @ColumnInfo(name = "capture_modes")
    val captureModes: String,
    @ColumnInfo(name = "enabled")
    val enabled: Boolean,
    @ColumnInfo(name = "default_category")
    val defaultCategory: String?,
    @ColumnInfo(name = "parser_profile")
    val parserProfile: String,
    @ColumnInfo(name = "priority")
    val priority: Int,
)
