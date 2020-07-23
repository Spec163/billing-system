package com.billing.system.springsecurityjwt.controller;


import com.billing.system.springsecurityjwt.config.jwt.JwtProvider;
import com.billing.system.springsecurityjwt.controller.request.AuthRequest;
import com.billing.system.springsecurityjwt.controller.request.RegistrationRequest;
import com.billing.system.springsecurityjwt.controller.response.AuthResponse;
import com.billing.system.springsecurityjwt.entity.AccountInfo;
import com.billing.system.springsecurityjwt.entity.UserEntity;
import com.billing.system.springsecurityjwt.repository.AccountInfoRepository;
import com.billing.system.springsecurityjwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@CrossOrigin
public class AuthController {
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final AccountInfoRepository accountInfoRepository;

    @Autowired
    public AuthController(
            final UserService userService,
            final JwtProvider jwtProvider,
            final AccountInfoRepository accountInfoRepository
    ) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.accountInfoRepository = accountInfoRepository;
    }


    @PostMapping("/registration") // Не работает валидация в RegistrationRequest
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {

        if (
            registrationRequest.getLogin() == null ||
            registrationRequest.getPassword() == null ||
            registrationRequest.getPhoneNumber() == null ||
            registrationRequest.getLogin().length() < 4 ||
            registrationRequest.getPassword().length() < 4 ||
            registrationRequest.getPhoneNumber().length() < 6
        )
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid data: \n" + registrationRequest.toString());

        if (accountInfoRepository.findByPhoneNumber(registrationRequest.getPhoneNumber()) != null)
            return ResponseEntity.badRequest().body("This phone number is already registered!");

        // проверка по логину есть в фильтре (Ответ: Ошибка 500)
        if (userService.findByLogin(registrationRequest.getLogin()) != null)
            return ResponseEntity.badRequest().body("This login is already taken!");

        UserEntity userEntity = new UserEntity();
        userEntity.setPassword(registrationRequest.getPassword());
        userEntity.setLogin(registrationRequest.getLogin());
        userEntity.setPhoneNumber(registrationRequest.getPhoneNumber());

        // ПЕРЕДЕЛАТЬ ЭТОТ УЖАС
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setLogin(registrationRequest.getLogin());
        accountInfo.setBalance(0L);
        accountInfo.setPhoneNumber(registrationRequest.getPhoneNumber());
        accountInfo.setTitle("Default");
        accountInfo.setPrice(0L);
        accountInfo.setCall(0L);
        accountInfo.setSms(0L);
        accountInfo.setInternet(0L);
        accountInfoRepository.save(accountInfo);

        userService.saveUser(userEntity);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/auth")
    public AuthResponse auth(@RequestBody AuthRequest request) {
        UserEntity userEntity = userService.findByLoginAndPassword(request.getLogin(), request.getPassword());
        String token = jwtProvider.generateToken(userEntity.getLogin());
        AuthResponse authResponse = new AuthResponse(token, userEntity.getRoleEntity().getName());
        return authResponse;
    }
}
