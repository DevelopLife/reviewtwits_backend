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
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseEntity {

    @Id @GeneratedValue
    private long reviewId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "projectId")
    private Project project;

    @Column(name = "certification_flag")
    @ColumnDefault(value = "false")
    private boolean certificationFlag;

    private String content;

    private String productUrl;

    private int score;

    @Transient
    @Setter
    private List<String> reviewImageNameList;
}