package vn.huuloc.boardinghouse.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.huuloc.boardinghouse.dto.ServiceFeeDto;
import vn.huuloc.boardinghouse.dto.constraint.ValidElectricityWaterNumber;
import vn.huuloc.boardinghouse.enums.InvoiceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ValidElectricityWaterNumber
public class InvoiceRequest {
    private Long id;
    @NotNull(message = "Hợp đồng không được để trống")
    private Long contractId;
    @NotNull(message = "Số điện cũ không được để trống")
    @Min(value = 0, message = "Số điện cũ phải lớn hơn hoặc bằng 0")
    private Double oldElectricityNumber;
    @NotNull(message = "Số nước cũ không được để trống")
    @Min(value = 0, message = "Số nước cũ phải lớn hơn hoặc bằng 0")
    private Double oldWaterNumber;
    @NotNull(message = "Số điện mới không được để trống")
    @Min(value = 0, message = "Số điện mới phải lớn hơn hoặc bằng 0")
    private Double newElectricityNumber;
    @NotNull(message = "Số nước mới không được để trống")
    @Min(value = 0, message = "Số nước mới phải lớn hơn hoặc bằng 0")
    private Double newWaterNumber;
    private BigDecimal electricityUnitPrice;
    private BigDecimal waterUnitPrice;
    @Min(value = 0, message = "Số điện không được nhỏ hơn 0")
    private BigDecimal paidAmount = BigDecimal.ZERO;
    @Min(value = 0, message = "Số điện không được nhỏ hơn 0")
    private BigDecimal discount = BigDecimal.ZERO;
    private List<ServiceFeeDto> serviceFees = List.of();
    private BigDecimal totalServiceFee;
    @NotNull(message = "Thời gian bắt đầu không được để trống")
    private LocalDateTime startDate;
    @NotNull(message = "Thời gian hết hạn không được để trống")
    private LocalDateTime endDate;
    private String note;
    private String adminNote;
    private InvoiceType type = InvoiceType.MONTHLY;
    @NotNull(message = "Tiền phòng không được để trống")
    @Min(value = 0, message = "Tiền phòng phải lớn hơn hoặc bằng 0")
    private BigDecimal roomAmount;

    private BigDecimal otherFee = BigDecimal.ZERO;
    private String otherFeeNote;
}
