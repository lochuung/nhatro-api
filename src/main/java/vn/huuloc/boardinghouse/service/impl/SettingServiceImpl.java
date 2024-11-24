package vn.huuloc.boardinghouse.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.huuloc.boardinghouse.model.dto.SettingDto;
import vn.huuloc.boardinghouse.model.entity.settings.Setting;
import vn.huuloc.boardinghouse.exception.BadRequestException;
import vn.huuloc.boardinghouse.repository.SettingRepository;
import vn.huuloc.boardinghouse.service.SettingCacheService;
import vn.huuloc.boardinghouse.service.SettingService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SettingServiceImpl implements SettingService {

    @Autowired
    private SettingCacheService settingCacheService;

    @Autowired
    private SettingRepository settingRepository;

    @Override
    public Map<String, SettingDto> getAllSettings() {
        return settingCacheService.getAllSettings();
    }

    @Override
    public String getSetting(String key) {
        return Optional.ofNullable(settingCacheService.getAllSettings().get(key))
                .map(SettingDto::getValue)
                .orElse(null);
    }

    @Override
    public Map<String, String> getSetting() {
        return Optional.ofNullable(settingCacheService.getAllSettings())
                .orElse(new HashMap<>())
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getValue()
                ));
    }

    @Override
    @CacheEvict(value = SettingCacheService.ALL_SETTINGS_CACHE_KEY, allEntries = true)
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
    @CacheEvict(value = SettingCacheService.ALL_SETTINGS_CACHE_KEY, allEntries = true)
    @Transactional
    public void updateSettings(List<SettingDto> settings) {
        if (CollectionUtils.isEmpty(settings)) {
            throw BadRequestException.message("settings must not be empty");
        }
        List<String> keys = settings.stream().map(SettingDto::getKey).toList();
        List<Setting> existingList = settingRepository.findByKeyIn(keys);
        Map<String, Setting> existingMap = Optional.ofNullable(existingList)
                .orElse(new ArrayList<>())
                .stream()
                .collect(Collectors.toMap(Setting::getKey, Function.identity(), (o, n) -> o));
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
