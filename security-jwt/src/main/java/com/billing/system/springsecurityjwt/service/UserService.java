package com.billing.system.springsecurityjwt.service;


import com.billing.system.springsecurityjwt.entity.RoleEntity;
import com.billing.system.springsecurityjwt.entity.UserEntity;
import com.billing.system.springsecurityjwt.repository.RoleEntityRepository;
import com.billing.system.springsecurityjwt.repository.UserEntityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final RoleEntityRepository roleEntityRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
        final UserEntityRepository userEntityRepository,
        final RoleEntityRepository roleEntityRepository,
        final PasswordEncoder passwordEncoder
    ) {
        this.userEntityRepository = userEntityRepository;
        this.roleEntityRepository = roleEntityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity saveUser(final UserEntity userEntity) {
        final RoleEntity userRole = this.roleEntityRepository.findByName("ROLE_USER");
        userEntity.setRoleEntity(userRole);
        userEntity.setPhoneNumber(userEntity.getPhoneNumber());
        userEntity.setPassword(this.passwordEncoder.encode(userEntity.getPassword()));
        return this.userEntityRepository.save(userEntity);
    }

    public UserEntity findByLogin(final String login) {
        return this.userEntityRepository.findByLogin(login);
    }

    public UserEntity findByLoginAndPassword(final String login, final String password) {
        final UserEntity userEntity = this.findByLogin(login);
        if (userEntity != null) {
            if (this.passwordEncoder.matches(password, userEntity.getPassword())) {
                return userEntity;
            }
        }
        return null;
    }
}
