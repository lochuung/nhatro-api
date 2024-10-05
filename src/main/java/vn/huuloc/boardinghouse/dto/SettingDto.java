package vn.huuloc.boardinghouse.dto;

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
    private String key;
    private String value;
}
