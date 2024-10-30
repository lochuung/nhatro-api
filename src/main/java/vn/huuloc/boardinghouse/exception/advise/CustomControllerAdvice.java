package vn.huuloc.boardinghouse.exception.advise;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import vn.huuloc.boardinghouse.exception.BadRequestException;
import vn.huuloc.boardinghouse.exception.ErrorMessage;
import vn.huuloc.boardinghouse.exception.ExpectationFailedException;
import vn.huuloc.boardinghouse.exception.UnauthorizedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@ResponseBody
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomControllerAdvice extends ResponseEntityExceptionHandler {



    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        log.error(e.getMessage(), e.getCause());

        List<String> errors = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        ErrorMessage error = ErrorMessage.builder()
                .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .timestamp(LocalDateTime.now())
                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .description(errors.get(0))
                .errors(errors)
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<Object> badRequest(BadRequestException e) {
        log.error("Bad request error: {}", e.getMessage());
        ErrorMessage error = ErrorMessage.builder().code(String.valueOf(HttpStatus.BAD_REQUEST.value())).timestamp(LocalDateTime.now()).description(e.getMessage()).message(e.getMessage()).build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler({ExpectationFailedException.class})
    public ResponseEntity<Object> failedException(BadRequestException e) {
        log.error("ExpectationFailedException error: {}", e.getMessage());
        ErrorMessage error = ErrorMessage.builder().code(String.valueOf(HttpStatus.EXPECTATION_FAILED.value())).timestamp(LocalDateTime.now()).description(e.getMessage()).message(e.getMessage()).build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler({BadCredentialsException.class,
            InternalAuthenticationServiceException.class,
            UnauthorizedException.class})
    public ResponseEntity<Object> unauthorized(Exception e) {
        log.error("Unauthorized error: {}", e.getMessage());
        ErrorMessage error = ErrorMessage.builder()
                .code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .message(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .timestamp(LocalDateTime.now())
                .description(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> ex(Exception e) {
        log.error("server error", e);
        ErrorMessage error = ErrorMessage.builder().code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())).message(e.getMessage()).timestamp(LocalDateTime.now()).description(e.getMessage()).build();
        return ResponseEntity.internalServerError().body(error);
    }
}