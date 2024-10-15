package vn.huuloc.boardinghouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.huuloc.boardinghouse.entity.ContractCustomerLinked;

import java.util.List;

public interface ContractCustomerLinkedRepository extends JpaRepository<ContractCustomerLinked, Long> {
    @Query("SELECT ccl FROM ContractCustomerLinked ccl WHERE ccl.contractId = ?1 AND ccl.customerId IN ?2 AND ccl.hasLeft = false")
    List<ContractCustomerLinked> findByRenterIds(Long contractId, List<Long> customerIds);

    @Query("SELECT ccl FROM ContractCustomerLinked ccl WHERE ccl.contractId = ?1 AND ccl.customerId = ?2 AND ccl.hasLeft = false")
    ContractCustomerLinked findByRenterId(Long id, Long customerId);

    @Query("SELECT ccl FROM ContractCustomerLinked ccl WHERE ccl.contractId = ?1 AND ccl.hasLeft = false AND ccl.isOwner = true")
    ContractCustomerLinked findOwnerByContractId(Long id);

    @Query("SELECT ccl FROM ContractCustomerLinked ccl WHERE ccl.contractId = ?1 AND ccl.hasLeft = false")
    List<ContractCustomerLinked> findRentersByContractId(Long contractId);
}
