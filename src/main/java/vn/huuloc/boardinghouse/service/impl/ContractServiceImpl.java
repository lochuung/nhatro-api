package vn.huuloc.boardinghouse.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.huuloc.boardinghouse.constant.SettingConstants;
import vn.huuloc.boardinghouse.dto.ContractDto;
import vn.huuloc.boardinghouse.dto.mapper.ContractMapper;
import vn.huuloc.boardinghouse.dto.mapper.CustomerMapper;
import vn.huuloc.boardinghouse.dto.request.CheckinRequest;
import vn.huuloc.boardinghouse.dto.request.CheckoutRequest;
import vn.huuloc.boardinghouse.dto.request.ContractCustomerRequest;
import vn.huuloc.boardinghouse.dto.request.CustomerRequest;
import vn.huuloc.boardinghouse.dto.sort.filter.SearchRequest;
import vn.huuloc.boardinghouse.dto.sort.filter.SearchSpecification;
import vn.huuloc.boardinghouse.entity.*;
import vn.huuloc.boardinghouse.enums.ContractStatus;
import vn.huuloc.boardinghouse.enums.RoomStatus;
import vn.huuloc.boardinghouse.exception.BadRequestException;
import vn.huuloc.boardinghouse.repository.ContractCustomerLinkedRepository;
import vn.huuloc.boardinghouse.repository.ContractRepository;
import vn.huuloc.boardinghouse.repository.RoomRepository;
import vn.huuloc.boardinghouse.service.ContractService;
import vn.huuloc.boardinghouse.service.CustomerService;
import vn.huuloc.boardinghouse.service.SettingService;
import vn.huuloc.boardinghouse.util.CommonUtils;
import vn.huuloc.boardinghouse.util.ContractUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {
    private final ContractRepository contractRepository;
    private final RoomRepository roomRepository;
    private final ContractCustomerLinkedRepository contractCustomerLinkedRepository;
    private final CustomerService customerService;
    private final SettingService settingService;

    private static final String CONTRACT_TEMPLATE = "templates/contract.docx";

    @Override
    @Transactional
    public ContractDto checkIn(CheckinRequest contractRequest) {
        Room room = roomRepository.findById(contractRequest.getRoomId())
                .orElseThrow(() -> BadRequestException.message("Không tìm thấy phòng trọ"));
        Contract contract = contractRepository.findByRoomIdAndStatus(contractRequest.getRoomId(), ContractStatus.OPENING);
        if (contract == null) {
            // create new contract
            contract = ContractMapper.INSTANCE.toEntity(contractRequest);
            contract.setNumberOfPeople(contractRequest.getCustomers().size());
            contract.setRoom(room);
            contract.setStatus(ContractStatus.OPENING);
            contract = contractRepository.save(contract);
            // add renters to contract
            Contract finalContract = contract;
            List<ContractCustomerLinked> renters = new ArrayList<>();

            contractRequest.getCustomers().forEach(customer -> {
                ContractCustomerLinked contractCustomerLinked = ContractCustomerLinked.builder()
                        .contractId(finalContract.getId()).hasLeft(false).build();
                if (CommonUtils.isNewRecord(customer.getId())) {
                    contractCustomerLinked.setCustomerId(customerService.saveOrUpdate(customer).getId());
                } else {
                    contractCustomerLinked.setCustomerId(customer.getId());
                }
                renters.add(contractCustomerLinked);
            });
            if (renters.isEmpty()) {
                throw BadRequestException.message("Không tìm thấy khách hàng thuê phòng");
            }
            renters.get(0).setOwner(true);
            contractCustomerLinkedRepository.saveAll(renters);

            // set room status
            room.setStatus(RoomStatus.RENTED);
            roomRepository.save(room);
            return ContractMapper.INSTANCE.toDto(contract);
        }

        // add new customer to contract
        return addMember(contractRequest, contract);
    }

    @Override
    @Transactional
    public ContractDto checkOut(CheckoutRequest checkoutRequest) {
        Contract contract = contractRepository.findById(checkoutRequest.getContractId())
                .orElseThrow(() -> BadRequestException.message("Không tìm thấy hợp đồng"));
        if (contract.getStatus() != ContractStatus.OPENING) {
            throw BadRequestException.message("Hợp đồng đã kết thúc");
        }
        contract.setEndDate(LocalDate.parse(checkoutRequest.getCheckoutDate()));
        contract.setStatus(ContractStatus.CLOSED);
        contractRepository.save(contract);

        // set room status
        Room room = contract.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);
        return ContractMapper.INSTANCE.toDto(contract);
    }

    @Override
    public ContractDto leave(ContractCustomerRequest request) {
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> BadRequestException.message("Không tìm thấy hợp đồng"));
        if (contract.getStatus() != ContractStatus.OPENING) {
            throw BadRequestException.message("Hợp đồng đã kết thúc");
        }
        ContractCustomerLinked renter = contractCustomerLinkedRepository
                .findByRenterId(contract.getId(), request.getCustomerId());
        if (renter == null) {
            throw BadRequestException.message("Khách hàng không thuê phòng này");
        }
        if (renter.isOwner()) {
            throw BadRequestException.message("Đổi chủ phòng để rời phòng");
        }

        renter.setHasLeft(true);
        if (request.getCheckoutDate() == null) {
            renter.setCheckoutDate(LocalDate.now());
        }
        renter.setCheckoutDate(LocalDate.parse(request.getCheckoutDate()));
        contractCustomerLinkedRepository.save(renter);

        // update number of people
        contract.setNumberOfPeople(contract.getNumberOfPeople() - 1);

        // set room status
        if (contract.getNumberOfPeople() == 0) {
            Room room = contract.getRoom();
            room.setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(room);
            contract.setStatus(ContractStatus.CLOSED);
        }
        contractRepository.save(contract);
        return ContractMapper.INSTANCE.toDto(contract);
    }

    @Override
    @Transactional
    public ContractDto addMember(CheckinRequest checkinRequest) {
        Contract contract = contractRepository.findById(checkinRequest.getContractId())
                .orElseThrow(() -> BadRequestException.message("Không tìm thấy hợp đồng"));
        if (contract.getStatus() != ContractStatus.OPENING) {
            throw BadRequestException.message("Hợp đồng đã kết thúc");
        }
        return addMember(checkinRequest, contract);
    }

    @Override
    public byte[] printContract(Long id) throws IOException {
        Contract contract = contractRepository.findById(id).orElseThrow(() ->
                BadRequestException.message("Không tìm thấy hợp đồng"));

        // Load the Word template from the resources folder
        try (InputStream templateStream = getClass().getClassLoader().getResourceAsStream(CONTRACT_TEMPLATE);
             XWPFDocument document = new XWPFDocument(templateStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // Prepare the data for placeholders
            Map<String, String> contractData = mapContractToPlaceholders(contract);

            // Replace placeholders in the document
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.getText(0);
                    if (text != null) {
                        for (Map.Entry<String, String> entry : contractData.entrySet()) {
                            text = text.replace("{{" + entry.getKey() + "}}", entry.getValue());
                        }
                        run.setText(text, 0);
                    }
                }
            }

            // Write the document content to the byte array output stream
            document.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private Map<String, String> mapContractToPlaceholders(Contract contract) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("'ngày' dd 'tháng' MM 'năm' yyyy");
        Map<String, String> data = new HashMap<>();
        Branch branch = contract.getRoom().getBranch();
        double electricUnitPrice = Double.parseDouble(settingService.getSetting(SettingConstants.ELECTRICITY_UNIT_PRICE));

        // lessor information
        data.put("lessor_name", StringUtils.upperCase(branch.getLessorName()));
        data.put("lessor_birth", branch.getLessorBirth());
        data.put("lessor_cccd", branch.getLessorCCCD());
        data.put("ngay_cap", branch.getNgayCap());
        data.put("noi_cap", branch.getNoiCap());
        data.put("lessor_address", branch.getLessorAddress());
        data.put("lessor_phone", branch.getLessorPhone());
        data.put("branch_name", branch.getName());
        data.put("branch_address", branch.getAddress());

        // print day
        data.put("print_day", LocalDate.now().format(formatter2));

        // Map room and branch information
        data.put("room_name", contract.getRoom().getName());
        data.put("room_address", contract.getRoom().getBranch().getAddress());

        // Map contract details
        data.put("start_date", contract.getStartDate().format(formatter2));

        Period period = Period.between(contract.getStartDate(), contract.getEndDate() != null ? contract.getEndDate() : LocalDate.now());
        int year = period.getYears();
        year = year == 0 ? 1 : year;
        data.put("period", String.valueOf(year));
        data.put("period_month", String.valueOf(year * 12));

        data.put("price", formatCurrency(contract.getPrice()));
        data.put("tien_chu", ContractUtils.getTienChu(contract.getPrice()));
        data.put("electric_unit_price", formatCurrency(BigDecimal.valueOf(electricUnitPrice)));
        data.put("checkin_electric_number", String.valueOf(contract.getCheckinElectricNumber()));
//        data.put("deposit", formatCurrency(contract.getDeposit()));
//        data.put("note", contract.getNote() != null ? contract.getNote() : "Không có");

        // find owner
        ContractCustomerLinked primaryCustomer = contractCustomerLinkedRepository.findOwnerByContractId(contract.getId());
        Customer owner = primaryCustomer.getCustomer();
        data.put("owner_name", StringUtils.upperCase(owner.getName()));
        data.put("owner_birth", owner.getBirthday());
        data.put("owner_cccd", owner.getIdNumber());
        data.put("owner_ngay_cap", owner.getIdDate());
        data.put("owner_noi_cap", owner.getIdPlace());
        data.put("owner_address", owner.getAddress());

        return data;
    }

    private static String formatCurrency(BigDecimal value) {
        if (value == null) return "0";

        // Configure the decimal format to use dot as a grouping separator
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');  // Set '.' as thousands separator
        symbols.setDecimalSeparator(',');

        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(value);
    }

    private ContractDto addMember(CheckinRequest checkinRequest, Contract contract) {
        List<Long> customerIds = checkinRequest.getCustomers().stream()
                .map(CustomerRequest::getId).toList();
        List<ContractCustomerLinked> renters = contractCustomerLinkedRepository
                .findByRenterIds(contract.getId(), customerIds);
        // filter customer is not renter
        List<CustomerRequest> newRenters = checkinRequest.getCustomers().stream()
                .filter(customerDto -> renters.stream().noneMatch(renter ->
                        renter.getCustomerId().equals(customerDto.getId()))).toList();
        if (newRenters.isEmpty()) {
            throw BadRequestException.message("Các khách hàng đều đã thuê phòng này");
        }
        List<ContractCustomerLinked> newRentersLinks = new ArrayList<>();
        newRenters.forEach(customerDto -> {
            ContractCustomerLinked contractCustomerLinked = ContractCustomerLinked.builder()
                    .contract(contract).hasLeft(false).build();
            if (CommonUtils.isNewRecord(customerDto.getId())) {
                contractCustomerLinked.setCustomerId(customerService.saveOrUpdate(customerDto).getId());
            } else {
                contractCustomerLinked.setCustomerId(customerDto.getId());
            }
            newRentersLinks.add(contractCustomerLinked);
        });
        contractCustomerLinkedRepository.saveAll(newRentersLinks);

        // update number of people
        contract.setNumberOfPeople(contract.getNumberOfPeople() + newRenters.size());
        contractRepository.save(contract);
        return ContractMapper.INSTANCE.toDto(contract);
    }

    @Override
    public ContractDto findById(Long id) {
        Contract contract = contractRepository.findById(id).orElseThrow(() ->
                BadRequestException.message("Không tìm thấy hợp đồng"));
        List<Customer> members = contractCustomerLinkedRepository.findRentersByContractId(id)
                .stream().map(ContractCustomerLinked::getCustomer).toList();
        ContractDto contractDto = ContractMapper.INSTANCE.toDto(contract);
        contractDto.setMembers(CustomerMapper.INSTANCE.toDto(members));
        return contractDto;
    }

    @Override
    public Page<ContractDto> search(SearchRequest searchRequest) {
        SearchSpecification<Contract> specification = new SearchSpecification<>(searchRequest);
        Pageable pageable = SearchSpecification.getPageable(searchRequest.getPage(), searchRequest.getSize());
        Page<Contract> contracts = contractRepository.findAll(specification, pageable);
        return contracts.map(ContractMapper.INSTANCE::toDto);
    }

    @Override
    @Transactional
    public ContractDto changeOwner(ContractCustomerRequest contractRequest) {
        Contract contract = contractRepository.findById(contractRequest.getContractId())
                .orElseThrow(() -> BadRequestException.message("Không tìm thấy hợp đồng"));
        if (contract.getStatus() != ContractStatus.OPENING) {
            throw BadRequestException.message("Hợp đồng đã kết thúc");
        }
        ContractCustomerLinked oldOwner = contractCustomerLinkedRepository
                .findOwnerByContractId(contract.getId());
        if (oldOwner == null) {
            throw BadRequestException.message("Không tìm thấy chủ phòng");
        }
        if (oldOwner.getCustomerId().equals(contractRequest.getCustomerId())) {
            throw BadRequestException.message("Khách hàng đã là chủ phòng");
        }
        oldOwner.setOwner(false);
        contractCustomerLinkedRepository.save(oldOwner);

        ContractCustomerLinked newOwner = contractCustomerLinkedRepository
                .findByRenterId(contract.getId(), contractRequest.getCustomerId());
        if (newOwner == null) {
            throw BadRequestException.message("Khách hàng không thuê phòng này");
        }
        newOwner.setOwner(true);
        contractCustomerLinkedRepository.save(newOwner);
        return ContractMapper.INSTANCE.toDto(contract);
    }
}