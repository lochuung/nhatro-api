package vn.huuloc.boardinghouse.service;

import vn.huuloc.boardinghouse.dto.CustomerDto;
import vn.huuloc.boardinghouse.dto.request.CustomerRequest;
import vn.huuloc.boardinghouse.dto.sort.filter.SearchRequest;

import java.util.List;

public interface CustomerService {
    List<CustomerDto> findAll();

    CustomerDto findById(Long id);

    List<CustomerDto> search(SearchRequest searchRequest);

    CustomerDto saveOrUpdate(CustomerRequest customerRequest);

    void deleteById(Long id);
}
