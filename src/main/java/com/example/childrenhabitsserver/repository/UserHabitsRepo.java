package com.example.childrenhabitsserver.repository;

import com.example.childrenhabitsserver.entity.HabitsStorage;
import com.example.childrenhabitsserver.entity.UserHabitsStorage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserHabitsRepo extends JpaRepository<UserHabitsStorage, String> {
    List<UserHabitsStorage> findByUserIdAndStatus(String userId, Integer status);
    List<UserHabitsStorage> findByUserId(String userId);
    Page<UserHabitsStorage> findByUserId(String userId, Pageable pageable);
}
