package com.developlife.reviewtwits.repository;

import com.developlife.reviewtwits.entity.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {

}
