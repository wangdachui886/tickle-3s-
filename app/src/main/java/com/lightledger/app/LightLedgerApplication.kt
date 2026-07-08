package com.lightledger.app

import android.app.Application
import com.lightledger.app.data.LightLedgerDatabase
import com.lightledger.app.data.LightLedgerRepository

class LightLedgerApplication : Application() {
    val database: LightLedgerDatabase by lazy {
        LightLedgerDatabase.create(this)
    }

    val repository: LightLedgerRepository by lazy {
        LightLedgerRepository(
            dao = database.lightLedgerDao(),
            appContext = this,
        )
    }
}
