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
@Entity(name = "Project")
public class Project extends BaseEntity {
    @Id @GeneratedValue
    private long projectId;
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;
    @Setter
    private String projectName;
    @Setter
    private String projectDescription;
    @Setter
    private String uriPattern;
    @Setter
    private String category;
    @Setter
    @Enumerated(value = EnumType.STRING)
    private Language language;
    @Setter
    @Column(length = 7)
    private String projectColor;
    @Setter
    @Builder.Default
    @Enumerated(value = EnumType.STRING)
    @ColumnDefault("'FREE_PLAN'")
    private ProjectPricePlan pricePlan = ProjectPricePlan.FREE_PLAN;

    @ColumnDefault("0")
    private int reviewCount;
}
