package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.FileManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileManagerRepository extends JpaRepository<FileManager, Long> {
    Optional<FileManager> findByFileStorageID(Long fileStorageId);
}
