package com.developlife.reviewtwits.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

    private boolean certificationFlag;

    @NotNull
    private boolean exist;

    private String content;

    private String productUrl;

    private int score;

    @Transient
    @Setter
    private List<String> reviewImageNameList;
}