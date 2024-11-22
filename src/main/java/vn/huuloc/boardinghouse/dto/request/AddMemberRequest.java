package vn.huuloc.boardinghouse.dto.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddMemberRequest {
    @NotNull(message = "Mã hợp đồng không được để trống")
    Long contractId;
    @NotNull(message = "Danh sách khách hàng không được để trống")
    @Valid
    private List<CustomerRequest> customers;
}
