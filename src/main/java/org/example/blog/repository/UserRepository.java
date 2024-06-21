package org.example.blog.repository;

import org.example.blog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email); // 이메일 기준으로 사용자 찾기
    User findByName(String name); // 사용자 이름으로 사용자 찾기
    List<User> findByEmailStatusTrue(); // 이메일 수신 상태 true 인 사용자 찾기
}
