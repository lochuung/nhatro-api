package vn.huuloc.boardinghouse.controller.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.huuloc.boardinghouse.model.dto.ContractDto;
import vn.huuloc.boardinghouse.model.dto.request.AddMemberRequest;
import vn.huuloc.boardinghouse.model.dto.request.CheckinRequest;
import vn.huuloc.boardinghouse.model.dto.request.CheckoutRequest;
import vn.huuloc.boardinghouse.model.dto.request.ContractCustomerRequest;
import vn.huuloc.boardinghouse.model.dto.sort.filter.ContractSearchRequest;
import vn.huuloc.boardinghouse.model.projection.LatestNumberIndex;
import vn.huuloc.boardinghouse.service.ContractService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ContractController {
    private final ContractService contractService;

    @PostMapping("/check-in")
    public ResponseEntity<ContractDto> checkIn(@Valid @RequestBody CheckinRequest checkinRequest) {
        return ResponseEntity.ok(contractService.checkIn(checkinRequest));
    }

    @PostMapping("/check-out")
    public ResponseEntity<ContractDto> checkOut(@Valid @RequestBody CheckoutRequest checkoutRequest) {
        return ResponseEntity.ok(contractService.checkOut(checkoutRequest));
    }

    @PostMapping("/add-member")
    public ResponseEntity<ContractDto> addMember(@Valid @RequestBody AddMemberRequest checkinRequest) {
        return ResponseEntity.ok(contractService.addMember(checkinRequest));
    }

    @PostMapping("/leave")
    public ResponseEntity<ContractDto> leave(@Valid @RequestBody ContractCustomerRequest request) {
        return ResponseEntity.ok(contractService.leave(request));
    }

    @PostMapping("/change-owner")
    public ResponseEntity<ContractDto> changeOwner(@Valid @RequestBody ContractCustomerRequest request) {
        return ResponseEntity.ok(contractService.changeOwner(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractDto> getContract(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.findById(id));
    }

    @PostMapping("/search")
    public ResponseEntity<Page<ContractDto>> search(@RequestBody ContractSearchRequest searchRequest) {
        return ResponseEntity.ok(contractService.search(searchRequest));
    }

    @PostMapping("/latest-number-index")
    public LatestNumberIndex findLatestNumberIndexById(@RequestParam Long id) {
        return contractService.findOldNumberIndexById(id);
    }

    @GetMapping("/all-available")
    public ResponseEntity<List<ContractDto>> getAll() {
        return ResponseEntity.ok(contractService.findAllAvailable());
    }

    @GetMapping("/all")
    public ResponseEntity<List<ContractDto>> getAllContracts() {
        return ResponseEntity.ok(contractService.findAll());
    }

    @GetMapping(value = "/print/{id}", produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public ResponseEntity<byte[]> printContract(@PathVariable Long id) throws IOException {
        return ResponseEntity.ok()
                .headers(buildHeaders(id.toString()))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(contractService.printContract(id));
    }

    private HttpHeaders buildHeaders(String contractCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        UUID uuid = UUID.randomUUID();
        headers.setContentDispositionFormData("attachment", "contract_" + uuid + ".docx");
        return headers;
    }

}
