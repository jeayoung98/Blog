package org.example.blog.service.user.userInterface;

import org.example.blog.domain.user.Role;
import org.example.blog.domain.user.UserRoleType;

public interface RoleInterface {
    Role getRoleByRoleType(UserRoleType roleType);
}
