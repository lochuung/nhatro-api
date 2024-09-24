package vn.huuloc.boardinghouse.service;

import vn.huuloc.boardinghouse.dto.response.BranchResponse;

import java.util.List;

public interface BranchService {
    List<BranchResponse> findAll();
}
