package org.example.blog.service.user;

import lombok.RequiredArgsConstructor;
import org.example.blog.domain.user.Role;
import org.example.blog.domain.user.UserRoleType;
import org.example.blog.repository.user.RoleRepository;
import org.example.blog.service.user.userInterface.RoleInterface;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService implements RoleInterface {
    private final RoleRepository roleRepository;

    @Override
    public Role getRoleByRoleType(UserRoleType roleType) {
        return roleRepository.findByRoleType(roleType);
    }
}
