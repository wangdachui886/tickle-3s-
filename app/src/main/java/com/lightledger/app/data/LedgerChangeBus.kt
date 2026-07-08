package com.lightledger.app.data

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object LedgerChangeBus {
    private val _changes = MutableSharedFlow<Unit>(extraBufferCapacity = 4)
    val changes = _changes.asSharedFlow()

    fun notifyChanged() {
        _changes.tryEmit(Unit)
    }
}
