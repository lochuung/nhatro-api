package vn.huuloc.boardinghouse.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import vn.huuloc.boardinghouse.dto.CustomerImageDto;
import vn.huuloc.boardinghouse.entity.CustomerImage;

import java.util.List;

@Mapper
public interface CustomerImageMapper {
    CustomerImageMapper INSTANCE = Mappers.getMapper(CustomerImageMapper.class);

    List<CustomerImageDto> toDtos(List<CustomerImage> allByCustomerId);

    CustomerImageDto toDto(CustomerImage save);
}