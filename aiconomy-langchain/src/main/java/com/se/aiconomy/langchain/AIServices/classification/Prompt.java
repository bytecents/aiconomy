package com.se.aiconomy.langchain.AIServices.classification;


import com.se.aiconomy.langchain.common.prompt.BasePromptTemplate;

public class Prompt extends BasePromptTemplate {

    private static final String PROMPT_TEMPLATE_CN = """
        将以下交易分类为以下类别之一：["日常消费", "房租", "娱乐", "水电费", "交通", "医疗", "教育", "餐饮", "购物", "其他"]。
        ### 交易详情：
        - **交易时间：** {{ transaction_time }}
        - **交易类型：** {{ transaction_type }}
        - **交易对方：** {{ counterparty }}
        - **产品：** {{ product }}
        - **收支：** {{ income_or_expense }}
        - **金额：** {{ amount }} {{ currency }}
        - **支付方式：** {{ payment_method }}
        - **状态：** {{ status }}
        - **交易 ID：** {{ transaction_id }}
        - **商户订单 ID：** {{ merchant_order_id }}
        - **备注：** {{ remark }}
        {% if extra_fields %}
        ### 其他信息：
        {% for key, value in extra_fields.items() %}
        - **{{ key }}：** {{ value }}
        {% endfor %}
        {% endif %}
        """;

    private static final String PROMPT_TEMPLATE_EN = """
        Classify the following transaction into one of the categories: ["groceries", "rent", "entertainment", "utilities", "transport", "healthcare", "education", "dining", "shopping", "other"].
        ### Transaction Details:
        - **Date:** {{ transaction_time }}
        - **Type:** {{ transaction_type }}
        - **Counterparty:** {{ counterparty }}
        - **Product:** {{ product }}
        - **Income/Expense:** {{ income_or_expense }}
        - **Amount:** {{ amount }} {{ currency }}
        - **Payment Method:** {{ payment_method }}
        - **Status:** {{ status }}
        - **Transaction ID:** {{ transaction_id }}
        - **Merchant Order ID:** {{ merchant_order_id }}
        - **Remark:** {{ remark }}
        {% if extra_fields %}
        ### Additional Information:
        {% for key, value in extra_fields.items() %}
        - **{{ key }}:** {{ value }}
        {% endfor %}
        {% endif %}
        """;

    private static final String PROMPT_TEMPLATE_JP = """
        次の取引を以下のカテゴリのいずれかに分類してください：["日常消費", "家賃", "エンターテイメント", "公共料金", "交通", "医療", "教育", "飲食", "ショッピング", "その他"]。
        ### 取引の詳細：
        - **取引時間：** {{ transaction_time }}
        - **取引タイプ：** {{ transaction_type }}
        - **取引先：** {{ counterparty }}
        - **製品：** {{ product }}
        - **収入または支出：** {{ income_or_expense }}
        - **金額：** {{ amount }} {{ currency }}
        - **支払い方法：** {{ payment_method }}
        - **ステータス：** {{ status }}
        - **取引 ID：** {{ transaction_id }}
        - **商人注文 ID：** {{ merchant_order_id }}
        - **備考：** {{ remark }}
        {% if extra_fields %}
        ### 追加情報：
        {% for key, value in extra_fields.items() %}
        - **{{ key }}：** {{ value }}
        {% endfor %}
        {% endif %}
        """;

    public Prompt() {
        super(PROMPT_TEMPLATE_CN, PROMPT_TEMPLATE_EN, PROMPT_TEMPLATE_JP);
    }
}
