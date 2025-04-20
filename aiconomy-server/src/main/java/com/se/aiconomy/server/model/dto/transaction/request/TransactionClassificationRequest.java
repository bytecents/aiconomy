package com.se.aiconomy.server.model.dto.transaction.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionClassificationRequest extends BaseRequest {
    private String filePath;  // 文件路径
}
