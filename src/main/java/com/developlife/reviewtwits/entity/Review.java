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
    @JoinColumn(name = "user_id")
    private User user; // user 핵심 정보만 전달할 수 있게 하기

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project; // project ID 만 들고오게 -> mapstruct 처리해서 projectID

    @ColumnDefault(value = "false")
    private boolean certificationFlag;

    private String content;

    private String productUrl;

    private int score;

    @Transient
    @Setter
    private List<String> reviewImageNameList;
}