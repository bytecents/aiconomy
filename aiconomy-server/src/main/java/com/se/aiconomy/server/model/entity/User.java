package com.se.aiconomy.server.model.entity;

import com.se.aiconomy.server.storage.common.Identifiable;
import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import io.jsondb.annotation.Secret;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
@Document(collection = "User", schemaVersion = "1.0")
public class User implements Identifiable{
    @Id
    @Setter
    private String id; // 唯一标识符
    @Setter
    private String email; // 邮箱
    @Setter
    @Secret
    private String password; // 密码
    @Setter
    private String avatarUrl; // 头像
    @Setter
    private String firstName; // 名字
    @Setter
    private String lastName; // 姓氏
    @Setter
    private String phone; // 手机号
    @Setter
    private LocalDate birthDate; // 出生日期
    @Setter
    private String currency; // 例如 USD, EUR, CNY
    @Setter
    private String financialGoal; // 理财目标：储蓄、投资、买房……
    @Setter
    private Double monthlyIncome; // 月收入
    @Setter
    private String mainExpenseType; // 主要开销：房租、教育、交通等

    private Map<String, Object> extraFields = new HashMap<>();

    public User(){}

    public User(String id, String email, String password, String avatarUrl,
                String firstName, String lastName, String phone, LocalDate birthDate,
                String currency, String financialGoal, Double monthlyIncome, String mainExpenseType) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.avatarUrl = avatarUrl;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.birthDate = birthDate;
        this.currency = currency;
        this.financialGoal = financialGoal;
        this.monthlyIncome = monthlyIncome;
        this.mainExpenseType = mainExpenseType;
    }

    // 添加扩展字段
    public void addExtraField(String key, Object value) {
        extraFields.put(key, value);
    }

    // 获取扩展字段
    public Object getExtraField(String key) {
        return extraFields.get(key);
    }
}
