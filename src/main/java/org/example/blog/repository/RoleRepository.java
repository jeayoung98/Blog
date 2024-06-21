package org.example.blog.repository;

import org.example.blog.domain.Role;
import org.example.blog.domain.UserRoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRoleType(UserRoleType roleType);
}
