package com.cxt.dormitory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 独立的密码编码器配置。
 * 单独拆分是为了避免循环依赖：SecurityConfig 依赖 UserService，
 * 而 UserService 依赖 PasswordEncoder，若 PasswordEncoder 仍声明在
 * SecurityConfig 中会形成 Bean 创建环（Spring Boot 2.6+ 默认禁止循环引用）。
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
