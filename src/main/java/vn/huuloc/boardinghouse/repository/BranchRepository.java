package vn.huuloc.boardinghouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.huuloc.boardinghouse.entity.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long> {
}
