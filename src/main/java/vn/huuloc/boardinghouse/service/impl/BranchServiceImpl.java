package vn.huuloc.boardinghouse.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.huuloc.boardinghouse.model.dto.mapper.BranchMapper;
import vn.huuloc.boardinghouse.model.dto.response.BranchResponse;
import vn.huuloc.boardinghouse.repository.BranchRepository;
import vn.huuloc.boardinghouse.service.BranchService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {
    private final BranchRepository branchRepository;

    @Override
    public List<BranchResponse> findAll() {
        return BranchMapper.INSTANCE.toDto(branchRepository.findAll());
    }
}
