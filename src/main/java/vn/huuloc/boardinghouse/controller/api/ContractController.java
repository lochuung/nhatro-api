package vn.huuloc.boardinghouse.controller.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contracts")
@SecurityRequirement(name = "bearerAuth")
public class ContractController {

        @PostMapping
        public String create() {
            return "Create contract";
        }

        @PutMapping
        public String update() {
            return "Update contract";
        }

        @DeleteMapping
        public String delete() {
            return "Delete contract";
        }

        @GetMapping("/{id}")
        public String get(@PathVariable("id") Long id) {
            return "Get contract " + id;
        }

        @GetMapping
        public String list() {
            return "List contracts";
        }
}
