package com.example.childrenhabitsserver.repository;

import com.example.childrenhabitsserver.entity.UserCustomStorge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserCustomStorge, String> {
    UserCustomStorge findByUsername(String username);
    UserCustomStorge findByEmail(String email);
    UserCustomStorge findByUsernameOrEmail(String username, String email);
    UserCustomStorge findByUsernameOrEmailAndStatus(String username, String email, Integer status);
}
