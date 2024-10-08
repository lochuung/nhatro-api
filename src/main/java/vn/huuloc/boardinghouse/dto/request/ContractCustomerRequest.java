package vn.huuloc.boardinghouse.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractCustomerRequest {
    @NotBlank(message = "Mã khách hàng không được để trống")
    private Long customerId;
    @NotBlank(message = "Mã hợp đồng không được để trống")
    private Long contractId;
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Ngày trả phòng không đúng định dạng yyyy-MM-dd")
    private String checkoutDate;
}
