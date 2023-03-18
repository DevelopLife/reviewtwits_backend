package com.developlife.reviewtwits.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "file_manager")
public class FileManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileManagerId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "file_storage_id")
    private FileInfo fileInfo; // 외래키 설정 필요

    private Long referenceId;

    private String referenceType;

    public FileManager() {
    }

    public FileManager(FileInfo fileInfo, Long referenceId, String referenceType) {
        this.fileInfo = fileInfo;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
    }
}
