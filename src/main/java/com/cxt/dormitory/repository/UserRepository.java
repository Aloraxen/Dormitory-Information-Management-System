package com.cxt.dormitory.repository;

import com.cxt.dormitory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findByRoleOrderByIdAsc(String role);
}
