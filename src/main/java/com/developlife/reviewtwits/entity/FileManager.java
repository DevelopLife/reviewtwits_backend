package com.developlife.reviewtwits.entity;

import javax.persistence.*;

@Entity
public class FileManager {

    @Id @GeneratedValue
    @Column(name = "file_manager_id")
    private Long fileManagerID;

    @JoinColumn(name = "file_storage_id")
    @Column(name = "file_storage_id")
    private Long fileStorageID; // 외래키 설정 필요

    @Column(name = "reference_id")
    private Long referenceID;

    @Column(name = "reference_type")
    private String referenceType;
}
