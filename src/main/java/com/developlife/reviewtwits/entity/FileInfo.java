package com.developlife.reviewtwits.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "file_storage")
public class FileInfo {

    @Id
    @Column(name = "file_storage_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileID;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "real_filename")
    private String realFilename;

    @Column(name = "original_filename")
    private String originalFilename;

    public FileInfo() {
    }

    public FileInfo(String filePath, String realFilename, String originalFilename) {
        this.filePath = filePath;
        this.realFilename = realFilename;
        this.originalFilename = originalFilename;
    }
}
