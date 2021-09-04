package uz.paynet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.paynet.payload.ApiResponse;
import uz.paynet.payload.LoginDto;
import uz.paynet.payload.RegisterDto;
import uz.paynet.service.AuthService;

@RestController
@RequestMapping("/api/chat")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/signup")
    public HttpEntity<?> registerUser(@RequestBody RegisterDto registerDto) {
        ApiResponse apiResponse = authService.registerUser(registerDto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 401).body(apiResponse);
    }

    @GetMapping("/confirmationEmail")
    public HttpEntity<?> verifyEmail(
            @RequestParam String emailCode, @RequestParam String email
    ) {
        ApiResponse apiResponse = authService.confirmationEmail(emailCode, email);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 401).body(apiResponse);
    }

    @PostMapping("/login")
    public HttpEntity<?> login(@RequestBody LoginDto loginDto) {
        ApiResponse apiResponse = authService.login(loginDto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 401).body(apiResponse);
    }

    @GetMapping("/getOnlineUser")
    public HttpEntity<?> getOnlineUser() {
        ApiResponse apiResponse = authService.getOnlineUser();
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 401).body(apiResponse);
    }

}
