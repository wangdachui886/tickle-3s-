package com.lightledger.app.data.model

object SourceType {
    const val NOTIFICATION = "notification"
    const val CSV = "csv"
    const val MANUAL = "manual"
    const val RECURRING = "recurring"
}

object RawEventStatus {
    const val PENDING = "pending"
    const val CONFIRMED = "confirmed"
    const val IGNORED = "ignored"
    const val DUPLICATE = "duplicate"
    const val FAILED = "failed"
}

object LedgerType {
    const val EXPENSE = "expense"
    const val INCOME = "income"
    const val TRANSFER = "transfer"
    const val REFUND = "refund"
    const val REIMBURSEMENT = "reimbursement"
    const val INVESTMENT = "investment"
    const val LOAN_OUT = "loan_out"
    const val LOAN_IN = "loan_in"
    const val ADJUSTMENT = "adjustment"
}

object MoneyDirection {
    const val OUT = "out"
    const val IN = "in"
    const val NEUTRAL = "neutral"
    const val UNKNOWN = "unknown"
}
