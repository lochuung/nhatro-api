package vn.huuloc.boardinghouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.huuloc.boardinghouse.entity.Contract;
import vn.huuloc.boardinghouse.enums.ContractStatus;

import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    Contract findByRoomIdAndStatus(Long roomId, ContractStatus status);
}
