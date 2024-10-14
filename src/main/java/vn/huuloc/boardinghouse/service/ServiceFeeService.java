package vn.huuloc.boardinghouse.service;

import vn.huuloc.boardinghouse.dto.ServiceFeeDto;

import java.util.List;

public interface ServiceFeeService {
    List<ServiceFeeDto> findAll();

    ServiceFeeDto findById(Long id);

    ServiceFeeDto upsert(ServiceFeeDto serviceFeeDto);

    void delete(Long id);
}
