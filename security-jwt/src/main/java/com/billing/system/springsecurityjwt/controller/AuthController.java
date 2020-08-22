package com.billing.system.springsecurityjwt.controller;


import javax.validation.Valid;
import com.billing.system.springsecurityjwt.controller.request.AuthRequest;
import com.billing.system.springsecurityjwt.controller.request.RegistrationRequest;
import com.billing.system.springsecurityjwt.controller.response.AuthResponse;
import com.billing.system.springsecurityjwt.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class AuthController {

    private final AccountService accountService;

    @Autowired
    public AuthController(final AccountService accountService) {
        this.accountService = accountService;
    }


    @PostMapping("/registration") // Не работает валидация в RegistrationRequest
    public ResponseEntity<String> registerUser(@Valid @RequestBody final RegistrationRequest registrationRequest) {
        return this.accountService.registerUser(registrationRequest);
    }

    @PostMapping("/auth")
    public AuthResponse auth(@RequestBody final AuthRequest request) {
        return this.accountService.userAuth(request);
    }
}
