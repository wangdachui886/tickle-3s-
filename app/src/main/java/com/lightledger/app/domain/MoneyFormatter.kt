package com.lightledger.app.domain

import java.math.RoundingMode
import java.util.Locale
import kotlin.math.abs

object MoneyFormatter {
    fun centsToDisplay(cents: Long?): String {
        if (cents == null) return "--"
        val sign = if (cents < 0) "-" else ""
        val absolute = abs(cents)
        return "$sign¥${absolute / 100}.${(absolute % 100).toString().padStart(2, '0')}"
    }

    fun centsToCsvAmount(cents: Long): String {
        return String.format(Locale.US, "%.2f", cents / 100.0)
    }

    fun yuanToCents(raw: String): Long? {
        val normalized = raw.replace(",", "").trim()
        return normalized.toBigDecimalOrNull()
            ?.movePointRight(2)
            ?.setScale(0, RoundingMode.HALF_UP)
            ?.toLong()
    }
}
