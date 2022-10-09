package com.example.childrenhabitsserver.repository;

import com.example.childrenhabitsserver.entity.UserCustomStorge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserCustomStorge, String> {
    UserCustomStorge findByUsername(String username);
}
