package com.lightledger.app.domain

data class ParsedBill(
    val amountCents: Long?,
    val merchant: String?,
    val sourceApp: String,
    val confidence: Float,
    val detectedTime: Long,
    val suggestedCategory: String,
)

object BillTextParser {
    private val amountPatterns = listOf(
        Regex("""(?:交易金额|订单金额|实付|金额|支出|消费|扣款|扣费|支付|付款|付款成功|支付成功|收款|退款)[^\d¥￥-]{0,18}(?:人民币|RMB|CNY|¥|￥)?\s*(?:活期)?\s*[-−]?\s*([0-9][0-9,]*(?:\.[0-9]{1,2})?)\s*(?:元|CNY)?""", RegexOption.IGNORE_CASE),
        Regex("""(?:人民币|RMB|CNY|¥|￥)\s*(?:活期)?\s*[-−]?\s*([0-9][0-9,]*(?:\.[0-9]{1,2})?)""", RegexOption.IGNORE_CASE),
        Regex("""[-−]?\s*([0-9][0-9,]*(?:\.[0-9]{1,2})?)\s*元"""),
    )

    private val merchantPatterns = listOf(
        Regex("""(?:向|在|商户|收款方|交易对象|对方|店铺|订单)[:：\s]*(.{2,40}?)(?:支付|付款|消费|扣款|扣费|成功|，|,|。|\n|$)"""),
        Regex("""(?:交易说明|商品说明|订单说明)[:：\s]*(.{2,40}?)(?:，|,|。|\n|$)"""),
        Regex("""(?:您在|您向)\s*(.{2,40}?)\s*(?:消费|支付|付款|扣款|扣费)"""),
    )

    private val billKeywords = listOf(
        "交易提醒",
        "交易金额",
        "支出",
        "消费",
        "扣款",
        "扣费",
        "支付",
        "付款",
        "退款",
        "尾号",
        "人民币",
        "¥",
        "￥",
        "订单",
        "实付",
        "支付成功",
        "付款成功",
    )

    fun parse(rawText: String, now: Long = System.currentTimeMillis()): ParsedBill {
        val clean = normalize(rawText)
        val source = detectSource(clean)
        val amount = detectAmount(clean)
        val merchant = detectMerchant(clean) ?: detectBankName(clean) ?: detectPlatformName(clean)
        val suggestedCategory = CategorySuggester.suggest(merchant, source, clean)

        return ParsedBill(
            amountCents = amount,
            merchant = merchant,
            sourceApp = source,
            confidence = buildConfidence(amount, merchant, source, clean),
            detectedTime = now,
            suggestedCategory = suggestedCategory,
        )
    }

    fun looksLikeBill(rawText: String): Boolean {
        val clean = normalize(rawText)
        return billKeywords.any { clean.contains(it, ignoreCase = true) } && detectAmount(clean) != null
    }

    private fun normalize(rawText: String): String {
        return rawText
            .replace("−", "-")
            .replace(Regex("""[ \t\r]+"""), " ")
            .trim()
    }

    private fun detectSource(text: String): String {
        return detectPlatformName(text)
            ?: detectBankName(text)
            ?: "账单文本"
    }

    private fun detectPlatformName(text: String): String? {
        return when {
            text.contains("微信", ignoreCase = true) || text.contains("WeChat", ignoreCase = true) -> "微信"
            text.contains("支付宝", ignoreCase = true) || text.contains("Alipay", ignoreCase = true) -> "支付宝"
            text.contains("美团", ignoreCase = true) -> "美团"
            text.contains("淘宝", ignoreCase = true) || text.contains("天猫", ignoreCase = true) -> "淘宝"
            text.contains("拼多多", ignoreCase = true) -> "拼多多"
            text.contains("京东", ignoreCase = true) -> "京东"
            text.contains("高德", ignoreCase = true) -> "高德"
            text.contains("哈啰", ignoreCase = true) || text.contains("哈罗", ignoreCase = true) -> "哈啰"
            text.contains("青桔", ignoreCase = true) -> "青桔"
            text.contains("单车", ignoreCase = true) || text.contains("骑行", ignoreCase = true) -> "共享单车"
            else -> null
        }
    }

    private fun detectBankName(text: String): String? {
        return when {
            text.contains("浦发", ignoreCase = true) -> "浦发银行"
            text.contains("招商", ignoreCase = true) || text.contains("招行", ignoreCase = true) -> "招商银行"
            text.contains("建设银行", ignoreCase = true) || text.contains("建行", ignoreCase = true) -> "建设银行"
            text.contains("工商银行", ignoreCase = true) || text.contains("工行", ignoreCase = true) -> "工商银行"
            text.contains("农业银行", ignoreCase = true) || text.contains("农行", ignoreCase = true) -> "农业银行"
            text.contains("中国银行", ignoreCase = true) || text.contains("中行", ignoreCase = true) -> "中国银行"
            text.contains("交通银行", ignoreCase = true) -> "交通银行"
            text.contains("中信", ignoreCase = true) -> "中信银行"
            text.contains("广发", ignoreCase = true) -> "广发银行"
            text.contains("平安", ignoreCase = true) -> "平安银行"
            text.contains("民生", ignoreCase = true) -> "民生银行"
            text.contains("兴业", ignoreCase = true) -> "兴业银行"
            text.contains("邮储", ignoreCase = true) -> "邮储银行"
            else -> null
        }
    }

    private fun detectAmount(text: String): Long? {
        return amountPatterns.firstNotNullOfOrNull { pattern ->
            pattern.find(text)
                ?.groupValues
                ?.getOrNull(1)
                ?.let(MoneyFormatter::yuanToCents)
        }?.takeIf { it > 0L }
    }

    private fun detectMerchant(text: String): String? {
        val fromPattern = merchantPatterns.firstNotNullOfOrNull { pattern ->
            pattern.find(text)?.groupValues?.getOrNull(1)?.trim()
        }
        return sanitizeMerchant(fromPattern)
    }

    private fun sanitizeMerchant(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val cleaned = raw
            .replace(Regex("""[，,。；;].*$"""), "")
            .replace(Regex("""\s+"""), " ")
            .trim(' ', '：', ':', '-', '·')
            .take(40)
        val badValues = setOf("交易提醒", "支付成功", "付款成功", "网上支付", "人民币活期")
        return cleaned
            .takeIf { it.length >= 2 && it !in badValues }
    }

    private fun buildConfidence(amount: Long?, merchant: String?, source: String, text: String): Float {
        var score = 0.15f
        if (amount != null) score += 0.45f
        if (!merchant.isNullOrBlank()) score += 0.2f
        if (source != "账单文本") score += 0.1f
        if (billKeywords.any { text.contains(it, ignoreCase = true) }) score += 0.1f
        return score.coerceIn(0f, 0.95f)
    }
}
