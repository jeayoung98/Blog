package org.example.blog.service.user.userInterface;

import jakarta.servlet.http.HttpServletRequest;
import org.example.blog.domain.user.User;

public interface UserInterface {
    User findUserById(Long userId);

    Long getUserIdFromCookie(HttpServletRequest request);
}
