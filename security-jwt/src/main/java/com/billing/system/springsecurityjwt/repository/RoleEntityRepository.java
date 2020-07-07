package com.billing.system.springsecurityjwt.repository;

import com.billing.system.springsecurityjwt.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleEntityRepository extends JpaRepository<RoleEntity, Integer> {

    RoleEntity findByName(String name);
}
