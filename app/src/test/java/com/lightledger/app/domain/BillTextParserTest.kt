package com.lightledger.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BillTextParserTest {
    @Test
    fun parseWechatPaymentText() {
        val parsed = BillTextParser.parse(
            rawText = "微信支付：向 XX餐厅 支付 38.00 元",
            now = 1_803_000_000_000,
        )

        assertEquals(3_800L, parsed.amountCents)
        assertEquals("XX餐厅", parsed.merchant)
        assertEquals("微信", parsed.sourceApp)
        assertEquals("餐饮", parsed.suggestedCategory)
        assertTrue(parsed.confidence >= 0.9f)
    }

    @Test
    fun parseAlipayTransportText() {
        val parsed = BillTextParser.parse(
            rawText = "支付宝通知：在高德打车消费 26.40 元",
            now = 1_803_000_000_000,
        )

        assertEquals(2_640L, parsed.amountCents)
        assertEquals("高德打车", parsed.merchant)
        assertEquals("支付宝", parsed.sourceApp)
        assertEquals("交通", parsed.suggestedCategory)
    }
}
