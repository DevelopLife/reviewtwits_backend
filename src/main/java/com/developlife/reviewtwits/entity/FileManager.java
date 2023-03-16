package com.developlife.reviewtwits.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "file_manager")
public class FileManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_manager_id")
    private Long fileManagerID;

    @OneToOne
    @JoinColumn(name = "file_storage_id")
    private FileInfo fileInfo; // 외래키 설정 필요

    @Column(name = "reference_id")
    private Long referenceID;

    @Column(name = "reference_type")
    private String referenceType;

    public FileManager() {
    }

    public FileManager(FileInfo fileInfo, Long referenceID, String referenceType) {
        this.fileInfo = fileInfo;
        this.referenceID = referenceID;
        this.referenceType = referenceType;
    }
}
