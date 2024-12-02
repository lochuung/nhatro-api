package vn.huuloc.boardinghouse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {
    private BigDecimal totalRevenue;
    private Long totalRooms;
    private Long occupiedRooms;
    private Long availableRooms;
}