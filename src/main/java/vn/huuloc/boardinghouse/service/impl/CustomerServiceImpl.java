package vn.huuloc.boardinghouse.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.cnj.shared.sortfilter.request.SearchRequest;
import vn.cnj.shared.sortfilter.specification.SearchSpecification;
import vn.huuloc.boardinghouse.model.dto.CustomerDto;
import vn.huuloc.boardinghouse.model.dto.mapper.CustomerMapper;
import vn.huuloc.boardinghouse.model.dto.request.CustomerRequest;
import vn.huuloc.boardinghouse.model.entity.Customer;
import vn.huuloc.boardinghouse.exception.BadRequestException;
import vn.huuloc.boardinghouse.repository.CustomerImageRepository;
import vn.huuloc.boardinghouse.repository.CustomerRepository;
import vn.huuloc.boardinghouse.service.CustomerService;
import vn.huuloc.boardinghouse.util.CommonUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerImageRepository customerImageRepository;

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
    public Page<CustomerDto> search(SearchRequest searchRequest) {
        Specification<Customer> searchSpec = new SearchSpecification<>(searchRequest);
        Pageable pageable = SearchSpecification.getPageable(searchRequest.getPage(),
                searchRequest.getSize());
        Page<Customer> customers = customerRepository.findAll(searchSpec, pageable);
        return customers.map(CustomerMapper.INSTANCE::toDto);
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
    @Transactional
    public void deleteById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> BadRequestException.message("Không tìm thấy khách hàng"));
        if (!customer.getContractCustomerLinkeds().isEmpty()) {
            throw BadRequestException.message("Khách hàng đã có hợp đồng");
        }
        customerRepository.delete(customer);
    }
}
