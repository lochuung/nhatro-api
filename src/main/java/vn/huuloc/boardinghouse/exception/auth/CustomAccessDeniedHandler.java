package vn.huuloc.boardinghouse.exception.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import vn.huuloc.boardinghouse.exception.ErrorMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        ErrorMessage errorResponse = ErrorMessage.builder()
                .code(String.valueOf(HttpStatus.FORBIDDEN.value()))
                .message(HttpStatus.FORBIDDEN.getReasonPhrase())
                .description(accessDeniedException.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(errorResponse));
        writer.flush();
    }
}
