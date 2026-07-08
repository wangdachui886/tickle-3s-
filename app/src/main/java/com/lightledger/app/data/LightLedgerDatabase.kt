package com.lightledger.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lightledger.app.data.model.CategoryEntity
import com.lightledger.app.data.model.RawEventEntity
import com.lightledger.app.data.model.RecurringRuleEntity
import com.lightledger.app.data.model.SettingEntity
import com.lightledger.app.data.model.SourceProfileEntity
import com.lightledger.app.data.model.TransactionEntity

@Database(
    entities = [
        RawEventEntity::class,
        TransactionEntity::class,
        CategoryEntity::class,
        SourceProfileEntity::class,
        RecurringRuleEntity::class,
        SettingEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class LightLedgerDatabase : RoomDatabase() {
    abstract fun lightLedgerDao(): LightLedgerDao

    companion object {
        fun create(context: Context): LightLedgerDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                LightLedgerDatabase::class.java,
                "light_ledger.db",
            )
                .addMigrations(Migration1To2)
                .enableMultiInstanceInvalidation()
                .build()
        }

        private val Migration1To2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE raw_events ADD COLUMN direction TEXT NOT NULL DEFAULT 'out'")
                db.execSQL("ALTER TABLE raw_events ADD COLUMN ledger_type TEXT NOT NULL DEFAULT 'expense'")
                db.execSQL("ALTER TABLE raw_events ADD COLUMN is_consumption INTEGER NOT NULL DEFAULT 1")
            }
        }
    }
}
