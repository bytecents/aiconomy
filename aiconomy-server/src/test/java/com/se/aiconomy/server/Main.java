package com.se.aiconomy.server;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.handler.AccountRequestHandler;
import com.se.aiconomy.server.handler.BudgetRequestHandler;
import com.se.aiconomy.server.handler.TransactionRequestHandler;
import com.se.aiconomy.server.handler.UserRequestHandler;
import com.se.aiconomy.server.langchain.common.model.BillType;
import com.se.aiconomy.server.langchain.common.model.Budget;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.dto.account.request.AddAccountsRequest;
import com.se.aiconomy.server.model.dto.account.request.DeleteAccountRequest;
import com.se.aiconomy.server.model.dto.account.request.GetAccountsByUserIdRequest;
import com.se.aiconomy.server.model.dto.budget.request.BudgetAddRequest;
import com.se.aiconomy.server.model.dto.budget.request.BudgetAnalysisRequest;
import com.se.aiconomy.server.model.dto.transaction.request.GetTransactionByUserIdRequest;
import com.se.aiconomy.server.model.dto.transaction.request.TransactionClassificationRequest;
import com.se.aiconomy.server.model.dto.transaction.request.TransactionImportRequest;
import com.se.aiconomy.server.model.dto.user.request.UserInfoRequest;
import com.se.aiconomy.server.model.dto.user.request.UserLoginRequest;
import com.se.aiconomy.server.model.dto.user.request.UserRegisterRequest;
import com.se.aiconomy.server.model.dto.user.response.UserInfo;
import com.se.aiconomy.server.model.entity.Account;
import com.se.aiconomy.server.service.UserService;
import com.se.aiconomy.server.service.impl.UserServiceImpl;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Main {
    static JSONStorageService jsonStorageService = JSONStorageServiceImpl.getInstance();
    static UserService userService = new UserServiceImpl(jsonStorageService);
    static UserRequestHandler userRequestHandler = new UserRequestHandler(userService);
    static TransactionRequestHandler transactionRequestHandler = new TransactionRequestHandler();
    static AccountRequestHandler accountRequestHandler = new AccountRequestHandler();
    static BudgetRequestHandler budgetRequestHandler = new BudgetRequestHandler();

    public static void main(String[] args) throws ServiceException {
        // 注册测试
        /*UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
                .email("2022213663@bupt.cn")
                .password("test")
                .firstName("Yujie")
                .lastName("Yang")
                .phoneNumber("13152810650")
                .birthDate(LocalDate.of(2004, 1, 1))
                .currency("USD")
                .financialGoal(List.of("Save for retirement", "Buy a house"))
                .monthlyIncome(10000.0)
                .mainExpenseType(List.of("Rent", "Groceries"))
                .build();

        UserInfo newUserInfo = userRequestHandler.handleRegisterRequest(userRegisterRequest);

        UserInfoRequest userInfoRequest = new UserInfoRequest();
        userInfoRequest.setUserId(newUserInfo.getId());

        UserInfo userinfo = userRequestHandler.handleGetUserInfoRequest(userInfoRequest);
        System.out.println(userinfo);*/

        // 登录
        UserInfo userInfo = userRequestHandler.handleLoginRequest(UserLoginRequest.builder().email("2022213661@bupt.cn").password("test").build());

        // 用户注销测试
        /*UserDeleteRequest userDeleteRequest = new UserDeleteRequest();
        userDeleteRequest.setUserId("87b91bbf-ee39-4a23-9ba6-b8d5f7e685e9");
        System.out.println(userRequestHandler.handleDeleteUserRequest(userDeleteRequest));*/

        // Transaction 分类测试 + 导入测试
        /*TransactionClassificationRequest transactionClassificationRequest = new TransactionClassificationRequest();
        transactionClassificationRequest.setUserId(userInfo.getId());
        String filePath = Objects.requireNonNull(Main.class.getClassLoader().getResource("transactions.csv")).getPath();
        transactionClassificationRequest.setFilePath(filePath);
        List<Map<Transaction, DynamicBillType>> classifiedTransactions = transactionRequestHandler.handleTransactionClassificationRequest(transactionClassificationRequest);

        TransactionImportRequest transactionImportRequest = new TransactionImportRequest();
        transactionImportRequest.setUserId(userInfo.getId());
        transactionImportRequest.setAccountId("1");
        transactionImportRequest.setTransactions(classifiedTransactions);
        List<Map<Transaction, DynamicBillType>> importedTransactions = transactionRequestHandler.handleTransactionImportRequest(transactionImportRequest);

        for (Map<Transaction, DynamicBillType> transaction : importedTransactions) {
            for (Map.Entry<Transaction, DynamicBillType> entry : transaction.entrySet()) {
                System.out.println("Transaction: " + entry.getKey());
                System.out.println("BillType: " + entry.getValue());
            }
        }*/

        // 根据用户ID获取交易记录测试
        /*GetTransactionByUserIdRequest getTransactionByUserIdRequest = new GetTransactionByUserIdRequest();
        getTransactionByUserIdRequest.setUserId(userInfo.getId());
        List<TransactionDto> transactionDtos = transactionRequestHandler.handleGetTransactionsByUserId(getTransactionByUserIdRequest);
        for (TransactionDto transactionDto : transactionDtos) {
            System.out.println(transactionDto);
        }*/

        // 添加Account测试
        /*List<Account> accounts = List.of(
                Account.builder()
                        .id("1")
                        .userId(userInfo.getId())
                        .bankName("Bank of America")
                        .accountType("Saving")
                        .accountName("John's Savings Account")
                        .balance(5000.00)
                        .creditLimit(2000.00)
                        .currentDebt(300.00)
                        .paymentDueDate(LocalDateTime.of(2025, 5, 15, 23, 59))
                        .minimumPayment(50.00)
                        .build(),

                Account.builder()
                        .id("2")
                        .userId(userInfo.getId())
                        .bankName("Chase")
                        .accountType("Checking")
                        .accountName("Alice's Checking Account")
                        .balance(1000.00)
                        .creditLimit(5000.00)
                        .currentDebt(1200.00)
                        .paymentDueDate(LocalDateTime.of(2025, 6, 1, 23, 59))
                        .minimumPayment(100.00)
                        .build(),

                Account.builder()
                        .id("3")
                        .userId(userInfo.getId())
                        .bankName("Wells Fargo")
                        .accountType("Credit")
                        .accountName("Bob's Credit Account")
                        .balance(0.00)
                        .creditLimit(15000.00)
                        .currentDebt(5000.00)
                        .paymentDueDate(LocalDateTime.of(2025, 4, 30, 23, 59))
                        .minimumPayment(250.00)
                        .build()
        );

        AddAccountsRequest addAccountsRequest = new AddAccountsRequest();
        addAccountsRequest.setUserId(userInfo.getId());
        addAccountsRequest.setAccounts(accounts);

        List<Account> accounts1 = accountRequestHandler.handleAddAccountRequest(addAccountsRequest);
        for (Account account : accounts1) {
            System.out.println(account);
        }*/

        // 根据用户ID获取账户信息测试
        /*List<Account> accounts = accountRequestHandler.handleGetAccountsByUserIdRequest(new GetAccountsByUserIdRequest(userInfo.getId()));

        for (Account account : accounts) {
            System.out.println(account);
        }*/

        // 删除账户测试
        /*for (Account account : accounts) {
            DeleteAccountRequest deleteAccountRequest = new DeleteAccountRequest();
            deleteAccountRequest.setUserId(userInfo.getId());
            deleteAccountRequest.setAccountId(account.getId());
            System.out.println(accountRequestHandler.handleDeleteAccountRequest(deleteAccountRequest));
        }

        System.out.println(accountRequestHandler.handleGetAccountsByUserIdRequest(new GetAccountsByUserIdRequest(userInfo.getId())));*/

        // 获取账单类别测试
        /*Set<DynamicBillType> billTypes = userRequestHandler.getBillTypes(userInfo.getId());
        for (DynamicBillType billType : billTypes) {
            System.out.println(billType);
        }*/

        // 添加账单类别测试
        /*Set<DynamicBillType> billTypes = userRequestHandler.addBillType(userInfo.getId(), DynamicBillType.createCustom("CustomBillType", "Custom Bill Type"));
        for (DynamicBillType billType : billTypes) {
            System.out.println(billType);
        }*/

        // 添加budgets测试
        /*budgetRequestHandler.handleBudgetAddRequest(new BudgetAddRequest(
                userInfo.getId(),
                "Transportation",
                1000.0,
                80.0,
                "notes"
        ));*/

        // AI Optimize测试
        /*BudgetAnalysisRequest budgetAnalysisRequest = new BudgetAnalysisRequest();
        budgetAnalysisRequest.setUserId(userInfo.getId());
        Budget.AIAnalysis aiAnalysis = budgetRequestHandler.handleBudgetAnalysisRequest(budgetAnalysisRequest);
        System.out.println("AI Analysis: " + aiAnalysis);*/
    }
}