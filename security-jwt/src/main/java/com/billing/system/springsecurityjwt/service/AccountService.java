package com.billing.system.springsecurityjwt.service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import com.billing.system.springsecurityjwt.config.jwt.JwtFilter;
import com.billing.system.springsecurityjwt.config.jwt.JwtProvider;
import com.billing.system.springsecurityjwt.controller.request.AuthRequest;
import com.billing.system.springsecurityjwt.controller.request.RegistrationRequest;
import com.billing.system.springsecurityjwt.controller.response.AuthResponse;
import com.billing.system.springsecurityjwt.entity.AccountInfo;
import com.billing.system.springsecurityjwt.entity.UserEntity;
import com.billing.system.springsecurityjwt.repository.AccountInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final JwtProvider jwtProvider;
    private final JwtFilter jwtFilter;
    private final AccountInfoRepository accountInfoRepository;
    private final UserService userService;

    @Autowired
    public AccountService(
        final JwtProvider jwtProvider,
        final JwtFilter jwtFilter,
        final AccountInfoRepository accountInfoRepository,
        final UserService userService
    ) {
        this.jwtProvider = jwtProvider;
        this.jwtFilter = jwtFilter;
        this.accountInfoRepository = accountInfoRepository;
        this.userService = userService;
    }

    public String getPhoneNumberFromRequest(final HttpServletRequest request) {
        String phoneNumber = null;
        final String token = this.jwtFilter.getTokenFromRequest(request);
        // вытаскиваем значение поля (phoneNumber) из токена
        phoneNumber = (String) this.jwtProvider.getClaimsFromToken(token).get("phoneNumber");
        return phoneNumber;
    }

    public List<AccountInfo> getUsersList() {

        return this.accountInfoRepository.findAll();
    }

    public AccountInfo findAccountByToken(final HttpServletRequest request) {
        return this.accountInfoRepository
            .findByPhoneNumber(this.getPhoneNumberFromRequest(request));
    }

    public AccountInfo findAccountByPhoneNumber(final String phoneNumber) {
        return this.accountInfoRepository
            .findByPhoneNumber(phoneNumber);
    }

    public AuthResponse userAuth(final AuthRequest request) {
        final UserEntity userEntity =
            this.userService.findByLoginAndPassword(request.getLogin(), request.getPassword());

        final String token = this.jwtProvider.generateToken(userEntity.getLogin());

        return new AuthResponse(token, userEntity.getRoleEntity().getName());
    }


    public ResponseEntity<String> registerUser(final RegistrationRequest registrationRequest) {
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

        if (this.accountInfoRepository.findByPhoneNumber(registrationRequest.getPhoneNumber()) != null)
            return ResponseEntity.badRequest().body("This phone number is already registered!");

        // проверка по логину есть в фильтре (Ответ: Ошибка 500)
        if (this.userService.findByLogin(registrationRequest.getLogin()) != null)
            return ResponseEntity.badRequest().body("This login is already taken!");

        final UserEntity userEntity = new UserEntity();
        userEntity.setPassword(registrationRequest.getPassword());
        userEntity.setLogin(registrationRequest.getLogin());
        userEntity.setPhoneNumber(registrationRequest.getPhoneNumber());

        // ПЕРЕДЕЛАТЬ ЭТОТ УЖАС
        final AccountInfo accountInfo = new AccountInfo();
        accountInfo.setLogin(registrationRequest.getLogin());
        accountInfo.setBalance(0L);
        accountInfo.setPhoneNumber(registrationRequest.getPhoneNumber());
        accountInfo.setTitle("Default");
        accountInfo.setPrice(0L);
        accountInfo.setCall(0L);
        accountInfo.setSms(0L);
        accountInfo.setInternet(0L);
        this.accountInfoRepository.save(accountInfo);

        this.userService.saveUser(userEntity);
        return ResponseEntity.ok("User registered successfully!");
    }
}
