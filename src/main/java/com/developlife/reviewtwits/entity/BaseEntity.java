package com.developlife.reviewtwits.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * @author ghdic
 * @since 2023/03/10
 */
@EntityListeners(AuditingEntityListener.class)
@Getter
@MappedSuperclass // 엔티티 속성만 상속
public class BaseEntity {
    @Setter
    @CreatedDate
    @Column(updatable = false, columnDefinition = "TIMESTAMP DEFAULT '0000-00-00 00:00:00'")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(columnDefinition = "TIMESTAMP DEFAULT '0000-00-00 00:00:00'")
    private LocalDateTime lastModifiedDate;
}
