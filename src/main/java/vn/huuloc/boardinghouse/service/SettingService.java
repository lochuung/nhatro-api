package vn.huuloc.boardinghouse.service;


import vn.huuloc.boardinghouse.model.dto.SettingDto;

import java.util.List;
import java.util.Map;

public interface SettingService {
    Map<String, SettingDto> getAllSettings();
    String getSetting(String key);
    Map<String, String> getSetting();
    void updateSetting(String key, String value);
    void updateSettings(List<SettingDto> settings);
}
