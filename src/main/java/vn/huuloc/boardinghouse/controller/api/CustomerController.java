package vn.huuloc.boardinghouse.controller.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.huuloc.boardinghouse.dto.CustomerDto;
import vn.huuloc.boardinghouse.dto.request.CustomerRequest;
import vn.huuloc.boardinghouse.dto.sort.filter.SearchRequest;
import vn.huuloc.boardinghouse.service.CustomerService;
import vn.huuloc.boardinghouse.util.ResponseUtils;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @PostMapping("/search")
    public ResponseEntity<List<CustomerDto>> search(@RequestBody SearchRequest searchRequest) {
        return ResponseEntity.ok(customerService.search(searchRequest));
    }

    @PostMapping("/upsert")
    public ResponseEntity<CustomerDto> upsert(@Valid @RequestBody CustomerRequest customerRequest) {
        return ResponseEntity.ok(customerService.saveOrUpdate(customerRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        customerService.deleteById(id);
        return ResponseEntity.ok(ResponseUtils.success());
    }
}
