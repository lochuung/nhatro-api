package vn.huuloc.boardinghouse.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import lombok.experimental.SuperBuilder;
import vn.huuloc.boardinghouse.config.DecimalSerializer;
import vn.huuloc.boardinghouse.dto.BaseDto;
import vn.huuloc.boardinghouse.entity.enums.ContractStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponse extends BaseDto {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfPeople;
    @JsonSerialize(using = DecimalSerializer.class)
    private BigDecimal price;
    @JsonSerialize(using = DecimalSerializer.class)
    private BigDecimal deposit;
    private ContractStatus status;
    private String note;
    private String code; // uuid
}
