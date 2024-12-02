package vn.huuloc.boardinghouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.huuloc.boardinghouse.model.entity.Branch;
import vn.huuloc.boardinghouse.model.entity.VerifyCode;

public interface VerifyCodeRepository extends JpaRepository<VerifyCode, Long> {
    VerifyCode findByEmailAndCode(String email, String code);
}
