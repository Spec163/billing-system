package com.billing.system.springsecurityjwt.config.jwt;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

@Component
@Log
public class JwtProvider {

    @Value("$(jwt.secret)")
    private String jwtSecret;

    @Autowired
    private UserEntityRepository userEntityRepository;

    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    // сохранять информацию через claims
    public String generateToken(final String login) {
        final Date date = Date.from(LocalDate.now().plusDays(20).atStartOfDay(ZoneId.systemDefault()).toInstant());
        final Map<String, Object> claims = new HashMap<>();
        final UserEntity userInfo = this.userEntityRepository.findByLogin(login);
        claims.put("phoneNumber", userInfo.getPhoneNumber());
        claims.put("login", userInfo.getLogin());
        final String result = Jwts.builder()
            .setSubject(login)
            .setExpiration(date)
            .setIssuedAt(new Date())
            .signWith(SignatureAlgorithm.HS512, this.jwtSecret)
            .addClaims(claims)
            .compact();
        logger.debug("created jwt = {}", result);
        return result;
    }

    boolean validateToken(final String token) {
        try {
            Jwts.parser().setSigningKey(this.jwtSecret).parseClaimsJws(token);
            return true;
        } catch (final Exception e) {
            log.severe("invalid token");
        }
        return false;
    }

    String getLoginFromToken(final String token) {
        final Claims claims = Jwts.parser().setSigningKey(this.jwtSecret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public Map<String, Object> getClaimsFromToken(final String token) {
        return Jwts.parser().setSigningKey(this.jwtSecret).parseClaimsJws(token).getBody();
    }
}
