package vn.huuloc.boardinghouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import vn.huuloc.boardinghouse.dto.SettingDto;
import vn.huuloc.boardinghouse.dto.mapper.SettingMapper;
import vn.huuloc.boardinghouse.repository.SettingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SettingCacheService {

    public static final String ALL_SETTINGS_CACHE_KEY = "setting";

    @Autowired
    private SettingRepository settingRepository;

    @Cacheable(ALL_SETTINGS_CACHE_KEY)
    public Map<String, SettingDto> getAllSettings() {
        List<SettingDto> settings = SettingMapper.INSTANCE.entityListDto2List(settingRepository.findAll());
        return Optional.ofNullable(settings).orElse(new ArrayList<>())
                .stream()
                .collect(Collectors.toMap(SettingDto::getKey, Function.identity(), (o, n) -> o));
    }
}
