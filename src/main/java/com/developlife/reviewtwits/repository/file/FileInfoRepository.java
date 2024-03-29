package com.developlife.reviewtwits.repository.file;

import com.developlife.reviewtwits.entity.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
    Optional<FileInfo> findByOriginalFilename(String originalFilename);
    Optional<FileInfo> findFileInfoByRealFilename(String realFilename);
    List<FileInfo> findFileInfosByRealFilenameIn(List<String> realFilenameList);
}
