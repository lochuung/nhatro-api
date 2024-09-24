package vn.huuloc.boardinghouse.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;
import vn.huuloc.boardinghouse.dto.BaseDto;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchResponse extends BaseDto {
    private Long id;
    private String name;
    private String address;
    private String description;
    private String image;
    private String phone;
    private String email;
    private Boolean status;
}
