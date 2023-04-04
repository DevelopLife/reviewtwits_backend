package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.FileManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileManagerRepository extends JpaRepository<FileManager, Long> {
    Optional<FileManager> findByFileInfo_FileID(Long fileStorageId);

    @Query(value = "SELECT fs.realFilename FROM fileStorage fs INNER JOIN fileManager fm ON fs.fileStorageId = fm.fileStorageId WHERE fm.referenceId = ?1 AND fm.referenceType LIKE ?2 AND fs.exist = true", nativeQuery = true)
    List<String> findRealFileNameByReferenceIdAAndReferenceType(Long referenceId, String referenceType);
}
