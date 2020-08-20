package com.billing.system.springsecurityjwt.config;

import com.billing.system.springsecurityjwt.entity.UserEntity;
import com.billing.system.springsecurityjwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public CustomUserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final UserEntity userEntity = this.userService.findByLogin(username);
        return CustomUserDetails.fromUserEntityToCustomUserDetails(userEntity);
    }
}
