package vn.huuloc.boardinghouse.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.cnj.shared.sortfilter.specification.SearchSpecification;
import vn.huuloc.boardinghouse.dto.ContractDto;
import vn.huuloc.boardinghouse.dto.mapper.ContractMapper;
import vn.huuloc.boardinghouse.dto.mapper.CustomerMapper;
import vn.huuloc.boardinghouse.dto.request.*;
import vn.huuloc.boardinghouse.dto.sort.filter.ContractSearchRequest;
import vn.huuloc.boardinghouse.entity.Contract;
import vn.huuloc.boardinghouse.entity.ContractCustomerLinked;
import vn.huuloc.boardinghouse.entity.Customer;
import vn.huuloc.boardinghouse.entity.Room;
import vn.huuloc.boardinghouse.enums.ContractStatus;
import vn.huuloc.boardinghouse.enums.RoomStatus;
import vn.huuloc.boardinghouse.exception.BadRequestException;
import vn.huuloc.boardinghouse.repository.ContractCustomerLinkedRepository;
import vn.huuloc.boardinghouse.repository.ContractRepository;
import vn.huuloc.boardinghouse.repository.RoomRepository;
import vn.huuloc.boardinghouse.service.ContractService;
import vn.huuloc.boardinghouse.service.CustomerService;
import vn.huuloc.boardinghouse.service.SettingService;
import vn.huuloc.boardinghouse.service.WordService;
import vn.huuloc.boardinghouse.util.CommonUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static vn.huuloc.boardinghouse.util.ContractUtils.mapContractToPlaceholders;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {
    private final ContractRepository contractRepository;
    private final RoomRepository roomRepository;
    private final ContractCustomerLinkedRepository contractCustomerLinkedRepository;
    private final CustomerService customerService;
    private final SettingService settingService;

    private final WordService wordService;
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

            contract.setOwner(Customer.builder().id(renters.get(0).getCustomerId()).build());

            contract = contractRepository.save(contract);
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
    public ContractDto addMember(AddMemberRequest addMemberRequest) {
        Contract contract = contractRepository.findById(addMemberRequest.getContractId())
                .orElseThrow(() -> BadRequestException.message("Không tìm thấy hợp đồng"));
        if (contract.getStatus() != ContractStatus.OPENING) {
            throw BadRequestException.message("Hợp đồng đã kết thúc");
        }
        return addMember(addMemberRequest, contract);
    }

    private ContractDto addMember(AddMemberRequest addMemberRequest, Contract contract) {
        List<Long> customerIds = addMemberRequest.getCustomers().stream()
                .map(CustomerRequest::getId).toList();
        List<ContractCustomerLinked> renters = contractCustomerLinkedRepository
                .findByRenterIds(contract.getId(), customerIds);
        // filter customer is not renter
        List<CustomerRequest> newRenters = addMemberRequest.getCustomers().stream()
                .filter(customerDto -> renters.stream().noneMatch(renter ->
                        renter.getCustomerId().equals(customerDto.getId()))).toList();
        if (newRenters.isEmpty()) {
            throw BadRequestException.message("Các khách hàng đều đã thuê phòng này");
        }
        List<ContractCustomerLinked> newRentersLinks = new ArrayList<>();
        newRenters.forEach(customerDto -> {
            ContractCustomerLinked contractCustomerLinked = ContractCustomerLinked.builder()
                    .contractId(contract.getId()).hasLeft(false).build();
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
    public byte[] printContract(Long id) throws IOException {
        Contract contract = contractRepository.findById(id).orElseThrow(() ->
                BadRequestException.message("Không tìm thấy hợp đồng"));

        // Prepare the data for placeholders
        Map<String, String> contractData = mapContractToPlaceholders(contract, settingService, contractCustomerLinkedRepository);

        return wordService.print(CONTRACT_TEMPLATE, contractData);
    }

    @Override
    public List<ContractDto> findAllAvailable() {
        List<Contract> contracts = contractRepository.findByStatus(ContractStatus.OPENING);
        return ContractMapper.INSTANCE.toDto(contracts);
    }

    @Override
    public List<ContractDto> findAll() {
        List<Contract> contracts = contractRepository.findAll();
        return ContractMapper.INSTANCE.toDto(contracts);
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
                    .contractId(contract.getId()).hasLeft(false).build();
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
//        contractDto.setOwner(CustomerMapper.INSTANCE.toDto(contractCustomerLinkedRepository.findOwnerByContractId(id).getCustomer()));
        return contractDto;
    }

    @Override
    public Page<ContractDto> search(ContractSearchRequest searchRequest) {
        SearchSpecification<Contract> specification = new SearchSpecification<>(searchRequest);

        Specification<Contract> roomIdSpec = Optional.ofNullable(searchRequest.getRoomCode())
                .map(code -> (Specification<Contract>) (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("room").get("code"), code))
                .orElse(null);

        Specification<Contract> statusSpec = Optional.ofNullable(searchRequest.getStatus())
                .map(status -> (Specification<Contract>) (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("status"), status))
                .orElse(null);

        Specification<Contract> finalSpec = specification.and(roomIdSpec).and(statusSpec);

        Pageable pageable = SearchSpecification.getPageable(searchRequest.getPage(), searchRequest.getSize());
        Page<Contract> contracts = contractRepository.findAll(finalSpec, pageable);
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
        contract.setOwner(newOwner.getCustomer());
        contract = contractRepository.save(contract);
        contractCustomerLinkedRepository.save(newOwner);
        return ContractMapper.INSTANCE.toDto(contract);
    }
}