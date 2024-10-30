package vn.huuloc.boardinghouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.huuloc.boardinghouse.config.DecimalSerializer;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ServiceFeeDto extends BaseDto {
    private Long id;
    private String code;
    @NotNull(message = "Tên dịch vụ không được để trống")
    private String name;
    private String description;
    @NotNull(message = "Giá dịch vụ không được để trống")
    @Min(value = 0, message = "Giá dịch vụ phải lớn hơn hoặc bằng 0")
    @JsonSerialize(using = DecimalSerializer.class)
    private BigDecimal unitPrice;
    private boolean active;
}