package com.example.childrenhabitsserver.repository;

import com.example.childrenhabitsserver.entity.HabitsStorage;
import com.example.childrenhabitsserver.entity.UserHabitsStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHabitsRepo extends JpaRepository<UserHabitsStorage, String> {
}
