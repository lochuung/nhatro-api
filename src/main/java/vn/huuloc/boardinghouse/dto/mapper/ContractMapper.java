package vn.huuloc.boardinghouse.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.huuloc.boardinghouse.dto.ContractDto;
import vn.huuloc.boardinghouse.dto.request.CheckinRequest;
import vn.huuloc.boardinghouse.entity.Contract;

@Mapper
public interface ContractMapper {
    ContractMapper INSTANCE = Mappers.getMapper(ContractMapper.class);

    ContractDto toDto(Contract contract);

    Contract toEntity(CheckinRequest contractRequest);
}
