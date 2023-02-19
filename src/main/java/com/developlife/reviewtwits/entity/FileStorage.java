package com.developlife.reviewtwits.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class FileStorage {

    @Id
    @Column(name = "file_storage_id")
    @GeneratedValue
    private Long fileStorageID;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "real_filename")
    private String realFilename;

    @Column(name = "original_filename")
    private String originalFilename;


}
