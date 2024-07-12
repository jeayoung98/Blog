package org.example.blog.service.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.blog.domain.user.Role;
import org.example.blog.domain.user.User;
import org.example.blog.domain.user.UserRoleType;
import org.example.blog.jwt.jwtUtil.JwtTokenizer;
import org.example.blog.repository.user.UserRepository;
import org.example.blog.service.user.userInterface.RoleInterface;
import org.example.blog.service.user.userInterface.UserInterface;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserInterface {
    private final UserRepository userRepository;
    private final RoleInterface roleService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;

    @Transactional
    public User createUser(String email, String password, String username ,String name, String profileImage, boolean emailStatus) throws Exception {
        if (userRepository.findByUsername(name) != null) {
            throw new Exception("이미 생성된 아이디 입니다.");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new Exception("이미 생성된 이메일 입니다.");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setProfileImage(profileImage);
        newUser.setEmailStatus(emailStatus);
        newUser.setPassword(passwordEncoder.encode(password));

        roleToUser(newUser, UserRoleType.user);

        return userRepository.save(newUser);
    }

    @Transactional
    public void roleToUser(User user, UserRoleType roleType) {
        Role role = roleService.getRoleByRoleType(roleType);
        Set<Role> set = new HashSet<>();
        set.add(role);
        if (role != null) {
            user.setRoles(set);
            userRepository.save(user);
        }
    }

    @Transactional
    @Override
    public Long getUserIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return jwtTokenizer.getUserIdFromToken(cookie.getValue());
                }
            }
        }
        return 0L;
    }


    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public boolean existsByName(String name) {
        return userRepository.findByUsername(name) != null;
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }


}