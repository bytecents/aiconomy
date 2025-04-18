package com.se.aiconomy.server.langchain.service.analysis.budget;

import com.se.aiconomy.server.langchain.common.prompt.BasePromptTemplate;

public class Prompt extends BasePromptTemplate {

    private static final String PROMPT_TEMPLATE_EN = """
        Budget Information:
        -------------------
        Total Budget: {{ total_budget }}
        Amount Spent: {{ spent }}
        Alerts: {{ alerts }}

        Category Budgets:
        {% for category in category_budgets %}
        - Category: {{ category.category }}
          Budget: {{ category.budget }}
          Spent: {{ category.spent }}
          Left: {{ category.left }}
          Percentage Used: {{ category.percentage_used }}%
          Over Budget: {{ category.over_budget }}
        {% endfor %}

        Please analyze the provided budget information and generate AI-based recommendations to improve financial health. Provide potential savings, prioritize actions, and suggest budget reallocations where applicable.
        """;

    private static final String PROMPT_TEMPLATE_CN = """
        预算信息：
        -------------------
        总预算：{{ total_budget }}
        已花费金额：{{ spent }}
        警报：{{ alerts }}

        类别预算：
        {% for category in category_budgets %}
        - 类别：{{ category.category }}
          预算：{{ category.budget }}
          已花费：{{ category.spent }}
          剩余：{{ category.left }}
          使用百分比：{{ category.percentage_used }}%
          是否超预算：{{ category.over_budget }}
        {% endfor %}

        请分析提供的预算信息，并生成基于AI的建议来改善财务健康。提供潜在节省额、优先采取的行动，并在必要时建议预算重新分配。
        """;


    private static final String PROMPT_TEMPLATE_JP = """
        予算情報：
        -------------------
        総予算：{{ total_budget }}
        使用金額：{{ spent }}
        警告：{{ alerts }}

        カテゴリ別予算：
        {% for category in category_budgets %}
        - カテゴリ：{{ category.category }}
          予算：{{ category.budget }}
          使用額：{{ category.spent }}
          残額：{{ category.left }}
          使用率：{{ category.percentage_used }}%
          予算オーバー：{{ category.over_budget }}
        {% endfor %}

        提供された予算情報を分析し、財務健康を改善するためのAIベースの提案を生成してください。潜在的な節約額を提供し、行動を優先し、必要に応じて予算の再配分を提案してください。
        """;


    public Prompt() {
        super(PROMPT_TEMPLATE_CN, PROMPT_TEMPLATE_EN, PROMPT_TEMPLATE_JP);
    }
}
