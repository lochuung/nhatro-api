package vn.huuloc.boardinghouse.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import vn.huuloc.boardinghouse.model.dto.CustomerDto;
import vn.huuloc.boardinghouse.model.dto.request.CustomerRequest;
import vn.huuloc.boardinghouse.model.entity.Customer;

import java.util.List;

@Mapper
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    List<CustomerDto> toDto(List<Customer> customers);

    CustomerDto toDto(Customer customer);

    Customer toEntity(CustomerRequest request);
}
