package vn.huuloc.boardinghouse.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import vn.cnj.shared.sortfilter.request.SearchRequest;
import vn.huuloc.boardinghouse.model.dto.ServiceFeeDto;
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

    @PostMapping("/search")
    public Page<ServiceFeeDto> search(@RequestBody SearchRequest searchRequest) {
        return serviceFeeService.search(searchRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        serviceFeeService.delete(id);
    }
}
