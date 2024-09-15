package vn.huuloc.boardinghouse.exception.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import vn.huuloc.boardinghouse.exception.ErrorMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ErrorMessage errorResponse = ErrorMessage.builder()
                .code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .message(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .description(authException.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(errorResponse));
        writer.flush();
    }
}
