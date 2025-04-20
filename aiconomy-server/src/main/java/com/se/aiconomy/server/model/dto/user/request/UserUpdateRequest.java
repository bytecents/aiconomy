package com.se.aiconomy.server.model.dto.user.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户信息更新请求DTO
 * 所有字段都是可选的，只更新非null的字段
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest extends BaseRequest {
    private String email;        // 邮箱
    private String password;     // 密码
    private String avatarUrl;    // 头像URL
    private String firstName;    // 名字
    private String lastName;     // 姓氏
    private String phone;        // 手机号
    private LocalDate birthDate; // 出生日期
    private String currency;     // 货币类型
    private List<String> financialGoal; // 理财目标
    private Double monthlyIncome; // 月收入
    private List<String> mainExpenseType; // 主要开销类型

    /**
     * 检查字段是否有更新
     *
     * @return 如果有任何字段不为null则返回true
     */
    public boolean hasUpdates() {
        return email != null ||
            password != null ||
            avatarUrl != null ||
            firstName != null ||
            lastName != null ||
            phone != null ||
            birthDate != null ||
            currency != null ||
            financialGoal != null ||
            monthlyIncome != null ||
            mainExpenseType != null;
    }
}