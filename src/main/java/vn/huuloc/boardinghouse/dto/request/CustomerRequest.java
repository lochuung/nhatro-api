package vn.huuloc.boardinghouse.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerRequest {
    private Long id;
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|84)(3|5|7|8|9)\\d{8}$", message = "Số điện thoại không hợp lệ")
    private String phone;
    @Email(message = "Email không hợp lệ")
    private String email;
    @NotBlank(message = "Tên không được để trống")
    private String name;
    private String name2;
    @NotBlank(message = "Ngày sinh không được để trống")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Ngày sinh không hợp lệ")
    private String birthday;
    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
    private String description;
    private boolean enabled = true;
    @NotBlank(message = "CCCD không được để trống")
    @Pattern(regexp = "^[0-9]{9,12}$", message = "CCCD không hợp lệ")
    private String idNumber;
    @NotBlank(message = "Nơi cấp không được để trống")
    private String idPlace;
    @NotBlank(message = "Ngày cấp không được để trống")
    private String idDate;
    private String bankName;
    private String bankNumber;
}
