package vn.huuloc.boardinghouse.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResetPasswordRequest {
    @Email
    private String email;
    private String verifyCode;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "Mật khẩu phải chứa ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường và số")
    private String newPassword;
}
