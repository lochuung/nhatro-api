package vn.huuloc.boardinghouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BoardinghouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoardinghouseApplication.class, args);
    }

}
