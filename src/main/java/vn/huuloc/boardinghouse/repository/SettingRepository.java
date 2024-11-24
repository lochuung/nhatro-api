package vn.huuloc.boardinghouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.huuloc.boardinghouse.model.entity.settings.Setting;

import java.util.List;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
    Setting findByKey(String key);

    List<Setting> findByKeyIn(List<String> keys);
}
