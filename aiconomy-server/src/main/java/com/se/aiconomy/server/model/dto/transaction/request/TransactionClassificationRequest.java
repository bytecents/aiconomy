package com.se.aiconomy.server.model.dto.transaction.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Request DTO for transaction classification.
 * <p>
 * This class is used to encapsulate the request for classifying transactions based on a file path.
 * It extends {@link BaseRequest} and contains the file path to be processed.
 * </p>
 *
 * <ul>
 *   <li><b>filePath</b>: The path of the file to be classified.</li>
 * </ul>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionClassificationRequest extends BaseRequest {
    /**
     * The path of the file to be classified.
     */
    private String filePath;
}
