package vn.huuloc.boardinghouse.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.huuloc.boardinghouse.dto.SettingDto;
import vn.huuloc.boardinghouse.service.SettingService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class SettingController {
    private final SettingService settingService;


    @GetMapping
    public Map<String, SettingDto> getSettings() {
        return settingService.getAllSettings();
    }

    @PostMapping
    public void updateSetting(SettingDto settingDto) {
        settingService.updateSetting(settingDto.getKey(), settingDto.getValue());
    }
}
