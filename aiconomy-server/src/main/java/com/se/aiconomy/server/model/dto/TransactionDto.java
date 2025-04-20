package com.se.aiconomy.server.model.dto;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import com.se.aiconomy.server.langchain.common.model.BillType;
import com.se.aiconomy.server.storage.common.Identifiable;
import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 交易记录类
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "transactions", schemaVersion = "1.0")
public class TransactionDto implements Identifiable {
    @Id
    @CsvBindByPosition(position = 0)
    private String id;

    @CsvBindByPosition(position = 1)
    @CsvDate(value = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime time;  // 交易时间

    @CsvBindByPosition(position = 2)
    private String type;  // 交易类型 (如 "消费", "转账")

    @CsvBindByPosition(position = 3)
    private String counterparty;  // 交易对方 (如 "Walmart", "支付宝")

    @CsvBindByPosition(position = 4)
    private String product;  // 商品名称 (如 "iPhone 15")

    @CsvBindByPosition(position = 5)
    private String incomeOrExpense;  // 收/支 ("收入" 或 "支出")

    @CsvBindByPosition(position = 6)
    private String amount;  // 金额（元）

    @CsvBindByPosition(position = 7)
    private String paymentMethod;  // 支付方式 (如 "微信支付", "信用卡")

    @CsvBindByPosition(position = 8)
    private String status;  // 交易状态 (如 "成功", "待支付")

    @CsvBindByPosition(position = 9)
    private String merchantOrderId;  // 商户单号

    @CsvBindByPosition(position = 10)
    private String remark;  // 备注

    private BillType billType;

    private String userId;
}