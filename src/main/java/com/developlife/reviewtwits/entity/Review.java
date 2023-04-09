package com.developlife.reviewtwits.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.List;

/**
 * @author WhalesBob
 * @since 2023-03-13
 */

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseEntity {

    @Id @GeneratedValue
    private long reviewId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder.Default
    @ColumnDefault(value = "false")
    private boolean certificationFlag = false;

    @Builder.Default
    @ColumnDefault(value = "true")
    private boolean exist = true;

    @Column(columnDefinition = "TEXT", length = 65535)
    private String content;
    @Column(length = 1024)
    private String productUrl;
    private String productName;

    private int score;

    @Builder.Default
    @ColumnDefault(value = "0")
    private int commentCount = 0;

    @Builder.Default
    @ColumnDefault(value = "0")
    private int reactionCount = 0;

    @Transient
    @Setter
    private List<String> reviewImageNameList;
}