package vn.huuloc.boardinghouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.huuloc.boardinghouse.entity.Room;
import vn.huuloc.boardinghouse.enums.ContractStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ContractDto extends BaseDto {
    private Long id;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfPeople;
    private BigDecimal price;
    private BigDecimal deposit;
    private ContractStatus status;
    private String note;
    private CustomerDto owner;
    private List<CustomerDto> members;
}
