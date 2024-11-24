package vn.huuloc.boardinghouse.model.dto.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckinRequest {
    Long contractId;
    @NotNull(message = "Mã phòng không được để trống")
    private Long roomId;
    @NotNull(message = "Ngày bắt đầu không được để trống")
    private Double checkinElectricNumber;
    @NotNull(message = "Ngày bắt đầu không được để trống")
    private Double checkinWaterNumber;
    private String roomStatus;
    private LocalDate startDate;
    @NotNull(message = "Giá thuê không được để trống")
    @Min(value = 0, message = "Giá thuê phải lớn hơn 0")
    private BigDecimal price;
    @NotNull(message = "Tiền cọc không được để trống")
    @Min(value = 0, message = "Tiền cọc phải lớn hơn 0")
    private BigDecimal deposit;
    private String note;

    @NotNull(message = "Danh sách khách hàng không được để trống")
    @Valid
    private List<CustomerRequest> customers;
}
