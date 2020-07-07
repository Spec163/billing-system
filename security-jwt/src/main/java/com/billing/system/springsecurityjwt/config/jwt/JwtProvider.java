package com.billing.system.springsecurityjwt.config.jwt;

import com.billing.system.springsecurityjwt.entity.UserEntity;
import com.billing.system.springsecurityjwt.repository.UserEntityRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Log
public class JwtProvider {

    @Value("$(jwt.secret)")
    private String jwtSecret;

    @Autowired
    private UserEntityRepository userEntityRepository;

    private Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    // сохранять информацию через claims
    public String generateToken(String login) {
        Date date = Date.from(LocalDate.now().plusDays(20).atStartOfDay(ZoneId.systemDefault()).toInstant());
        final Map<String, Object> claims = new HashMap<>();
        UserEntity userInfo = userEntityRepository.findByLogin(login);
        claims.put("phoneNumber", userInfo.getPhoneNumber());
        claims.put("login", userInfo.getLogin());
        String result = Jwts.builder()
                .setSubject(login)
                .setExpiration(date)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .addClaims(claims)
                .compact();
        logger.debug("created jwt = {}", result);
        return result;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.severe("invalid token");
        }
        return false;
    }

    public String getLoginFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public Map<String, Object> getClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }
}
