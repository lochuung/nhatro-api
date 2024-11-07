package vn.huuloc.boardinghouse.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.huuloc.boardinghouse.dto.SettingDto;
import vn.huuloc.boardinghouse.service.SettingService;

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
    public void updateSetting(@Valid @RequestBody SettingDto settingDto) {
        settingService.updateSetting(settingDto.getKey(), settingDto.getValue());
    }
}
