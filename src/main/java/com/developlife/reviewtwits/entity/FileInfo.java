package com.developlife.reviewtwits.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "file_storage")
public class FileInfo {

    @Id
    @Column(name = "file_storage_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileID;

    private String filePath;

    private String realFilename;

    private String originalFilename;

    @Builder.Default
    @ColumnDefault(value = "true")
    private boolean exist = true;
}
