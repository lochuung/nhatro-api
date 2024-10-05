package vn.huuloc.boardinghouse.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.huuloc.boardinghouse.dto.CustomerDto;
import vn.huuloc.boardinghouse.dto.request.CustomerRequest;
import vn.huuloc.boardinghouse.entity.Customer;

import java.util.List;

@Mapper
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    List<CustomerDto> toDtos(List<Customer> customers);

    CustomerDto toDto(Customer customer);

    Customer toEntity(CustomerRequest request);
}
