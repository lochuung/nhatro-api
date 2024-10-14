package vn.huuloc.boardinghouse.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.huuloc.boardinghouse.constant.SettingConstants;
import vn.huuloc.boardinghouse.dto.InvoiceDto;
import vn.huuloc.boardinghouse.dto.ServiceFeeDto;
import vn.huuloc.boardinghouse.dto.mapper.InvoiceMapper;
import vn.huuloc.boardinghouse.dto.request.InvoiceRequest;
import vn.huuloc.boardinghouse.dto.sort.filter.SearchRequest;
import vn.huuloc.boardinghouse.dto.sort.filter.SearchSpecification;
import vn.huuloc.boardinghouse.entity.Contract;
import vn.huuloc.boardinghouse.entity.Invoice;
import vn.huuloc.boardinghouse.entity.ServiceFee;
import vn.huuloc.boardinghouse.enums.ContractStatus;
import vn.huuloc.boardinghouse.enums.InvoiceType;
import vn.huuloc.boardinghouse.exception.BadRequestException;
import vn.huuloc.boardinghouse.repository.ContractRepository;
import vn.huuloc.boardinghouse.repository.InvoiceRepository;
import vn.huuloc.boardinghouse.repository.ServiceFeeRepository;
import vn.huuloc.boardinghouse.service.InvoiceService;
import vn.huuloc.boardinghouse.service.SettingService;
import vn.huuloc.boardinghouse.util.CommonUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final ServiceFeeRepository serviceFeeRepository;
    private final ContractRepository contractRepository;
    private final SettingService settingService;

    @Override
    @Transactional
    public InvoiceDto create(InvoiceRequest invoiceRequest) {
        double electricUnitPrice = Double.parseDouble(settingService
                .getSetting(SettingConstants.ELECTRICITY_UNIT_PRICE));
        double waterUnitPrice = Double.parseDouble(settingService
                .getSetting(SettingConstants.WATER_UNIT_PRICE));

        if (!CommonUtils.isNewRecord(invoiceRequest.getId())) {
            throw new RuntimeException("Hóa đơn đã tồn tại");
        }
        Contract contract = contractRepository.findById(invoiceRequest.getContractId())
                .orElseThrow(() -> new RuntimeException("Hợp đồng không tồn tại"));
        if (contract.getStatus() == ContractStatus.CLOSED) {
            throw new RuntimeException("Hợp đồng đã đóng");
        }

        Invoice invoice = InvoiceMapper.INSTANCE.toEntity(invoiceRequest);
        invoice.setContract(contract);
        invoice.setWaterUnitPrice(BigDecimal.valueOf(waterUnitPrice));
        invoice.setElectricityUnitPrice(BigDecimal.valueOf(electricUnitPrice));

        invoice.setElectricityAmount(BigDecimal.valueOf((invoice.getNewElectricityNumber() - invoice.getOldElectricityNumber())
                * electricUnitPrice));
        invoice.setWaterAmount(BigDecimal.valueOf((invoice.getNewWaterNumber() - invoice.getOldWaterNumber())
                * waterUnitPrice));

        if (invoiceRequest.getType() == InvoiceType.MONTHLY) {

            List<Long> serviceFeeIds = invoiceRequest.getServiceFees().stream()
                    .map(ServiceFeeDto::getId)
                    .collect(Collectors.toList());
            List<ServiceFee> serviceFees = serviceFeeRepository.findAllById(serviceFeeIds);
            if (serviceFees.size() != serviceFeeIds.size()) {
                throw BadRequestException.message("Một số dịch vụ không tồn tại");
            }
            invoice.setServiceFees(serviceFees);
            BigDecimal total = serviceFees.stream()
                    .map(ServiceFee::getUnitPrice)
                    .reduce(invoice.getElectricityAmount().add(invoice.getWaterAmount()), BigDecimal::add);


            invoice.setTotalAmount(total);
            invoice.setDueDate(invoiceRequest.getDueDate());
            return InvoiceMapper.INSTANCE.toDto(invoiceRepository.save(invoice));
        }
        if (invoiceRequest.getType() == InvoiceType.CHECK_OUT) {
            BigDecimal unpaidAmount = invoiceRepository
                    .findUnpaidInvoicesByContract(contract.getId())
                    .stream()
                    .map(i -> i.getTotalAmount().subtract(i.getPaidAmount()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal deposit = contract.getDeposit();
            BigDecimal finalAmount = invoiceRequest.getCustomAmount().add(unpaidAmount).subtract(deposit);

            invoice.setContract(contract);
            invoice.setCustomAmount(finalAmount);
            contractRepository.save(contract);

            return InvoiceMapper.INSTANCE.toDto(invoiceRepository.save(invoice));
        }
        throw BadRequestException.message("Loại hóa đơn không hợp lệ");
    }

    @Override
    public InvoiceDto update(InvoiceRequest invoiceRequest) {
        Invoice invoice = invoiceRepository.findById(invoiceRequest.getId())
                .orElseThrow(() -> BadRequestException.message("Hóa đơn không tồn tại"));
        invoice.setPaidAmount(invoiceRequest.getPaidAmount());
        invoice.setNote(invoiceRequest.getNote());
        invoice.setAdminNote(invoiceRequest.getAdminNote());
        if (invoice.getType() == InvoiceType.MONTHLY) {
            double electricUnitPrice = Double.parseDouble(settingService
                    .getSetting(SettingConstants.ELECTRICITY_UNIT_PRICE));
            double waterUnitPrice = Double.parseDouble(settingService
                    .getSetting(SettingConstants.WATER_UNIT_PRICE));

            invoice.setOldElectricityNumber(invoiceRequest.getOldElectricityNumber());
            invoice.setOldWaterNumber(invoiceRequest.getOldWaterNumber());
            invoice.setNewElectricityNumber(invoiceRequest.getNewElectricityNumber());
            invoice.setNewWaterNumber(invoiceRequest.getNewWaterNumber());
            invoice.setUsageElectricityNumber(invoice.getNewElectricityNumber() - invoice.getOldElectricityNumber());
            invoice.setUsageWaterNumber(invoice.getNewWaterNumber() - invoice.getOldWaterNumber());
            invoice.setDueDate(invoiceRequest.getDueDate());
            invoice.setElectricityAmount(BigDecimal.valueOf((invoice.getNewElectricityNumber() - invoice.getOldElectricityNumber())
                    * electricUnitPrice));
            invoice.setWaterAmount(BigDecimal.valueOf((invoice.getNewWaterNumber() - invoice.getOldWaterNumber())
                    * waterUnitPrice));
            BigDecimal total = invoice.getElectricityAmount().add(invoice.getWaterAmount());

            invoice.getServiceFees().clear();
            for (ServiceFeeDto serviceFeeDto : invoiceRequest.getServiceFees()) {
                ServiceFee serviceFee = serviceFeeRepository.findById(serviceFeeDto.getId())
                        .orElseThrow(() -> BadRequestException.message("Dịch vụ không tồn tại"));
                invoice.getServiceFees().add(serviceFee);
                total = total.add(serviceFee.getUnitPrice());
            }

            invoice.setTotalAmount(total);
            return InvoiceMapper.INSTANCE.toDto(invoiceRepository.save(invoice));
        }
        if (invoice.getType() == InvoiceType.CHECK_OUT) {
            // Ensure the new custom amount reflects any outstanding balances
            BigDecimal unpaidAmount = invoiceRepository
                    .findUnpaidInvoicesByContract(invoice.getContract().getId())
                    .stream()
                    .map(i -> i.getTotalAmount().subtract(i.getPaidAmount()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal deposit = invoice.getContract().getDeposit();
            BigDecimal finalAmount = invoiceRequest.getCustomAmount().add(unpaidAmount).subtract(deposit);

            invoice.setCustomAmount(finalAmount);
            return InvoiceMapper.INSTANCE.toDto(invoiceRepository.save(invoice));
        }
        throw BadRequestException.message("Loại hóa đơn không hợp lệ");
    }

    @Override
    public void delete(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> BadRequestException.message("Hóa đơn không tồn tại"));
        invoiceRepository.delete(invoice);
    }

    @Override
    public InvoiceDto findById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> BadRequestException.message("Hóa đơn không tồn tại"));
        return InvoiceMapper.INSTANCE.toDto(invoice);
    }

    @Override
    public Page<InvoiceDto> search(SearchRequest searchRequest) {
        SearchSpecification<Invoice> specification = new SearchSpecification<>(searchRequest);
        Pageable pageable = SearchSpecification.getPageable(searchRequest.getPage(),
                searchRequest.getSize());
        return invoiceRepository.findAll(specification, pageable)
                .map(InvoiceMapper.INSTANCE::toDto);
    }
}
