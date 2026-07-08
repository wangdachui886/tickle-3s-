package com.lightledger.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.lightledger.app.data.model.CategoryEntity
import com.lightledger.app.data.model.RawEventEntity
import com.lightledger.app.data.model.RecurringRuleEntity
import com.lightledger.app.data.model.RawEventStatus
import com.lightledger.app.data.model.SettingEntity
import com.lightledger.app.data.model.SourceProfileEntity
import com.lightledger.app.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LightLedgerDao {
    @Query("SELECT * FROM raw_events ORDER BY created_at DESC")
    fun observeRawEvents(): Flow<List<RawEventEntity>>

    @Query("SELECT * FROM raw_events WHERE status = 'pending' ORDER BY created_at DESC")
    fun observePendingRawEvents(): Flow<List<RawEventEntity>>

    @Query("SELECT * FROM transactions ORDER BY datetime DESC, created_at DESC")
    fun observeTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM categories ORDER BY display_order ASC")
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM settings ORDER BY key ASC")
    fun observeSettings(): Flow<List<SettingEntity>>

    @Query("SELECT * FROM sources ORDER BY priority DESC, display_name ASC")
    fun observeSources(): Flow<List<SourceProfileEntity>>

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun countCategories(): Int

    @Query("SELECT COUNT(*) FROM categories WHERE name = :name")
    suspend fun countCategoryByName(name: String): Int

    @Query("SELECT COALESCE(MAX(display_order), 0) FROM categories")
    suspend fun maxCategoryOrder(): Int

    @Query("SELECT COUNT(*) FROM sources")
    suspend fun countSources(): Int

    @Query("SELECT COUNT(*) FROM raw_events WHERE image_path = :imagePath")
    suspend fun countRawEventsByImagePath(imagePath: String): Int

    @Query("SELECT * FROM raw_events WHERE raw_event_id = :id LIMIT 1")
    suspend fun getRawEvent(id: String): RawEventEntity?

    @Query("SELECT * FROM raw_events WHERE status = 'pending' ORDER BY created_at DESC LIMIT 1")
    suspend fun getLatestPendingRawEvent(): RawEventEntity?

    @Query("SELECT * FROM transactions ORDER BY datetime DESC, created_at DESC")
    suspend fun getAllTransactions(): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE datetime >= :startMillis AND datetime < :endMillis ORDER BY datetime DESC, created_at DESC")
    suspend fun getTransactionsBetween(startMillis: Long, endMillis: Long): List<TransactionEntity>

    @Query("SELECT * FROM categories ORDER BY display_order ASC")
    suspend fun getAllCategories(): List<CategoryEntity>

    @Query("SELECT value FROM settings WHERE `key` = :key LIMIT 1")
    suspend fun getSettingValue(key: String): String?

    @Query("SELECT * FROM raw_events ORDER BY created_at DESC")
    suspend fun getAllRawEvents(): List<RawEventEntity>

    @Query("SELECT COUNT(*) FROM raw_events WHERE status = 'pending'")
    suspend fun countPendingRawEvents(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRawEvent(rawEvent: RawEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSources(sources: List<SourceProfileEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringRule(rule: RecurringRuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: SettingEntity)

    @Update
    suspend fun updateRawEvent(rawEvent: RawEventEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE transaction_id = :transactionId")
    suspend fun deleteTransaction(transactionId: String)

    @Query("UPDATE raw_events SET status = :status, updated_at = :updatedAt WHERE raw_event_id = :rawEventId")
    suspend fun updateRawEventStatus(rawEventId: String, status: String, updatedAt: Long)

    @Transaction
    suspend fun confirm(rawEvent: RawEventEntity, transaction: TransactionEntity) {
        insertTransaction(transaction)
        updateRawEvent(
            rawEvent.copy(
                status = RawEventStatus.CONFIRMED,
                updatedAt = transaction.createdAt,
            ),
        )
    }
}
