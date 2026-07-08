package com.lightledger.app.data

import com.lightledger.app.data.model.CategoryEntity
import com.lightledger.app.data.model.SourceProfileEntity

object SeedData {
    val categories = listOf(
        CategoryEntity("food", "餐饮", 10, true),
        CategoryEntity("transport", "交通", 20, true),
        CategoryEntity("clothing", "服饰", 30, true),
        CategoryEntity("shopping", "购物", 40, true),
        CategoryEntity("daily", "日用品", 50, true),
        CategoryEntity("service", "服务", 60, false),
        CategoryEntity("education", "教育", 70, false),
        CategoryEntity("entertainment", "娱乐", 80, false),
        CategoryEntity("sports", "运动", 90, false),
        CategoryEntity("housing", "房租", 100, false),
        CategoryEntity("utilities", "生活缴费", 110, false),
        CategoryEntity("telecom", "通讯", 120, false),
        CategoryEntity("health", "医疗", 130, false),
        CategoryEntity("travel", "旅行", 140, false),
        CategoryEntity("pet", "宠物", 150, false),
        CategoryEntity("insurance", "保险", 160, false),
        CategoryEntity("charity", "公益", 170, false),
        CategoryEntity("red_packet", "发红包", 180, false),
        CategoryEntity("transfer", "转账", 190, false),
        CategoryEntity("family_card", "亲属卡", 200, false),
        CategoryEntity("relationship", "其他人情", 210, false),
        CategoryEntity("refund_out", "退还", 220, false),
        CategoryEntity("gift", "礼物", 260, false),
        CategoryEntity("beauty", "美容", 230, false),
        CategoryEntity("digital", "数码", 240, false),
        CategoryEntity("repayment", "还款", 250, false),
        CategoryEntity("other", "其他", 999, true),
    )

    val sources = listOf(
        SourceProfileEntity("wechat", "微信", "com.tencent.mm", "payment", "csv", true, null, "manual_v1", 100),
        SourceProfileEntity("alipay", "支付宝", "com.eg.android.AlipayGphone", "payment", "csv", true, null, "manual_v1", 95),
        SourceProfileEntity("unionpay", "云闪付", "com.unionpay", "payment", "csv", false, null, "manual_v1", 75),
        SourceProfileEntity("meituan", "美团", "com.sankuai.meituan", "shopping", "csv", true, "餐饮", "manual_v1", 70),
        SourceProfileEntity("pinduoduo", "拼多多", "com.xunmeng.pinduoduo", "shopping", "csv", true, "购物", "manual_v1", 65),
        SourceProfileEntity("gaode", "高德", "com.autonavi.minimap", "transport", "csv", true, "交通", "manual_v1", 60),
    )
}
