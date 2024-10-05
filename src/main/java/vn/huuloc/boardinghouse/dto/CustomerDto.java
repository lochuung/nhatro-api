package vn.huuloc.boardinghouse.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CustomerDto extends BaseDto {
    private Long id;
    private String name;
    private String name2;
    private String phone;
    private String email;
    private String birthday;
    private String address;
    private String description;
    private boolean enabled;

    private String idNumber;
    private String idPlace;
    private String idDate;
    private String bankName;
    private String bankNumber;
}
