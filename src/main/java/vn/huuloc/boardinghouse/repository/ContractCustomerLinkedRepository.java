package vn.huuloc.boardinghouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.huuloc.boardinghouse.entity.Contract;
import vn.huuloc.boardinghouse.entity.ContractCustomerLinked;

import java.util.List;

public interface ContractCustomerLinkedRepository extends JpaRepository<ContractCustomerLinked, Long> {
    @Query("SELECT ccl FROM ContractCustomerLinked ccl WHERE ccl.contractId = ?1 AND ccl.customerId IN ?2 AND ccl.isRenter = true")
    List<ContractCustomerLinked> findByRenterIds(Long contractId, List<Long> customerIds);

    @Query("SELECT ccl FROM ContractCustomerLinked ccl WHERE ccl.contractId = ?1 AND ccl.customerId = ?2 AND ccl.isRenter = true")
    ContractCustomerLinked findByRenterId(Long id, Long customerId);

    @Query("SELECT ccl FROM ContractCustomerLinked ccl WHERE ccl.contractId = ?1 AND ccl.isRenter = true AND ccl.isOwner = true")
    ContractCustomerLinked findOwnerByContractId(Long id);
}
