package com.developlife.reviewtwits.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "file_storage")
public class FileInfo {

    @Id
    @Column(name = "file_storage_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileID;

    private String filePath;

    private String realFilename;

    private String originalFilename;

    public FileInfo() {
    }

    public FileInfo(String filePath, String realFilename, String originalFilename) {
        this.filePath = filePath;
        this.realFilename = realFilename;
        this.originalFilename = originalFilename;
    }
}
