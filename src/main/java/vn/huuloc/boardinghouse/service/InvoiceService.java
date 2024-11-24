package vn.huuloc.boardinghouse.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import vn.huuloc.boardinghouse.model.dto.InvoiceDto;
import vn.huuloc.boardinghouse.model.dto.request.InvoiceRequest;
import vn.huuloc.boardinghouse.model.dto.request.MonthYearRequest;
import vn.huuloc.boardinghouse.model.dto.sort.filter.InvoiceSearchRequest;

import java.io.IOException;

public interface InvoiceService {
    InvoiceDto create(InvoiceRequest invoiceRequest);

    InvoiceDto update(InvoiceRequest invoiceRequest);

    void delete(Long id);

    InvoiceDto findById(Long id);

    Page<InvoiceDto> search(InvoiceSearchRequest searchRequest);

    byte[] print(Long id) throws IOException;

    void generateInvoices(MonthYearRequest monthRecord);

    byte[] printMonthlyInvoices(@Valid MonthYearRequest monthRecord) throws IOException;
}
