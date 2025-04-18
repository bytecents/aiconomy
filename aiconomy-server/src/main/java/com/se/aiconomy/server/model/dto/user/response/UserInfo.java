package com.se.aiconomy.server.model.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
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
}
