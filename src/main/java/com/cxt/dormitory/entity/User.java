package com.cxt.dormitory.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户账户表 t_user：登录账号，角色区分权限
 */
@Entity
@Table(name = "t_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 登录账号 */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** 登录密码（BCrypt加密） */
    @Column(nullable = false, length = 100)
    private String password;

    /** 姓名 */
    @Column(nullable = false, length = 50)
    private String name;

    /** 角色：ROLE_ADMIN / ROLE_MANAGER / ROLE_STUDENT */
    @Column(nullable = false, length = 20)
    private String role;

    /** 是否启用 */
    @Column(nullable = false)
    private Boolean enabled = true;

    /** 创建时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
