package vn.huuloc.boardinghouse.controller.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.huuloc.boardinghouse.dto.ContractDto;
import vn.huuloc.boardinghouse.dto.request.CheckinRequest;
import vn.huuloc.boardinghouse.dto.request.CheckoutRequest;
import vn.huuloc.boardinghouse.dto.request.ContractRequest;
import vn.huuloc.boardinghouse.dto.sort.filter.SearchRequest;
import vn.huuloc.boardinghouse.service.ContractService;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ContractController {
    private final ContractService contractService;

    @PostMapping("/check-in")
    public ResponseEntity<ContractDto> checkIn(@RequestBody CheckinRequest checkinRequest) {
        return ResponseEntity.ok(contractService.checkIn(checkinRequest));
    }

    @PostMapping("/check-out")
    public ResponseEntity<ContractDto> checkOut(@RequestBody CheckoutRequest checkoutRequest) {
        return ResponseEntity.ok(contractService.checkOut(checkoutRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractDto> getContract(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.findById(id));
    }

    @PostMapping("/search")
    public ResponseEntity<Page<ContractDto>> search(@RequestBody SearchRequest searchRequest) {
        return ResponseEntity.ok(contractService.search(searchRequest));
    }

}
