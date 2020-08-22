package com.billing.system.springsecurityjwt.controller;


import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import com.billing.system.springsecurityjwt.config.jwt.JwtFilter;
import com.billing.system.springsecurityjwt.config.jwt.JwtProvider;
import com.billing.system.springsecurityjwt.entity.AccountInfo;
import com.billing.system.springsecurityjwt.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class TestSecurityController {
    private final JwtProvider provider;
    private final JwtFilter jwtFilter;

    @Autowired
    public TestSecurityController(
        final JwtProvider provider,
        final JwtFilter jwtFilter,
        final AccountService accountService
    ) {
        this.provider = provider;
        this.jwtFilter = jwtFilter;
        this.accountService = accountService;
    }

    private final AccountService accountService;


    @GetMapping("/admin/get")
    public String getAdmin() {
        return "Hi admin";
    }

    // Не смотри на этот метод
    @GetMapping("/user/info")
    public String getUserInfo(final HttpServletRequest request) {
        // в попытках идентификации
        final String token = this.jwtFilter.getTokenFromRequest(request);

        final String info = this.provider
            .getClaimsFromToken(token)
            .entrySet().stream().map(ks -> String.format("%s = %s", ks.getKey(), ks.getValue()))
            .collect(Collectors.joining(", "));

        return String.format("Your number: %s for Token: %s", info, token);
    }

    @GetMapping("/user/forbidden")
    public String getUserError() {
        return "Forbidden";
    }

    @GetMapping("admin/users")
    public List<AccountInfo> getUsersList() {
        return this.accountService.getUsersList();
    }

    @PostMapping("profile")
    public AccountInfo getAccountInformation(final HttpServletRequest request) {
        return this.accountService.findAccountByToken(request);
    }
}
