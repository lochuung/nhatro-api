package vn.huuloc.boardinghouse.controller.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.huuloc.boardinghouse.dto.response.BranchResponse;
import vn.huuloc.boardinghouse.service.BranchService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class BranchController {
    private final BranchService branchService;

    @GetMapping
    public ResponseEntity<List<BranchResponse>> findAll() {
        return ResponseEntity.ok(branchService.findAll());
    }
}
