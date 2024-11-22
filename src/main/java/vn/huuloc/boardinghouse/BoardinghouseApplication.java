package vn.huuloc.boardinghouse;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import vn.huuloc.boardinghouse.constant.SettingConstants;
import vn.huuloc.boardinghouse.service.SettingService;

@SpringBootApplication
@EnableFeignClients
public class BoardinghouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoardinghouseApplication.class, args);
    }


    @Bean
    CommandLineRunner init(SettingService settingService) {
        return args -> {
            settingService.updateSetting(SettingConstants.ELECTRICITY_UNIT_PRICE, "3000");
            settingService.updateSetting(SettingConstants.WATER_UNIT_PRICE, "6700");
            settingService.updateSetting(SettingConstants.START_DAY_OF_INVOICE, "1");
        };
    }
}
