package com.billing.system.springsecurityjwt.config.jwt;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import com.billing.system.springsecurityjwt.config.CustomUserDetails;
import com.billing.system.springsecurityjwt.config.CustomUserDetailsService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import static org.springframework.util.StringUtils.hasText;

@Component
@Log
public class JwtFilter extends GenericFilterBean {

    private static final String AUTHORIZATION = "Authorization";

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public void doFilter(
        final ServletRequest servletRequest,
        final ServletResponse servletResponse,
        final FilterChain filterChain
    ) throws IOException, ServletException {
        this.logger.info("do filter...");
        final String token = this.getTokenFromRequest((HttpServletRequest) servletRequest);
        if (token != null && this.jwtProvider.validateToken(token)) {
            final String userLogin = this.jwtProvider.getLoginFromToken(token);
            final CustomUserDetails customUserDetails = this.customUserDetailsService.loadUserByUsername(userLogin);
            final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    // private
    public String getTokenFromRequest(final HttpServletRequest request) {
        final String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    public String getPhoneNumberFromRequest(final HttpServletRequest request) {
        String phoneNumber = null;
        final String token = this.getTokenFromRequest((HttpServletRequest) request);
        // вытаскиваем значение поля (phoneNumber) из токена
        phoneNumber = (String) this.jwtProvider.getClaimsFromToken(token).get("phoneNumber");
        return phoneNumber;
    }
}
