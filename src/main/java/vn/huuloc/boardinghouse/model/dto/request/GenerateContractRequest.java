package vn.huuloc.boardinghouse.model.dto.request;

import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GenerateContractRequest {
    private Long contractId;
    @Min(value = 1, message = "Số điện mới phải lớn hơn 0")
    private Double newElectricityIndex;
    @Min(value = 1, message = "Số nước mới phải lớn hơn 0")
    private Double newWaterIndex;
}
