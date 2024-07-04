package org.example.blog.repository.user;

import org.example.blog.domain.user.Role;
import org.example.blog.domain.user.UserRoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRoleType(UserRoleType roleType);
}
