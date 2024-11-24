package vn.huuloc.boardinghouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.huuloc.boardinghouse.model.entity.ServiceFee;

public interface ServiceFeeRepository extends JpaRepository<ServiceFee, Long>, JpaSpecificationExecutor<ServiceFee> {
}
