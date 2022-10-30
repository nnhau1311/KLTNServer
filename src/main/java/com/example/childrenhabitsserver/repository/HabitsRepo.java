package com.example.childrenhabitsserver.repository;

import com.example.childrenhabitsserver.entity.HabitsStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitsRepo extends JpaRepository<HabitsStorage, String> {
}
