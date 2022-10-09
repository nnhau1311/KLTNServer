package com.example.childrenhabitsserver.repository;

import com.example.childrenhabitsserver.entity.TestJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepo extends JpaRepository<TestJPA, String> {
}
