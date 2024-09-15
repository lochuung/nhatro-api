package vn.huuloc.boardinghouse.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.huuloc.boardinghouse.exception.ErrorMessage;
import vn.huuloc.boardinghouse.service.auth.JwtService;
import vn.huuloc.boardinghouse.util.AuthUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Builder
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private JwtService jwtService;

    private UserDetailsService userDetailService;

    private ObjectMapper objectMapper;

    @Value("#{'${security.ignore.paths}'.split(',')}")
    private List<String> ignoreList;

    @Value("${app.security.enabled:true}")
    private boolean isSecuredMode;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!isSecuredMode) {
            filterChain.doFilter(request, response);
            return;
        }
        if (ignoreList.stream().anyMatch(s -> request.getServletPath().contains(s))) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Lấy jwt từ request
            String jwt = AuthUtils.getJwtFromRequest(request);

            if (!StringUtils.hasText(jwt) && !jwtService.validateToken(jwt)) {
                unAuthorizedResponse(response);
                return;
            }

            String userEmail = jwtService.extractUsername(jwt);
            UserDetails userDetails = userDetailService.loadUserByUsername(userEmail);
            if (userDetails == null) {
                unAuthorizedResponse(response);
                return;
            }
            if (!userDetails.isEnabled()) {
                unAuthorizedResponse(response);
                return;
            }
            // Nếu người dùng hợp lệ, set thông tin cho Seturity Context
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error(e.getMessage());
            unAuthorizedResponse(response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void unAuthorizedResponse(HttpServletResponse response) throws IOException {
        ErrorMessage error = ErrorMessage.builder().code(String.valueOf(HttpStatus.UNAUTHORIZED.value())).timestamp(LocalDateTime.now()).message(HttpStatus.UNAUTHORIZED.getReasonPhrase()).description(HttpStatus.UNAUTHORIZED.getReasonPhrase()).build();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(error));
        writer.flush();
    }

}