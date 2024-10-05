package vn.huuloc.boardinghouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.huuloc.boardinghouse.entity.CustomerImage;

import java.util.List;
import java.util.Optional;

public interface CustomerImageRepository extends JpaRepository<CustomerImage, Long> {
    List<CustomerImage> findAllByCustomerId(Long id);

    Optional<CustomerImage> findByImageKey(String imageKey);

    List<CustomerImage> findByCustomerId(Long customerId);
}
