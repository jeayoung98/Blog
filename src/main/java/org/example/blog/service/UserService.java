package org.example.blog.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.blog.domain.Blog;
import org.example.blog.domain.Role;
import org.example.blog.domain.User;
import org.example.blog.domain.UserRoleType;
import org.example.blog.repository.BlogRepository;
import org.example.blog.repository.RoleRepository;
import org.example.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public User createUser(String email, String password, String name, String profileImage, boolean emailStatus) throws Exception {
        if (userRepository.findByName(name) != null) {
            throw new Exception("이미 생성된 아이디 입니다.");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new Exception("이미 생성된 이메일 입니다.");
        }

        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setProfileImage(profileImage);
        newUser.setEmailStatus(emailStatus);
        newUser.setPassword(password);

        roleToUser(newUser, UserRoleType.user);

        return userRepository.save(newUser);
    }
    @Transactional
    public void roleToUser(User user, UserRoleType roleType) {
        Role role = roleRepository.findByRoleType(roleType);
        Set<Role> set = new HashSet<>();
        set.add(role);
        if (role != null) {
            user.setRoles(set);
            userRepository.save(user);
        }
    }
    @Transactional
    public boolean loginUser(String email, String password, HttpServletResponse response) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            Cookie cookie = new Cookie("userId", user.getId().toString());
            cookie.setHttpOnly(true);
            cookie.setMaxAge(7 * 24 * 60 * 60);
            cookie.setPath("/api");
            response.addCookie(cookie);
            return true;
        }
        return false;
    }
    @Transactional
    public Long getUserIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("userId".equals(cookie.getName())) {
                    return Long.valueOf(cookie.getValue());
                }
            }
        }
        return null;
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public boolean existsByName(String name) {
        return userRepository.findByName(name) != null;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}