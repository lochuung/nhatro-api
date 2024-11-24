package vn.huuloc.boardinghouse.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.huuloc.boardinghouse.config.DecimalSerializer;
import vn.huuloc.boardinghouse.enums.InvoiceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InvoiceDto extends BaseDto {
    private Long id;
    private ContractDto contract;
    private double oldElectricityNumber;
    private double oldWaterNumber;
    private double newElectricityNumber;
    private double newWaterNumber;
    private double usageElectricityNumber;
    private double usageWaterNumber;
    @JsonSerialize(using = DecimalSerializer.class)
    private BigDecimal electricityUnitPrice;
    @JsonSerialize(using = DecimalSerializer.class)
    private BigDecimal waterUnitPrice;
    @JsonSerialize(using = DecimalSerializer.class)
    private BigDecimal electricityAmount;
    @JsonSerialize(using = DecimalSerializer.class)
    private BigDecimal waterAmount;
    @JsonSerialize(using = DecimalSerializer.class)
    private BigDecimal totalServiceFee;
    @JsonSerialize(using = DecimalSerializer.class)
    private BigDecimal subTotal;
    @JsonSerialize(using = DecimalSerializer.class)
    private BigDecimal discount;
    @JsonSerialize(using = DecimalSerializer.class)
    private BigDecimal totalAmount;
    @JsonSerialize(using = DecimalSerializer.class)
    private BigDecimal paidAmount;
    private List<ServiceFeeDto> serviceFees;
    private LocalDateTime printDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String totalAmountInWords;
    private String note;
    private String adminNote;
    private InvoiceType type;
    @JsonSerialize(using = DecimalSerializer.class)
    private BigDecimal roomAmount;

    @JsonSerialize(using = DecimalSerializer.class)
    private BigDecimal otherFee;
    private String otherFeeNote;
}
