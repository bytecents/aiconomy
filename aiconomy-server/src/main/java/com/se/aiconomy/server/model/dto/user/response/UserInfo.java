package com.se.aiconomy.server.model.dto.user.response;

import com.se.aiconomy.server.langchain.common.model.BillType;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class UserInfo {
    private String id; // 唯一标识符
    private String email; // 邮箱
    private String avatarUrl; // 头像
    private String firstName; // 名字
    private String lastName; // 姓氏
    private String phone; // 手机号
    private LocalDate birthDate; // 出生日期
    private String currency; // 例如 USD, EUR, CNY
    private List<String> financialGoal; // 理财目标：储蓄、投资、买房……
    private Double monthlyIncome; // 月收入
    private List<String> mainExpenseType; // 主要开销：房租、教育、交通等
    private Set<DynamicBillType> billTypes; // 账单类型
}
