package vn.huuloc.boardinghouse.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import vn.cnj.shared.sortfilter.request.SearchRequest;
import vn.cnj.shared.sortfilter.specification.SearchSpecification;
import vn.huuloc.boardinghouse.constant.SettingConstants;
import vn.huuloc.boardinghouse.dto.InvoiceDto;
import vn.huuloc.boardinghouse.dto.ServiceFeeDto;
import vn.huuloc.boardinghouse.dto.mapper.InvoiceMapper;
import vn.huuloc.boardinghouse.dto.request.InvoiceRequest;
import vn.huuloc.boardinghouse.entity.Contract;
import vn.huuloc.boardinghouse.entity.Invoice;
import vn.huuloc.boardinghouse.entity.ServiceFee;
import vn.huuloc.boardinghouse.enums.ContractStatus;
import vn.huuloc.boardinghouse.enums.InvoiceType;
import vn.huuloc.boardinghouse.exception.BadRequestException;
import vn.huuloc.boardinghouse.repository.ContractRepository;
import vn.huuloc.boardinghouse.repository.InvoiceRepository;
import vn.huuloc.boardinghouse.repository.ServiceFeeRepository;
import vn.huuloc.boardinghouse.service.ContractService;
import vn.huuloc.boardinghouse.service.InvoiceService;
import vn.huuloc.boardinghouse.service.PdfService;
import vn.huuloc.boardinghouse.service.SettingService;
import vn.huuloc.boardinghouse.util.CommonUtils;
import vn.huuloc.boardinghouse.util.InvoiceUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final ServiceFeeRepository serviceFeeRepository;
    private final ContractRepository contractRepository;
    private final SettingService settingService;
    private final ContractService contractService;

    private final PdfService pdfService;

    private static final String INVOICE_TEMPLATE = "invoice";

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
        invoice.setWaterUnitPrice(invoice.getWaterUnitPrice() == null ? BigDecimal.valueOf(waterUnitPrice) : invoice.getWaterUnitPrice());
        invoice.setElectricityUnitPrice(invoice.getElectricityUnitPrice() == null ? BigDecimal.valueOf(electricUnitPrice) : invoice.getElectricityUnitPrice());
        invoice.setElectricityAmount(InvoiceUtils.calculateElectricityAmount(invoice));
        invoice.setWaterAmount(InvoiceUtils.calculateWaterAmount(invoice));

        BigDecimal total = invoice.getElectricityAmount().add(invoice.getWaterAmount());

        if (invoiceRequest.getType() == InvoiceType.MONTHLY) {

            List<Long> serviceFeeIds = invoiceRequest.getServiceFees().stream()
                    .map(ServiceFeeDto::getId)
                    .collect(Collectors.toList());
            List<ServiceFee> serviceFees = serviceFeeRepository.findAllById(serviceFeeIds);
            if (serviceFees.size() != serviceFeeIds.size()) {
                throw BadRequestException.message("Một số dịch vụ không tồn tại");
            }
            invoice.setServiceFees(serviceFees);

            for (ServiceFee serviceFee : serviceFees) {
                total = total.add(serviceFee.getUnitPrice());
            }

            total = total.add(contract.getPrice());


            invoice.setSubTotal(total);
            invoice.setDiscount(invoiceRequest.getDiscount());
            total = total.subtract(invoiceRequest.getDiscount());
            invoice.setTotalAmount(total);
            invoice.setEndDate(invoiceRequest.getEndDate());
            return InvoiceMapper.INSTANCE.toDto(invoiceRepository.save(invoice));
        }
        if (invoiceRequest.getType() == InvoiceType.CHECK_OUT) {
            int dayOfMonth = LocalDateTime.now().getDayOfMonth();
            if (invoiceRequest.getCustomAmount() == null) {
                BigDecimal rateOfMonthsDecimal = BigDecimal.valueOf(dayOfMonth * 1.0 / 30);
                BigDecimal monthlyPrice = contract.getPrice().multiply(rateOfMonthsDecimal);
                total = total.add(monthlyPrice);
            } else {
                total = total.add(invoiceRequest.getCustomAmount());
            }
            invoice.setSubTotal(total);
            invoice.setDiscount(invoiceRequest.getDiscount());
            total = total.subtract(invoiceRequest.getDiscount());
            invoice.setTotalAmount(total);
            invoice.setCustomAmount(invoiceRequest.getCustomAmount());
            return InvoiceMapper.INSTANCE.toDto(invoiceRepository.save(invoice));
        }
        throw BadRequestException.message("Loại hóa đơn không hợp lệ");
    }

    @Override
    public InvoiceDto update(InvoiceRequest invoiceRequest) {
        Invoice invoice = invoiceRepository.findById(invoiceRequest.getId())
                .orElseThrow(() -> BadRequestException.message("Hóa đơn không tồn tại"));
        double electricUnitPrice = Double.parseDouble(settingService
                .getSetting(SettingConstants.ELECTRICITY_UNIT_PRICE));
        double waterUnitPrice = Double.parseDouble(settingService
                .getSetting(SettingConstants.WATER_UNIT_PRICE));
        invoice.setPaidAmount(invoiceRequest.getPaidAmount());
        invoice.setNote(invoiceRequest.getNote());
        invoice.setAdminNote(invoiceRequest.getAdminNote());
        invoice.setOldElectricityNumber(invoiceRequest.getOldElectricityNumber());
        invoice.setOldWaterNumber(invoiceRequest.getOldWaterNumber());
        invoice.setNewElectricityNumber(invoiceRequest.getNewElectricityNumber());
        invoice.setNewWaterNumber(invoiceRequest.getNewWaterNumber());
        invoice.setUsageElectricityNumber(invoice.getNewElectricityNumber() - invoice.getOldElectricityNumber());
        invoice.setUsageWaterNumber(invoice.getNewWaterNumber() - invoice.getOldWaterNumber());
        invoice.setStartDate(invoiceRequest.getStartDate());
        invoice.setEndDate(invoiceRequest.getEndDate());

        invoice.setElectricityUnitPrice(invoice.getElectricityUnitPrice() == null ? BigDecimal.valueOf(electricUnitPrice) : invoice.getElectricityUnitPrice());
        invoice.setWaterUnitPrice(invoice.getWaterUnitPrice() == null ? BigDecimal.valueOf(waterUnitPrice) : invoice.getWaterUnitPrice());
        invoice.setElectricityAmount(InvoiceUtils.calculateElectricityAmount(invoice));
        invoice.setWaterAmount(InvoiceUtils.calculateWaterAmount(invoice));


        BigDecimal total = invoice.getElectricityAmount().add(invoice.getWaterAmount());

        if (invoice.getType() == InvoiceType.MONTHLY) {

            total = total.add(invoice.getContract().getPrice());

            invoice.getServiceFees().clear();
            for (ServiceFeeDto serviceFeeDto : invoiceRequest.getServiceFees()) {
                ServiceFee serviceFee = serviceFeeRepository.findById(serviceFeeDto.getId())
                        .orElseThrow(() -> BadRequestException.message("Dịch vụ không tồn tại"));
                invoice.getServiceFees().add(serviceFee);
                total = total.add(serviceFee.getUnitPrice());
            }

            invoice.setSubTotal(total);
            invoice.setDiscount(invoiceRequest.getDiscount());
            total = total.subtract(invoiceRequest.getDiscount());
            invoice.setTotalAmount(total);
            return InvoiceMapper.INSTANCE.toDto(invoiceRepository.save(invoice));
        }
        if (invoice.getType() == InvoiceType.CHECK_OUT) {
            int dayOfMonth = LocalDateTime.now().getDayOfMonth();
            if (invoice.getCustomAmount() == null) {
                BigDecimal numberOfMonthsDecimal = BigDecimal.valueOf(dayOfMonth);
                BigDecimal monthlyPrice = invoice.getContract().getPrice().multiply(numberOfMonthsDecimal).multiply(BigDecimal.valueOf(1.0 / 30));
                total = total.add(monthlyPrice);
            } else {
                total = total.add(invoiceRequest.getCustomAmount());
            }

            invoice.setSubTotal(total);
            invoice.setDiscount(invoiceRequest.getDiscount());
            total = total.subtract(invoiceRequest.getDiscount());
            invoice.setTotalAmount(total);
            invoice.setCustomAmount(invoiceRequest.getCustomAmount());
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
        InvoiceDto invoiceDto = InvoiceMapper.INSTANCE.toDto(invoice);
        invoiceDto.setContract(contractService.findById(invoice.getContract().getId()));
        return invoiceDto;
    }

    @Override
    public Page<InvoiceDto> search(SearchRequest searchRequest) {
        SearchSpecification<Invoice> specification = new SearchSpecification<>(searchRequest);
        Pageable pageable = SearchSpecification.getPageable(searchRequest.getPage(),
                searchRequest.getSize());
        return invoiceRepository.findAll(specification, pageable)
                .map(InvoiceMapper.INSTANCE::toDto);
    }

    @Override
    public byte[] print(Long id) throws IOException {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> BadRequestException.message("Hóa đơn không tồn tại"));
        invoice.setPrintDate(LocalDateTime.now());
        invoice = invoiceRepository.save(invoice);
        InvoiceDto invoiceDto = InvoiceMapper.INSTANCE.toDto(invoice);
        invoiceDto.setContract(contractService.findById(invoice.getContract().getId()));

        // Process the HTML template with Thymeleaf
        Context context = new Context();
        context.setVariable("invoice", invoiceDto);

        return pdfService.generatePdfFromSource(INVOICE_TEMPLATE, context);
    }
}
