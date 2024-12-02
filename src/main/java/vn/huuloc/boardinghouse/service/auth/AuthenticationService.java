package vn.huuloc.boardinghouse.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.huuloc.boardinghouse.constant.SecurityConstants;
import vn.huuloc.boardinghouse.controller.auth.AuthenticationRequest;
import vn.huuloc.boardinghouse.controller.auth.AuthenticationResponse;
import vn.huuloc.boardinghouse.exception.BadRequestException;
import vn.huuloc.boardinghouse.exception.UnauthorizedException;
import vn.huuloc.boardinghouse.model.dto.message.NotificationMessage;
import vn.huuloc.boardinghouse.model.dto.request.ForgotPasswordRequest;
import vn.huuloc.boardinghouse.model.dto.request.ResetPasswordRequest;
import vn.huuloc.boardinghouse.model.entity.User;
import vn.huuloc.boardinghouse.model.entity.VerifyCode;
import vn.huuloc.boardinghouse.repository.UserRepository;
import vn.huuloc.boardinghouse.repository.VerifyCodeRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    //    private final UserRepository userRepository;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private VerifyCodeRepository verifyCodeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ObjectMapper objectMapper;

//  public AuthenticationResponse register(RegisterRequest request) {
//    var user = User.builder()
//        .firstname(request.getFirstname())
//        .lastname(request.getLastname())
//        .email(request.getEmail())
//        .password(passwordEncoder.encode(request.getPassword()))
//        .role(request.getRole())
//        .build();
//    var savedUser = repository.save(user);
//    var jwtToken = jwtService.generateToken(user);
//    var refreshToken = jwtService.generateRefreshToken(user);
//    saveUserToken(savedUser, jwtToken);
//    return AuthenticationResponse.builder()
//        .accessToken(jwtToken)
//            .refreshToken(refreshToken)
//        .build();
//  }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserDetails user = userDetailsService.loadUserByUsername(request.getEmail());
        if (user == null) {
            throw BadRequestException.message("Can not find user: " + request.getEmail());
        }
        String jwtToken = jwtService.generateToken(user);
//        var refreshToken = jwtService.generateRefreshToken(user);
//        revokeAllUserTokens(user);
//        saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder().accessToken(jwtToken)
                //.refreshToken(refreshToken)
                .build();
    }

//  private void saveUserToken(User user, String jwtToken) {
//    var token = Token.builder()
//        .user(user)
//        .token(jwtToken)
//        .tokenType(TokenType.BEARER)
//        .expired(false)
//        .revoked(false)
//        .build();
//    tokenRepository.save(token);
//  }

//  private void revokeAllUserTokens(User user) {
//    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
//    if (validUserTokens.isEmpty())
//      return;
//    validUserTokens.forEach(token -> {
//      token.setExpired(true);
//      token.setRevoked(true);
//    });
//    tokenRepository.saveAll(validUserTokens);
//  }

    public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(SecurityConstants.BEARER)) {
            throw new UnauthorizedException();
        }
        final String refreshToken = authHeader.substring(SecurityConstants.BEARER.length());
        final String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            UserDetails user = userDetailsService.loadUserByUsername(userEmail);
            if (user == null) {
                throw BadRequestException.message("Can not find user: " + userEmail);
            }
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
//                revokeAllUserTokens(user);
//                saveUserToken(user, accessToken);
                return AuthenticationResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
            }
        }
        throw new UnauthorizedException();
    }

    public void forgotPassword(ForgotPasswordRequest request) throws JsonProcessingException {

        UserDetails user = userDetailsService.loadUserByUsername(request.getEmail());
        if (user == null) {
            throw BadRequestException.message("Can not find user: " + request.getEmail());
        }

        // Generate random 6-digit code
        String code = String.format("%06d", new Random().nextInt(999999));

        // Save verification code to repository
        VerifyCode verifyCode = VerifyCode.builder()
                .email(request.getEmail())
                .code(code)
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .build();
        verifyCodeRepository.save(verifyCode);

        // Send notification via RabbitMQ
        NotificationMessage message = NotificationMessage.builder()
                .email(request.getEmail())
                .title("Đặt lại mật khẩu")
                .body("Mã xác thực của bạn là: " + code)
                .build();
        String jsonMessage = objectMapper.writeValueAsString(message);
        rabbitTemplate.convertAndSend("notifications", jsonMessage);
    }

    public void verifyCode(ResetPasswordRequest request) {
        VerifyCode verifyCode = verifyCodeRepository.findByEmailAndCode(request.getEmail(), request.getVerifyCode());
        if (verifyCode == null || verifyCode.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw BadRequestException.message("Mã xác thực không hợp lệ hoặc đã hết hạn.");
        }
    }

    public void resetPassword(ResetPasswordRequest request) {
        verifyCode(request); // Verify the code before resetting the password

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> BadRequestException.message("Không tìm thấy tài khoản: " + request.getEmail()));
        if (request.getNewPassword() == null) {
            throw BadRequestException.message("Mật khẩu mới phải được nhập.");
        }

        // Update the user's password
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
}
