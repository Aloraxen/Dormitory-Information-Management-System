package com.cxt.dormitory.service;

import com.cxt.dormitory.entity.User;
import com.cxt.dormitory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("账号不存在：" + username);
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole())
                .disabled(!Boolean.TRUE.equals(user.getEnabled()))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User save(User form) {
        if (form.getId() == null) {
            if (userRepository.existsByUsername(form.getUsername())) {
                throw new IllegalArgumentException("登录账号已存在：" + form.getUsername());
            }
            form.setPassword(passwordEncoder.encode(form.getPassword()));
        } else {
            User old = userRepository.findById(form.getId()).orElse(null);
            if (old != null) {
                String newPwd = form.getPassword();
                if (newPwd == null || newPwd.trim().isEmpty()) {
                    form.setPassword(old.getPassword());
                } else {
                    form.setPassword(passwordEncoder.encode(newPwd));
                }
            }
        }
        if (form.getEnabled() == null) {
            form.setEnabled(true);
        }
        return userRepository.save(form);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public void toggleEnabled(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setEnabled(!Boolean.TRUE.equals(user.getEnabled()));
            userRepository.save(user);
        }
    }
}
