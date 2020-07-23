package com.billing.system.springsecurityjwt.controller;


import com.billing.system.springsecurityjwt.config.jwt.JwtFilter;
import com.billing.system.springsecurityjwt.config.jwt.JwtProvider;
import com.billing.system.springsecurityjwt.entity.AccountInfo;
import com.billing.system.springsecurityjwt.repository.AccountInfoRepository;
import com.billing.system.springsecurityjwt.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class TestSecurityController {
    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider provider;

    @Autowired
    public TestSecurityController(
            final UserEntityRepository userEntityRepository,
            final PasswordEncoder passwordEncoder,
            final JwtProvider provider
            ) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;
        this.provider = provider;
    }
    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private AccountInfoRepository accountInfoRepository;

    @GetMapping("/admin/get")
    public String getAdmin() {
        return "Hi admin";
    }

    // Не смотри на этот метод
    @GetMapping("/user/info")
    public String getUserInfo(ServletRequest servletRequest) {
        // в попытках идентификации
        String token = jwtFilter.getTokenFromRequest((HttpServletRequest) servletRequest);

        String info = this.provider
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
    public List<AccountInfo> getUserList() {
        List<AccountInfo> usersInfoList = accountInfoRepository.findAll();

        return usersInfoList;
    }
}
