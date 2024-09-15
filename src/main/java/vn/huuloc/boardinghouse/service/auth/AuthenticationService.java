package vn.huuloc.boardinghouse.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.huuloc.boardinghouse.constant.SecurityConstants;
import vn.huuloc.boardinghouse.controller.auth.AuthenticationRequest;
import vn.huuloc.boardinghouse.controller.auth.AuthenticationResponse;
import vn.huuloc.boardinghouse.exception.BadRequestException;
import vn.huuloc.boardinghouse.exception.UnauthorizedException;

import java.io.IOException;
import java.util.Map;

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
    private HttpServletRequest httpServletRequest;

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

}
