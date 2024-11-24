package vn.huuloc.boardinghouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.huuloc.boardinghouse.model.entity.Contract;
import vn.huuloc.boardinghouse.enums.ContractStatus;
import vn.huuloc.boardinghouse.model.projection.ContractWithLatestNumberIndex;
import vn.huuloc.boardinghouse.model.projection.LatestNumberIndex;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long>, JpaSpecificationExecutor<Contract> {
    Contract findByRoomIdAndStatus(Long roomId, ContractStatus status);

    List<Contract> findByStatus(ContractStatus contractStatus);

    List<Contract> findAllByStatus(ContractStatus contractStatus);

    @Query("""
                SELECT new vn.huuloc.boardinghouse.model.projection.LatestNumberIndex(
                    i.newElectricityNumber,
                    i.newElectricityNumber
                )
                FROM Contract c
                JOIN c.invoices i
                WHERE c = :contract
                  AND i.startDate = (
                      SELECT MAX(i2.startDate)
                      FROM Invoice i2
                      WHERE i2.contract = c
                  )
            """)
    LatestNumberIndex findLatestNumberIndexById(@Param("contract") Contract contract);


    @Query("""
                SELECT new vn.huuloc.boardinghouse.model.projection.ContractWithLatestNumberIndex(
                    i.newElectricityNumber,
                    i.newWaterNumber,
                    c
                )
                FROM Contract c
                JOIN c.invoices i
                WHERE c.status = :contractStatus
                  AND
                i.startDate = (
                      SELECT MAX(i2.startDate)
                      FROM Invoice i2
                      WHERE i2.contract.id = c.id
                  )
            """)
    List<ContractWithLatestNumberIndex> findAllWithLatestNumberIndex(@Param("contractStatus") ContractStatus status);
}
