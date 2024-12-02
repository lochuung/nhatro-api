package vn.huuloc.boardinghouse.service;

import vn.huuloc.boardinghouse.dto.DashboardStatsDTO;
import vn.huuloc.boardinghouse.dto.MonthlyIncomeDTO;
import vn.huuloc.boardinghouse.dto.RoomOccupancyDTO;
import java.util.List;

public interface DashboardService {
    DashboardStatsDTO getDashboardStats();
    List<MonthlyIncomeDTO> getMonthlyIncome();
    List<RoomOccupancyDTO> getRoomOccupancy();
    Long getPendingInvoicesCount();
}
