package com.developlife.reviewtwits.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author ghdic
 * @since 2023/02/27
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "RefreshToken")
public class RefreshToken extends BaseEntity {
    @Id @GeneratedValue
    private long id;
    private String token;
    @Column(unique = true)
    private String accountId;
}
