package vn.huuloc.boardinghouse.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.huuloc.boardinghouse.model.dto.response.BranchResponse;
import vn.huuloc.boardinghouse.model.entity.Branch;

import java.util.List;

@Mapper
public interface BranchMapper {
    BranchMapper INSTANCE = Mappers.getMapper(BranchMapper.class);

    List<BranchResponse> toDto(List<Branch> branches);
}
