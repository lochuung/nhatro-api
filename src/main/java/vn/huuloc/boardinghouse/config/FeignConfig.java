package vn.huuloc.boardinghouse.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {


    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }

}
