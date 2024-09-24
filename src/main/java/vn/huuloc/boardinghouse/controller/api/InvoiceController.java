package vn.huuloc.boardinghouse.controller.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invoices")
@SecurityRequirement(name = "bearerAuth")
public class InvoiceController {

    @PostMapping
    public String create() {
        return "Create invoice";
    }

    @PutMapping
    public String update() {
        return "Update invoice";
    }

    @DeleteMapping
    public String delete() {
        return "Delete invoice";
    }

    @GetMapping("/{id}")
    public String get(@PathVariable("id") Long id) {
        return "Get invoice " + id;
    }

    @GetMapping
    public String list() {
        return "List invoices";
    }
}
