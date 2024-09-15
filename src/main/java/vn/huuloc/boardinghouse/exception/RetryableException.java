package vn.huuloc.boardinghouse.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetryableException extends RuntimeException {
    private String code;
    private String message;

    public static RetryableException message(String message) {
        return RetryableException.builder().message(message).build();
    }
}