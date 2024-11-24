package vn.huuloc.boardinghouse.controller.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.huuloc.boardinghouse.model.dto.CustomerImageDto;
import vn.huuloc.boardinghouse.service.CustomerImageService;
import vn.huuloc.boardinghouse.util.ResponseUtils;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer-images")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CustomerImageController {
    @Autowired
    private final CustomerImageService customerImageService;

    @GetMapping("/{id}")
    public ResponseEntity<List<CustomerImageDto>> getImages(@PathVariable Long id) {
        return ResponseEntity.ok(customerImageService.getImages(id));
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<CustomerImageDto> upload(
            @RequestParam("id") Long id,
            @RequestParam("type") String type,
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(customerImageService.uploadOne(id, type, file));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCustomerImage(@PathVariable Long id) {
        customerImageService.delete(id);
        return ResponseEntity.ok().body(ResponseUtils.success());
    }
}
