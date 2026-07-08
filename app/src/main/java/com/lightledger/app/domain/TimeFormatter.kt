package com.lightledger.app.domain

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeFormatter {
    private val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
    private val month = SimpleDateFormat("yyyy-MM", Locale.CHINA)

    fun formatDateTime(epochMillis: Long): String = dateTime.format(Date(epochMillis))

    fun formatMonth(epochMillis: Long): String = month.format(Date(epochMillis))
}
