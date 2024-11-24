package vn.huuloc.boardinghouse.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.huuloc.boardinghouse.model.dto.ContractDto;
import vn.huuloc.boardinghouse.model.dto.request.CheckinRequest;
import vn.huuloc.boardinghouse.model.entity.Contract;

import java.util.List;

@Mapper
public interface ContractMapper {
    ContractMapper INSTANCE = Mappers.getMapper(ContractMapper.class);

    ContractDto toDto(Contract contract);

    List<ContractDto> toDto(List<Contract> contracts);

    Contract toEntity(CheckinRequest contractRequest);
}
