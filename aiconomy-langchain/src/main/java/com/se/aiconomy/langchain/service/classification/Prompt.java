package com.se.aiconomy.langchain.service.classification;


import com.se.aiconomy.langchain.common.prompt.BasePromptTemplate;

public class Prompt extends BasePromptTemplate {

    private static final String PROMPT_TEMPLATE_EN = """
        Classify the following transaction into one of the categories: ["groceries", "rent", "entertainment", "utilities", "transport", "healthcare", "education", "dining", "shopping", "other"].
        ### Transaction Details:
        - **Transaction ID:** {{ id }}
        - **Date:** {{ time }}
        - **Type:** {{ type }}
        - **Counterparty:** {{ counterparty }}
        - **Product:** {{ product }}
        - **Income/Expense:** {{ income_or_expense }}
        - **Amount:** {{ amount }} {{ currency }}
        - **Payment Method:** {{ payment_method }}
        - **Status:** {{ status }}
        - **Merchant Order ID:** {{ merchant_order_id }}
        - **Remark:** {{ remark }}
        """;

    private static final String PROMPT_TEMPLATE_CN = """
        将以下交易分类为以下类别之一：["日常消费", "房租", "娱乐", "水电费", "交通", "医疗", "教育", "餐饮", "购物", "其他"]。
        ### 交易详情：
        - **交易 ID：** {{ id }}
        - **交易时间：** {{ time }}
        - **交易类型：** {{ type }}
        - **交易对方：** {{ counterparty }}
        - **产品：** {{ product }}
        - **收支：** {{ income_or_expense }}
        - **金额：** {{ amount }} {{ currency }}
        - **支付方式：** {{ payment_method }}
        - **状态：** {{ status }}
        - **商户订单 ID：** {{ merchant_order_id }}
        - **备注：** {{ remark }}
        """;

    private static final String PROMPT_TEMPLATE_JP = """
        次の取引を以下のカテゴリのいずれかに分類してください：["日常消費", "家賃", "エンターテイメント", "公共料金", "交通", "医療", "教育", "飲食", "ショッピング", "その他"]。
        ### 取引の詳細：
        - **取引 ID：** {{ id }}
        - **取引時間：** {{ time }}
        - **取引タイプ：** {{ type }}
        - **取引先：** {{ counterparty }}
        - **製品：** {{ product }}
        - **収入または支出：** {{ income_or_expense }}
        - **金額：** {{ amount }} {{ currency }}
        - **支払い方法：** {{ payment_method }}
        - **ステータス：** {{ status }}
        - **商人注文 ID：** {{ merchant_order_id }}
        - **備考：** {{ remark }}
        """;

    public Prompt() {
        super(PROMPT_TEMPLATE_CN, PROMPT_TEMPLATE_EN, PROMPT_TEMPLATE_JP);
    }
}
