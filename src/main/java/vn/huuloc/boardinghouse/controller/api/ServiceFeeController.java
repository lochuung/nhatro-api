package vn.huuloc.boardinghouse.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.huuloc.boardinghouse.dto.ServiceFeeDto;
import vn.huuloc.boardinghouse.service.ServiceFeeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/service-fees")
@RequiredArgsConstructor
public class ServiceFeeController {
    private final ServiceFeeService serviceFeeService;

    @GetMapping
    public List<ServiceFeeDto> getAll() {
        return serviceFeeService.findAll();
    }

    @GetMapping("/{id}")
    public ServiceFeeDto getById(@PathVariable Long id) {
        return serviceFeeService.findById(id);
    }

    @PostMapping("/upsert")
    public ServiceFeeDto upsert(@Valid @RequestBody ServiceFeeDto serviceFeeDto) {
        return serviceFeeService.upsert(serviceFeeDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        serviceFeeService.delete(id);
    }
}
