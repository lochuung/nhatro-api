package vn.huuloc.boardinghouse.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.huuloc.boardinghouse.model.dto.ServiceFeeDto;
import vn.huuloc.boardinghouse.model.entity.ServiceFee;

import java.util.List;

@Mapper
public interface ServiceFeeMapper {
    ServiceFeeMapper INSTANCE = Mappers.getMapper(ServiceFeeMapper.class);

    List<ServiceFeeDto> toDto(List<ServiceFee> all);

    ServiceFeeDto toDto(ServiceFee serviceFee);

    ServiceFee toEntity(ServiceFeeDto serviceFeeDto);
}
