package vn.huuloc.boardinghouse.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.huuloc.boardinghouse.service.DashboardService;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/income")
    public ResponseEntity<?> getMonthlyIncome() {
        return ResponseEntity.ok(dashboardService.getMonthlyIncome());
    }

    @GetMapping("/occupancy")
    public ResponseEntity<?> getRoomOccupancy() {
        return ResponseEntity.ok(dashboardService.getRoomOccupancy());
    }

    @GetMapping("/invoices")
    public ResponseEntity<?> getPendingInvoicesCount() {
        return ResponseEntity.ok(dashboardService.getPendingInvoicesCount());
    }
}
