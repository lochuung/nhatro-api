package vn.huuloc.boardinghouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.huuloc.boardinghouse.entity.Branch;
import vn.huuloc.boardinghouse.entity.ServiceFee;

public interface ServiceFeeRepository extends JpaRepository<ServiceFee, Long> {
}
