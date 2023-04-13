package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.FileManager;
import com.developlife.reviewtwits.type.FileReferenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileManagerRepository extends JpaRepository<FileManager, Long> {
    Optional<FileManager> findByFileInfo_FileID(Long fileStorageId);

    @Query(value = "SELECT fs.real_filename FROM file_storage fs INNER JOIN file_manager fm ON fs.file_storage_id = fm.file_storage_id WHERE fm.reference_id = ?1 AND fm.reference_type = ?2 AND fs.exist = true", nativeQuery = true)
    List<String> findRealFileNameByReferenceIdAndReferenceType(Long referenceId, String referenceType);
}
