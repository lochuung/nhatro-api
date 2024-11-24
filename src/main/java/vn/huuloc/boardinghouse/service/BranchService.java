package vn.huuloc.boardinghouse.service;

import vn.huuloc.boardinghouse.model.dto.response.BranchResponse;

import java.util.List;

public interface BranchService {
    List<BranchResponse> findAll();
}
