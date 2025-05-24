package com.se.aiconomy.server.langchain.service.classification;

import com.se.aiconomy.server.langchain.common.model.BillTypeRegistry;
import com.se.aiconomy.server.langchain.common.prompt.BasePromptTemplate;

/**
 * Prompt template for transaction classification in multiple languages.
 * <p>
 * This class provides prompt templates in English, Chinese, and Japanese for classifying
 * transactions into predefined categories. The templates include placeholders for transaction
 * details and dynamically insert all available category names from {@link BillTypeRegistry}.
 * </p>
 */
public class Prompt extends BasePromptTemplate {

    /**
     * English prompt template for transaction classification.
     * The template includes placeholders for transaction details and a list of all categories.
     */
    private static final String PROMPT_TEMPLATE_EN = """
            Classify the following transaction into one of the categories: %s.
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
            """.formatted(BillTypeRegistry.getInstance().getAllTypeNames());

    /**
     * Chinese prompt template for transaction classification.
     * The template includes placeholders for transaction details and a list of all categories.
     */
    private static final String PROMPT_TEMPLATE_CN = """
            将以下交易分类为以下类别之一：%s。
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
            """.formatted(BillTypeRegistry.getInstance().getAllTypeNames());

    /**
     * Japanese prompt template for transaction classification.
     * The template includes placeholders for transaction details and a list of all categories.
     */
    private static final String PROMPT_TEMPLATE_JP = """
            次の取引を以下のカテゴリのいずれかに分類してください：%s。
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
            """.formatted(BillTypeRegistry.getInstance().getAllTypeNames());

    /**
     * Constructs a new {@code Prompt} instance with all supported language templates.
     */
    public Prompt() {
        super(PROMPT_TEMPLATE_CN, PROMPT_TEMPLATE_EN, PROMPT_TEMPLATE_JP);
    }

    /**
     * Builds a string representation of all available categories.
     *
     * @return a string containing all category names
     */
    private String buildCategoriesString() {
        return BillTypeRegistry.getInstance().getAllTypeNames().toString();
    }
}