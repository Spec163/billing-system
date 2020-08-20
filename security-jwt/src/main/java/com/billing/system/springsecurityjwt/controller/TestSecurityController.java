package com.billing.system.springsecurityjwt.controller;


import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
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

@RestController
@CrossOrigin
public class TestSecurityController {
    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider provider;
    private final JwtFilter jwtFilter;
    private final AccountInfoRepository accountInfoRepository;

    @Autowired
    public TestSecurityController(
        final UserEntityRepository userEntityRepository,
        final PasswordEncoder passwordEncoder,
        final JwtProvider provider,
        final JwtFilter jwtFilter,
        final AccountInfoRepository accountInfoRepository
    ) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;
        this.provider = provider;
        this.jwtFilter = jwtFilter;
        this.accountInfoRepository = accountInfoRepository;
    }


    @GetMapping("/admin/get")
    public String getAdmin() {
        return "Hi admin";
    }

    // Не смотри на этот метод
    @GetMapping("/user/info")
    public String getUserInfo(final ServletRequest servletRequest) {
        // в попытках идентификации
        final String token = this.jwtFilter.getTokenFromRequest((HttpServletRequest) servletRequest);


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
    public List<AccountInfo> getUserList() {
        final List<AccountInfo> usersInfoList = this.accountInfoRepository.findAll();

        return usersInfoList;
    }
}
