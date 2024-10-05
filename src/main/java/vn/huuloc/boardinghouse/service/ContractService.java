package vn.huuloc.boardinghouse.service;

import org.springframework.data.domain.Page;
import vn.huuloc.boardinghouse.dto.ContractDto;
import vn.huuloc.boardinghouse.dto.request.CheckinRequest;
import vn.huuloc.boardinghouse.dto.request.CheckoutRequest;
import vn.huuloc.boardinghouse.dto.sort.filter.SearchRequest;

public interface ContractService {
    ContractDto checkIn(CheckinRequest contractRequest);

    ContractDto checkOut(CheckoutRequest checkoutRequest);

    ContractDto findById(Long id);

    Page<ContractDto> search(SearchRequest searchRequest);
}
