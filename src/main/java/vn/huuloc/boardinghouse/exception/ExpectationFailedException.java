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
public class ExpectationFailedException extends RuntimeException {
    private String code;
    private String message;

    public static ExpectationFailedException message(String message) {
        return ExpectationFailedException.builder()
                .message(message)
                .build();
    }
}