package com.cxt.dormitory.config;

import com.cxt.dormitory.entity.User;
import com.cxt.dormitory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 系统启动时确保默认演示账号存在（sql/dormitory.sql 中已包含同样的账号，
 * 这里作为兜底：如果用户手动清空了账户表，重启后依然可以登录）。
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        ensureUser("admin", "admin123", "张伟", "ROLE_ADMIN");
        ensureUser("manager", "manager123", "李静", "ROLE_MANAGER");
        ensureUser("student01", "student123", "王小明", "ROLE_STUDENT");
        ensureUser("student02", "stu02@123", "李思思", "ROLE_STUDENT");
        ensureUser("student03", "stu03@123", "赵宇航", "ROLE_STUDENT");
    }

    private void ensureUser(String username, String password, String name, String role) {
        User existing = userRepository.findByUsername(username);
        if (existing != null) {
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setRole(role);
        user.setEnabled(true);
        userRepository.save(user);
    }
}
