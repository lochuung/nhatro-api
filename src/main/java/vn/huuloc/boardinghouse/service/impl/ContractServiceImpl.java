package vn.huuloc.boardinghouse.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import vn.huuloc.boardinghouse.dto.ContractDto;
import vn.huuloc.boardinghouse.dto.mapper.ContractMapper;
import vn.huuloc.boardinghouse.dto.request.CheckinRequest;
import vn.huuloc.boardinghouse.dto.request.CheckoutRequest;
import vn.huuloc.boardinghouse.dto.request.CustomerRequest;
import vn.huuloc.boardinghouse.dto.sort.filter.SearchRequest;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {
    private final ContractRepository contractRepository;
    private final RoomRepository roomRepository;
    private final ContractCustomerLinkedRepository contractCustomerLinkedRepository;

    @Override
    @Transactional
    public ContractDto checkIn(CheckinRequest contractRequest) {
        Room room = roomRepository.findById(contractRequest.getRoomId())
                .orElseThrow(() -> BadRequestException.message("Không tìm thấy phòng trọ"));
        Contract contract = contractRepository.findByRoomIdAndStatus(contractRequest.getRoomId(),
                ContractStatus.OPENING);
        if (contract == null) {
            // create new contract
            contract = ContractMapper.INSTANCE.toEntity(contractRequest);
            contract.setNumberOfPeople(contractRequest.getCustomers().size());
            contract.setRoom(room);
            contract.setStatus(ContractStatus.OPENING);
            contract = contractRepository.save(contract);

            Contract finalContract = contract;
            List<ContractCustomerLinked> renters = new ArrayList<>();
            contractRequest.getCustomers().forEach(customer -> {
                ContractCustomerLinked contractCustomerLinked = ContractCustomerLinked.builder()
                        .contract(finalContract)
                        .customerId(customer.getId())
                        .isRenter(true)
                        .build();
                renters.add(contractCustomerLinked);
            });
            contractCustomerLinkedRepository.saveAll(renters);

            // set room status
            room.setStatus(RoomStatus.RENTED);
            roomRepository.save(room);
            return ContractMapper.INSTANCE.toDto(contract);
        }


        // add new customer to contract
        List<Long> customerIds = contractRequest.getCustomers().stream()
                .map(CustomerRequest::getId).toList();
        List<ContractCustomerLinked> renters = contractCustomerLinkedRepository
                .findByRenterIds(contract.getId(), customerIds);
        // filter customer is not renter
        List<CustomerRequest> newRenters = contractRequest.getCustomers().stream()
                .filter(customerDto -> renters.stream()
                        .noneMatch(renter ->
                                renter.getCustomerId().equals(customerDto.getId())))
                .toList();
        if (newRenters.isEmpty()) {
            throw BadRequestException.message("Các khách hàng đều đã thuê phòng này");
        }
        List<ContractCustomerLinked> newRentersLinks = new ArrayList<>();
        Contract finalContract = contract;
        newRenters.forEach(customerDto -> {
            ContractCustomerLinked contractCustomerLinked = ContractCustomerLinked.builder()
                    .contract(finalContract)
                    .customerId(customerDto.getId())
                    .isRenter(true)
                    .build();
            newRentersLinks.add(contractCustomerLinked);
        });
        contractCustomerLinkedRepository.saveAll(newRentersLinks);

        // update number of people
        contract.setNumberOfPeople(contract.getNumberOfPeople() + newRenters.size());
        contractRepository.save(contract);
        return ContractMapper.INSTANCE.toDto(contract);
    }

    @Override
    public ContractDto checkOut(CheckoutRequest checkoutRequest) {
        return null;
    }

    @Override
    public ContractDto findById(Long id) {
        return null;
    }

    @Override
    public Page<ContractDto> search(SearchRequest searchRequest) {
        return null;
    }
}
