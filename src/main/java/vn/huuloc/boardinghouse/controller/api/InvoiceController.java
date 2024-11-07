package vn.huuloc.boardinghouse.controller.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.cnj.shared.sortfilter.request.SearchRequest;
import vn.huuloc.boardinghouse.dto.InvoiceDto;
import vn.huuloc.boardinghouse.dto.request.InvoiceRequest;
import vn.huuloc.boardinghouse.dto.sort.filter.InvoiceSearchRequest;
import vn.huuloc.boardinghouse.service.InvoiceService;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceDto> create(@Valid @RequestBody InvoiceRequest invoiceRequest) {
        return ResponseEntity.ok(invoiceService.create(invoiceRequest));
    }

    @PutMapping
    public ResponseEntity<InvoiceDto> update(@Valid @RequestBody InvoiceRequest invoiceRequest) {
        return ResponseEntity.ok(invoiceService.update(invoiceRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDto> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.findById(id));
    }

    @PostMapping("/search")
    public ResponseEntity<Page<InvoiceDto>> search(@Valid @RequestBody InvoiceSearchRequest searchRequest) {
        return ResponseEntity.ok(invoiceService.search(searchRequest));
    }


    @GetMapping(value = "/print/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> printInvoice(@PathVariable Long id) throws IOException {
        byte[] pdfBytes = invoiceService.print(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "invoice-" + UUID.randomUUID().toString() + ".pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

}
