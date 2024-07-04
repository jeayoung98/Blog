package org.example.blog.repository.user;

import org.example.blog.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email); // 이메일 기준으로 사용자 찾기
    User findByUsername(String username); // 사용자 이름으로 사용자 찾기
    List<User> findByEmailStatusTrue(); // 이메일 수신 상태 true 인 사용자 찾기
}
