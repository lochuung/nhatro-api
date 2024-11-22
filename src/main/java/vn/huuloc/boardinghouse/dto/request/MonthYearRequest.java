package vn.huuloc.boardinghouse.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MonthYearRequest {
    @Pattern(regexp = "((0?[1-9])|(1[0-2]))\\/(\\d{4})", message = "Không đúng định dạng MM/YYYY")
    private String monthYear;

    @Min(value = 1, message = "Ngày bắt đầu phải lớn hơn 0")
    @Max(value = 31, message = "Ngày bắt đầu phải nhỏ hơn hoặc bằng 31")
    private Integer startDay;
}
