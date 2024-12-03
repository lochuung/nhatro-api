
package vn.huuloc.boardinghouse.model.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private BigDecimal paidAmount;
}