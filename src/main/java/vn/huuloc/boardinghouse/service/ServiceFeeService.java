package vn.huuloc.boardinghouse.service;

import org.springframework.data.domain.Page;
import vn.cnj.shared.sortfilter.request.SearchRequest;
import vn.huuloc.boardinghouse.model.dto.ServiceFeeDto;

import java.util.List;

public interface ServiceFeeService {
    List<ServiceFeeDto> findAll();

    ServiceFeeDto findById(Long id);

    ServiceFeeDto upsert(ServiceFeeDto serviceFeeDto);

    void delete(Long id);

    Page<ServiceFeeDto> search(SearchRequest serviceFeeDto);
}
