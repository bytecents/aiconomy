package com.se.aiconomy.server.model.dto.transaction.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class TransactionClassificationRequest extends BaseRequest {
    private String filePath;  // 文件路径
}
