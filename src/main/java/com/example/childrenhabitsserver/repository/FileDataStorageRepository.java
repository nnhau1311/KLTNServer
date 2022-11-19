package com.example.childrenhabitsserver.repository;

import com.example.childrenhabitsserver.entity.FileDataStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDataStorageRepository extends JpaRepository<FileDataStorage, String> {

}
