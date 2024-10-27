package vn.huuloc.boardinghouse.service;

import org.springframework.data.domain.Page;
import vn.cnj.shared.sortfilter.request.SearchRequest;
import vn.huuloc.boardinghouse.dto.InvoiceDto;
import vn.huuloc.boardinghouse.dto.request.InvoiceRequest;

import java.io.IOException;

public interface InvoiceService {
    InvoiceDto create(InvoiceRequest invoiceRequest);

    InvoiceDto update(InvoiceRequest invoiceRequest);

    void delete(Long id);

    InvoiceDto findById(Long id);

    Page<InvoiceDto> search(SearchRequest searchRequest);

    byte[] print(Long id) throws IOException;
}
