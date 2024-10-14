package vn.huuloc.boardinghouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import vn.huuloc.boardinghouse.config.DecimalSerializer;
import vn.huuloc.boardinghouse.constant.DbConstants;
import vn.huuloc.boardinghouse.entity.Contract;
import vn.huuloc.boardinghouse.entity.Invoice;
import vn.huuloc.boardinghouse.entity.common.BaseEntity;

import java.math.BigDecimal;
import java.util.List;

import static vn.huuloc.boardinghouse.util.CommonUtils.defaultBigDecimalIfNull;

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