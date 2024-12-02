package vn.huuloc.boardinghouse.controller.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.huuloc.boardinghouse.model.dto.request.ForgotPasswordRequest;
import vn.huuloc.boardinghouse.model.dto.request.ResetPasswordRequest;
import vn.huuloc.boardinghouse.service.auth.AuthenticationService;
import vn.huuloc.boardinghouse.util.ResponseUtils;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
public class AuthenticationController {

    @Autowired
    private AuthenticationService service;

//    @PostMapping("/register")
//    public ResponseEntity<AuthenticationResponse> register(
//            @RequestBody RegisterRequest request
//    ) {
//        return ResponseEntity.ok(service.register(request));
//    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(@RequestBody ForgotPasswordRequest request) throws JsonProcessingException {
        service.forgotPassword(request);
        return ResponseEntity.ok(ResponseUtils.success());
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Object> verifyCode(@RequestBody ResetPasswordRequest request) {
        service.verifyCode(request);
        return ResponseEntity.ok(ResponseUtils.success());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@RequestBody ResetPasswordRequest request) {
        service.resetPassword(request);
        return ResponseEntity.ok(ResponseUtils.success());
    }

//    @PostMapping("/refresh-token")
//    public ResponseEntity<AuthenticationResponse> refreshToken(
//            HttpServletRequest request,
//            HttpServletResponse response
//    ) throws IOException {
//        return ResponseEntity.ok(service.refreshToken(request, response));
//    }

}
