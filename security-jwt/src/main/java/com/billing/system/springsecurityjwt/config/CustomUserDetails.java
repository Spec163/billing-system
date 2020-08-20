package com.billing.system.springsecurityjwt.config;

import java.util.Collection;
import java.util.Collections;
import com.billing.system.springsecurityjwt.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private String login;
    private String password;
    private Collection<? extends GrantedAuthority> grantedAuthorities;

    static CustomUserDetails fromUserEntityToCustomUserDetails(final UserEntity userEntity) {
        final CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.login = userEntity.getLogin();
        customUserDetails.password = userEntity.getPassword();
        customUserDetails.grantedAuthorities = Collections
            .singletonList(new SimpleGrantedAuthority(userEntity.getRoleEntity().getName()));
        return customUserDetails;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
