package com.aurionpro.repository;

import com.aurionpro.entity.User;
import com.aurionpro.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(UserRole role);

    List<User> findByRoleAndIsActiveTrue(UserRole role);

    List<User> findByRoleInAndIsActiveTrue(List<UserRole> roles);
}