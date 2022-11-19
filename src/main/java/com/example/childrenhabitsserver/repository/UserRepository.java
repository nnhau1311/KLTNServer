package com.example.childrenhabitsserver.repository;

import com.example.childrenhabitsserver.entity.UserCustomStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserCustomStorage, String> {
    UserCustomStorage findByUsername(String username);
    UserCustomStorage findByEmail(String email);
    UserCustomStorage findByUsernameOrEmail(String username, String email);
    UserCustomStorage findByUsernameOrEmailAndStatusIs(String username, String email, Integer status);
}
