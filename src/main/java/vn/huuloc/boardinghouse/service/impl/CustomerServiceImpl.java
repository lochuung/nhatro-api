package vn.huuloc.boardinghouse.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.huuloc.boardinghouse.dto.CustomerDto;
import vn.huuloc.boardinghouse.dto.mapper.CustomerMapper;
import vn.huuloc.boardinghouse.dto.request.CustomerRequest;
import vn.huuloc.boardinghouse.dto.sort.filter.SearchRequest;
import vn.huuloc.boardinghouse.dto.sort.filter.SearchSpecification;
import vn.huuloc.boardinghouse.entity.Customer;
import vn.huuloc.boardinghouse.exception.BadRequestException;
import vn.huuloc.boardinghouse.repository.CustomerRepository;
import vn.huuloc.boardinghouse.service.CustomerService;
import vn.huuloc.boardinghouse.util.CommonUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    @Override
    public List<CustomerDto> findAll() {
        List<Customer> customers = customerRepository.findAll();
        return CustomerMapper.INSTANCE.toDto(customers);
    }

    @Override
    public CustomerDto findById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> BadRequestException.message("Không tìm thấy khách hàng"));
        return CustomerMapper.INSTANCE.toDto(customer);
    }

    @Override
    public List<CustomerDto> search(SearchRequest searchRequest) {
        Specification<Customer> searchSpec = new SearchSpecification<>(searchRequest);
        Pageable pageable = SearchSpecification.getPageable(searchRequest.getPage(),
                searchRequest.getSize());
        List<Customer> customers = customerRepository.findAll(searchSpec, pageable).getContent();
        return CustomerMapper.INSTANCE.toDto(customers);
    }

    @Override
    public CustomerDto saveOrUpdate(CustomerRequest request) {
        Long id = request.getId();
        Optional<Customer> customerPhone = StringUtils.hasText(request.getPhone()) ?
                customerRepository.findByPhone(request.getPhone()) : Optional.empty();
        if (CommonUtils.isNewRecord(id)) {
            if (customerPhone.isPresent()) {
                throw BadRequestException.message("Số điện thoại đã tồn tại");
            }
            if (request.getIdNumber() != null && customerRepository.existsByIdNumber(request.getIdNumber())) {
                throw BadRequestException.message("Số CMND đã tồn tại");
            }
            Customer customer = CustomerMapper.INSTANCE.toEntity(request);
            customer.setId(null);
            customer.setEnabled(true);
            return CustomerMapper.INSTANCE.toDto(customerRepository.save(customer));
        }
        Customer customerExisted = customerRepository.findById(id)
                .orElseThrow(() -> BadRequestException.message("Không tìm thấy khách hàng"));
        if (customerPhone.isPresent() && !customerPhone.get().getId().equals(id)) {
            throw BadRequestException.message("Số điện thoại đã tồn tại");
        }
        if (request.getIdNumber() != null && !request.getIdNumber().equals(customerExisted.getIdNumber())
                && customerRepository.existsByIdNumber(request.getIdNumber())) {
            throw BadRequestException.message("Số CMND đã tồn tại");
        }

        Customer customer = CustomerMapper.INSTANCE.toEntity(request);
        customer.setId(id);
        customer.setEnabled(customerExisted.isEnabled());
        return CustomerMapper.INSTANCE.toDto(customerRepository.save(customer));
    }

    @Override
    public void deleteById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> BadRequestException.message("Không tìm thấy khách hàng"));
        customerRepository.delete(customer);
    }
}
