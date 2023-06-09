package com.developlife.reviewtwits.entity;

import com.developlife.reviewtwits.type.project.Device;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author WhalesBob
 * @since 2023-04-21
 */

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class StatInfo {

    @Id
    @GeneratedValue
    private long statId;

    private String inflowUrl;

    private String productUrl;

    @ManyToOne
    private Project project;

    @ManyToOne
    private Product product;

    @ManyToOne
    private User user;

    @Enumerated(value = EnumType.STRING)
    private Device device;

    @CreatedDate
    @Column(updatable = false, columnDefinition = "TIMESTAMP DEFAULT '0000-00-00 00:00:00")
    private LocalDateTime createdDate;
}