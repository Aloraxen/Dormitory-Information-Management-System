package com.cxt.dormitory.config;

import com.cxt.dormitory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security 安全配置：
 *   ROLE_ADMIN    系统管理员：全部功能 + 用户管理与备份恢复
 *   ROLE_MANAGER  宿管员：学生/宿舍/住宿/申请处理
 *   ROLE_STUDENT  学生：查看与维护个人信息、提交申请、查看本人住宿信息
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                        // 放行登录页与静态资源
                        .antMatchers("/login", "/css/**", "/js/**", "/vendor/**", "/images/**").permitAll()
                        // 系统管理（用户管理、备份恢复）仅管理员
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        // 学生自助功能（放在 /students/** 之前，优先匹配）
                        .antMatchers("/students/my/**").hasAnyRole("ADMIN", "MANAGER", "STUDENT")
                        // 宿管相关功能：管理员与宿管员
                        .antMatchers("/students/**", "/dorm/**", "/stay/**",
                                "/apply/list/**", "/apply/handle/**").hasAnyRole("ADMIN", "MANAGER")
                        // 学生个人功能
                        .antMatchers("/my/**", "/apply/my/**", "/apply/new/**", "/apply/save/**").hasAnyRole("ADMIN", "MANAGER", "STUDENT")
                        // 其余请求均需登录
                        .anyRequest().authenticated()
                .and()
                        .formLogin()
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll()
                .and()
                        .logout()
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                .and()
                        .exceptionHandling()
                        .accessDeniedPage("/403");
    }
}
