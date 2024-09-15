package vn.huuloc.boardinghouse.exception.advise;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import vn.huuloc.boardinghouse.exception.BadRequestException;
import vn.huuloc.boardinghouse.exception.ErrorMessage;
import vn.huuloc.boardinghouse.exception.ExpectationFailedException;
import vn.huuloc.boardinghouse.exception.UnauthorizedException;

import java.time.LocalDateTime;

@ControllerAdvice
@ResponseBody
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<Object> badRequest(BadRequestException e) {
        logger.error("BadRequestException error", e);
        ErrorMessage error = ErrorMessage.builder().code(String.valueOf(HttpStatus.BAD_REQUEST.value())).timestamp(LocalDateTime.now()).description(e.getMessage()).message(e.getMessage()).build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler({ExpectationFailedException.class})
    public ResponseEntity<Object> failedException(BadRequestException e) {
        logger.error("ExpectationFailedException error", e);
        ErrorMessage error = ErrorMessage.builder().code(String.valueOf(HttpStatus.EXPECTATION_FAILED.value())).timestamp(LocalDateTime.now()).description(e.getMessage()).message(e.getMessage()).build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<Object> badCredentials(BadCredentialsException e) {
        logger.error(e.getMessage(), e.getCause());
        ErrorMessage error = ErrorMessage.builder()
                .code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .message(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .timestamp(LocalDateTime.now())
                .description(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<Object> unauthorized(UnauthorizedException e) {
        logger.error("UnauthorizedException error", e);
        ErrorMessage error = ErrorMessage.builder().code(String.valueOf(HttpStatus.UNAUTHORIZED.value())).timestamp(LocalDateTime.now()).description(HttpStatus.UNAUTHORIZED.getReasonPhrase()).message(e.getMessage()).build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> ex(Exception e) {
        logger.error("server error", e);
        ErrorMessage error = ErrorMessage.builder().code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())).message(e.getMessage()).timestamp(LocalDateTime.now()).description(e.getMessage()).build();
        return ResponseEntity.internalServerError().body(error);
    }
}