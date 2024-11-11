package vn.huuloc.boardinghouse.service.impl;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import vn.cnj.shared.sortfilter.specification.SearchSpecification;
import vn.huuloc.boardinghouse.constant.SettingConstants;
import vn.huuloc.boardinghouse.dto.InvoiceDto;
import vn.huuloc.boardinghouse.dto.ServiceFeeDto;
import vn.huuloc.boardinghouse.dto.mapper.ContractMapper;
import vn.huuloc.boardinghouse.dto.mapper.InvoiceMapper;
import vn.huuloc.boardinghouse.dto.request.InvoiceRequest;
import vn.huuloc.boardinghouse.dto.sort.filter.InvoiceSearchRequest;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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
    public Page<InvoiceDto> search(InvoiceSearchRequest searchRequest) {
        Specification<Invoice> specification = new SearchSpecification<>(searchRequest);
        Pageable pageable = SearchSpecification.getPageable(searchRequest.getPage(), searchRequest.getSize());

        // Tạo từng Specification tạm thời và kết hợp chúng sau
        Specification<Invoice> roomIdSpec = Optional.ofNullable(searchRequest.getRoomId())
                .map(this::byRoomId)
                .orElse(Specification.where(null));

        Specification<Invoice> monthSpec = Optional.ofNullable(searchRequest.getMonth())
                .map(month -> {
                    String[] monthYear = month.split("/");
                    if (monthYear.length != 2) {
                        throw BadRequestException.message("Tháng không hợp lệ");
                    }
                    int monthValue = Integer.parseInt(monthYear[0]);
                    int yearValue = Integer.parseInt(monthYear[1]);
                    return byMonthAndYear(monthValue, yearValue);
                })
                .orElse(Specification.where(null));

        Specification<Invoice> isPaidSpec = Optional.ofNullable(searchRequest.getIsPaid())
                .map(this::byIsPaid)
                .orElse(Specification.where(null));

        Specification<Invoice> searchSpec = Optional.ofNullable(searchRequest.getKeyword())
                .map(keyword -> (Specification<Invoice>) (root, query, criteriaBuilder) -> {
                    String keywordLike = "%" + keyword + "%";
                    Predicate roomNamePredicate = criteriaBuilder.like(root.get("contract").get("room").get("name"), keywordLike);
                    Predicate roomCodePredicate = criteriaBuilder.like(root.get("contract").get("room").get("code"), keywordLike);
                    return criteriaBuilder.or(roomNamePredicate, roomCodePredicate);
                })
                .orElse(Specification.where(null));

        Specification<Invoice> typeSpec = Optional.ofNullable(searchRequest.getType())
                .map(type -> (Specification<Invoice>)(root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("type"), type))
                .orElse(Specification.where(null));

        // Kết hợp tất cả Specifications
        specification = specification.and(roomIdSpec).and(monthSpec).and(isPaidSpec)
                .and(searchSpec).and(typeSpec);

        // Thực hiện truy vấn và ánh xạ kết quả sang DTO
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
        Contract contract = invoice.getContract();
        invoiceDto.setContract(ContractMapper.INSTANCE.toDto(contract));

        // Process the HTML template with Thymeleaf
        Context context = new Context();
        context.setVariable("invoice", invoiceDto);

        return pdfService.generatePdfFromSource(INVOICE_TEMPLATE, context);
    }


    // Phương thức helper cho RoomId
    private Specification<Invoice> byRoomId(Long roomId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("contract").get("room").get("id"), roomId);
    }

    // Phương thức helper cho Month và Year
    private Specification<Invoice> byMonthAndYear(int month, int year) {
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1);

        return (root, query, criteriaBuilder) -> {
            Predicate startDatePredicate = criteriaBuilder.between(root.get("startDate"), startDate, endDate);
            Predicate endDatePredicate = criteriaBuilder.between(root.get("endDate"), startDate, endDate);
            return criteriaBuilder.or(startDatePredicate, endDatePredicate);
        };
    }

    // Phương thức helper cho IsPaid
    private Specification<Invoice> byIsPaid(boolean isPaid) {
        return (root, query, criteriaBuilder) -> {
            Predicate paidPredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("paidAmount"), root.get("totalAmount"));
            return isPaid ? paidPredicate : criteriaBuilder.not(paidPredicate);
        };
    }
}
