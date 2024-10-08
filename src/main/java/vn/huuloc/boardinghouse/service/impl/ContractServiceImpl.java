package vn.huuloc.boardinghouse.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.huuloc.boardinghouse.dto.ContractDto;
import vn.huuloc.boardinghouse.dto.mapper.ContractMapper;
import vn.huuloc.boardinghouse.dto.request.CheckinRequest;
import vn.huuloc.boardinghouse.dto.request.CheckoutRequest;
import vn.huuloc.boardinghouse.dto.request.ContractCustomerRequest;
import vn.huuloc.boardinghouse.dto.request.CustomerRequest;
import vn.huuloc.boardinghouse.dto.sort.filter.SearchRequest;
import vn.huuloc.boardinghouse.dto.sort.filter.SearchSpecification;
import vn.huuloc.boardinghouse.entity.Contract;
import vn.huuloc.boardinghouse.entity.ContractCustomerLinked;
import vn.huuloc.boardinghouse.entity.Room;
import vn.huuloc.boardinghouse.enums.ContractStatus;
import vn.huuloc.boardinghouse.enums.RoomStatus;
import vn.huuloc.boardinghouse.exception.BadRequestException;
import vn.huuloc.boardinghouse.repository.ContractCustomerLinkedRepository;
import vn.huuloc.boardinghouse.repository.ContractRepository;
import vn.huuloc.boardinghouse.repository.RoomRepository;
import vn.huuloc.boardinghouse.service.ContractService;
import vn.huuloc.boardinghouse.service.CustomerService;
import vn.huuloc.boardinghouse.util.CommonUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {
    private final ContractRepository contractRepository;
    private final RoomRepository roomRepository;
    private final ContractCustomerLinkedRepository contractCustomerLinkedRepository;
    private final CustomerService customerService;

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
                        .contract(finalContract).hasLeft(false).build();
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
        return ContractMapper.INSTANCE.toDto(contract);
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