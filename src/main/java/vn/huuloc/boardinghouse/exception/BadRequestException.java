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
public class BadRequestException extends RuntimeException {
    private String code;
    private String message;

    public static BadRequestException message(String message) {
        return BadRequestException.builder()
                .message(message)
                .build();
    }
}