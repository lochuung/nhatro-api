package vn.huuloc.boardinghouse.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;
import vn.huuloc.boardinghouse.dto.BaseDto;
import vn.huuloc.boardinghouse.enums.RoomStatus;

import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse extends BaseDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private RoomStatus status;
    private Integer capacity;
    private String type;
    private BranchResponse branch;
    private Integer numberOfPeople;
}
