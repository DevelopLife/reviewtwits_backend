package com.developlife.reviewtwits.repository.file;

import com.developlife.reviewtwits.entity.FileManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileManagerRepository extends JpaRepository<FileManager,Long>, FileCustomRepository {
    FileManager findByFileInfo_FileID(Long fileStorageId);
    List<String> getRealFilename(Long referenceId, String referenceType);
}
