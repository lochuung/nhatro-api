package vn.huuloc.boardinghouse.service;

import org.springframework.data.domain.Page;
import vn.huuloc.boardinghouse.dto.ContractDto;
import vn.huuloc.boardinghouse.dto.request.AddMemberRequest;
import vn.huuloc.boardinghouse.dto.request.CheckinRequest;
import vn.huuloc.boardinghouse.dto.request.CheckoutRequest;
import vn.huuloc.boardinghouse.dto.request.ContractCustomerRequest;
import vn.huuloc.boardinghouse.dto.sort.filter.ContractSearchRequest;

import java.io.IOException;
import java.util.List;

public interface ContractService {
    ContractDto checkIn(CheckinRequest contractRequest);

    ContractDto checkOut(CheckoutRequest checkoutRequest);

    ContractDto findById(Long id);

    Page<ContractDto> search(ContractSearchRequest searchRequest);

    ContractDto changeOwner(ContractCustomerRequest contractRequest);

    ContractDto leave(ContractCustomerRequest request);

    ContractDto addMember(AddMemberRequest addMemberRequest);

    byte[] printContract(Long id) throws IOException;

    List<ContractDto> findAllAvailable();

    List<ContractDto> findAll();
}
