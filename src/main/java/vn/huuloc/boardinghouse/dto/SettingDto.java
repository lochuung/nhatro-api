package vn.huuloc.boardinghouse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SettingDto extends BaseDto {
    private Long id;
    @NotNull(message = "Khóa không được để trống")
    private String key;
    @NotNull(message = "Giá trị không được để trống")
    private String value;
}
