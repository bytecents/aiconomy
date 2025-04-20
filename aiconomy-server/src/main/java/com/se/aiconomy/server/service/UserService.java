package com.se.aiconomy.server.service;

import com.se.aiconomy.server.langchain.common.model.BillType;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.model.entity.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    void register(User user); // 注册用户

    User login(String email, String password); // 用户登录

    boolean emailExists(String email); // 检查邮箱是否已存在

    User getUserById(String id); // 根据ID获取用户信息

    List<User> getAllUsers(); // 获取所有用户信息

    void updateUser(User user); // 更新用户信息

    void deleteUserById(String id); // 根据ID删除用户

    Set<DynamicBillType> addBillType(String id, DynamicBillType billType); // 添加账单类型

    Set<DynamicBillType> getBillTypes(String id); // 获取账单类型列表
}
