package vn.huuloc.boardinghouse.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.huuloc.boardinghouse.dto.SettingDto;
import vn.huuloc.boardinghouse.dto.mapper.SettingMapper;
import vn.huuloc.boardinghouse.entity.settings.Setting;
import vn.huuloc.boardinghouse.exception.BadRequestException;
import vn.huuloc.boardinghouse.repository.SettingRepository;
import vn.huuloc.boardinghouse.service.SettingService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SettingServiceImpl implements SettingService {

    public static final String ALL_SETTINGS_CACHE_KEY = "setting";
    @Autowired
    private SettingRepository settingRepository;

    @Cacheable(ALL_SETTINGS_CACHE_KEY)
    @Override
    public Map<String, SettingDto> getAll() {
        List<SettingDto> settings = SettingMapper.INSTANCE.entityListDto2List(settingRepository.findAll());
        return Optional.ofNullable(settings).orElse(new ArrayList<>()).stream().collect(Collectors.toMap(SettingDto::getKey, Function.identity(), (o, n) -> o));
    }



    @Override
    public String getSetting(String key) {
        return Optional.ofNullable(getAll().get(key)).map(SettingDto::getValue).orElse(null);
    }

    @Override
    public Map<String, String> getSetting() {
        return Optional.ofNullable(getAll()).orElse(new HashMap<>()).entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getValue()
                ));
    }

    @Override
    @CacheEvict(value = ALL_SETTINGS_CACHE_KEY, allEntries = true)
    @Transactional
    public void updateSetting(String key, String value) {
        Setting setting = settingRepository.findByKey(key);
        if (setting == null) {
            setting = new Setting();
            setting.setKey(key);
        }
        setting.setValue(value);
        settingRepository.save(setting);
    }

    @Override
    @CacheEvict(value = ALL_SETTINGS_CACHE_KEY, allEntries = true)
    @Transactional
    public void updateSettings(List<SettingDto> settings) {
        if (CollectionUtils.isEmpty(settings)) {
            throw BadRequestException.message("settings must not be empty");
        }
        List<String> keys = settings.stream().map(SettingDto::getKey).toList();
        List<Setting> existingList = settingRepository.findByKeyIn(keys);
        Map<String, Setting> existingMap = Optional.ofNullable(existingList).orElse(new ArrayList<>()).stream().collect(Collectors.toMap(Setting::getKey, Function.identity(), (o, n) -> o));
        List<Setting> newSettings = new ArrayList<>();
        for (SettingDto s : settings) {
            Setting newSetting = existingMap.get(s.getKey());
            if (newSetting == null) {
                newSetting = new Setting();
                newSetting.setKey(s.getKey());
            }
            newSetting.setValue(s.getValue());
            newSettings.add(newSetting);
        }
        settingRepository.saveAll(newSettings);
    }
}
