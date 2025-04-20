package com.se.aiconomy.server.model.entity;

import com.se.aiconomy.server.langchain.common.model.BillTypeRegistry;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.storage.common.Identifiable;
import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import io.jsondb.annotation.Secret;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@Document(collection = "User", schemaVersion = "1.0")
@NoArgsConstructor
@AllArgsConstructor
public class User implements Identifiable {
    @Id
    private String id; // 唯一标识符
    private String email; // 邮箱
    @Secret
    private String password; // 密码
    private String avatarUrl; // 头像
    private String firstName; // 名字
    private String lastName; // 姓氏
    private String phone; // 手机号
    private LocalDate birthDate; // 出生日期
    private String currency; // 例如 USD, EUR, CNY
    private List<String> financialGoal; // 理财目标：储蓄、投资、买房……
    private Double monthlyIncome; // 月收入
    private List<String> mainExpenseType; // 主要开销：房租、教育、交通等
    private Set<DynamicBillType> billTypes; // 改用 DynamicBillType

    public void addBillType(String type, String displayName) {
        if (billTypes == null) {
            billTypes = new HashSet<>();
        }
        DynamicBillType newType = BillTypeRegistry.getInstance()
            .registerCustomType(type, displayName);
        billTypes.add(newType);
    }
}
