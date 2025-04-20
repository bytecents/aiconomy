package com.se.aiconomy.server;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.handler.AccountRequestHandler;
import com.se.aiconomy.server.handler.TransactionRequestHandler;
import com.se.aiconomy.server.handler.UserRequestHandler;
import com.se.aiconomy.server.langchain.common.model.BillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.dto.account.request.AddAccountsRequest;
import com.se.aiconomy.server.model.dto.account.request.DeleteAccountRequest;
import com.se.aiconomy.server.model.dto.account.request.GetAccountsByUserIdRequest;
import com.se.aiconomy.server.model.dto.transaction.request.GetTransactionByUserIdRequest;
import com.se.aiconomy.server.model.dto.transaction.request.TransactionClassificationRequest;
import com.se.aiconomy.server.model.dto.transaction.request.TransactionImportRequest;
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

public class Main {
    static JSONStorageService jsonStorageService = JSONStorageServiceImpl.getInstance();
    static UserService userService = new UserServiceImpl(jsonStorageService);
    static UserRequestHandler userRequestHandler = new UserRequestHandler(userService);
    static TransactionRequestHandler transactionRequestHandler = new TransactionRequestHandler();
    static AccountRequestHandler accountRequestHandler = new AccountRequestHandler();

    public static void main(String[] args) throws ServiceException {
        // 注册测试
        /*UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
            .email("2022213670@bupt.cn")
            .password("123456")
            .firstName("John")
            .lastName("Doe")
            .phoneNumber("1234567890")
            .birthDate(LocalDate.of(1990, 1, 1))
            .currency("USD")
            .financialGoal(List.of("Save for retirement", "Buy a house"))
            .monthlyIncome(5000.0)
            .mainExpenseType(List.of("Rent", "Groceries"))
            .build();
        System.out.println(userRequestHandler.handleRegisterRequest(userRegisterRequest));

        UserInfoRequest userInfoRequest = new UserInfoRequest();
        userInfoRequest.setUserId("6199fb65-6dbd-443b-98c2-909fcf1c92d4");

        UserInfo userinfo = userRequestHandler.handleGetUserInfoRequest(userInfoRequest);
        System.out.println(userinfo);*/

        // 登录
        UserInfo userInfo = userRequestHandler.handleLoginRequest(UserLoginRequest.builder().email("2022213670@bupt.cn").password("123456").build());

        // 用户注销测试
        /*UserDeleteRequest userDeleteRequest = new UserDeleteRequest();
        userDeleteRequest.setUserId("6199fb65-6dbd-443b-98c2-909fcf1c92d4");
        System.out.println(userRequestHandler.handleDeleteUserRequest(userDeleteRequest));*/

        // Transaction 分类测试
        /*TransactionClassificationRequest transactionClassificationRequest = new TransactionClassificationRequest();
        transactionClassificationRequest.setUserId(userInfo.getId());
        transactionClassificationRequest.setFilePath("/Users/charles/GitHub/Bytecents/aiconomy/aiconomy-server/src/test/resources/transactions.csv");
        List<Map<Transaction, BillType>> classifiedTransactions = transactionRequestHandler.handleTransactionClassificationRequest(transactionClassificationRequest);

        // Transaction 导入测试
        TransactionImportRequest transactionImportRequest = new TransactionImportRequest();
        transactionImportRequest.setUserId(userInfo.getId());
        transactionImportRequest.setAccountId("1");
        transactionImportRequest.setTransactions(classifiedTransactions);
        List<Map<Transaction, BillType>> importedTransactions = transactionRequestHandler.handleTransactionImportRequest(transactionImportRequest);

        for (Map<Transaction, BillType> transaction : importedTransactions) {
            for (Map.Entry<Transaction, BillType> entry : transaction.entrySet()) {
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
    }
}