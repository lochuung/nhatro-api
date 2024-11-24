package vn.huuloc.boardinghouse.service;

import org.springframework.data.domain.Page;
import vn.cnj.shared.sortfilter.request.SearchRequest;
import vn.huuloc.boardinghouse.model.dto.CustomerDto;
import vn.huuloc.boardinghouse.model.dto.request.CustomerRequest;

import java.util.List;

public interface CustomerService {
    List<CustomerDto> findAll();

    CustomerDto findById(Long id);

    Page<CustomerDto> search(SearchRequest searchRequest);

    CustomerDto saveOrUpdate(CustomerRequest customerRequest);

    void deleteById(Long id);
}
