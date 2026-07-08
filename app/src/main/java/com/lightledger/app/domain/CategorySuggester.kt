package com.lightledger.app.domain

object CategorySuggester {
    private val merchantRules = listOf(
        Rule("餐饮", listOf("餐厅", "饭", "面", "粉", "咖啡", "奶茶", "麦当劳", "肯德基", "瑞幸", "星巴克", "外卖")),
        Rule("交通", listOf("高德", "滴滴", "打车", "地铁", "公交", "铁路", "12306", "航旅", "出租", "单车", "骑行", "哈啰", "哈罗", "青桔", "美团单车")),
        Rule("购物", listOf("淘宝", "天猫", "京东", "拼多多", "抖音", "得物", "小红书", "商城", "超市")),
        Rule("服饰", listOf("衣服", "服饰", "服装", "鞋", "包", "优衣库", "zara", "hm")),
        Rule("通讯", listOf("移动", "联通", "电信", "话费", "流量")),
        Rule("生活缴费", listOf("房租", "物业", "水费", "电费", "燃气")),
        Rule("医疗", listOf("医院", "药房", "药店", "体检", "医保")),
        Rule("娱乐", listOf("电影", "影院", "游戏", "会员", "视频")),
        Rule("教育", listOf("课程", "书店", "图书", "教育", "ChatGPT", "OpenAI")),
    )

    fun suggest(merchant: String?, sourceApp: String?, rawText: String?): String {
        val haystack = listOfNotNull(merchant, sourceApp, rawText).joinToString(" ")
        if (haystack.isBlank()) return "其他"
        return merchantRules.firstOrNull { rule ->
            rule.keywords.any { keyword -> haystack.contains(keyword, ignoreCase = true) }
        }?.category ?: "其他"
    }

    fun quickCategories(suggested: String?): List<String> {
        val base = listOf("餐饮", "交通", "购物", "娱乐", "书籍", "宠物", "房租", "服务", "日用品", "其他")
        if (suggested.isNullOrBlank() || suggested == "其他") return base
        return (listOf(suggested) + base).distinct()
    }

    private data class Rule(
        val category: String,
        val keywords: List<String>,
    )
}
