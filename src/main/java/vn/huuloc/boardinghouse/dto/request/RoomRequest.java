package vn.huuloc.boardinghouse.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import vn.huuloc.boardinghouse.config.DecimalSerializer;
import vn.huuloc.boardinghouse.enums.RoomStatus;
import java.math.BigDecimal;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {
    private Long id;
    @NotBlank(message = "Tên phòng không được để trống")
    private String name;
    private String description;
    @JsonSerialize(using = DecimalSerializer.class)
    @NotNull(message = "Giá phòng không được để trống")
    @Min(value = 0, message = "Giá phòng phải lớn hơn 0")
    private BigDecimal price;
    private RoomStatus status;
    @NotNull(message = "Sức chứa không được để trống")
    @Min(value = 0, message = "Sức chứa phải lớn hơn 0")
    private Integer capacity;
    private String type;
    @NotNull(message = "Chi nhánh không được để trống")
    private Long branchId;
}
