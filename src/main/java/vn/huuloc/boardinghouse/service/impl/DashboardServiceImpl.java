package vn.huuloc.boardinghouse.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import vn.huuloc.boardinghouse.dto.DashboardStatsDTO;
import vn.huuloc.boardinghouse.dto.MonthlyIncomeDTO;
import vn.huuloc.boardinghouse.dto.RoomOccupancyDTO;
import vn.huuloc.boardinghouse.repository.InvoiceRepository;
import vn.huuloc.boardinghouse.repository.RoomRepository;
import vn.huuloc.boardinghouse.service.DashboardService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final RoomRepository roomRepository;
    private final InvoiceRepository invoiceRepository;

    @Cacheable(value = "dashboardStats", key = "'stats'")
    @Override
    public DashboardStatsDTO getDashboardStats() {
        Long totalRooms = roomRepository.countTotalRooms();
        Long occupiedRooms = roomRepository.countOccupiedRooms();
        
        return DashboardStatsDTO.builder()
                .totalRevenue(invoiceRepository.calculateTotalRevenue())
                .totalRooms(totalRooms)
                .occupiedRooms(occupiedRooms)
                .availableRooms(totalRooms - occupiedRooms)
                .build();
    }

    @Cacheable(value = "monthlyIncome", key = "#root.methodName")
    @Override
    public List<MonthlyIncomeDTO> getMonthlyIncome() {
        List<MonthlyIncomeDTO> incomeList = new ArrayList<>();
        LocalDate now = LocalDate.now();
        
        for (int i = 5; i >= 0; i--) {
            LocalDate date = now.minusMonths(i);
            BigDecimal income = invoiceRepository.calculateMonthlyIncome(date.getYear(), date.getMonthValue());
            incomeList.add(MonthlyIncomeDTO.builder()
                    .month(date.getMonth().name())
                    .income(income != null ? income : BigDecimal.ZERO)
                    .build());
        }
        return incomeList;
    }

    @Cacheable(value = "roomOccupancy", key = "#root.methodName")
    @Override
    public List<RoomOccupancyDTO> getRoomOccupancy() {
        List<RoomOccupancyDTO> occupancyList = new ArrayList<>();
        LocalDate now = LocalDate.now();
        
        for (int i = 5; i >= 0; i--) {
            LocalDate date = now.minusMonths(i);
            occupancyList.add(RoomOccupancyDTO.builder()
                    .month(date.getMonth().name())
                    .occupied(roomRepository.countOccupiedRoomsForMonth(date.getYear(), date.getMonthValue()))
                    .available(roomRepository.countAvailableRoomsForMonth(date.getYear(), date.getMonthValue()))
                    .build());
        }
        return occupancyList;
    }

    @Cacheable(value = "pendingInvoices", key = "#root.methodName")
    @Override
    public Long getPendingInvoicesCount() {
        return invoiceRepository.countPendingInvoices();
    }
}

