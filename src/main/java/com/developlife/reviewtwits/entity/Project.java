package com.developlife.reviewtwits.entity;

import com.developlife.reviewtwits.type.project.Language;
import com.developlife.reviewtwits.type.project.ProjectPricePlan;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * @author ghdic
 * @since 2023/03/10
 */
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
@Setter
@Entity(name = "Project")
public class Project extends BaseEntity {
    @Id @GeneratedValue
    private long projectId;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @Column(unique = true)
    private String projectName;

    private String projectDescription;

    private String uriPattern;

    private String category;

    @Enumerated(value = EnumType.STRING)
    private Language language;

    @Column(length = 7)
    private String projectColor;

    @Builder.Default
    @Enumerated(value = EnumType.STRING)
    @ColumnDefault("'FREE_PLAN'")
    private ProjectPricePlan pricePlan = ProjectPricePlan.FREE_PLAN;

    @ColumnDefault("0")
    private int reviewCount;
}
