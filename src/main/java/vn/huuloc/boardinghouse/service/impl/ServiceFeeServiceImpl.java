package vn.huuloc.boardinghouse.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.huuloc.boardinghouse.dto.ServiceFeeDto;
import vn.huuloc.boardinghouse.dto.mapper.ServiceFeeMapper;
import vn.huuloc.boardinghouse.entity.ServiceFee;
import vn.huuloc.boardinghouse.exception.BadRequestException;
import vn.huuloc.boardinghouse.repository.ServiceFeeRepository;
import vn.huuloc.boardinghouse.service.ServiceFeeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceFeeServiceImpl implements ServiceFeeService {
    private final ServiceFeeRepository serviceFeeRepository;

    @Override
    public List<ServiceFeeDto> findAll() {
        return ServiceFeeMapper.INSTANCE.toDto(serviceFeeRepository.findAll());
    }

    @Override
    public ServiceFeeDto findById(Long id) {
        ServiceFee serviceFee = serviceFeeRepository.findById(id)
                .orElseThrow(()-> BadRequestException.message("Không tìm thấy phí dịch vụ"));
        return ServiceFeeMapper.INSTANCE.toDto(serviceFee);
    }

    @Override
    public ServiceFeeDto upsert(ServiceFeeDto serviceFeeDto) {
        ServiceFee serviceFee = ServiceFeeMapper.INSTANCE.toEntity(serviceFeeDto);
        serviceFee = serviceFeeRepository.save(serviceFee);
        return ServiceFeeMapper.INSTANCE.toDto(serviceFee);
    }

    @Override
    public void delete(Long id) {
        ServiceFee serviceFee = serviceFeeRepository.findById(id)
                .orElseThrow(()-> BadRequestException.message("Không tìm thấy phí dịch vụ"));
        serviceFeeRepository.delete(serviceFee);
    }
}